package com.osu.sc.meadows;

import server.Client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class ClientLocationService extends Service
{
	//Network and GPS update frequency in milliseconds.
	private static final int NETWORK_PERIOD = 4000;
	private static final int GPS_PERIOD = 4000;

	@Override
	public void onCreate()
	{
		//Ensure that the client is immediately started up when the app opens.
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
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_PERIOD, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_PERIOD, 0, locationListener);
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

}
