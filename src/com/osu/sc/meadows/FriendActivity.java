package com.osu.sc.meadows;

import java.util.ArrayList;
import java.util.Calendar;

import server.Client;
import server.Server;
import server.ServerEvents;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;

import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

/*
 * Place holder activity for eventual Social features
 */
public class FriendActivity extends ListActivity 
{
	String text = "";
	Client client = Client.GetInstance();
	ServerEvents events = ServerEvents.GetInstance();
	private UsersUpdatedListener friendsListener;
	private UsersUpdatedListener blockedUsersListener;
	private UsersUpdatedListener friendRequestsListener;
	private UsersUpdatedListener loginSuccessListener;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        ArrayList<User> friends = client.GetFriends();
    	setListAdapter(new FriendAdapter(this, friends));
        //setContentView(R.layout.friendlayout);
        
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
    	loginSuccessListener = new UsersUpdatedListener()
    	{
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			OnLogin();
    		}
    	};
    	
    	//Register the listeners.
        events.AddFriendsUpdatedListener(friendsListener);
    	events.AddBlockedUsersUpdatedListener(blockedUsersListener);
    	events.AddFriendRequestsUpdatedListener(friendRequestsListener);
    	events.AddLoginSuccessListener(loginSuccessListener);
    }
    
    
    /**
     * @param view
     */
    public void ButtonClicked(View view)
    {
    	//EditText textBox = (EditText)findViewById(R.id.testText);
    	//text += "Logged in as " + client.GetFirstName() + '\n';
    	//textBox.setText(text);
    	
    	ArrayList<User> friends = client.GetFriendRequests();
    	setListAdapter(new FriendAdapter(this, friends));
    	
    }
    
    public void OnFriendsUpdated(ArrayList<User> users)
    {
//    	EditText textBox = (EditText)findViewById(R.id.testText);
//    	text += "FriendsUpdated: "+Integer.toString(users.size()) + '\n';
//    	
//    	Client client = Client.GetInstance();
//    	ArrayList<User> visibleUsers = client.GetVisibleFriends();
//    	text += "VisibleFriendsUpdated: "+Integer.toString(visibleUsers.size()) + '\n';
//    	
//
//    	textBox.setText(text);d
    }
    
    public void OnLogin()
    {
//    	EditText textBox = (EditText)findViewById(R.id.testText);
//    	text += "Logged in as " + client.GetFirstName() + '\n';
//    	textBox.setText(text);
    }
    
    public void OnBlockedUsersUpdated(ArrayList<User> users)
    {
//    	EditText textBox = (EditText)findViewById(R.id.testText);
//    	text += "BlockedUsersUpdated: "+Integer.toString(users.size()) + '\n';
//    	textBox.setText(text);
    }
    
    public void OnFriendRequestsUpdated(ArrayList<User> users)
    {
//    	EditText textBox = (EditText)findViewById(R.id.testText);
//    	text += "OnFriendRequestsUpdated: "+Integer.toString(users.size()) + '\n';
//    	textBox.setText(text);
    }
    
    @Override
    public void onDestroy()
    {
    	//Unregister the listeners.
        events.RemoveFriendsUpdatedListener(friendsListener);
    	events.RemoveBlockedUsersUpdatedListener(blockedUsersListener);
    	events.RemoveFriendRequestsUpdatedListener(friendRequestsListener);
    	events.RemoveLoginSuccessListener(loginSuccessListener);
    	
    	//Call the base class destroy.
    	super.onDestroy();
    }
    
}
