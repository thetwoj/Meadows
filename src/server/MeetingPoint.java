package server;

public class MeetingPoint 
{
	int _creatorUid;
	int _mid;
	long _time;
	String _description;
	double _latitude;
	double _longitude;
	
	public MeetingPoint(int creatorUid, int mid,  long time, String description, double latitude, double longitude)
	{
		_creatorUid = creatorUid;
		_mid = mid;
		_time = time;
		_description = description;
		_latitude = latitude;
		_longitude = longitude;
	}
	
	protected int 	 	GetMid()		 { return _mid; }
	public 	  String 	GetDescription() { return _description; }
	public    long 	 	GetTime()	     { return _time; }
	public    double 	GetLongitude()	 { return _longitude; }
	public    double 	GetLatitude()	 { return _latitude; }
}
