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
import android.view.View;
import android.widget.ViewSwitcher;

public class MeadowsMapActivity extends MapActivity
{
	private MapView m_view;
	private MapController mc;
	private GeoPoint loc;
	private ViewSwitcher switcher;

	class MapOverlay extends com.google.android.maps.Overlay
	{
		private static final int MARKER_OFFSET_X = 16;
		private static final int MARKER_OFFSET_Y = 35;
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
			//The marker's pin point is at (MARKER_OFFSET_X, MARKER_OFFSET_Y) on the bitmap.
			canvas.drawBitmap(bmp, screenPts.x - MARKER_OFFSET_X, screenPts.y - MARKER_OFFSET_Y, null);
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
		setViewByLayout(R.layout.maplayout);
		switcher = (ViewSwitcher) findViewById(R.id.mapSwitcher);
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
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
	}

	public void locChanged(double lat, double lon)
	{
		int lat_int = (int) (lat * 1E6);
		int lon_int = (int) (lon * 1E6);
		loc = new GeoPoint(lat_int, lon_int);
		mc.animateTo(loc);
	}
	
	public void setViewByLayout(int layout)
	{
			setContentView(layout);
	}
	
	public void toggleView(View currentView)
	{
		switcher.showNext();
	}


	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
}