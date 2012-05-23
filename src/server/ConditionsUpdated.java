package server;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ConditionsUpdated extends EventObject {
	
	private Conditions _conditions;
	
	public ConditionsUpdated(Object source, Conditions conditions)
	{
		super(source);
		_conditions = conditions;
	}

	public Conditions GetConditions()
	{
		return _conditions;
	}
}
