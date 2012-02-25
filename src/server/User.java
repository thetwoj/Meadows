package server;

import android.graphics.PointF;


public class User 
{
	//private variables
	private String _firstName;
	private String _lastName;
	private String _email;
	private double _longitude;
	private double _latitude;
	private int    _uid;
	private long _timestamp;
	//whether or not the client is sharing his location with this user
	private boolean _shareWithUser;
	//whether or not this user  is sharing his location with the client
	private boolean _shareWithClient;
	private PointF  _mapLocation;
	
	
	
	//public getters
	public String 	GetFirstName() 		{ return _firstName; }
	public String 	GetLastName()  		{ return _lastName;  }
	public String 	GetEmail()  		{ return _email;  }
	public double  	GetLatitude()  		{ return _latitude;  }
	public double  	GetLongitude() 		{ return _longitude; }
	public long		GetTimestamp()		{ return _timestamp; }
	public boolean 	GetShareWithUser()	{ return _shareWithUser; }
	public boolean 	GetShareWithClient(){ return _shareWithClient; }
	public boolean  GetVisible()		{ return _shareWithUser && _shareWithClient; }
	public PointF   GetMapLocation()    { return _mapLocation; }
	
	//protected getters
	protected int GetUid() { return _uid; }
	
	//public setters
	public void SetMapLocation      (PointF mapLocation)        { _mapLocation = mapLocation; }
	
	//protected setters
	protected void SetFirstName		(String firstName) 			{ _firstName = firstName; }
	protected void SetLastName		(String lastName) 			{ _lastName = lastName; }
	protected void SetEmail			(String email) 				{ _email = email; }
	protected void SetLongitude		(double longitude) 			{ _longitude = longitude; }
	protected void SetLatitude		(double latitude) 		    { _latitude = latitude; }
	protected void SetShareWithUser	(boolean value)				{ _shareWithUser = value; }
	protected void SetShareWithClient(boolean value) 			{ _shareWithClient = value; }
	
	
	//Constructor for User class which fills local variables with passed values
	public User( int uid,
				 String firstName,
				 String lastName, 
				 String email, 
				 double longitude, 
				 double latitude,
				 long timestamp,
				 boolean shareWithUser,
				 boolean shareWithClient)
	{
		_uid			= uid;
		_firstName 		= firstName;
		_lastName 		= lastName;
		_email 			= email;
		_longitude 		= longitude;
		_latitude 		= latitude;
		_shareWithUser 	= shareWithUser;
		_shareWithClient= shareWithClient;
		_timestamp 		= timestamp;
		_mapLocation    = null;
	}

	
	//toString override so that User.ToString() returns user's name
	public String toString(){ return GetFirstName() + " " + GetLastName(); }
}
