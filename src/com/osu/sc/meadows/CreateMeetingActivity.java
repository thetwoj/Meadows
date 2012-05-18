package com.osu.sc.meadows;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

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
		Intent intentData = new Intent();
		String mDesc = ((EditText) findViewById(R.id.meetingDesc)).getText().toString();
		int hour = ((TimePicker) findViewById(R.id.timePicker1)).getCurrentHour();
		int minute = ((TimePicker) findViewById(R.id.timePicker1)).getCurrentMinute();
		int day = ((DatePicker) findViewById(R.id.datePicker1)).getDayOfMonth();
		int month = ((DatePicker) findViewById(R.id.datePicker1)).getMonth();
		int year = ((DatePicker) findViewById(R.id.datePicker1)).getYear();
		GregorianCalendar gc = new GregorianCalendar(year, month, day, hour, minute);
		long time = gc.getTimeInMillis();
		
		intentData.putExtra("Description", mDesc);
		intentData.putExtra("Time", time);
		this.setResult(RESULT_OK, intentData);
		finish();
	}
	
	public void cancelCreateMeeting(View v)
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
}
