package server;

import java.util.ArrayList;


public class Conditions 
{
	ArrayList<Lift> _lifts = new ArrayList<Lift>(); 
	ArrayList<ParkingLot> _lots = new ArrayList<ParkingLot>(); 
	int _temp = Integer.MAX_VALUE;
	
	String _liftId = "id=\"LiftsStatus_liftsList";
	String _lotId = "id=\"parking";
	String _spanId = "<span";
	String _tbodyId = "<tbody";
	String _tbodyEndId = "</tbody";
	String _trId = "<tr";
	String _tdId = "<td";
	String _tempId = "class=\"conditions-temp\"";
	
	public Conditions(String result)
	{
		try{ _ParseResult(result); }
		catch(Exception e){}
	}
	
	public ArrayList<Lift> 			GetLifts() 		{ return _lifts; }
	public ArrayList<ParkingLot>  	GetParkingLots()	{ return _lots; }
	public int 						GetTemp()		{ return _temp; }
	
	private void _ParseResult(String result)
	{
		//parse lifts
		int nextLift = GetNextIndex(0, _liftId, result);
		while(nextLift != -1)
		{
			String name = GetNextSpanContent(nextLift, result);
			String status = GetNextTdContent(nextLift, result);
			_lifts.add(new Lift(name, status));
			nextLift = GetNextIndex(nextLift, _liftId, result);
		}
		
		//parse parking lots
		int nextLot = GetNextIndex(0, _lotId, result);
		nextLot = GetNextIndex(nextLot, _tbodyId, result);
		nextLot = GetNextIndex(nextLot, _trId, result);
		int bodyEnd = GetNextIndex(nextLot, _tbodyEndId, result);
		while(nextLot != -1 && nextLot < bodyEnd)
		{
			String name = GetNextSpanContent(nextLot, result);
			String status = GetNextTdContent(nextLot, result);
			_lots.add(new ParkingLot(name, status));
			nextLot = GetNextIndex(nextLot, _trId, result);
		}
		
		//parse temp
		int tempStart = GetNextIndex(0, _tempId, result);
		String temp = GetNextSpanContent(tempStart, result);
		_temp = ParseTemp(temp);
	}
	
	private int ParseTemp(String temp)
	{
		String num = "";
		for(int i = 0; i < temp.length(); i++)
		{
			char charI = temp.charAt(i);
			if(charI >= '0' && charI <= '9')
				num += charI;
		}
		return Integer.parseInt(num);
	}
	
	//Starting at start, looks through 'whole' until 'part' is found,
	//then returns the index of the beginning of the specified element
	private int GetNextIndex(int start, String part, String whole)
	{
		if (start < 0) return -1;
		//Starting at start, loop through whole looking for part
		for(int i = start; i < whole.length(); i++)
		{
			//attempt to match part
			for(int ii = 0; ii + i < whole.length() && ii <= part.length(); ii++)
			{
				//if match is found
				if(ii == part.length())
					return ii + i;
				
				//if no match found
				if(whole.charAt(i+ii) != part.charAt(ii))
					break;
			}
		}
		return -1;
	}
	
	//Returns the content of the next span
	private String GetNextSpanContent(int start, String whole)
	{
		return GetNextContent(start, _spanId, whole);
	}
	
	private String GetNextTdContent(int start, String whole)
	{
		return GetNextContent(start, _tdId, whole);
	}
	
	private String GetNextContent(int start, String part,String whole)
	{
		int spanStart = GetNextIndex(start, part, whole);
		if(spanStart == -1)
			return "";
			
		//iterate to char following ">"
		while(whole.charAt(spanStart++) != '>'){}
		
		String result = "";
		for(int i = spanStart; i < whole.length(); i++)
		{
			//if end of span reached
			if(whole.charAt(i) == '<')
				break;
			
			result += whole.charAt(i);			
		}
		return result;
	}
}
