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
	public static final String MEADOWS_USER_LATITUDE = "meadows_user_latitude";
	public static final String MEADOWS_USER_LONGITUDE = "meadows_user_longitude";
	
	//Information for autologin procedure
	public static final String MEADOWS_USER_EMAIL = "meadows_user_email";
	public static final String MEADOWS_USER_PASS = "meadows_user_pass";
	public static final String MEADOWS_USER_AUTOLOGIN = "meadows_user_autologin";
	
	//Maximum possible latitude and longitude.
	private static final double LAT_MAX = 90.0;
	private static final double LON_MAX = 180.0;
	private static final String INVALID_LAT_LON = "360.0";

	@Override
	public void onCreate()
	{
		//Set up the client immediately when the application opens.
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		Boolean autolog = prefs.getBoolean(MEADOWS_USER_AUTOLOGIN, false);
		
		if(autolog == true)
		{
			String email = prefs.getString(MEADOWS_USER_EMAIL, "");
			String pass = prefs.getString(MEADOWS_USER_PASS, "");
			Client.GetInstance().Login(email, pass);
		}
		else
		{
			Client.GetInstance();
		}
		
		//Start the location listener.
		LocationListener locationListener = new LocationListener()
		{

			@Override
			public void onLocationChanged(Location location) 
			{
				Client.GetInstance().SetLocation(location.getLatitude(), location.getLongitude());
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
				System.out.println();
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
		double lat = Double.parseDouble(prefs.getString(MEADOWS_USER_LATITUDE, INVALID_LAT_LON));
		double lon = Double.parseDouble(prefs.getString(MEADOWS_USER_LONGITUDE, INVALID_LAT_LON));
		
		//Return if there's no previous location.
		if(Math.abs(lat) > LAT_MAX || Math.abs(lon) > LON_MAX)
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
		editor.putString(MEADOWS_USER_LATITUDE, Double.toString(Client.GetInstance().GetLatitude()));
		editor.putString(MEADOWS_USER_LONGITUDE, Double.toString(Client.GetInstance().GetLongitude()));
		editor.commit();
	}

}
