package server;

import java.util.ArrayList;
import java.awt.*;

import android.os.Handler;

public class Client
{
	//singleton instance of client
	private static Client _client;
	
	//private variables
	private ArrayList<User> _blockedUsers;
	private ArrayList<User> _friends;
	private ArrayList<User> _friendRequests;
	private String 	_firstName;
	private String	_lastName;
	private String	_phoneNumber;
	private String	_longitude;
	private String 	_latitude;
	private int 	_clientUid;
	private boolean _globalVisibility;
	
	private Server _server = Server.GetInstance();
	
	
	//public getters
	public String 	GetFirstName() 			{ return _firstName; }
	public String 	GetLastName()  			{ return _lastName;  }
	public String 	GetPhoneNumber()  		{ return _phoneNumber;  }
	public String 	GetLatitude()  			{ return _latitude;  }
	public String 	GetLongitude() 			{ return _longitude; }
	public boolean 	GetGlobalVisibility()	{ return _globalVisibility; }
	
	protected Client()
	{
		//Register so that successful logins updates local variables
		ServerEvents events = ServerEvents.GetInstance();
		events.AddLoginSuccessListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			//get client's user object
    			ArrayList<User> users = event.GetUsers();
    			User user = users.get(0);
    			
    			_firstName = user.GetFirstName();
    			_lastName = user.GetLastName();
    			_clientUid = user.GetUid();
    			_globalVisibility = user.GetShareLocation();
    		}
    	});
		
		events.AddFriendsUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			_friends = users;
    		}
    	});
    	
    	events.AddBlockedUsersUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			_blockedUsers = users;
    		}
    	});
    	
    	events.AddFriendRequestsUpdatedListener(new UsersUpdatedListener(){
    		public void EventFired(UsersUpdatedEvent event)
    		{
    			ArrayList<User> users = event.GetUsers();
    			_friendRequests = users;
    		}
    	});
	}
	
	public void Login(String clientPhoneNumber)
	{
		_server.Login(clientPhoneNumber);
	}
	
	public void CreateUser(String firstName, String lastName, String phoneNumber)
	{
		_server.AddUser(phoneNumber, firstName, lastName);
	}
	
	public void RequestUpdateFriends()
	{
		if(LoggedIn())
			_server.RequestUpdateFriends(_clientUid);
	}
	
	public void RequestUpdateBlockedUsers()
	{
		if(LoggedIn())
			_server.RequestUpdateBlockedUsers(_clientUid);
	}
	
	public void RequestUpdateFriendRequests()
	{
		if(LoggedIn())
			_server.RequestUpdateFriendRequests(_clientUid);
	}
	
	public void DenyFriendRequest(User user)
	{
		if(!LoggedIn() || !_friendRequests.contains(user))
			return;
		
		_server.RemoveFriend(user.GetUid(), _clientUid);
		
		_friendRequests.remove(user);
		ServerEvents.GetInstance()._InvokeFriendRequestsUpdated(_friendRequests);	
	}
	
	public void AcceptFriendRequest(User user)
	{
		//ensure logged in and valid request
		if(!LoggedIn())
			return;
		
		//send server request to add friend
		_server.AddFriend(_clientUid, user.GetPhoneNumber());
		
		if(_friendRequests.contains(user))
		{
			_friendRequests.remove(user);
			ServerEvents.GetInstance()._InvokeFriendRequestsUpdated(_friendRequests);
		}
		
		if( !_friends.contains(user))
		{
			_friends.add(user);
			ServerEvents.GetInstance()._InvokeFriendsUpdated(_friends);			
		}
	}
	
	public void BlockUser(User user)
	{
		if(!LoggedIn())
			return;

		_server.BlockUser(_clientUid, user.GetUid());
		
		if(_friendRequests.contains(user))
		{
			_friendRequests.remove(user);
			ServerEvents.GetInstance()._InvokeFriendRequestsUpdated(_friendRequests);
		}
		
		if(!_blockedUsers.contains(user))
		{
			_blockedUsers.add(user);
			ServerEvents.GetInstance()._InvokeBlockedUsersUpdated(_blockedUsers);
		}
	}
	
	public void UnblockUser(User user)
	{
		if(!LoggedIn())
			return;
		
		_server.UnblockUser(_clientUid, user.GetUid());
		
		if(_blockedUsers.contains(user))
		{
			_blockedUsers.remove(user);
			ServerEvents.GetInstance()._InvokeBlockedUsersUpdated(_blockedUsers);
		}
	}
	
	public void AddFriend(String phoneNumber)
	{
		if(LoggedIn())
			_server.AddFriend(_clientUid, phoneNumber);
	}
	
	public void SetShareLocation(User user, boolean value)
	{
		if(LoggedIn())
			_server.SetShareLocation(_clientUid, user.GetUid(), value);
	}
	
	public void SetFirstName(String value)
	{
		if(LoggedIn() && _firstName != value)
		{
			_firstName = value;
			_server.UpdateClientData(
					_clientUid, 
					_firstName, 
					_lastName,
					_phoneNumber, 
					_globalVisibility);		
		}
	}
	
	public void SetLastName(String value)
	{
		if(LoggedIn() && _lastName != value)
		{
			_lastName = value;
			_server.UpdateClientData(
					_clientUid, 
					_firstName, 
					_lastName,
					_phoneNumber, 
					_globalVisibility);		
		}
	}
	
	public void SetPhoneNumber(String value)
	{
		if(LoggedIn() && _phoneNumber != value)
		{
			_phoneNumber = value;
			_server.UpdateClientData(
					_clientUid, 
					_firstName, 
					_lastName,
					_phoneNumber, 
					_globalVisibility);	
		}
	}
	
	public void SetLocation(String latitude, String longitude)
	{
		if(LoggedIn() && (_latitude != latitude || _longitude != longitude))
		{
			_latitude = latitude;
			_longitude = longitude;
			_server.UpdateLocation(
					_clientUid, 
					_latitude,
					_longitude);		
		}
	}
	
	public void SetGlobalVisibility(boolean value)
	{
		if(!LoggedIn() && _globalVisibility != value)
		{
			_globalVisibility = value;
			_server.UpdateClientData(
					_clientUid, 
					_firstName, 
					_lastName,
					_phoneNumber,  
					_globalVisibility);	
		}
	}
	
	/* Returns whether or not the client has logged in */
	public boolean LoggedIn()
	{
		return _clientUid >= 0;
	}
	
	/* returns the singleton instance of Client */
	public static Client GetInstance()
	{
		if( _client == null )
			_client = new Client();
		return _client;
	}	
}
