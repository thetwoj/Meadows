package server;

public class MeetingPoint 
{
	int _creatorUid;
	int _mid;
	long _time;
	String _description;
	long _latitude;
	long _longitude;
	
	protected int 	 GetMid()		 { return _mid; }
	public 	  String GetDescription(){ return _description; }
	public    long 	 GetTime()	     { return _time; }
	public    long 	 GetLongitude()	 { return _longitude; }
	public    long 	 GetLatitude()	 { return _latitude; }
}
