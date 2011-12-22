package com.osu.sc.meadows;

import MeadowsServer.MeadowsServer;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.os.Bundle;

/*
 * Place holder activity for eventual Social features
 */
public class SocialActivity extends Activity 
{
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sociallayout);
        
    }
    
    /**
     * @param view
     */
    public void Test(View view)
    {
    	EditText text = (EditText)findViewById(R.id.testText);
    	String testString = MeadowsServer.Test();
    	text.setText(testString);
    }
    
    
}
