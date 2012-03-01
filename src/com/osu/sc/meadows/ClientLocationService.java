package com.osu.sc.meadows;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import server.Client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

	final int MEADOWS_STATUS_ID = 1;

	Timer pollTimer;
	LocationListener locationListener;

	Notification notify;
	NotificationManager nM;

	Client client = Client.GetInstance();
	
	@Override
	public void onCreate()
	{
		// Create service status bar notification
		initServiceNotification();

		//Set up the client immediately when the application opens.
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		Boolean autolog = prefs.getBoolean(MEADOWS_USER_AUTOLOGIN, false);

		// If auto-login is selected by the user
		if(autolog)
		{
			// Get the email and pass from the prefs file and attempt to login
			String email = prefs.getString(MEADOWS_USER_EMAIL, "");
			String pass = prefs.getString(MEADOWS_USER_PASS, "");
			Client.GetInstance().Login(email, pass);
		}

		//Start the location listener.
		locationListener = new LocationListener()
		{

			@Override
			public void onLocationChanged(Location location) 
			{
				Client.GetInstance().SetLocation(location.getLatitude(), location.getLongitude());
				Client.GetInstance().SetTimestamp(Calendar.getInstance().getTimeInMillis());
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

		//Start the location listener.
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Client.GetInstance().GetNetworkPeriod(), 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Client.GetInstance().GetGPSPeriod(), 0, locationListener);

		//Schedule periodic client updates.
		pollTimer = new Timer();
		TimerTask pollTask = new TimerTask()
		{
			public void run() 
			{
				Client.GetInstance().RequestUpdateFriendRequests();
				Client.GetInstance().RequestUpdateFriends();
			}
		};

		pollTimer.scheduleAtFixedRate(pollTask, 0, Client.GetInstance().GetNetworkPeriod());

		//Restore the most recent user location.		
		restoreUserLocation();

		//Restore friend locations.
		restoreFriendLocations();
	}

	public void initServiceNotification()
	{
		// Initialize the NotificationManager to produce the ongoing notification
		String ns = Context.NOTIFICATION_SERVICE;
		nM = (NotificationManager) getSystemService(ns);

		// Select icon, ticker text and when the notification should display (now)
		int icon = R.drawable.icon;
		CharSequence tickerText = "Service started!";
		long when = System.currentTimeMillis();

		// Initialize notification with all information from above
		notify = new Notification(icon, tickerText, when);

		// Set notification to be an ongoing event and not cleared by "clear all"
		notify.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

		// Set Title and Text seen when status bar is pulled down
		Context context = this;
		CharSequence ongoingTitle = "OSU Meadows";
		CharSequence ongoingText = "Service running...";

		// Link the ongoing notification to the Homescreen of the app
		Intent ongoingIntent = new Intent(this, MeadowsActivity.class);

		// Set flags so that the intent will pull current Meadows instance to front
		// instead of creating a second instance of the activity
		ongoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ongoingIntent, 0);

		// Set all of the notification information
		notify.setLatestEventInfo(context, ongoingTitle, ongoingText, contentIntent);

		// Send notification
		nM.notify(MEADOWS_STATUS_ID, notify);
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		nM.cancel(MEADOWS_STATUS_ID);

		pollTimer.cancel();

		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationManager.removeUpdates(locationListener);

		//Save the user location.
		saveUserLocation();

		//Save the friend locations.
		saveFriendLocations();

		//Call the base class.
		super.onDestroy();
	}

	protected void restoreFriendLocations()
	{
	}

	protected void saveFriendLocations()
	{
	}

	protected void restoreUserLocation()
	{
		//Load the last location from the shared preferences.
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

		//Temporarily catch class cast exceptions until we make sure no one has the old data types in their prefs.
		double lat = Double.parseDouble(INVALID_LAT_LON);
		double lon = Double.parseDouble(INVALID_LAT_LON);
		try
		{
			lat = Double.parseDouble(prefs.getString(MEADOWS_USER_LATITUDE, INVALID_LAT_LON));
			lon = Double.parseDouble(prefs.getString(MEADOWS_USER_LONGITUDE, INVALID_LAT_LON));
		}
		catch(Exception e)
		{
			return;
		}

		//Return if there's no previous location.
		if(Math.abs(lat) > LAT_MAX || Math.abs(lon) > LON_MAX)
			return;

		//Update the map position.
		Client.GetInstance().SetLocation(lat, lon);
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
