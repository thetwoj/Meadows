package com.osu.sc.meadows;

import server.Client;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginRegisterActivity extends Activity implements View.OnClickListener 
{

	Client client = Client.GetInstance();
	String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	String PASS_REGEX = "[^\\s]";
	String NAME_REGEX = "[a-zA-Z]+";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);

	}

	public LoginRegisterActivity() {
		// TODO Auto-generated constructor stub
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	@Override
	public void onClick(View v) {
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
		AlertDialog alert = builder.create();

		// Switch to the Registration view
		if(v.getId() == R.id.registerSwitch)
		{
			setContentView(R.layout.registerlayout);
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

			// REGISTRATION VALIDITY CHECKS
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
				//alert.setMessage("Registration Successful!");
				//alert.show();

				if(isNetworkAvailable())
				{
					client.CreateUser(rFirstName, rLastName, rEmail, rPass, rSecurityQ, rSQAnswer);
					Intent myFriendIntent = new Intent(LoginRegisterActivity.this, FriendActivity.class);
					LoginRegisterActivity.this.startActivity(myFriendIntent);
					finish();
				}
				else
				{
					alert.setMessage("No Internet connection available, cannot connect at this time!");
					alert.show();
				}
			}			

		}
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
				//alert.setMessage("Checks passed, trying to login!");
				//alert.show();
				if(isNetworkAvailable())
				{
					client.Login(lEmail, lPass);
					Intent myFriendIntent = new Intent(LoginRegisterActivity.this, FriendActivity.class);
					LoginRegisterActivity.this.startActivity(myFriendIntent);
					finish();
				}
				else
				{
					alert.setMessage("No Internet connection available, cannot connect at this time!");
					alert.show();
				}
			}
		}	

	}

}
