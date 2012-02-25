package com.osu.sc.meadows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import server.Client;
import server.ServerEvents;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

	ArrayList<User> friends;
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
		friends = client.GetFriends();

		// Sort the friends by first name
		Collections.sort(friends, this);

		lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		View header = inflater.inflate(R.layout.friendheader, (ViewGroup) findViewById(R.id.friendHeaderRoot));
		lv.addHeaderView(header, null, false);

		lv.setAdapter(new FriendAdapter(this, friends));
		
		// Dismiss the friend loading message
		loadingFriends.dismiss();

		//Create the listeners.
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

		//Register the listeners.
		events.AddFriendsUpdatedListener(friendsListener);
		events.AddBlockedUsersUpdatedListener(blockedUsersListener);
		events.AddFriendRequestsUpdatedListener(friendRequestsListener);
	}

	public void OnFriendsUpdated(ArrayList<User> users)
	{
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
		int numberOfRequests = users.size();

		ImageView requestCount = (ImageView) findViewById(R.id.friendRequestCount);

		Drawable zero = getResources().getDrawable(R.drawable.zero);
		Drawable one = getResources().getDrawable(R.drawable.one);
		Drawable two = getResources().getDrawable(R.drawable.two);
		Drawable three = getResources().getDrawable(R.drawable.three);
		Drawable four = getResources().getDrawable(R.drawable.four);
		Drawable five = getResources().getDrawable(R.drawable.five);

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

		requestCount.invalidate();
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
		else if(v.getId() == R.id.friendRequestCount)
		{
			Intent myFriendRequestIntent = new Intent(FriendActivity.this, FriendRequestActivity.class);
			FriendActivity.this.startActivity(myFriendRequestIntent);
		}
	}

	@Override
	public void onDestroy()
	{
		//Unregister the listeners.
		events.RemoveFriendsUpdatedListener(friendsListener);
		events.RemoveBlockedUsersUpdatedListener(blockedUsersListener);

		//Call the base class destroy.
		super.onDestroy();
	}

	@Override
	public int compare(User lhs, User rhs) {
		return lhs.GetFirstName().compareTo(rhs.GetFirstName());
	}

}
