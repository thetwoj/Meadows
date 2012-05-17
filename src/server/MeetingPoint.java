package server;

import java.text.DateFormat;
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
		SimpleDateFormat df = new SimpleDateFormat("HH:mm aaa");
		_timeString = df.format(d);
	}
	
	protected int 	 	GetMid()		 { return _mid; }
	public 	  String 	GetDescription() { return _description; }
	public    long 	 	GetTime()	     { return _time; }
	public    String    GetTimeString()  { return _timeString; }
	public    double 	GetImageLocY()	 { return _imageLocY; }
	public    double 	GetImageLocX()	 { return _imageLocX; }
}
