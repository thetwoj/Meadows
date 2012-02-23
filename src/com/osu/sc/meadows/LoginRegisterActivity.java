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
		
		// Initialize the alert box for error reporting
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.icon);
		builder.setTitle("Error");
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
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
	}
	
	// When login is successful
	public void onLoginSuccess()
	{
		// Dismiss the logging in message
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
		loggingIn.dismiss();
		
		// Alert user of the error
		alert.setTitle("Login Failed! Please try again later");
		alert.show();
		
		// Send the user back to the home screen, destroy listeners
		finish();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		return activeNetworkInfo != null;
	}

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
		

		// Switch to the Registration view
		if(v.getId() == R.id.registerSwitch)
		{
			setContentView(R.layout.registerlayout);
		}

		if(v.getId() == R.id.loginSwitch)
		{
			setContentView(R.layout.loginlayout);
		}

		// Attempt to Register 
		else if(v.getId() == R.id.registerButton)
		{
			EditText mEmail = (EditText) findViewById(R.id.registerEmail);
			EditText mPass = (EditText) findViewById(R.id.registerPassword);
			EditText mFirstName = (EditText) findViewById(R.id.registerFirstName);
			EditText mLastName = (EditText) findViewById(R.id.registerLastName);
			EditText mSecurityQ = (EditText) findViewById(R.id.secureQInput);
			EditText mSQAnswer = (EditText) findViewById(R.id.secureQAnswer);

			String rEmail = mEmail.getText().toString();
			String rPass = mPass.getText().toString();
			String rFirstName = mFirstName.getText().toString();
			String rLastName = mLastName.getText().toString();
			String rSecurityQ = mSecurityQ.getText().toString();
			String rSQAnswer = mSQAnswer.getText().toString();

			// Email validity checks
			if(rEmail.isEmpty())
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
			else if(rSecurityQ.isEmpty())
			{
				alert.setMessage("Please enter a Security Question");
				alert.show();
			}

			// SQ Answer validity checks
			else if(rSQAnswer.isEmpty())
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
			if(lEmail.isEmpty())
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
