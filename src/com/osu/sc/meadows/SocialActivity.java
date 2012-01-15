package com.osu.sc.meadows;

import java.util.ArrayList;
import java.util.Calendar;

import MeadowsServer.Server;
import MeadowsServer.Server;
import MeadowsServer.User;
import MeadowsServer.Client;
import MeadowsServer.UsersUpdatedEvent;
import MeadowsServer.UsersUpdatedListener;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.os.Bundle;

/*
 * Place holder activity for eventual Social features
 */
public class SocialActivity extends Activity 
{
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sociallayout);
        
    }
    
    /**
     * @param view
     */
    /**
     * @param view
     */
    public void ButtonClicked(View view)
    {
    	
    	EditText text = (EditText)findViewById(R.id.testText);
    	Server server = Server.GetServerInstance();
    	Client client = Client.GetClientInstance();
    	client.AddUsersUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			OnUsersUpdated(users);
    		}
    	});
    	server.RefreshUserData(25);
    }
    
    public void OnUsersUpdated(ArrayList<User> users)
    {
    	EditText text = (EditText)findViewById(R.id.testText);
    	text.setText(Integer.toString(users.size()));
    }
    
    
}
