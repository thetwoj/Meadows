package com.osu.sc.meadows;

import com.google.android.maps.MapView.LayoutParams;

import server.Client;
import server.Conditions;
import server.ConditionsListener;
import server.ConditionsUpdated;
import server.ServerEvents;
import server.ParkingLot;
import server.Lift;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * Place holder activity for eventual Updates features
 */
public class UpdatesActivity extends Activity 
{
	private int darkFont = 0xff555555;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.updateslayout);
	    // TODO Auto-generated method stub
	    
	    ServerEvents events = ServerEvents.GetInstance();
	    events.AddConditionsUpdatedListener(new ConditionsListener(){
	    	public void EventFired(ConditionsUpdated event)
	    	{
	    		Conditions conditions = event.GetConditions();
	    		RefreshUi(conditions);
	    	}
	    });
	}
	
	public void Test(View view)
	{	
		Client client = Client.GetInstance();
		client.RequestConditions();
	}
	
	public void RefreshUi(Conditions conditions)
	{
		LinearLayout parkingLots = (LinearLayout) findViewById(R.id.ParkingLots);
		LinearLayout lifts		 = (LinearLayout) findViewById(R.id.Lifts);
		parkingLots.removeAllViews();
		lifts.removeAllViews();
		
		for(ParkingLot lot : conditions.GetParkingLots())
		{
			//create wrapper and set settings
			FrameLayout wrapper = new FrameLayout(this);
			wrapper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			//append name and status to wrapper
			wrapper.addView(CreateTextView(lot.GetName(), Gravity.LEFT));
			wrapper.addView(CreateTextView(lot.GetStatus(), Gravity.RIGHT));
			
			//append wrapper to ui
			parkingLots.addView(wrapper);
		}
		
		for(Lift lift : conditions.GetLifts())
		{
			FrameLayout wrapper = new FrameLayout(this);
			wrapper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			wrapper.addView(CreateTextView(lift.GetName(), Gravity.LEFT));
			wrapper.addView(CreateTextView(lift.GetStatus(), Gravity.RIGHT));
			
			lifts.addView(wrapper);
		}
	}
	
	private TextView CreateTextView(String value, int gravity)
	{
		TextView tv = new TextView(this);
		tv.setGravity(gravity);
		tv.setText(value);
		tv.setTextSize(14);
		tv.setTextColor(darkFont);
		return tv;
	}
	
	
}
