package com.osu.sc.meadows;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CreateMeetingActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.meetinglayout);
	}
	
	public void acceptCreateMeeting(View v)
	{
		setResult(RESULT_OK);
		finish();
	}
	
	public void cancelCreateMeeting(View v)
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
}
