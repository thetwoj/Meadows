package com.osu.sc.meadows;

import java.text.DecimalFormat;
import java.util.List;
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
import com.google.android.apps.mytracks.stats.TripStatistics;

/*
 * Place holder activity for eventual Stats features
 */
public class StatsActivity extends Activity 
{

	private static final String TAG = StatsActivity.class.getSimpleName();

	Boolean recording = false;

	// utils to access the MyTracks content provider
	private MyTracksProviderUtils myTracksProviderUtils;

	private TextView maxspeeddisp;
	private TextView averagespeeddisp;
	private TextView totaldistancedisp;
	private TextView totaltimedisp;
	private TextView movingtimedisp;

	private TextView maxspeedhist;
	private TextView averagespeedhist;
	private TextView totaldistancehist;
	private TextView totaltimehist;
	private TextView movingtimehist;

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
		maxspeeddisp = (TextView) findViewById(R.id.maxspeed);
		averagespeeddisp = (TextView) findViewById(R.id.averagespeed);
		totaldistancedisp = (TextView) findViewById(R.id.totaldistance);
		totaltimedisp = (TextView) findViewById(R.id.totaltime);
		movingtimedisp = (TextView) findViewById(R.id.movingtime);


		// for the MyTracks service
		intent = new Intent();
		ComponentName componentName = new ComponentName(
				getString(R.string.mytracks_service_package), getString(R.string.mytracks_service_class));
		intent.setComponent(componentName);

		//update the historical stats
		updateHistorical();
		final Button recordingButton = (Button) findViewById(R.id.recording_button);
		recordingButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// If not currently recording
				// If myTracks is connected successfully
				if(myTracksService == null)
				{
					// Throw connection error
					throwAlert("Connection Error", "Google MyTracks App must be installed and have sharing " +
							"with third party apps set to true!");
					return;

				}
				if (recording == false) 
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
				// If currently recording
				else 
				{
					// Try to stop the currently recording track
					try {
						myTracksService.endCurrentTrack();
					} catch (RemoteException e) {
						Log.e(TAG, "RemoteException", e);
					}

					// Get the statistics from recorded track.
					Track track = myTracksProviderUtils.getLastTrack();
					// If the track is null then permissions aren't correct
					if(track == null)
					{
						// Alert user of the permissions error
						// Throw connection error
						throwAlert("Permissions Error", "Google MyTracks App must be installed and have sharing " +
								"with third party apps set to true!");
						return;
					}
					// If not null, go ahead and parse the data retrieved
					else
					{

						TripStatistics stats = track.getStatistics();

						updateHistorical();

						// Set fields to their respective statistics.
						maxspeeddisp.setText(speedConvert(stats.getMaxSpeed()));
						movingtimedisp.setText( timeConvert(stats.getMovingTime()));
						totaltimedisp.setText( timeConvert(stats.getTotalTime()));
						averagespeeddisp.setText( speedConvert(stats.getAverageMovingSpeed()));
						totaldistancedisp.setText( distanceConvert(stats.getTotalDistance()));



						startService(intent);
						bindService(intent, serviceConnection, 0);

						recordingButton.setText("Start");
						recording = false;
					}
				}
			}
		});
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

	// Throws alert with given title and message
	protected void throwAlert(String title, String message)
	{
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
				return;  
			} });   
		alertDialog.show();
	}


	//Populates the historical fields
	protected void updateHistorical()
	{
		List<Track> tracks = myTracksProviderUtils.getAllTracks();
		maxspeedhist = (TextView) findViewById(R.id.maxspeedhist);
		averagespeedhist = (TextView) findViewById(R.id.averagespeedhist);
		totaldistancehist = (TextView) findViewById(R.id.totaldistancehist);
		totaltimehist = (TextView) findViewById(R.id.totaltimehist);
		movingtimehist = (TextView) findViewById(R.id.movingtimehist);

		int count = 0;
		double maxSpeed = 0, tmpMaxSpeed, avgSpeed, totalSpeed = 0, totalDistance = 0;
		long totalTime = 0, movingTime = 0;



		for(Track track : tracks)
		{
			TripStatistics stats = track.getStatistics();
			tmpMaxSpeed = stats.getMaxSpeed();
			maxSpeed = tmpMaxSpeed > maxSpeed ? tmpMaxSpeed : maxSpeed;
			totalSpeed += stats.getAverageMovingSpeed();
			totalDistance += stats.getTotalDistance();
			totalTime += stats.getTotalTime();
			movingTime += stats.getMovingTime();
			++count;
		}

		avgSpeed = totalSpeed / count;

		maxspeedhist.setText(speedConvert(maxSpeed));
		movingtimehist.setText( timeConvert(movingTime));
		totaltimehist.setText( timeConvert(totalTime));
		averagespeedhist.setText( speedConvert(avgSpeed));
		totaldistancehist.setText( distanceConvert(totalDistance));

	}

	//Converts the speed from m/s to km/hr
	protected String speedConvert(double speed)
	{
		DecimalFormat df = new DecimalFormat ("####.###");

		speed *= 3.6;
		return df.format(speed) + " km/hr";
	}

	//Converts the time into a more reader friendly format
	protected String timeConvert(long milliseconds)
	{
		DecimalFormat df = new DecimalFormat("##00");
		long x, seconds, minutes, hours;
		x = milliseconds / 1000;
		seconds = x % 60;
		x /= 60;
		minutes = x % 60;
		x /= 60;
		hours = x % 24;

		return String.format( df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds));
	}

	//If the distance is greater than 3000 meters, convert to kilometers. Cut off decimal place at 3 regardless.
	protected String distanceConvert(double distance)
	{
		DecimalFormat df = new DecimalFormat ("######.###");
		if(distance > 3000)
			return  df.format(distance / 1000) + " km";
		else
			return df.format(distance) + "m";
	}
}
