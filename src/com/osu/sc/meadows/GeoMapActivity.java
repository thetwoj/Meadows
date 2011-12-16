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
	private MapView m_view;
	private MapController mc;
	private GeoPoint worldLoc;
	private ViewSwitcher switcher;
	private static final int MARKER_OFFSET_X = 16;
	private static final int MARKER_OFFSET_Y = 35;
	private int geoFile;
	private double mapTheta;
	private GeoImageViewTouch geoImageView;
	private boolean showingGoogleMap;
	List<GeoreferencedPoint> geoReferencedPoints;
	
	
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

	class MapOverlay extends com.google.android.maps.Overlay
	{
		@Override
		public boolean draw(Canvas canvas, MapView mapView,
				boolean shadow, long when)
		{
			super.draw(canvas, mapView, shadow);
			if(worldLoc == null)
				return true;

			Point screenPts = new Point();
			mapView.getProjection().toPixels(worldLoc, screenPts);

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
			//The marker's pin point is at (MARKER_OFFSET_X, MARKER_OFFSET_Y) on the bitmap.
			canvas.drawBitmap(bmp, screenPts.x - MARKER_OFFSET_X, screenPts.y - MARKER_OFFSET_Y, null);
			return true;
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
	      this.geoActivity.locChanged(location.getLatitude(), location.getLongitude());
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
		double pixelsUPerLon = (pair.second.u - pair.first.u) / (pair.second.getLongitudeE6() - pair.first.getLongitudeE6());
		double pixelsVPerLat = (pair.second.v - pair.first.v) / (pair.second.getLatitudeE6() - pair.first.getLatitudeE6());
		double u = pair.first.u + (worldLoc.getLongitudeE6() - pair.first.getLongitudeE6()) * pixelsUPerLon;
		double v = pair.first.v + (worldLoc.getLatitudeE6() - pair.first.getLatitudeE6()) * pixelsVPerLat;
		int x = (int)(Math.round(u * Math.cos(Math.toRadians(this.mapTheta)) - v * Math.sin(Math.toRadians(this.mapTheta))));
		int y = (int)(Math.round(u * Math.sin(Math.toRadians(this.mapTheta)) + v * Math.cos(Math.toRadians(this.mapTheta))));
		return new Point(x, y);
	}
	
	public void setGeoFile(int fileid)
	{
		this.geoFile = fileid;
	}
	
	public void loadGeoreferencedPoints()
	{
		geoReferencedPoints.clear();
		DataInputStream din = null;
		try
	    {
	       din = new DataInputStream(getApplicationContext().getResources().openRawResource(this.geoFile));
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
		setViewByLayout(R.layout.maplayout);
		geoReferencedPoints = new ArrayList<GeoreferencedPoint>();
		switcher = (ViewSwitcher) findViewById(R.id.mapSwitcher);
		this.m_view = (MapView) findViewById(R.id.mapView);
		this.geoImageView = (GeoImageViewTouch) findViewById(R.id.meadowsImageView);
		m_view.setSatellite(true);
		m_view.setBuiltInZoomControls(true);
		mc = m_view.getController();
		mc.setZoom(20);
		this.showingGoogleMap = true;
		
		//Load map overlays.
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = m_view.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		//Temporarily set meadows map here.
		setGeoFile(R.raw.meadows);
		//Load georeferenced points from the file.
		loadGeoreferencedPoints();

		GeoLocationListener locationListener = new GeoLocationListener(this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, locationListener);
	}
	

	public void locChanged(double lat, double lon)
	{
		int lat_int = (int) (lat * 1E6);
		int lon_int = (int) (lon * 1E6);
		worldLoc = new GeoPoint(lat_int, lon_int);
		Point geoMapLoc = getGeoMapPosition(worldLoc);
		this.geoImageView.setLoc(geoMapLoc);
		if(this.showingGoogleMap)
		{
			mc.animateTo(worldLoc);
		}
		else
		{
			this.geoImageView.invalidate();
		}
	}
	
	public void setViewByLayout(int layout)
	{
		setContentView(layout);
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
	
	public void toggleView(View currentView)
	{
		this.showingGoogleMap = !this.showingGoogleMap;
		switcher.showNext();
	}


	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
}