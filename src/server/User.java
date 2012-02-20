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
	//whether or not this user  is sharing his location with the client
	private boolean _locationShared;
	//whether or not the client is sharing his location with this user
	private boolean _shareLocation;
	private PointF  _mapLocation;
	
	
	
	//public getters
	public String 	GetFirstName() 		{ return _firstName; }
	public String 	GetLastName()  		{ return _lastName;  }
	public String 	GetEmail()  		{ return _email;  }
	public double  	GetLatitude()  		{ return _latitude;  }
	public double  	GetLongitude() 		{ return _longitude; }
	public long		GetTimestamp()		{ return _timestamp; }
	public boolean 	GetLocationShared() { return _locationShared; }
	public boolean 	GetShareLocation()  { return _shareLocation; }
	public boolean  GetVisible()		{ return _locationShared && _shareLocation; }
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
	protected void SetLocationShared(boolean locationShared)	{ _locationShared = locationShared; }
	protected void SetShareLocation	(boolean value) 			{ _shareLocation = value; }
	
	
	//Constructor for User class which fills local variables with passed values
	public User( int uid,
				 String firstName,
				 String lastName, 
				 String email, 
				 double longitude, 
				 double latitude,
				 long timestamp,
				 boolean locationShared,
				 boolean shareLocation)
	{
		_uid			= uid;
		_firstName 		= firstName;
		_lastName 		= lastName;
		_email 			= email;
		_longitude 		= longitude;
		_latitude 		= latitude;
		_locationShared = locationShared;
		_shareLocation 	= shareLocation;
		_timestamp 		= timestamp;
		_mapLocation    = null;
	}

	
	//toString override so that User.ToString() returns user's name
	public String toString(){ return GetFirstName() + " " + GetLastName(); }
}
