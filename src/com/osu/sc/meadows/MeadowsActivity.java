package com.osu.sc.meadows;

import server.Client;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
	String SELECTED_NETWORK_PERIOD = "meadows_user_update_interval";
	String SELECTED_GPS_PERIOD = "meadows_location_update_interval";
	String SELECTED_GPS_ACCURACY = "meadows_gps_accuracy";
	String DISABLE_AUTOLOG = "meadows_autolog_disable";
	String MEADOWS_USER_AUTOLOGIN = "meadows_user_autologin";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// Get the shared preferences file
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

		// Retrieve the selected interval for updates from prefs file
		setUpdatesInterval(Integer.parseInt(prefs.getString(SELECTED_NETWORK_PERIOD, "15000")));
		
		// Create a Location Manager to check if GPS is enabled
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		// If GPS is disabled, call function to alert user
		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        buildAlertMessageNoGps();
	    }

		// Setup a listener to detect changes to the prefs file
		settingsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefer, String key) {
				// If it was the update period that was changed
				if(key.equals(SELECTED_GPS_PERIOD))
				{
					// Update the GPS interval to the new value
					setLocationInterval(Integer.parseInt(prefer.getString(SELECTED_GPS_PERIOD, "500")));
					// Restart service with new preferences
					restartService(prefer);
				}
				else if(key.equals(SELECTED_NETWORK_PERIOD))
				{
					// Update the Network interval to the new value
					setUpdatesInterval(Integer.parseInt(prefer.getString(SELECTED_NETWORK_PERIOD, "15000")));
					// Restart service with new preferences
					restartService(prefer);
				}
				else if(key.equals(SELECTED_GPS_ACCURACY))
				{
					// Update the Network interval to the new value
					setGPSAccuracy(Integer.parseInt(prefer.getString(SELECTED_GPS_ACCURACY, "20")));
					// Restart service with new preferences
					restartService(prefer);
				}
				// TODO FIX THIS SHIT
				//				else if(key.equals(DISABLE_AUTOLOG))
				//				{
				//					SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
				//					SharedPreferences.Editor editor = prefs.edit();
				//					editor.putBoolean(MEADOWS_USER_AUTOLOGIN, false);
				//					editor.commit();
				//					 
				//					Toast.makeText(getBaseContext(), "Auto-Login Disabled", Toast.LENGTH_LONG);
				//				}
			}
		};

		// Register the change listener to retrieved prefs file
		prefs.registerOnSharedPreferenceChangeListener(settingsListener);

		setContentView(R.layout.main);
		
		locationServiceIntent = new Intent(MeadowsActivity.this, ClientLocationService.class);

		// Check to see if service is already running, indicating that the app
		// was idle long enough for the main activity to be cleaned up. In this
		// case we don't want to start a second service, we want to continue using
		// the one that is already running
		if(!isMyServiceRunning())
		{
			//Start up the location service.
			startService(locationServiceIntent);
		}
	}
	
	// Method to create alert to let user know GPS is disabled and give them
	// the chance to turn it on before continuing with a link to the phone settings
	private void buildAlertMessageNoGps() {
		// Initialize alert, set message and button options
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?"+"\n"+"\n"+"This app will not function correctly without it!")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    // Create and show alert
	    final AlertDialog alert = builder.create();
	    alert.show();
	}

	// Called after prefs file is changed in order to stop the current service
	// and restart a new service with the new update interval in place
	public void restartService(SharedPreferences prefers)
	{
		// Stop current service
		stopService(locationServiceIntent);

		// Start a new service with the new update value
		locationServiceIntent = new Intent(MeadowsActivity.this, ClientLocationService.class);
		startService(locationServiceIntent);
	}

	// Called when the prefs file is first read or changed in order to set the 
	// correct interval for Network updates
	public void setUpdatesInterval(int interval)
	{
		client.SetNetworkPeriod(interval);
	}

	// Called when the prefs file is first read or changed in order to set the 
	// correct interval for the GPS updates
	public void setLocationInterval(int interval)
	{
		client.SetGPSPeriod(interval);
	}

	// Called when the prefs file is first read or changed in order to set the 
	// correct interval for Network updates
	public void setGPSAccuracy(int interval)
	{
		client.SetGPSAccuracy(interval);
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

	// Utility method to check and see if the location service is already running
	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (ClientLocationService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBackPressed()
	{
		// Make sure that the location service has been stopped when the activity
		// is backed out of
		client.LogOut();
		stopService(locationServiceIntent);

		super.onBackPressed();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}