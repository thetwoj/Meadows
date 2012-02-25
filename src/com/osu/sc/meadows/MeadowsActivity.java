package com.osu.sc.meadows;


import server.Client;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

/*
 * The Homescreen activity class - contains links to all other main activities
 */
public class MeadowsActivity extends Activity implements View.OnClickListener 
{
	Client client = Client.GetInstance();
	private Intent locationServiceIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Start up the location service.
        locationServiceIntent = new Intent(MeadowsActivity.this, ClientLocationService.class);
        startService(locationServiceIntent);
    }
    
    // Called when the soft "menu" key is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	// Inflate the menu's xml file and display it
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    // TEMPORARY METHOD TO REVERT AUTOLOGIN TO FALSE
    public void menuClick(View v) {
    	String MEADOWS_USER_AUTOLOGIN = "meadows_user_autologin";
    	String SHARED_PREFERENCES_NAME = "AppPreferences";

    	SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean(MEADOWS_USER_AUTOLOGIN, false);
		editor.commit();
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
        
        	// If the Friend button is pressed, start social activity
            case R.id.friendButton:
            	if(client.LoggedIn() == true)
            	{
            		Intent myFriendIntent = new Intent(MeadowsActivity.this, FriendActivity.class);
            		MeadowsActivity.this.startActivity(myFriendIntent);
            	}
            	else
            	{
            		Intent myLoginRegisterIntent = new Intent(MeadowsActivity.this, LoginRegisterActivity.class);
            		MeadowsActivity.this.startActivity(myLoginRegisterIntent);
            	}
            break;
            
            // If the Map button is pressed, start map activity
            case R.id.mapButton:
            	Intent myMapIntent = new Intent(MeadowsActivity.this, GeoMapActivity.class);
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
    
    public void onDestroy()
    {
    	//Make sure that the location service has been stopped.
		stopService(locationServiceIntent);
    	super.onDestroy();
    }
}