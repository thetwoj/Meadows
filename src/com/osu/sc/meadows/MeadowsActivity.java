package com.osu.sc.meadows;

import server.Client;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/*
 * The Homescreen activity class - contains links to all other main activities
 */
public class MeadowsActivity extends Activity implements View.OnClickListener 
{
	Client client = Client.GetInstance();
	Intent locationServiceIntent;
	SharedPreferences.OnSharedPreferenceChangeListener settingsListener;

	String SHARED_PREFERENCES_NAME = "AppPreferences";
	String NETWORK_PERIOD = "meadows_user_network_period";
	String GPS_PERIOD = "meadows_user_gps_period";
	String SELECTED_PERIOD = "meadows_user_update_interval";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// Get the shared preferences file
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

		// Retrieve the selected interval for updates from prefs file
		setUpdatesInterval(Integer.parseInt(prefs.getString(SELECTED_PERIOD, "15000")));

		// Setup a listener to detect changes to the prefs file
		settingsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefer, String key) {
				// If it was the update period that was changed
				if(key.equals(SELECTED_PERIOD))
				{
					// Set the update intervals to the newly selected value
					settingsChanged(prefer);
				}
			}
		};

		// Register the change listener to retrieved prefs file
		prefs.registerOnSharedPreferenceChangeListener(settingsListener);

		setContentView(R.layout.main);

		//Start up the location service.
		locationServiceIntent = new Intent(MeadowsActivity.this, ClientLocationService.class);
		startService(locationServiceIntent);
	}

	// Called after prefs file is changed in order to stop the current service
	// and restart a new service with the new update interval in place
	public void settingsChanged(SharedPreferences prefers)
	{
		// Stop current service
		stopService(locationServiceIntent);

		// Update the GPS and Network intervals to the new value
		setUpdatesInterval(Integer.parseInt(prefers.getString(SELECTED_PERIOD, "15000")));

		// Start a new service with the new update value
		locationServiceIntent = new Intent(MeadowsActivity.this, ClientLocationService.class);
		startService(locationServiceIntent);
	}

	// Called when the prefs file is first read or changed in order to set the 
	// correct interval for Network and GPS updates
	public void setUpdatesInterval(int interval)
	{
		client.SetNetworkPeriod(interval);
		client.SetGPSPeriod(interval);
	}

	// Called when the soft "menu" key is pressed
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu's xml file and display it
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	// Called when a button within the soft-menu is pressed
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// If the settings button is pressed
		if(item.getItemId() == R.id.settingsMenuButton)
		{
			// Start up the settings activity
			Intent mySettingsIntent = new Intent(MeadowsActivity.this, SettingsActivity.class);
			MeadowsActivity.this.startActivity(mySettingsIntent);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 * Depending on which button is pressed on the homescreen, the corresponding activity
	 * is started via an intent
	 */
	public void onClick(View v) 
	{
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

	@Override
	public void onDestroy()
	{
		//Make sure that the location service has been stopped.
		stopService(locationServiceIntent);
		super.onDestroy();
	}
}