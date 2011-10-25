package com.osu.sc.meadows;

import android.app.Activity;
import android.content.Context;
import com.osu.sc.meadows.MeadowsLocationListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class Meadows_CapstoneActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        //System.out.println("Test");
        TextView tv = new TextView(this);
        tv.setText("Init");
        setContentView(tv);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	
    	MeadowsLocationListener locationListener = new MeadowsLocationListener(tv);
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 2, locationListener);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 2, locationListener);
    }
}