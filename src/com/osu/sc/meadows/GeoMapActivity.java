package com.osu.sc.meadows;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import server.Client;
import server.ServerEvents;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;

import com.google.android.maps.GeoPoint;

import com.osu.sc.mapframework.ClosestPointTrio;
import com.osu.sc.mapframework.GeoImageViewTouch;
import com.osu.sc.mapframework.GeoreferencedPoint;
import com.osu.sc.mapframework.MeetingPoint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;

import android.os.Bundle;

public class GeoMapActivity extends Activity
{
	//The GeoImageView that this activity holds.
	private GeoImageViewTouch geoImageView;
	
	//A list of the geo referenced points for this map.
	List<GeoreferencedPoint> geoReferencedPoints;
	
	//A list for the meeting points for this map.
	List<MeetingPoint> meetingPoints;
	
	//The currently long pressed location on the image for setting meeting points.
	private PointF longPressLoc;
	
	//The current map position of the user.
	private PointF userMapLoc;
	
	//The current map position of the user's friends.
	private ArrayList<PointF> friendsMapLoc;
	
	//The currently displayed map id.
	private int currentMapFileId;

	//Meeting request code.
	private static final int MEETING_REQUEST_CODE = 0;
	
	private UsersUpdatedListener clientLocationListener;
	private UsersUpdatedListener friendsLocationListener;
	
	//On activity creation.
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Set up the client location listener.
		clientLocationListener = new UsersUpdatedListener()
		{
			public void EventFired(UsersUpdatedEvent event) 
			{
				if(event == null)
					return;
				
				ArrayList<User> users = event.GetUsers();
				if(users == null || users.size() <= 0)
					return;
				
				User user = users.get(0);
				if(user == null)
					return;
				
				userLocChanged(user);
			}
			
		};
		
		//Register the listener with the event system.
		ServerEvents.GetInstance().AddClientLocationUpdatedListener(clientLocationListener);
		
		//Set up the friends location listener.
		friendsLocationListener = new UsersUpdatedListener()
		{

			@Override
			public void EventFired(UsersUpdatedEvent event) 
			{
				if(event == null)
					return;
				
				ArrayList<User> users = event.GetUsers();
				friendsLocChanged(users);
			}
		};
		
		//Register the listener with the event system.
		ServerEvents.GetInstance().AddFriendsUpdatedListener(friendsLocationListener);
		
		//Set the content view to the map layout.
		setContentView(R.layout.maplayout);
	
		//Create a list for the meeting points.
		this.meetingPoints = new ArrayList<MeetingPoint>();
		
		//Create a list for the friends.
		this.friendsMapLoc = new ArrayList<PointF>();
		
		//Save the image view.
		this.geoImageView = (GeoImageViewTouch) findViewById(R.id.meadowsImageView);
		
		//Set the image view's activity to this.
		this.geoImageView.setGeoMapActivity(this);
		
		//Load georeferenced points from the meadows data file.
		this.currentMapFileId = R.raw.meadows;
		
		//Create a new 2d tree to hold the geo points.
		loadGeoreferencedPoints(this.currentMapFileId);
		
		//Get the initial friend positions from the client.
		Client client = Client.GetInstance();
		ArrayList<User> users = client.GetFriends();
		friendsLocChanged(users);
		
