package server;

import java.util.EventListener;

/*
 * This interface defines what functions will be required by any MeetingPointsUpdatedListener
 */
public interface MeetingPointsUpdatedListener extends EventListener
{
	public void EventFired(MeetingPointsUpdatedEvent event);
}