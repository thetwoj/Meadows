package com.osu.sc.meadows;

import server.Client;
import server.Conditions;
import server.ConditionsListener;
import server.ConditionsUpdated;
import server.Server;
import server.ServerEvents;
import server.ParkingLot;
import server.Lift;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * Place holder activity for eventual Updates features
 */
public class UpdatesActivity extends Activity 
{
	
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
		parkingLots.removeAllViews();
		for(ParkingLot lot : conditions.GetParkingLots())
		{
			TextView tv = new TextView(this);
			tv.setText(lot.GetName());
			parkingLots.addView(tv);
		}
	}
	
}
