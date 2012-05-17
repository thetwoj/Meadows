package com.osu.sc.meadows;

import server.Client;
import server.Conditions;
import server.ConditionsListener;
import server.ConditionsUpdated;
import server.Server;
import server.ServerEvents;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
	    		TestFunc(conditions);
	    	}
	    });
	}
	
	public void Test(View view)
	{	
		Client client = Client.GetInstance();
		client.RequestConditions();
	}
	
	public void TestFunc(Conditions conditions)
	{
		TextView textBox = (TextView) findViewById(R.id.TextBox);
		textBox.append("Blahblah");
	}
	
}
