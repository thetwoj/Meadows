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
import android.os.Bundle;

/*
 * Place holder activity for eventual Social features
 */
public class SocialActivity extends Activity 
{
	String text = "";
	Client client = Client.GetInstance();
	ServerEvents events = ServerEvents.GetInstance();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sociallayout);
        
        events.AddFriendsUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			OnFriendsUpdated(users);
    		}
    	});
    	
    	events.AddBlockedUsersUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			OnBlockedUsersUpdated(users);
    		}
    	});
    	
    	events.AddFriendRequestsUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			OnFriendRequestsUpdated(users);
    		}
    	});
    	
    	events.AddLoginSuccessListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			OnLogin();
    		}
    	});        
    }
    
    /**
     * @param view
     */
    /**
     * @param view
     */
    public void ButtonClicked(View view)
    {
    	client.Login("12345");
    }
    
    public void OnFriendsUpdated(ArrayList<User> users)
    {
    	EditText textBox = (EditText)findViewById(R.id.testText);
    	text += "FriendsUpdated: "+Integer.toString(users.size()) + '\n';
    	textBox.setText(text);
    }
    
    public void OnLogin()
    {
    	EditText textBox = (EditText)findViewById(R.id.testText);
    	text += "Logged in as " + Boolean.toString(client.GetInstance().GetGlobalVisibility()) + '\n';
    	textBox.setText(text);
    }
    
    public void OnBlockedUsersUpdated(ArrayList<User> users)
    {
    	EditText textBox = (EditText)findViewById(R.id.testText);
    	text += "BlockedUsersUpdated: "+Integer.toString(users.size()) + '\n';
    	textBox.setText(text);
    }
    
    public void OnFriendRequestsUpdated(ArrayList<User> users)
    {
    	EditText textBox = (EditText)findViewById(R.id.testText);
    	text += "OnFriendRequestsUpdated: "+Integer.toString(users.size()) + '\n';
    	textBox.setText(text);
    }
    
}
