package server;


public class User 
{
	//private variables
	private String _firstName;
	private String _lastName;
	private String _phoneNumber;
	private String _longitude;
	private String _latitude;
	private int    _uid;
	private long _timestamp;
	private boolean _isBlocked;
	//whether or not this user  is sharing his location with the client
	private boolean _locationShared;
	//whether or not the client is sharing his location with this user
	private boolean _shareLocation;
	
	
	
	//public getters
	public String 	GetFirstName() 		{ return _firstName; }
	public String 	GetLastName()  		{ return _lastName;  }
	public String 	GetPhoneNumber()  	{ return _phoneNumber;  }
	public String 	GetLatitude()  		{ return _latitude;  }
	public String 	GetLongitude() 		{ return _longitude; }
	public long		GetTimestamp()		{ return _timestamp; }
	public boolean 	GetLocationShared() { return _locationShared; }
	public boolean 	GetIsBlocked()  	{ return _isBlocked; }
	public boolean 	GetShareLocation()  { return _shareLocation; }
	public boolean  GetUserVisible()	{ return _locationShared && _shareLocation && !_isBlocked; }
	
	//protected getters
	protected int GetUid() { return _uid; }
	
	
	//protected setters
	protected void SetFirstName		(String firstName) 			{ _firstName = firstName; }
	protected void SetLastName		(String lastName) 			{ _lastName = lastName; }
	protected void SetPhoneNumber	(String phoneNumber) 		{ _phoneNumber = phoneNumber; }
	protected void SetLongitude		(String longitude) 			{ _longitude = longitude; }
	protected void SetLatitude		(String latitude) 			{ _latitude = latitude; }
	protected void SetIsBlocked		(boolean value)				{ _isBlocked = value; }
	protected void SetLocationShared(boolean locationShared)	{ _locationShared = locationShared; }
	protected void setShareLocation	(boolean value) 			{ _shareLocation = value; }
	
	
	//Constructor for User class which fills local variables with passed values
	public User( int uid,
				 String firstName,
				 String lastName, 
				 String phoneNumber, 
				 String longitude, 
				 String latitude,
				 long timestamp,
				 boolean isBlocked,
				 boolean locationShared,
				 boolean shareLocation)
	{
		_uid			= uid;
		_firstName 		= firstName;
		_lastName 		= lastName;
		_phoneNumber 	= phoneNumber;
		_longitude 		= longitude;
		_latitude 		= latitude;
		_isBlocked		= isBlocked;
		_locationShared = locationShared;
		_shareLocation 	= shareLocation;
		_timestamp 		= timestamp;
	}
	
	//toString override so that User.ToString() returns user's name
	public String toString(){ return GetFirstName() + " " + GetLastName(); }
}
