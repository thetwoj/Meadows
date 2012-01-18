package server;

import java.util.EventListener;

/*
 * This interface defines what functions will be required by any UsersUpdatedEventListener
 */
public interface UsersUpdatedListener extends EventListener
{
	public void EventFired(UsersUpdatedEvent event);
}

