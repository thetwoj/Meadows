package server;
import java.util.EventListener;

public interface ConditionsListener extends EventListener
{
	public void EventFired(ConditionsUpdated event);
}
