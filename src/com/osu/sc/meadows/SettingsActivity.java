package com.osu.sc.meadows;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity 
{
	String SHARED_PREFERENCES_NAME = "AppPreferences";

	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {   
		super.onCreate(savedInstanceState);
		
		// Set the preferences file so that it is pointing to the same file as the
		// rest of the application
        this.getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCES_NAME);
        
        // Load up the preferences xml file
        addPreferencesFromResource(R.xml.settingslayout);
    }
}
