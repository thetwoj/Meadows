package server;

public class Lift 
{
	String _name;
	String _status;
	
	public Lift(String name, String status)
	{
		_name = name;
		_status = status;
	}
	
	public String GetName() 	{ return _name; }
	public String GetStatus() 	{ return _status; }
}
