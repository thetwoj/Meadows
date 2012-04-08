package server;
import java.util.ArrayList;
import java.util.EventObject;

/*
 * This is an event that will be fired whenever the server successfully logs in.
 */
@SuppressWarnings("serial")
public class MeetingPointsUpdatedEvent extends EventObject
{
	private ArrayList<MeetingPoint> _meetingPoints;
	
	public MeetingPointsUpdatedEvent(Object source, ArrayList<MeetingPoint> meetingPoints)
	{
		super(source);
		_meetingPoints = meetingPoints;
	}
	
	public ArrayList<MeetingPoint> GetMeetingPoints()
	{
		return _meetingPoints;
	}
}
