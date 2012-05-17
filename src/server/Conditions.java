package server;

import android.database.CursorJoiner.Result;

public class Conditions 
{
	String _liftId = "id=\"LiftsStatus_liftsList";
	
	public Conditions(String result)
	{
		_ParseResult(result);
	}
	
	private void _ParseResult(String result)
	{
		int nextLift = GetNextIndex(0, _liftId, result);;
		while(nextLift != -1)
		{
			
			nextLift = GetNextIndex(nextLift, _liftId, result);
			String lift = GetNextSpanContent(nextLift, result);
			lift = lift + "";
		}
		
	}
	
	//Starting at start, looks through 'whole' until 'part' is found,
	//then returns the index of the beginning of the specified element
	private int GetNextIndex(int start, String part, String whole)
	{
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
		int spanStart = GetNextIndex(start, "<span", whole);
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
