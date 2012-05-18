package com.osu.sc.meadows;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity 
{
	String SHARED_PREFERENCES_NAME = "AppPreferences";
	String MEADOWS_USER_AUTOLOGIN = "meadows_user_autologin";
	String DISABLE_AUTOLOG = "meadows_autolog_disable";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{   
		super.onCreate(savedInstanceState);

		// Set the preferences file so that it is pointing to the same file as the
		// rest of the application
		this.getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCES_NAME);

		// Load up the preferences xml file
		addPreferencesFromResource(R.xml.settingslayout);

		// Get the current status of the auto-log feature
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		final Boolean autolog = prefs.getBoolean(MEADOWS_USER_AUTOLOGIN, false);

		// Get references to the preference that disables auto-login
		final Preference mPref = (Preference) findPreference(DISABLE_AUTOLOG);

		// If auto-log is already set to false, disable corresponding preference
		if(!autolog)
		{
			mPref.setEnabled(false);
		}else{
			// If auto-log set to true, enable preference in order to allow user
			// to disable the auto-log feature if needed
			mPref.setEnabled(true);

			// Setup listener for click on the auto-log disable preference
			mPref.setOnPreferenceClickListener(new OnPreferenceClickListener() 
			{
				public boolean onPreferenceClick(Preference preference) 
				{
					// Disable auto-login
					disableAutolog(false);

					// Let user know that auto-log was disabled
					Toast.makeText(getBaseContext(), "Auto-Login Disabled", Toast.LENGTH_LONG).show();

					// Set preference enabled to false, auto-log is already disabled
					mPref.setEnabled(false);

					// Return true to indicated click was handled successfully
					return true;
				}
			});
		}
	}

	// Method called to disable auto-log and save to preferences file
	protected void disableAutolog(Boolean autolog)
	{
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(MEADOWS_USER_AUTOLOGIN, autolog);

		// Save changes
		editor.commit();
	}
}
