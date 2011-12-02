package com.osu.sc.meadows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.Track;
import com.google.android.apps.mytracks.services.ITrackRecordingService;

/*
 * Place holder activity for eventual Stats features
 */
public class StatsActivity extends Activity 
{

	private static final String TAG = StatsActivity.class.getSimpleName();

	Boolean recording = false;

	// utils to access the MyTracks content provider
	private MyTracksProviderUtils myTracksProviderUtils;

	// display output from the MyTracks content provider
	private TextView outputTextView;
	private TextView averagespeed;
	private TextView totaldistance;
	private TextView totaltime;
	private TextView movingtime;

	// MyTracks service
	private ITrackRecordingService myTracksService;

	// intent to access the MyTracks service
	private Intent intent;

	// connection to the MyTracks service
	private ServiceConnection serviceConnection = new ServiceConnection() 
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) 
		{
			myTracksService = ITrackRecordingService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) 
		{
			myTracksService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statslayout);

		// for the MyTracks content provider
		myTracksProviderUtils = MyTracksProviderUtils.Factory.get(this);

		// Various text views for displaying statistics
		outputTextView = (TextView) findViewById(R.id.output);
		averagespeed = (TextView) findViewById(R.id.averagespeed);
		totaldistance = (TextView) findViewById(R.id.totaldistance);
		totaltime = (TextView) findViewById(R.id.totaltime);
		movingtime = (TextView) findViewById(R.id.movingtime);

		// for the MyTracks service
		intent = new Intent();
		ComponentName componentName = new ComponentName(
				getString(R.string.mytracks_service_package), getString(R.string.mytracks_service_class));
		intent.setComponent(componentName);

		final Button recordingButton = (Button) findViewById(R.id.recording_button);
		recordingButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// If not currently recording
				if (recording == false)
				{
					// If myTracks is connected successfully
					if (myTracksService != null) 
					{
						// Try to start a new track
						try {
							myTracksService.startNewTrack();
							
							// Change recording bool to indicate track is recording
							recording = true;
							
							// Set text from "Start" to "Stop"
							recordingButton.setText("Stop");
						} 
						catch (RemoteException e) 
						{
							Log.e(TAG, "RemoteException", e);
						}
					}
				} 
				// If currently recording
				else 
				{
					// If myTracks is connected successfully
					if (myTracksService != null) 
					{
						// Try to stop the currently recording track
						try {
							myTracksService.endCurrentTrack();
						} catch (RemoteException e) {
							Log.e(TAG, "RemoteException", e);
						}
					}

					// use the MyTracks content provider to get all the tracks
					Track tracks = myTracksProviderUtils.getLastTrack();
					outputTextView.setText(tracks.getStatistics() + "");

					/* Set the retrieved statistics to the output TextView 
					 * (for debugging/verify, will be removed)
					 */
					String data = outputTextView.getText().toString();

					// Scrape the statistics for the corresponding field
					String mtime = Scrape(data.indexOf("Moving Time:") + 13, data);
					String ttime = Scrape(data.indexOf("Total Time:") + 12, data);
					String aspeed = Scrape(data.indexOf("Average Speed:") + 15, data);
					String tdistance = Scrape(data.indexOf("Total Distance:") + 16, data);

					// Set the statistics to display in the appropriate locations
					movingtime.setText(mtime);
					totaltime.setText(ttime);
					averagespeed.setText(aspeed);
					totaldistance.setText(tdistance);

					//movingtime.setText(Scrape(data.indexOf("Moving Time" + 13), data));

					startService(intent);
					bindService(intent, serviceConnection, 0);

					recordingButton.setText("Start");
					recording = false;
				}
			}
		});

		// Alert dialog to explain that MyTracks app must be installed for statistics to work
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("MyTracks Warning");
		alertDialog.setMessage("You must have the Google MyTracks app installed for this page to function correctly!");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
				return;  
			} });   

		// Display alert dialog
		alertDialog.show();
	}

	/** Function to scrape the values given from the getStatistics() call to myTracks
	 * 
	 * @param strBeg
	 * @param data
	 * @return
	 */
	public String Scrape(int strBeg, String data) 
	{
		String temp = "";

		/* Iterate through the value (chars) of the statistic
		 * and append them to temp, which will be returned
		 */
		for( int i = strBeg; data.charAt(i) != ';'; i++ ) 
		{
			temp += (data.charAt(i));
		}

		return temp;
	}

	@Override
	protected void onStart() 
	{
		super.onStart();

		// start and bind the MyTracks service
		startService(intent);
		bindService(intent, serviceConnection, 0);
	}

	@Override
	protected void onStop() 
	{
		super.onStop();

		// unbind and stop the MyTracks service
		if (myTracksService != null) 
		{
			unbindService(serviceConnection);
		}
		stopService(intent);
	}
}
