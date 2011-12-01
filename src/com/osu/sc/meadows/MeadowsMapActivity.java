package com.osu.sc.meadows;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MeadowsMapActivity extends MapActivity
{
	private MapView m_view;
	private MapController mc;
	private GeoPoint loc;
	/** Called when the activity is first created. */

	class MapOverlay extends com.google.android.maps.Overlay
	{
		@Override
		public boolean draw(Canvas canvas, MapView mapView,
				boolean shadow, long when)
		{
			super.draw(canvas, mapView, shadow);
			if(loc == null)
				return true;

			Point screenPts = new Point();
			mapView.getProjection().toPixels(loc, screenPts);

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
			//The marker's pin point is at (10, 34) on the bitmap.
			canvas.drawBitmap(bmp, screenPts.x - 10, screenPts.y - 34, null);
			return true;
		}
	}
	
	public class MeadowsLocationListener implements LocationListener
	{
	   private MeadowsMapActivity meadows_activity;
	   public MeadowsLocationListener(MeadowsMapActivity mc_act)
	   {
	      super();
	      this.meadows_activity = mc_act;
	   }
	   public void onLocationChanged(Location location)
	   {
	      this.meadows_activity.locChanged(location.getLatitude(), location.getLongitude());
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

	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		m_view = (MapView) findViewById(R.id.mapView);
		m_view.setSatellite(true);
		m_view.setBuiltInZoomControls(true);
		mc = m_view.getController();
		mc.setZoom(20);
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = m_view.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		MeadowsLocationListener locationListener = new MeadowsLocationListener(this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
	}

	public void locChanged(double lat, double lon)
	{
		int lat_int = (int) (lat * 1E6);
		int lon_int = (int) (lon * 1E6);
		loc = new GeoPoint(lat_int, lon_int);
		mc.animateTo(loc);
	}


	@Override
	protected boolean isRouteDisplayed() 
	{
		// TODO Auto-generated method stub
		return false;
	}
}