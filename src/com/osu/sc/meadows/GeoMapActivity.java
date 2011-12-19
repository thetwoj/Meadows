package com.osu.sc.meadows;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ViewSwitcher;

public class GeoMapActivity extends MapActivity
{
	private double mapTheta;
	private static final int NETWORK_PERIOD = 4000;
	private static final int GPS_PERIOD = 4000;
	private GeoImageViewTouch geoImageView;
	List<GeoreferencedPoint> geoReferencedPoints;
	public static final String SHARED_PREFERENCES_NAME = "AppPreferences";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	private static final int LAT_MAX = (int) (90 * 1E6);
	private static final int LON_MAX = (int) (180 * 1E6);
	
	
	//Points u,v indicate an (x,y) point on the map that has been rotated by -theta degrees to make it north up.
	public class GeoreferencedPoint extends GeoPoint
	{
		public double u;
		public double v;
		public GeoreferencedPoint(int latitudeE6, int longitudeE6, double u, double v)
		{
			super(latitudeE6, longitudeE6);
			this.u = u;
			this.v = v;
		}
	}
	
	public class ClosestPointPair
	{
		public GeoreferencedPoint first;
		public GeoreferencedPoint second;
		public ClosestPointPair(GeoreferencedPoint first, GeoreferencedPoint second)
		{
			this.first = first;
			this.second = second;
		}
	}
	
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
	
	public long distanceBetween(GeoPoint first, GeoPoint second)
	{
		long dlat = first.getLatitudeE6() - second.getLatitudeE6();
		long dlon = first.getLongitudeE6() - second.getLongitudeE6();
		long square_distance = dlat * dlat + dlon * dlon;
		return (long) Math.sqrt(square_distance);
	}
	
	public ClosestPointPair getClosestPointPair(GeoPoint worldLoc)
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
	
	public Point getGeoMapPosition(GeoPoint worldLoc)
	{
		ClosestPointPair pair = getClosestPointPair(worldLoc);
		if(pair == null)
			return null;
		
		double pixelsUPerLon = (pair.second.u - pair.first.u) / (pair.second.getLongitudeE6() - pair.first.getLongitudeE6());
		double pixelsVPerLat = (pair.second.v - pair.first.v) / (pair.second.getLatitudeE6() - pair.first.getLatitudeE6());
		double u = pair.first.u + (worldLoc.getLongitudeE6() - pair.first.getLongitudeE6()) * pixelsUPerLon;
		double v = pair.first.v + (worldLoc.getLatitudeE6() - pair.first.getLatitudeE6()) * pixelsVPerLat;
		int x = (int)(Math.round(u * Math.cos(Math.toRadians(this.mapTheta)) - v * Math.sin(Math.toRadians(this.mapTheta))));
		int y = (int)(Math.round(u * Math.sin(Math.toRadians(this.mapTheta)) + v * Math.cos(Math.toRadians(this.mapTheta))));
		return new Point(x, y);
	}
	
	public void loadGeoreferencedPoints(int fileid)
	{
		geoReferencedPoints.clear();
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
	    	   int x = Integer.parseInt(st.nextToken());
	    	   int y = Integer.parseInt(st.nextToken());
	    	   double lat = Double.parseDouble(st.nextToken());
	    	   double lon = Double.parseDouble(st.nextToken());
	    	   int lat_int = (int) (lat * 1E6);
	   		   int lon_int = (int) (lon * 1E6);
	    	   
	    	   //Before adding the point, rotate it by -theta so that it's north up.
	    	   double u =  x * Math.cos(Math.toRadians(theta)) + y * Math.toRadians(theta);
	    	   double v = -x * Math.sin(Math.toRadians(theta)) + y * Math.cos(Math.toRadians(theta));
	    	   geoReferencedPoints.add(new GeoreferencedPoint(lat_int, lon_int, u, v));
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

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Set the content view to the map layout.
		setContentView(R.layout.maplayout);
		
		//Create the list for the geo referenced points.
		geoReferencedPoints = new ArrayList<GeoreferencedPoint>();
		
		//Save the image view.
		this.geoImageView = (GeoImageViewTouch) findViewById(R.id.meadowsImageView);
		
		//Load georeferenced points from the meadows data file.
		loadGeoreferencedPoints(R.raw.meadows);
		
		//Restore the most recent location.
		restoreLocation();

		//Start the location listener.
		GeoLocationListener locationListener = new GeoLocationListener(this);
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_PERIOD, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_PERIOD, 0, locationListener);
	}
	

	public void locChanged(double lat, double lon, boolean save)
	{
		GeoPoint worldLoc = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
		locChanged(worldLoc, save);
	}
	
	public void locChanged(GeoPoint worldLoc, boolean save)
	{
		if(worldLoc == null)
			return;
		
		//Get the geo referenced map position from the world location.
		Point geoMapLoc = getGeoMapPosition(worldLoc);
		
		if(geoMapLoc == null)
			return;
				
		//Update the map position on the imageview and redraw.
		this.geoImageView.setLoc(geoMapLoc);
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
		GeoPoint worldLoc = loadLocation();
		
		//Return if there's no previous location.
		if(worldLoc == null)
			return;
		
		//Update the map position.
		locChanged(worldLoc, false);
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

	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
}