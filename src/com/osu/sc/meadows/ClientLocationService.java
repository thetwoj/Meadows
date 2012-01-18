package com.osu.sc.meadows;

import server.Client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class ClientLocationService extends Service
{
	
	//Information for saving last known location.
	public static final String SHARED_PREFERENCES_NAME = "AppPreferences";
	public static final String USER_LATITUDE = "user_latitude";
	public static final String USER_LONGITUDE = "user_longitude";
	
	//Maximum possible latitude and longitude.
	private static final int LAT_MAX = (int) (90 * 1E6);
	private static final int LON_MAX = (int) (180 * 1E6);

	@Override
	public void onCreate()
	{
		//Set up the client immediately when the application opens.
		Client.GetInstance();

		//Start the location listener.
		LocationListener locationListener = new LocationListener()
		{

			@Override
			public void onLocationChanged(Location location) 
			{
				Client.GetInstance().SetLocation((int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6));
			}

			@Override
			public void onProviderDisabled(String provider) 
			{
			}

			@Override
			public void onProviderEnabled(String provider) 
			{
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) 
			{
			}

		};

		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Client.GetInstance().GetNetworkPeriod(), 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Client.GetInstance().GetGPSPeriod(), 0, locationListener);
		
		//Restore the most recent user location.
	    restoreUserLocation();
	    
	    //Restore friend locations.
	    restoreFriendLocations();
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override
	public void onDestroy()
	{
		
		//Save the user location.
		saveUserLocation();
		
		//Save the friend locations.
		saveFriendLocations();
		
		//Call the base class.
		super.onDestroy();
	}
	
	protected void restoreUserLocation()
	{
		//Load the last location from the shared preferences.
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		int lat = prefs.getInt(USER_LATITUDE, Integer.MAX_VALUE);
		int lon = prefs.getInt(USER_LONGITUDE, Integer.MAX_VALUE);
		
		//Return if there's no previous location.
		if(lat > LAT_MAX || lon > LON_MAX)
			return;
		
		//Update the map position.
		Client.GetInstance().SetLocation(lat, lon);
	}
	
	protected void restoreFriendLocations()
	{
	}
	
	protected void saveFriendLocations()
	{
	}
	
	protected void saveUserLocation()
	{
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(USER_LATITUDE, Client.GetInstance().GetLatitude());
		editor.putInt(USER_LONGITUDE, Client.GetInstance().GetLongitude());
		editor.commit();
	}

}
