package com.osu.sc.meadows;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.Client;
import server.MeetingPoint;
import server.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ViewMeetingActivity extends Activity
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
	    setContentView(R.layout.viewmeetinglayout);
	    TextView tvCreator = (TextView) findViewById(R.id.viewMeetingCreator);
	    User creator = Client.GetInstance().GetMeetingPointCreator(mPoint);
	    String creatorName;
	    if(creator == null)
	    	creatorName = "Unknown";
	    else
	    	creatorName = creator.GetFirstName() + " " + creator.GetLastName();
	    
	    tvCreator.setText(creatorName);
	    TextView tvdesc = (TextView) findViewById(R.id.viewMeetingDesc);
	    tvdesc.setText(mPoint.GetDescription());
	    
	    TextView tvtime = (TextView) findViewById(R.id.viewMeetingTime);
	    long time = mPoint.GetTime();
	    Date d = new Date(time);
	    SimpleDateFormat df = new SimpleDateFormat("h:mm aaa");
	    tvtime.setText(df.format(d));
	}
	
	public void acceptViewMeeting(View v)
	{
		this.setResult(RESULT_OK);
		finish();
	}
	
	protected void cancelActivity()
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
}