		//Get the initial user position from the client.
		userLocChanged(new GeoPoint(client.GetLatitude(), client.GetLongitude()));
	}
	
	@Override
	protected void onDestroy()
	{
		//Unregister the client location listener.
		ServerEvents.GetInstance().RemoveClientLocationUpdatedListener(clientLocationListener);
		
		//Force destroy the bitmap.
		this.geoImageView.dispose();
		
		System.gc();
		
		//Call the base class.
		super.onDestroy();
		
	}
	
	//Interface
	
	public ArrayList<PointF> getFriendsMapLoc()
	{
		return this.friendsMapLoc;
	}
	
	public PointF getUserMapLoc()
	{
		return this.userMapLoc;
	}
	
	public List<MeetingPoint> getMeetingPoints()
	{
		return this.meetingPoints;
	}
	
	public void meetingSelected(MeetingPoint mPoint)
	{
		//TODO
	}
	
	//Initiate the create meeting activity.
	public void startCreateMeeting(PointF imageLoc)
	{
		this.longPressLoc = imageLoc;
		Intent meetingIntent = new Intent(this, CreateMeetingActivity.class);
		startActivityForResult(meetingIntent, MEETING_REQUEST_CODE);
	}
	
	public void userSelected()
	{
		//TODO
	}
	
	
	//Utility functions
	protected void createMeetingPoint(PointF imageLoc)
	{
		this.meetingPoints.add(new MeetingPoint(this.currentMapFileId, imageLoc));
		this.geoImageView.invalidate();
	}
	
	protected long squareDistanceBetween(GeoPoint first, GeoPoint second)
	{
		long dlat = first.getLatitudeE6() - second.getLatitudeE6();
		long dlon = first.getLongitudeE6() - second.getLongitudeE6();
		long square_distance = dlat * dlat + dlon * dlon;
		return square_distance;
	}

	protected ClosestPointTrio getClosestPointTrio(GeoPoint worldLoc)
	{
		//Ensure there's at least 3 geo referenced points, otherwise return null.
		if(this.geoReferencedPoints.size() < 3)
			return null;

		long firstDist = Long.MAX_VALUE;
		long secondDist = Long.MAX_VALUE;
		long thirdDist = Long.MAX_VALUE;
		GeoreferencedPoint firstPoint = null;
		GeoreferencedPoint secondPoint = null;
		GeoreferencedPoint thirdPoint = null;
		for(GeoreferencedPoint newPoint : this.geoReferencedPoints)
		{
			//Keep going if the point is farther away than the 2 mins.
			long newDist = squareDistanceBetween(worldLoc, newPoint);
			if(newDist >= thirdDist)
				continue;

			//If it's closer than the first point, move the first point to the second point
			//and update the first point.
			if(newDist < firstDist)
			{
				thirdDist = secondDist;
				thirdPoint = secondPoint;
				secondPoint = firstPoint;
				secondDist = firstDist;
				firstPoint = newPoint;
				firstDist = newDist;
			}
			//Otherwise, just update the second point to the new point.
			else if(newDist < secondDist)
			{
				thirdDist = secondDist;
				thirdPoint = secondPoint;
				secondDist = newDist;
				secondPoint = newPoint;
			}
			else
			{
				thirdDist = newDist;
				thirdPoint = newPoint;
			}
		}

		return new ClosestPointTrio(firstPoint, secondPoint, thirdPoint);
	}

	protected PointF getGeoMapPosition(GeoPoint worldLoc)
	{
		ClosestPointTrio trio = getClosestPointTrio(worldLoc);
		if(trio == null || trio.first == null || trio.second == null || trio.third == null)
			return null;
		
		double x1 = trio.first.getLongitudeE6();
		double y1 = trio.first.getLatitudeE6();
		double x2 = trio.second.getLongitudeE6();
		double y2 = trio.second.getLatitudeE6();
		double x3 = trio.third.getLongitudeE6();
		double y3 = trio.third.getLatitudeE6();
		double x4 = worldLoc.getLongitudeE6();
		double y4 = worldLoc.getLatitudeE6();
		
		double u1 = trio.first.x;
		double v1 = trio.first.y;
		double u2 = trio.second.x;
		double v2 = trio.second.y;
		double u3 = trio.third.x;
		double v3 = trio.third.y;
		
		
		double numeratorA = (x4 - x3) * (y2 - y3) - (y4 - y3) * (x2 - x3);
		double numeratorB = (x4 - x3) * (y4 - y3) - (y1 - y3) * (x4 - x3);
		double denominatorAB = (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
		
		double a = numeratorA / denominatorAB;
		double b = numeratorB / denominatorAB;
		
		double u4 = u3 + a * (u1 - u3) + b * (u2 - u3);
		double v4 = v3 + a * (v1 - v3) + b * (v2 - v3);
		
		return new PointF((float)u4, (float)v4);
	}
	
	protected void loadGeoreferencedPoints(int fileid)
	{
		this.geoReferencedPoints = new ArrayList<GeoreferencedPoint>();
		DataInputStream din = null;
		try
	    {
	       din = new DataInputStream(getApplicationContext().getResources().openRawResource(fileid));
	       BufferedReader br = new BufferedReader(new InputStreamReader(din));
	       String strLine;
	       StringTokenizer st;
	       while ((strLine = br.readLine()) != null)   {
	    	   st = new StringTokenizer(strLine);
	    	   float x = Float.parseFloat(st.nextToken());
	    	   float y = Float.parseFloat(st.nextToken());
	    	   double lat = Double.parseDouble(st.nextToken());
	    	   double lon = Double.parseDouble(st.nextToken());
	    	   int lat_int = (int) (lat * 1E6);
	   		   int lon_int = (int) (lon * 1E6);
	    	   
	    	   this.geoReferencedPoints.add(new GeoreferencedPoint(lat_int, lon_int, x, y));
	       }
	       
	    }
	    catch(FileNotFoundException fe)
	    {
	      System.out.println("FileNotFoundException : " + fe);
	    }
	    catch(IOException ioe)
	    {
	      System.out.println("IOException : " + ioe);
	    }
		finally
		{
			if(din != null) try {din.close();} catch (IOException e) {}
		}
	  }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		//Ensure the request code is for the meeting activity.
		if(requestCode != MEETING_REQUEST_CODE)
			return;
		
		//Do nothing if the user canceled.
		if(resultCode == RESULT_CANCELED)
			return;
		
		//If there's no valid long press location, return.
		if(this.longPressLoc == null)
			return;
		
		//Create a meeting point at the long press location.
		createMeetingPoint(this.longPressLoc);
		
    }
	
	protected void friendsLocChanged(ArrayList<User> users)
	{
		if(users == null || users.size() <= 0)
			return;
		
		ArrayList<PointF> mapLocs = new ArrayList<PointF>();
		for(User user : users)
		{
			PointF mapLoc = getGeoMapPosition(new GeoPoint(user.GetLatitude(), user.GetLongitude()));
			if(mapLoc == null)
				continue;
			
			mapLocs.add(mapLoc);
		}
		
		friendsMapLoc = mapLocs;
		
		this.geoImageView.invalidate();
	}

	protected void userLocChanged(User user)
	{
		if(user == null)
			return;
		
		userLocChanged(new GeoPoint(user.GetLatitude(), user.GetLongitude()));
	}
	
	protected void userLocChanged(GeoPoint loc)
	{
		//Get the geo referenced map position from the world location.
		userMapLoc = getGeoMapPosition(loc);
				
		if(userMapLoc == null)
			return;
						
		//Update the map position on the imageview and redraw.
		this.geoImageView.invalidate();
	}
}