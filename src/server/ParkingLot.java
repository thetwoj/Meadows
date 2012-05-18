package server;

public class ParkingLot 
{
	String _name;
	String _status;
	
	public ParkingLot(String name, String status)
	{
		_name = name;
		_status = status;
	}
	
	public String GetName() 	{ return _name; }
	public String GetStatus() 	{ return _status; }
}
