package server;

import java.util.ArrayList;

import android.graphics.PointF;

public class Client
{
	//singleton instance of client
	private static Client _client;
	
	//private variables
	ArrayList<User> _blockedUsers			= new ArrayList<User>();
	ArrayList<User> _friends  				= new ArrayList<User>();
	ArrayList<User> _friendRequests  		= new ArrayList<User>();
	ArrayList<MeetingPoint> _meetingPoints 	= new ArrayList<MeetingPoint>();
	
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
	
	private int NETWORK_PERIOD = 10000;
    private int GPS_PERIOD = 10000;
	
	private Server _server = Server.GetInstance();
	
	
	//public getters
	public String  	GetFirstName() 			{ return _firstName; }
	public String 	GetLastName()  			{ return _lastName;  }
	public String  	GetEmail()  			{ return _email;  }
	public double 	GetLatitude()  		    { return _latitude;  }
	public double  	GetLongitude() 		    { return _longitude; }
	public boolean 	GetGlobalVisibility()	{ return _globalVisibility; }
	public int      GetNetworkPeriod()      { return NETWORK_PERIOD; }
	public int      GetGPSPeriod()          { return GPS_PERIOD; }
	public PointF   GetMapLocation()        { return _mapLocation; }
	public long     GetTimestamp()          { return _timestamp; }
	public String	GetSecretQuestion()		{ return _secretQuestion; }

	public ArrayList<MeetingPoint>	GetMeetingPoints() 	{ return _meetingPoints; }
 	public ArrayList<User> 			GetFriends()        { return _friends;   }
	public ArrayList<User> 			GetBlockedUsers()   { return _blockedUsers;   }
	public ArrayList<User> 			GetFriendRequests()	{ return _friendRequests; }
	public ArrayList<User> 			GetVisibleFriends() 
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
		
		_server.RemoveFriendRequest(_clientUid, user.GetUid());
		
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
	
	public void RemoveFriend(User friend)
	{
		if(!LoggedIn() || !_friends.contains(friend))
			return;
		
		_server.RemoveFriend(_clientUid, friend.GetUid());
		_friends.remove(friend);
		ServerEvents.GetInstance()._InvokeFriendsUpdated(_friends);
	}
	
	public void SetShareLocation(User user, boolean value, CallBack callBack)
	{
		if(LoggedIn())
		{
			user.SetShareWithUser(value);
			_server.SetShareLocation(_clientUid, user.GetUid(), value, callBack);
			ServerEvents.GetInstance()._InvokeFriendsUpdated(GetFriends());
		}
	}

	
	public void CreateMeetingPoint(String description, long time)
	{
		if(LoggedIn())
		{
			Server server = Server.GetInstance();
			server.CreateMeetingPoint(_clientUid, description, time);
			server.RequestUpdateMeetingPoints(_clientUid);
		}
			
	}
	
	public void DeleteMeetingPoint(MeetingPoint meetingPoint)
	{
		if(LoggedIn())
		{
			Server server = Server.GetInstance();
			server.DeleteMeetingPoint(_clientUid, meetingPoint.GetMid());
		}
			
	}
	
	public void UpdateMeetingPoint(MeetingPoint meetingPoint, String description)
	{
		UpdateMeetingPoint(meetingPoint, description, meetingPoint.GetTime());
	}
	
	public void UpdateMeetingPoint(MeetingPoint meetingPoint, long time)
	{
		UpdateMeetingPoint(meetingPoint, meetingPoint.GetDescription(), time);
	}
	
	public void UpdateMeetingPoint(MeetingPoint meetingPoint, String description, long time)
	{
		if(LoggedIn())
		{
			Server server = Server.GetInstance();
			server.UpdateMeetingPoint(_clientUid, description, time, meetingPoint.GetMid());
		}
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
		if(_latitude == latitude && _longitude == longitude)
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
	
	public void SetGPSPeriod(int period)
	{
		NETWORK_PERIOD = period;
	}
	
	public void SetNetworkPeriod(int period)
	{
		GPS_PERIOD = period;
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
