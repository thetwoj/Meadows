package com.osu.sc.meadows;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LocationActivity extends Activity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loc_layout);
		
        Button button3 = (Button) findViewById(R.id.activity_1but);
		
        button3.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
        	}
        });
	}
	
}
