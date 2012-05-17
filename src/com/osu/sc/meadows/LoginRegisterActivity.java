package com.osu.sc.meadows;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import server.Client;
import server.ServerEvents;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginRegisterActivity extends Activity implements View.OnClickListener 
{
	public static final String SHARED_PREFERENCES_NAME = "AppPreferences";
	public static final String MEADOWS_USER_EMAIL = "meadows_user_email";
	public static final String MEADOWS_USER_PASS = "meadows_user_pass";
	public static final String MEADOWS_USER_AUTOLOGIN = "meadows_user_autologin";

	ServerEvents events = ServerEvents.GetInstance();
	private UsersUpdatedListener loginSuccessListener;
	private UsersUpdatedListener loginFailureListener;

	Client client = Client.GetInstance();

	String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	String PASS_REGEX = "[^\\s]";
	String NAME_REGEX = "[a-zA-Z]+";

	AlertDialog alert;
	ProgressDialog loggingIn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Initialize the alert box for error reporting later on
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.icon);
		builder.setTitle("Error");
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// When "OK" is clicked, dismiss the alert
				dialog.dismiss();
			}
		});

		alert = builder.create();

		// Restore state, start layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);

		// Set up to listen for login success
		loginSuccessListener = new UsersUpdatedListener()
		{
			public void EventFired(UsersUpdatedEvent event)
			{
				// Call method on login success
				onLoginSuccess();
			}
		};

		// Set up to listen for login failure
		loginFailureListener = new UsersUpdatedListener()
		{
			public void EventFired(UsersUpdatedEvent event)
			{
				// Call method on login success
				onLoginFailure();
			}
		};

		// Register as a login listeners
		events.AddLoginSuccessListener(loginSuccessListener);
		events.AddLoginFailureListener(loginFailureListener);

		//Set up the client immediately when the application opens.
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		Boolean autolog = prefs.getBoolean(MEADOWS_USER_AUTOLOGIN, false);

		// If auto-login is selected by the user
		if(autolog)
		{
			// Retrieve state of "Remember Me" CheckBox, see if checked
			CheckBox cb = (CheckBox)findViewById(R.id.rememberMeCheckBox);
			cb.setChecked(true);

			// Get the email and pass from the prefs file and attempt to login
			String email = prefs.getString(MEADOWS_USER_EMAIL, "");
			String pass = prefs.getString(MEADOWS_USER_PASS, "");

			// Get the text fields so that the can be populated with saved data
			EditText mEmail = (EditText) findViewById(R.id.loginEmail);
			EditText mPass = (EditText) findViewById(R.id.loginPassword);

			// Set the user/pass fields to the saved values
			mEmail.setText(email);
			mPass.setText(pass);

			// Check to see if network is available before trying to log in
			if(isNetworkAvailable())
			{
				// Attempt to login with saved credentials
				client.Login(email, pass);

				// Display logging in message
				loggingIn = ProgressDialog.show(this, "", "Logging in, please wait...", true);
			}
			else 
			{
				alert.setMessage("No Internet connection available, cannot connect at this time!");
				alert.show();
			}
		}
	}

	// When login is successful
	public void onLoginSuccess()
	{
		// Dismiss the logging in message
		if(loggingIn != null)
			loggingIn.dismiss();

		// Destroy listeners in order to avoid weird issues later on
		events.RemoveLoginSuccessListener(loginSuccessListener);
		events.RemoveLoginSuccessListener(loginFailureListener);

		// Start Friends activity
		Intent myFriendIntent = new Intent(LoginRegisterActivity.this, FriendActivity.class);
		LoginRegisterActivity.this.startActivity(myFriendIntent);

		// Finish this activity, don't want the back button to bring user back to login screen
		finish();
	}

	public void onLoginFailure()
	{
		// Dismiss the logging in message
		if(loggingIn != null)
			loggingIn.dismiss();

		// Alert user of the error
		AlertDialog.Builder alert = new AlertDialog.Builder(this); 

		alert.setTitle("Login Error"); 
		alert.setMessage("User not found, please double check your credentials and try again"); 

		alert.setNeutralButton("OK", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int whichButton) {
				// Send the user back to the home screen, destroy listeners
				finish();
			}
		});

		alert.show();
	}

	// Method for determining whether or not the user is in service (wifi or mobile network)
	private boolean isNetworkAvailable() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		// Return true if either wifi or mobile network is available
		return (mWifi.isAvailable() || mMobile.isAvailable());
	}

	// Method to encrypt the password and secret answer using md5
	private String md5(String in) 
	{
		MessageDigest digest;
		try 
		{
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) 
			{
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} 
		catch (NoSuchAlgorithmException e) 
		{ 
			e.printStackTrace(); 
		}
		return null;
	}

	// Method called to save the user's credentials in the case that auto-login is selected
	protected void saveUserCredentials(String email, String pass, Boolean autolog)
	{
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(MEADOWS_USER_EMAIL, email);
		editor.putString(MEADOWS_USER_PASS, pass);
		editor.putBoolean(MEADOWS_USER_AUTOLOGIN, autolog);
		editor.commit();
	}

	@Override
	public void onClick(View v) {

		// If "New? Register here!" was clicked
		if(v.getId() == R.id.registerSwitch)
		{
			// Switch to registration layout
			setContentView(R.layout.registerlayout);
		}

		// If "Already have account? Login here!" was clicked
		if(v.getId() == R.id.loginSwitch)
		{
			// Switch back to login layout
			setContentView(R.layout.loginlayout);
		}

		// Attempt to Register 
		else if(v.getId() == R.id.registerButton)
		{
			String rEmail = ((EditText) findViewById(R.id.registerEmail)).getText().toString();
			String rPass = ((EditText) findViewById(R.id.registerPassword)).getText().toString();
			String rFirstName = ((EditText) findViewById(R.id.registerFirstName)).getText().toString();
			String rLastName = ((EditText) findViewById(R.id.registerLastName)).getText().toString();
			String rSecurityQ = ((EditText) findViewById(R.id.secureQInput)).getText().toString();
			String rSQAnswer = ((EditText) findViewById(R.id.secureQAnswer)).getText().toString();

			// Email validity checks
			if(rEmail.length() <= 0)
			{
				alert.setMessage("Please enter an email address");
				alert.show();
			}
			else if(!rEmail.matches(EMAIL_REGEX))
			{
				alert.setMessage("Email formatted incorrectly");
				alert.show();
			}

			// Password validity checks
			else if(rPass.length() < 6)
			{
				alert.setMessage("Password must be at least 6 characters");
				alert.show();
			}
			else if(rPass.matches(PASS_REGEX))
			{
				alert.setMessage("Password cannot contain spaces");
				alert.show();
			}

			// Name validity checks
			else if(!rFirstName.matches(NAME_REGEX))
			{
				alert.setMessage("First Name must contain only letters");
				alert.show();
			}
			else if(!rLastName.matches(NAME_REGEX))
			{
				alert.setMessage("Last Name must contain only letters");
				alert.show();
			}

			// Security Question validity checks
			else if(rSecurityQ.length() <= 0)
			{
				alert.setMessage("Please enter a Security Question");
				alert.show();
			}

			// SQ Answer validity checks
			else if(rSQAnswer.length() <= 0)
			{
				alert.setMessage("Please enter an answer to your Security Question");
				alert.show();
			}

			// All checks passed, try to log in
			else
			{
				// Check to see if user has service
				if(isNetworkAvailable())
				{		
					// Remove case sensitivity from secret question answer
					rSQAnswer = rSQAnswer.toLowerCase();

					// Encrypt password and security question answer
					String ePass = md5(rPass);
					String eSQAnswer = md5(rSQAnswer);

					// Create user with input values
					client.CreateUser(rFirstName, rLastName, rEmail, ePass, rSecurityQ, eSQAnswer);

					// Start the friend activity, log on is performed when creation succeeds
					Intent myFriendIntent = new Intent(LoginRegisterActivity.this, FriendActivity.class);
					LoginRegisterActivity.this.startActivity(myFriendIntent);

					// Finish this activity, don't want the back button to bring used back to this screen
					finish();
				}
				// User does not have service
				else
				{
					alert.setMessage("No Internet connection available, cannot connect at this time!");
					alert.show();
				}
			}			
		}
		// Attempting to login
		else if(v.getId() == R.id.loginButton)
		{
			EditText mEmail = (EditText) findViewById(R.id.loginEmail);
			EditText mPass = (EditText) findViewById(R.id.loginPassword);
			String lEmail = mEmail.getText().toString();
			String lPass = mPass.getText().toString();

			// Email validity checks
			if(lEmail.length() <= 0)
			{
				alert.setMessage("Please enter an email address");
				alert.show();
			}
			else if(!lEmail.matches(EMAIL_REGEX))
			{
				alert.setMessage("Email formatted incorrectly");
				alert.show();
			}

			// Password validity checks
			else if(lPass.length() < 6)
			{
				alert.setMessage("Password must be at least 6 characters");
				alert.show();
			}
			else if(lPass.matches(PASS_REGEX))
			{
				alert.setMessage("Password cannot contain spaces");
				alert.show();
			}

			// All checks passed, try to log in
			else
			{
				// Check to make sure that user has service
				if(isNetworkAvailable())
				{
					// Encrypt password
					String ePass = md5(lPass);
					Boolean autolog = false;

					// Retrieve state of "Remember Me" CheckBox, see if checked
					CheckBox cb = (CheckBox)findViewById(R.id.rememberMeCheckBox);

					if(cb.isChecked())
						autolog = true;

					// Save the user email, encrypted password and auto login preference
					saveUserCredentials(lEmail, ePass, autolog);

					// Attempt to log in with supplied credentials
					client.Login(lEmail, ePass);

					// Logging in progress message
					loggingIn = ProgressDialog.show(this, "", "Logging in, please wait...", true);
				}
				// No service
				else
				{
					alert.setMessage("No Internet connection available, cannot connect at this time!");
					alert.show();
				}
			}
		}	
	}

	@Override
	public void onDestroy()
	{
		//Unregister the listeners.
		events.RemoveLoginSuccessListener(loginSuccessListener);
		events.RemoveLoginSuccessListener(loginFailureListener);

		//Call the base class destroy.
		super.onDestroy();
	}
}
