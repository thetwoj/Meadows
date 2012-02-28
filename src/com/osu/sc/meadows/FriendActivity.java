package com.osu.sc.meadows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import server.Client;
import server.ServerEvents;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

/*
 * Place holder activity for eventual Social features
 */
public class FriendActivity extends ListActivity implements Comparator<User>
{
	Client client = Client.GetInstance();
	ServerEvents events = ServerEvents.GetInstance();

	private UsersUpdatedListener friendsListener;
	private UsersUpdatedListener blockedUsersListener;
	private UsersUpdatedListener friendRequestsListener;

	// Global Visibility
	ToggleButton globalVisible;
	OnCheckedChangeListener gVisible;

	ArrayList<User> friends;
	ArrayList<User> friendRequests;

	// Loading message
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
		friends = client.GetFriends();

		// Sort the friends by first name
		Collections.sort(friends, this);

		// Get a new ListView and inflater to create the Friends layout
		lv = getListView();
		LayoutInflater inflater = getLayoutInflater();

		// Set up the Friends header and add it to the View
		View header = inflater.inflate(R.layout.friendheader, (ViewGroup) findViewById(R.id.friendHeaderRoot));
		lv.addHeaderView(header, null, false);

		// Set the FriendAdapter to be filling the listView with sorted friends list
		lv.setAdapter(new FriendAdapter(this, friends));

		// Dismiss the friend loading message
		loadingFriends.dismiss();

		// Set the "Global Visibility" button to match current Client info
		globalVisible = (ToggleButton) findViewById(R.id.globalVisibleToggle);
		globalVisible.setChecked(Client.GetInstance().GetGlobalVisibility());

		// Listener for "Global Visibility" button
		gVisible = new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
				// On click, set Global Visibility to the checked state of toggleButton
				Client.GetInstance().SetGlobalVisibility(buttonView.isChecked());
			}
		};

		//Create the event listeners.
		friendsListener = new UsersUpdatedListener()
		{
			public void EventFired(UsersUpdatedEvent event)
			{
				ArrayList<User> users = event.GetUsers();
				OnFriendsUpdated(users);
			}
		};
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

		// Register the onCheck listener to the "Global Visibility" toggleButton
		globalVisible.setOnCheckedChangeListener(gVisible);

		//Register the listeners.
		events.AddFriendsUpdatedListener(friendsListener);
		events.AddBlockedUsersUpdatedListener(blockedUsersListener);
		events.AddFriendRequestsUpdatedListener(friendRequestsListener);
	}

	public void OnFriendsUpdated(ArrayList<User> users)
	{
		// When friends are updated, update the FriendAdapter so they are shown
		friends = client.GetFriends();
		Collections.sort(friends, this);
		setListAdapter(new FriendAdapter(this, friends));
	}

	public void OnLogin()
	{

	}

	public void OnBlockedUsersUpdated(ArrayList<User> users)
	{

	}

	public void OnFriendRequestsUpdated(ArrayList<User> users)
	{
		// Determine the number of pending friend requests
		int numberOfRequests = users.size();

		ImageView requestCount = (ImageView) findViewById(R.id.friendRequestCount);

		// Get all of the friend request notification icons
		Drawable zero = getResources().getDrawable(R.drawable.zero);
		Drawable one = getResources().getDrawable(R.drawable.one);
		Drawable two = getResources().getDrawable(R.drawable.two);
		Drawable three = getResources().getDrawable(R.drawable.three);
		Drawable four = getResources().getDrawable(R.drawable.four);
		Drawable five = getResources().getDrawable(R.drawable.five);

		// Depending on the number of requests, set the appropriate notification icon
		switch(numberOfRequests)
		{
		case(0):
			requestCount.setBackgroundDrawable(zero);
		break;
		case(1):
			requestCount.setBackgroundDrawable(one);
		break;
		case(2):
			requestCount.setBackgroundDrawable(two);
		break;
		case(3):
			requestCount.setBackgroundDrawable(three);
		break;
		case(4):
			requestCount.setBackgroundDrawable(four);
		break;
		case(5):
			requestCount.setBackgroundDrawable(five);
		break;
		default:
			requestCount.setBackgroundDrawable(five);
			break;
		}

		// Invalidate the ImageView to force re-draw
		requestCount.invalidate();
	}

	public void OnClick(View v)
	{
		// Add Friend button click
		if(v.getId() == R.id.addFriendButton)
		{
			LayoutInflater factory = LayoutInflater.from(this);            
			final View textEntryView = factory.inflate(R.layout.addfrienddialoglayout, null);

			AlertDialog.Builder alert = new AlertDialog.Builder(this); 

			// Set text to prompt user for friend's email
			alert.setTitle("Add Friend"); 
			alert.setMessage("Enter your friend's email:"); 

			// Set an EditText view to get the email of desired friend 
			alert.setView(textEntryView); 

			final EditText input1 = (EditText) textEntryView.findViewById(R.id.addFriendEmail);

			// Add a positive button, "Add" to the dialog
			alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) { 
					// Get the email from the dialog box and send friend request to server
					String friendEmail = input1.getText().toString();
					client.AddFriend(friendEmail);
				}
			});
			// Add a neutral button, "Cancel" to the dialog
			alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int whichButton) { 
					// Do nothing, just let the dialog dismiss itself
				}
			});

			// Dismiss the dialog after a button has been pressed
			alert.show();

		}
		// Friend Request Notification Count click
		else if(v.getId() == R.id.friendRequestCount)
		{
			// Start up the Friend Request activity
			Intent myFriendRequestIntent = new Intent(FriendActivity.this, FriendRequestActivity.class);
			FriendActivity.this.startActivity(myFriendRequestIntent);
		}
	}

	@Override
	public void onDestroy()
	{
		//Unregister the listeners.
		events.AddFriendRequestsUpdatedListener(friendRequestsListener);
		events.RemoveFriendsUpdatedListener(friendsListener);
		events.RemoveBlockedUsersUpdatedListener(blockedUsersListener);

		//Call the base class destroy.
		super.onDestroy();
	}

	// Comparison function used to alphabetize friends list
	@Override
	public int compare(User lhs, User rhs) {
		return lhs.GetFirstName().compareTo(rhs.GetFirstName());
	}

}
