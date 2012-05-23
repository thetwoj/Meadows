package server;

import java.util.ArrayList;
import java.util.EventObject;

/*
 * This is an event that will be fired whenever the server successfully logs in.
 */
@SuppressWarnings("serial")
public class UsersUpdatedEvent extends EventObject
{
	private ArrayList<User> _users;
	
	public UsersUpdatedEvent(Object source, ArrayList<User> users)
	{
		super(source);
		_users = users;
	}
	
	public ArrayList<User> GetUsers()
	{
		return _users;
	}
}
