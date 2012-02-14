package com.osu.sc.meadows;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import server.Client;
import server.ServerEvents;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;

import com.osu.sc.mapframework.ClosestPointTrio;
import com.osu.sc.mapframework.GeoImageViewTouch;
import com.osu.sc.mapframework.GeoreferencedPoint;
import com.osu.sc.mapframework.GeoPoint;
import com.osu.sc.mapframework.MeetingPoint;
import com.osu.sc.mapframework.MapUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;

import android.location.Location;
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
	
	//The default center point when the map page is opened when there is no valid map user location.
	private PointF homePointLoc;
	
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
		
		//Unregister the friends location listener.
		ServerEvents.GetInstance().RemoveFriendsUpdatedListener(friendsLocationListener);
		
		//Force destroy the bitmap.
		this.geoImageView.dispose();
		
		this.geoImageView = null;
		
		System.gc();
		
		//Call the base class.
		super.onDestroy();
		
	}
	
	//Interface
	
	public ArrayList<PointF> getFriendsMapLoc()
	{
		return this.friendsMapLoc;
	}
	
	public PointF getHomePointLoc()
	{
		return this.homePointLoc;
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
	
	protected ClosestPointTrio getClosestPointTrioSmart(GeoPoint worldLoc)
	{
		if(this.geoReferencedPoints.size() < 3)
			return null;
		
		//Compute distances from current location to geo point.
		for (GeoreferencedPoint point : this.geoReferencedPoints)
		{
			float[] results = new float[1];
			Location.distanceBetween(worldLoc.lat, worldLoc.lon, point.lat, point.lon, results);
			point.distanceToLocation = results[0];
		}
		
		//Sort the list.
		Collections.sort(this.geoReferencedPoints, new Comparator<GeoreferencedPoint>() 
		{
			public int compare(GeoreferencedPoint p1, GeoreferencedPoint p2)
			{
				return Float.compare(p1.distanceToLocation, p2.distanceToLocation);
			}
		});
		
		//Next index to increment, starts at third index.
		double highestSuitability = Double.NEGATIVE_INFINITY;
		GeoreferencedPoint bestFirst = this.geoReferencedPoints.get(0);
		GeoreferencedPoint bestSecond = null;
		GeoreferencedPoint bestThird = null;
		
		//Move outward in the permutation of points, keeping the first one and changing second and third to find a good match.
		for(int i = 2; i < this.geoReferencedPoints.size(); i++)
		{
			for(int j = 1; j < i; j++)
			{
			
				GeoreferencedPoint secondPoint = this.geoReferencedPoints.get(j);
				GeoreferencedPoint thirdPoint = this.geoReferencedPoints.get(i);
				double suitability = getSuitability(worldLoc, bestFirst, secondPoint, thirdPoint);
				if(suitability > highestSuitability)
				{
					highestSuitability = suitability;
					bestSecond = secondPoint;
					bestThird = thirdPoint;
				
					//Return the set early if it's good enough.
					if(suitability > 0.05)
						return new ClosestPointTrio(bestFirst, bestSecond, bestThird);
				
				}
			}
		}
		
		if(bestSecond == null)
			return null;
		
		return new ClosestPointTrio(bestFirst, bestSecond, bestThird);
	}
	
	protected double getSuitability(GeoPoint worldLoc, GeoPoint first, GeoPoint second, GeoPoint third)
	{
		//Compute the perimeter of the triangle.
		double side1 = MapUtils.pyth(first, second);
		double side2 = MapUtils.pyth(first, third);
		double side3 = MapUtils.pyth(second, third);
		double perimeter = side1 + side2 + side3;
		
		//Compute the average latitude and longitude of the triangle.
		GeoPoint averagePoint = new GeoPoint((first.lat + second.lat + third.lat) / 3, (first.lon + second.lon + third.lon) / 3);
		
		//Compute the distance between the current location and the average latitude and longitude.
		double distance_from_average = MapUtils.pyth(worldLoc, averagePoint);
		
		//Compute the area of the triangle.
		double half_perim = perimeter / 2;
		double area = Math.sqrt(half_perim * (half_perim - side1) * (half_perim - side2) * (half_perim - side3));
		
		//Return the weighted ratio of area to perimeter and average distance.
	    double area_perim_ratio = 5.5 * area / (perimeter * perimeter);
	    double dist_perim_ratio = -1.0 * distance_from_average / perimeter;
	    double perim_ratio = -5.0 * perimeter;
		return area_perim_ratio + dist_perim_ratio + perim_ratio;
		
	}

	protected PointF getGeoMapPosition(GeoPoint worldLoc)
	{
		ClosestPointTrio trio = getClosestPointTrioSmart(worldLoc);
		if(trio == null)
			return null;
		
		double x1 = trio.first.lon;
		double y1 = trio.first.lat;
		double x2 = trio.second.lon;
		double y2 = trio.second.lat;
		double x3 = trio.third.lon;
		double y3 = trio.third.lat;
		double x4 = worldLoc.lon;
		double y4 = worldLoc.lat;
		
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
	       
	       //Load in the default home point.
	       strLine = br.readLine();
	       st = new StringTokenizer(strLine);
	       float homeX = Float.parseFloat(st.nextToken());
	       float homeY = Float.parseFloat(st.nextToken());
	       this.homePointLoc = new PointF(homeX, homeY);
	       
	       //Load in all of the georeferenced points.
	       while ((strLine = br.readLine()) != null)   {
	    	   st = new StringTokenizer(strLine);
	    	   float x = Float.parseFloat(st.nextToken());
	    	   float y = Float.parseFloat(st.nextToken());
	    	   double lat = Double.parseDouble(st.nextToken());
	    	   double lon = Double.parseDouble(st.nextToken());
	    	   
	    	   this.geoReferencedPoints.add(new GeoreferencedPoint(lat, lon, x, y));
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