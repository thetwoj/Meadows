package com.osu.sc.meadows;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;

/*
 * The Homescreen activity class - contains links to all other main activities
 */
public class MeadowsActivity extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        // Declare the ImageButtons on the homescreen by finding their ID's declared in 
        // the main.xml file
        final ImageButton socialB = (ImageButton) findViewById(R.id.socialButton);
        final ImageButton mapB = (ImageButton) findViewById(R.id.mapButton);
        final ImageButton updatesB = (ImageButton) findViewById(R.id.updatesButton);
        final ImageButton statsB = (ImageButton) findViewById(R.id.statsButton);
        
        // Set onClickListener to all ImageButtons so that app can detect when they are pressed
        socialB.setOnClickListener(this);
        mapB.setOnClickListener(this);
        updatesB.setOnClickListener(this);
        statsB.setOnClickListener(this);
    }
    
    // Called when the soft "menu" key is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	// Inflate the menu's xml file and display it
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 * Depending on which button is pressed on the homescreen, the corresponding activity
	 * is started via an intent
	 */
    public void onClick(View v) {
        switch(v.getId()) {
        
        	// If the Social button is pressed, start social activity
            case R.id.socialButton:
            	Intent mySocialIntent = new Intent(MeadowsActivity.this, SocialActivity.class);
            	MeadowsActivity.this.startActivity(mySocialIntent);
            break;
            
            // If the Map button is pressed, start map activity
            case R.id.mapButton:
            	Intent myMapIntent = new Intent(MeadowsActivity.this, MeadowsMapActivity.class);
            	MeadowsActivity.this.startActivity(myMapIntent);
            break;
            
            // If the Updates button is pressed, start updates activity
            case R.id.updatesButton:
            	Intent myUpdatesIntent = new Intent(MeadowsActivity.this, UpdatesActivity.class);
            	MeadowsActivity.this.startActivity(myUpdatesIntent);
            break;
            
            // If the Stats button is pressed, start Stats activity
            case R.id.statsButton:
            	Intent myStatsIntent = new Intent(MeadowsActivity.this, StatsActivity.class);
            	MeadowsActivity.this.startActivity(myStatsIntent);
            break;
            
            // Shouldn't ever be possible, but just in case
            default:
            	break;
        }
    }
}