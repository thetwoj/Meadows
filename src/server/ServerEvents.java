package server;

import java.util.ArrayList;

public class ServerEvents 
{
	//Private singleton instance of ServerEvents
	private static final ServerEvents _serverEvents = new ServerEvents();
	//overloaded constructor to prevent object missuse
	protected ServerEvents(){}
	
	//collections of event listeners
	ArrayList<UsersUpdatedListener> _friendsUpdatedListeners 		= new ArrayList<UsersUpdatedListener>();
	ArrayList<UsersUpdatedListener> _blockedUsersUpdatedListeners 	= new ArrayList<UsersUpdatedListener>();
	ArrayList<UsersUpdatedListener> _friendRequestsUpdatedListeners = new ArrayList<UsersUpdatedListener>();
	ArrayList<UsersUpdatedListener> _loginFailureListeners 			= new ArrayList<UsersUpdatedListener>();
	ArrayList<UsersUpdatedListener> _loginSuccessListeners 			= new ArrayList<UsersUpdatedListener>();
	
	
	public void AddFriendsUpdatedListener(UsersUpdatedListener listener)
	{
		_friendsUpdatedListeners.add(listener);
	}
	
	public void RemoveFriendsUpdatedListener(UsersUpdatedListener listener)
	{
		_friendsUpdatedListeners.remove(listener);
	}
	
	protected void _InvokeFriendsUpdated(ArrayList<User> users)
	{
		//create the event
		UsersUpdatedEvent event = new UsersUpdatedEvent(this, users);
		//execute each listener passing the new event
		for(UsersUpdatedListener listener : _friendsUpdatedListeners)
			listener.EventFired(event);
	}
	
	
	
	public void AddBlockedUsersUpdatedListener(UsersUpdatedListener listener)
	{
		_blockedUsersUpdatedListeners.add(listener);
	}
	
	public void RemoveBlockedUsersUpdatedListener(UsersUpdatedListener listener)
	{
		_blockedUsersUpdatedListeners.remove(listener);
	}
	
	protected void _InvokeBlockedUsersUpdated(ArrayList<User> users)
	{
		//create the event
		UsersUpdatedEvent event = new UsersUpdatedEvent(this, users);
		//execute each listener passing the new event
		for(UsersUpdatedListener listener : _blockedUsersUpdatedListeners)
			listener.EventFired(event);
	}
	
	

	public void AddFriendRequestsUpdatedListener(UsersUpdatedListener listener)
	{
		_friendRequestsUpdatedListeners.add(listener);
	}
	
	public void RemoveFriendRequestsUpdatedListener(UsersUpdatedListener listener)
	{
		_friendRequestsUpdatedListeners.remove(listener);
	}
	
	protected void _InvokeFriendRequestsUpdated(ArrayList<User> users)
	{
		//create the event
		UsersUpdatedEvent event = new UsersUpdatedEvent(this, users);
		//execute each listener passing the new event
		for(UsersUpdatedListener listener : _friendRequestsUpdatedListeners)
			listener.EventFired(event);
	}
	
	

	public void AddLoginSuccessListener(UsersUpdatedListener listener)
	{
		_loginSuccessListeners.add(listener);
	}
	
	public void RemoveLoginSuccessListener(UsersUpdatedListener listener)
	{
		_loginSuccessListeners.remove(listener);
	}
	
	protected void _InvokeLoginSuccess(ArrayList<User> users)
	{
		//create the event
		UsersUpdatedEvent event = new UsersUpdatedEvent(this, users);
		//execute each listener passing the new event
		for(UsersUpdatedListener listener : _loginSuccessListeners)
			listener.EventFired(event);
		
		//after logging in, we will always want to get the relevent users
		//to the client
		Client client = Client.GetInstance();
		client.RequestUpdateFriends();
		client.RequestUpdateBlockedUsers();
		client.RequestUpdateFriendRequests();
	}
	
	

	public void AddLoginFailureListener(UsersUpdatedListener listener)
	{
		_loginFailureListeners.add(listener);
	}
	
	public void RemoveLoginFailureListener(UsersUpdatedListener listener)
	{
		_loginFailureListeners.remove(listener);
	}
	
	protected void _InvokeLoginFailure()
	{
		//create the event
		UsersUpdatedEvent event = new UsersUpdatedEvent(this, null);
		//execute each listener passing the new event
		for(UsersUpdatedListener listener : _loginFailureListeners)
			listener.EventFired(event);
	}
	
	
	
	public static ServerEvents GetInstance()
	{
		return _serverEvents;
	}
}

