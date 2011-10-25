package com.osu.sc.meadows;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;
public class MeadowsLocationListener implements LocationListener 
{
	private TextView view;
	public MeadowsLocationListener(TextView view)
	{
		super();
		this.view = view;
	}
	public void onLocationChanged(Location location) 
	{
		String update = "Latitude: " + Double.toString(location.getLatitude()) + "\nLongitude: " + Double.toString(location.getLongitude());
		this.view.setText(update);
	}
	public void onProviderDisabled(String provider) 
	{
		this.view.setText(provider + " Disabled");
	}
	public void onProviderEnabled(String provider) 
	{
		this.view.setText(provider + " Enabled");
	}
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
	}
}
