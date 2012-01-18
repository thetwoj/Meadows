package com.osu.sc.meadows;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.android.maps.GeoPoint;

import com.osu.sc.mapframework.ClosestPointPair;
import com.osu.sc.mapframework.GeoImageViewTouch;
import com.osu.sc.mapframework.GeoreferencedPoint;
import com.osu.sc.mapframework.MeetingPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;

import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GeoMapActivity extends Activity
{
	//Amount in counter clockwise degrees that the map has been rotated from true north.
	private double mapTheta;
	
	//Network and GPS update frequency in milliseconds.
	private static final int NETWORK_PERIOD = 4000;
	private static final int GPS_PERIOD = 4000;
	private static final int MAX_GEOREFERENCE_POINTS = 100;
	
	//The GeoImageView that this activity holds.
	private GeoImageViewTouch geoImageView;
	
	//A list of the geo referenced points for this map.
	List<GeoreferencedPoint> geoReferencedPoints;
	
	//A list for the meeting points for this map.
	List<MeetingPoint> meetingPoints;
	
	//The currently long pressed location on the image for setting meeting points.
	private PointF longPressLoc;
	
	//The current map position of the user.
	private PointF mapLoc;
	
	private GeoPoint userWorldLoc;
	
	//The currently displayed map id.
	private int currentMapFileId;
	
	//Information for saving last known location.
	public static final String SHARED_PREFERENCES_NAME = "AppPreferences";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	
	//Maximum possible latitude and longitude.
	private static final int LAT_MAX = (int) (90 * 1E6);
	private static final int LON_MAX = (int) (180 * 1E6);

	//Meeting request code.
	private static final int MEETING_REQUEST_CODE = 0;
	
	//On activity creation.
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Set the content view to the map layout.
		setContentView(R.layout.maplayout);
	
		//Create a list for the meeting points.
		this.meetingPoints = new ArrayList<MeetingPoint>();
		
		//Save the image view.
		this.geoImageView = (GeoImageViewTouch) findViewById(R.id.meadowsImageView);
		
		//Set the image view's activity to this.
		this.geoImageView.setGeoMapActivity(this);
		
		//Load georeferenced points from the meadows data file.
		this.currentMapFileId = R.raw.meadows;
		
		//Create a new 2d tree to hold the geo points.
		loadGeoreferencedPoints(this.currentMapFileId);
		
		//Restore the most recent location.
		restoreLocation();

		//Start the location listener.
		GeoLocationListener locationListener = new GeoLocationListener(this);
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_PERIOD, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_PERIOD, 0, locationListener);
	}
	
	
	//Inner classes
	
	public class GeoLocationListener implements LocationListener
	{
	   private GeoMapActivity geoActivity;
	   public GeoLocationListener(GeoMapActivity mc_act)
	   {
	      super();
	      this.geoActivity = mc_act;
	   }
	   public void onLocationChanged(Location location)
	   {
	      this.geoActivity.locChanged(location.getLatitude(), location.getLongitude(), true);
	   }
	   public void onProviderDisabled(String provider)
	   {
	   }
	   public void onProviderEnabled(String provider)
	   {
	   }
	   public void onStatusChanged(String provider, int status, Bundle extras)
	   {
	   }
	}
	
	//Interface
	
	public PointF getMapLocation()
	{
		return this.mapLoc;
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
	
	protected long distanceBetween(GeoPoint first, GeoPoint second)
	{
		long dlat = first.getLatitudeE6() - second.getLatitudeE6();
		long dlon = first.getLongitudeE6() - second.getLongitudeE6();
		long square_distance = dlat * dlat + dlon * dlon;
		return (long) Math.sqrt(square_distance);
	}

	protected ClosestPointPair getClosestPointPair(GeoPoint worldLoc)
	{
		//Ensure there's at least 2 geo referenced points, otherwise return null.
		if(this.geoReferencedPoints.size() < 2)
			return null;

		long firstDist = Integer.MAX_VALUE;
		long secondDist = Integer.MAX_VALUE;
		GeoreferencedPoint firstPoint = null;
		GeoreferencedPoint secondPoint = null;
		for(GeoreferencedPoint newPoint : this.geoReferencedPoints)
		{
			//Keep going if the point is farther away than the 2 mins.
			long newDist = distanceBetween(worldLoc, newPoint);
			if(newDist >= secondDist)
				continue;

			//If it's closer than the first point, move the first point to the second point
			//and update the first point.
			if(newDist < firstDist)
			{
				secondPoint = firstPoint;
				secondDist = firstDist;
				firstPoint = newPoint;
				firstDist = newDist;
			}
			//Otherwise, just update the second point to the new point.
			else
			{
				secondDist = newDist;
				secondPoint = newPoint;
			}
		}

		return new ClosestPointPair(firstPoint, secondPoint);
	}

	protected PointF getGeoMapPosition(GeoPoint worldLoc)
	{
		ClosestPointPair pair = getClosestPointPair(worldLoc);
		if(pair == null)
			return null;

		//Take each of the two closest points and rotate their coordinates by -theta to make them north up.
		double u1 =  pair.first.x * Math.cos(Math.toRadians(this.mapTheta)) + pair.first.y * Math.sin(Math.toRadians(this.mapTheta));
		double v1 = -pair.first.x * Math.sin(Math.toRadians(this.mapTheta)) + pair.first.y * Math.cos(Math.toRadians(this.mapTheta));
		double u2 =  pair.second.x * Math.cos(Math.toRadians(this.mapTheta)) + pair.second.y * Math.sin(Math.toRadians(this.mapTheta));
		double v2 = -pair.second.x * Math.sin(Math.toRadians(this.mapTheta)) + pair.second.y * Math.cos(Math.toRadians(this.mapTheta));

		//Get the number of pixels U and V that are gained/lost per unit of longitude and latitude, respectively.
		double pixelsUPerLon = (u2 - u1) / (pair.second.getLongitudeE6() - pair.first.getLongitudeE6());
		double pixelsVPerLat = (v2 - v1) / (pair.second.getLatitudeE6() - pair.first.getLatitudeE6());

		//Starting at the closest point, shift the current location depending on the pixels/lon and pixels/lat 
		//and the difference of lat and lon between our current location and the closest point.
		double u = u1 + (worldLoc.getLongitudeE6() - pair.first.getLongitudeE6()) * pixelsUPerLon;
		double v = v1 + (worldLoc.getLatitudeE6() - pair.first.getLatitudeE6()) * pixelsVPerLat;

		//Rotate the points back by theta to return the coordinates to their original orientation.
		double x = u * Math.cos(Math.toRadians(this.mapTheta)) - v * Math.sin(Math.toRadians(this.mapTheta));
	    double y = u * Math.sin(Math.toRadians(this.mapTheta)) + v * Math.cos(Math.toRadians(this.mapTheta));
		return new PointF((float)x, (float)y);
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
	       //Read the first line to get the dimensions of the map and the theta orientation.   
	       strLine = br.readLine();
	       st = new StringTokenizer(strLine);
	       double theta = Double.parseDouble(st.nextToken());
	       this.mapTheta = theta;
	       
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

	protected void locChanged(double lat, double lon, boolean save)
	{
		userWorldLoc = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
		locChanged(userWorldLoc, save);
	}
	
	protected void locChanged(GeoPoint worldLoc, boolean save)
	{
		if(worldLoc == null)
			return;
		
		//Get the geo referenced map position from the world location.
		mapLoc = getGeoMapPosition(worldLoc);
		
		if(mapLoc == null)
			return;
				
		//Update the map position on the imageview and redraw.
		this.geoImageView.invalidate();
		
		if(!save)
			return;
		
		//Save the location to the preferences so we can load it if necessary.
		saveLocation(worldLoc);
	}
	
	protected GeoPoint loadLocation()
	{
		//Load the last location from the shared preferences.
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
	    int lat = prefs.getInt(LATITUDE, Integer.MAX_VALUE);
	    int lon = prefs.getInt(LONGITUDE, Integer.MAX_VALUE);
	    
	    //Return null if there was no valid location.
	    if(lat > LAT_MAX || lon > LON_MAX)
	    	return null;
	    
	    return new GeoPoint(lat, lon);
	}
	
	protected void restoreLocation()
	{
		//Load the location from the preferences.
		userWorldLoc = loadLocation();
		
		//Return if there's no previous location.
		if(userWorldLoc == null)
			return;
		
		//Update the map position.
		locChanged(userWorldLoc, false);
	}
	
	protected void saveLocation(GeoPoint loc)
	{
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(LATITUDE, loc.getLatitudeE6());
		editor.putInt(LONGITUDE, loc.getLongitudeE6());
		editor.commit();
	}

	
	/*
	public void logData() throws IOException
	{
		File path = Environment.getExternalStorageDirectory();
	    File file = new File(path, "LatLong.txt");
	    byte[] data;
	    String lat_long = "Latitude: " + Double.toString(current_lat) + " Longitude: " + Double.toString(current_lon) + '\n';
	    data = lat_long.getBytes();
	    OutputStream os = new FileOutputStream(file, true);
	    os.write(data);
	    os.close();
	}
	*/

}