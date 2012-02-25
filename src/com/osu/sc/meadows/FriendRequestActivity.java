package com.osu.sc.meadows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import server.Client;
import server.Server;
import server.ServerEvents;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/*
 * Place holder activity for eventual Social features
 */
public class FriendRequestActivity extends ListActivity implements Comparator<User>
{
	Client client = Client.GetInstance();
	ServerEvents events = ServerEvents.GetInstance();
	private UsersUpdatedListener blockedUsersListener;
	private UsersUpdatedListener friendRequestsListener;

	ArrayList<User> friendRequests;

	ProgressDialog loadingFriends;

	ListView lv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// Display loading message in case this takes a second or two
		loadingFriends = ProgressDialog.show(this, "", "Loading friends, please wait...", true);

		// Get the client's friends
		friendRequests = client.GetFriendRequests();

		// Sort the friends by first name
		Collections.sort(friendRequests, this);

		lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		View requestheader = inflater.inflate(R.layout.friendrequestheader, (ViewGroup) findViewById(R.id.friendRequestHeaderRoot));
		lv.addHeaderView(requestheader, null, false);

		lv.setAdapter(new FriendRequestAdapter(this, friendRequests));

		// Dismiss the friend loading message
		loadingFriends.dismiss();

		//Create the listeners.
		blockedUsersListener = new UsersUpdatedListener()
		{
			public void EventFired(UsersUpdatedEvent event)
			{
				ArrayList<User> users = event.GetUsers();
				OnBlockedUsersUpdated(users);
			}
		};
		friendRequestsListener = new UsersUpdatedListener()
		{
			public void EventFired(UsersUpdatedEvent event)
			{
				ArrayList<User> users = event.GetUsers();
				OnFriendRequestsUpdated(users);
			}
		};

		//Register the listeners.
		events.AddBlockedUsersUpdatedListener(blockedUsersListener);
		events.AddFriendRequestsUpdatedListener(friendRequestsListener);
	}

	public void OnBlockedUsersUpdated(ArrayList<User> users)
	{

	}

	public void OnFriendRequestsUpdated(ArrayList<User> users)
	{
		friendRequests = client.GetFriendRequests();
		Collections.sort(friendRequests, this);
		lv.setAdapter(new FriendRequestAdapter(this, friendRequests));
	}

	public void OnClick(View v)
	{
		if(v.getId() == R.id.addFriendButton)
		{
			LayoutInflater factory = LayoutInflater.from(this);            
			final View textEntryView = factory.inflate(R.layout.addfrienddialoglayout, null);

			AlertDialog.Builder alert = new AlertDialog.Builder(this); 

			alert.setTitle("Add Friend"); 
			alert.setMessage("Enter your friend's email:"); 
			
			// Set an EditText view to get user input  
			alert.setView(textEntryView); 

			final EditText input1 = (EditText) textEntryView.findViewById(R.id.addFriendEmail);

			alert.setPositiveButton("Add", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int whichButton) { 
					String friendEmail = input1.getText().toString();
					client.AddFriend(friendEmail);
				}
			});
			alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int whichButton) { 
		
				}
			});
			
			alert.show();
			
		}
	}

	@Override
	public void onDestroy()
	{
		//Unregister the listeners.
		events.RemoveBlockedUsersUpdatedListener(blockedUsersListener);
		events.RemoveFriendRequestsUpdatedListener(friendRequestsListener);

		//Call the base class destroy.
		super.onDestroy();
	}

	@Override
	public int compare(User lhs, User rhs) {
		return lhs.GetFirstName().compareTo(rhs.GetFirstName());
	}

}
