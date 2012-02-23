package server;

import java.util.ArrayList;

import android.graphics.PointF;

public class Client
{
	//singleton instance of client
	private static Client _client;
	
	//private variables
	private ArrayList<User> _blockedUsers = new ArrayList<User>();
	private ArrayList<User> _friends  = new ArrayList<User>();
	private ArrayList<User> _friendRequests  = new ArrayList<User>();
	
	protected String 	_firstName;
	protected String	_lastName;
	protected String	_email;
	protected double  	_longitude;
	protected double  	_latitude;
	protected int 		_clientUid;
	protected boolean 	_globalVisibility;
	protected String 	_secretQuestion;
	protected long    	_timestamp;
	
	private PointF  _mapLocation;
	
	private static final int NETWORK_PERIOD = 4000;
    private static final int GPS_PERIOD = 4000;
	
	private Server _server = Server.GetInstance();
	
	
	//public getters
	public String 	       GetFirstName() 			{ return _firstName; }
	public String 		   GetLastName()  			{ return _lastName;  }
	public String 		   GetEmail()  				{ return _email;  }
	public double   	   GetLatitude()  		    { return _latitude;  }
	public double  	       GetLongitude() 		    { return _longitude; }
	public boolean 	       GetGlobalVisibility()	{ return _globalVisibility; }
	public int             GetNetworkPeriod()       { return NETWORK_PERIOD; }
	public int             GetGPSPeriod()           { return GPS_PERIOD; }
	public PointF          GetMapLocation()         { return _mapLocation; }
	public long            GetTimestamp()           { return _timestamp; }
	public String		   GetSecretQuestion()		{ return _secretQuestion; }

	public ArrayList<User> GetFriends()             { return _friends;   }
	public ArrayList<User> GetBlockedUsers()        { return _blockedUsers;   }
	public ArrayList<User> GetFriendRequests()		{ return _friendRequests; }
	public ArrayList<User> GetVisibleFriends() 
	{ 
		//create list to be returned
		ArrayList<User> retList = new ArrayList<User>();
		
		//loop through all friends
		for(User user : _friends)
			if(user.GetVisible())
				retList.add(user); 
		
		return retList;
	}
	
	protected Client()
	{
		ServerEvents events = ServerEvents.GetInstance();
		
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
	
	public void Login(String email, String password)
	{
		_server.Login(email, password);
	}
	
	public void CreateUser(String firstName, String lastName, String email, String password, String secretQuestion, String secretAnswer)
	{
		_server.AddUser(email, password, secretQuestion, secretAnswer, firstName, lastName);
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
		_server.AddFriend(_clientUid, user.GetEmail());
		
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
	
	public void AddFriend(String recieverEmail)
	{
		if(LoggedIn())
			_server.AddFriend(_clientUid, recieverEmail);
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
					_email, 
					_secretQuestion,
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
					_email, 
					_secretQuestion,
					_globalVisibility);		
		}
	}
	
	public void SetEmail(String value)
	{
		if(LoggedIn() && _email.toLowerCase() != value)
		{
			_email = value;
			_server.UpdateClientData(
					_clientUid, 
					_firstName, 
					_lastName,
					_email, 
					_secretQuestion,
					_globalVisibility);	
		}
	}
	
	public void SetLocation(double latitude, double longitude)
	{
		if(!(_latitude != latitude || _longitude != longitude))
			return;
		
		_latitude = latitude;
		_longitude = longitude;
		
		//send request to update location to server if logged in
		if(LoggedIn())
		{
			_server.UpdateLocation(
				_clientUid, 
				_latitude,
				_longitude);	
		}
		
		//create a ClientLocationUpdated event
		ServerEvents events = ServerEvents.GetInstance();
		
		//create user to be sent in event
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User(_clientUid,
				_firstName,
				_lastName,
				_email,
				_longitude,
				_latitude,
				-1,
				false,
				false));
		
		//invoke event
		events._InvokeClientLocationUpdated(users);
	}
	
	public void SetGlobalVisibility(boolean value)
	{
		if(LoggedIn() && _globalVisibility != value)
		{
			_globalVisibility = value;
			_server.UpdateClientData(
					_clientUid, 
					_firstName, 
					_lastName,
					_email,  
					_secretQuestion,
					_globalVisibility);	
		}
	}

	
	public void SetMapLocation(PointF location)
	{
		_mapLocation = location;
	}
	

	
	public void SetTimestamp(long timestamp)
	{
		_timestamp = timestamp;
	}
	
	/* Returns whether or not the client has logged in */
	public boolean LoggedIn()
	{
		return _clientUid > 0;
	}
	
	/* returns the singleton instance of Client */
	public static Client GetInstance()
	{
		if( _client == null )
			_client = new Client();
		return _client;
	}	
}
