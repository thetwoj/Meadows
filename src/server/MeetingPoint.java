package server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MeetingPoint 
{
	int _creatorUid;
	int _mid;
	long _time;
	String _description;
	double _imageLocX;
	double _imageLocY;
	String _timeString;
	String _dateTimeString;
	
	public MeetingPoint(int creatorUid, int mid,  long time, String description, double imageLocX, double imageLocY)
	{
		_creatorUid = creatorUid;
		_mid = mid;
		_time = time;
		_description = description;
		_imageLocX = imageLocX;
		_imageLocY = imageLocY;
		
		//Save a string representing the time so it does not need to get recomputed.
		Date d = new Date(time);
		SimpleDateFormat df1 = new SimpleDateFormat("h:mm aaa");
		_timeString = df1.format(d);
		
		SimpleDateFormat df2 = new SimpleDateFormat("h:mm aaa\nMMMM dd, yyyy");
		_dateTimeString = df2.format(d);
	}
	
	public    int 	 	GetMid()		    { return _mid; }
	public 	  String 	GetDescription()    { return _description; }
	public    long 	 	GetTime()	        { return _time; }
	public 	  String    GetDateTimeString() { return _dateTimeString; }
	public    String    GetTimeString()     { return _timeString; }
	public    double 	GetImageLocY()	    { return _imageLocY; }
	public    double 	GetImageLocX()	    { return _imageLocX; }
	

	public boolean ClientIsOwner()
	{
		return Client.GetInstance().GetClientUid() == _creatorUid;
	}
}
