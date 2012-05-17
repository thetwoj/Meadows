package com.osu.sc.meadows;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import server.Client;
import server.MeetingPoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class EditMeetingActivity extends Activity
{
	protected int mId;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    mId = getIntent().getIntExtra("mId", -1);
	    if(mId == -1)
	    	cancelActivity();
	
	    MeetingPoint mPoint = Client.GetInstance().GetMeetingPoint(mId);
	    if(mPoint == null)
	    	cancelActivity();
	    setContentView(R.layout.editmeetinglayout);
	    EditText et = (EditText) findViewById(R.id.editMeetingDesc);
	    et.setText(mPoint.GetDescription());
	    TimePicker tp = (TimePicker) findViewById(R.id.editTimePicker1);
	    long time = mPoint.GetTime();
	    Date d = new Date(time);
	    tp.setCurrentHour(d.getHours());
	    tp.setCurrentMinute(d.getMinutes());
	}
	
	public void acceptEditMeeting(View v)
	{
		Intent intentData = new Intent();
		String mDesc = ((EditText) findViewById(R.id.editMeetingDesc)).getText().toString();
		int hour = ((TimePicker) findViewById(R.id.editTimePicker1)).getCurrentHour();
		int minute = ((TimePicker) findViewById(R.id.editTimePicker1)).getCurrentMinute();
		Calendar c = Calendar.getInstance();
		GregorianCalendar gc = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hour, minute);
		long time = gc.getTimeInMillis();
		
		intentData.putExtra("Deleted", false);
		intentData.putExtra("mId", mId);
		intentData.putExtra("Description", mDesc);
		intentData.putExtra("Time", time);
		this.setResult(RESULT_OK, intentData);
		finish();
	}
	
	public void cancelEditMeeting(View v)
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
	public void deleteMeeting(View v)
	{
		Intent intentData = new Intent();
		intentData.putExtra("Deleted", true);
		intentData.putExtra("mId", mId);
		this.setResult(RESULT_OK, intentData);
		finish();
	}
	
	protected void cancelActivity()
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
}
