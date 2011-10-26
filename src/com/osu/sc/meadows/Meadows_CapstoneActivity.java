package com.osu.sc.meadows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.osu.sc.meadows.MeadowsLocationListener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Meadows_CapstoneActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        TextView tv = (TextView) findViewById(R.id.tv001);
        Button button = (Button) findViewById(R.id.locbutton);
        Button button2 = (Button) findViewById(R.id.actbutton);
        
    	final LocationManager lManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	
    	final MeadowsLocationListener locationListener = new MeadowsLocationListener(tv);
        
        tv.setText("...");
        button.setText("Get Loc");
        
        button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Button button = (Button) findViewById(R.id.locbutton);
				TextView tv = (TextView) findViewById(R.id.tv001);
				
				if(button.getText().toString() == "Get Loc"){
					locationGet(tv, lManager, locationListener);
					button.setText("Stop");
				} else {
					locationStop(tv, lManager, locationListener);
					button.setText("Get Loc");
					tv.setText("...");
				}
				//Intent intent = new Intent(v.getContext(), LocationActivity.class);
				//startActivityForResult(intent, 0);
			}
        }); 
        
        button2.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(v.getContext(), LocationActivity.class);
				startActivityForResult(intent, 0);
        	}
        });
    }    
    
    public void locationGet(TextView tv, LocationManager lManager, LocationListener locationListener){
    	
    	lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
    
    public void locationStop(TextView tv, LocationManager lManager, LocationListener locationListener){
    	
    	lManager.removeUpdates(locationListener);
    }
}