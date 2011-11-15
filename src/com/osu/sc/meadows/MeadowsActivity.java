package com.osu.sc.meadows;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MeadowsActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        final ImageButton socialB = (ImageButton) findViewById(R.id.socialButton);
        socialB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(v.getContext(), SocialActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        final ImageButton mapB = (ImageButton) findViewById(R.id.mapButton);
        mapB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(v.getContext(), MapActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        final ImageButton updatesB = (ImageButton) findViewById(R.id.updatesButton);
        updatesB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(v.getContext(), UpdatesActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        final ImageButton statsB = (ImageButton) findViewById(R.id.statsButton);
        statsB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent myIntent = new Intent(v.getContext(), StatsActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }
}