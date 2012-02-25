package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/* 
 * This is the server class used to interact with the database containing all user data.
 * This class will not be interacted with directly outside of this library, but rather 
 */
public class Server 
{	
	ArrayList<HttpPostTask> tasks = new ArrayList<HttpPostTask>();
	//private singleton instance of Server
	private static Server _server;
	/* Returns the singleton instance of the server */
	protected static Server GetInstance()
	{		
		if( _server == null )
			_server = new Server();
		return _server;
	}
	
	
	//Creates a new user in the server with the given data
	protected void AddUser(final String email, 
			final String password, 
			String secretQuestion, 
			String secretAnswer, 
			String firstName, 
			String lastName)
	{
		//parse firstName to remove invalid input and provide proper formatting
		firstName = firstName.toLowerCase();
		String temp = "";
		//ensure values between a and z
		for(int i = 0; i < firstName.length(); i++)
			if(firstName.charAt(i) >= 'a' && firstName.charAt(i) <= 'z')
				temp += firstName.charAt(i);
		//capitalize first letter
		firstName = Character.toUpperCase(temp.charAt(0)) + temp.substring(1);
		
		
		//parse lastName to remove invalid input and provide proper formatting
		lastName = lastName.toLowerCase();
		temp = "";
		//ensure values between a and z
		for(int i = 0; i < lastName.length(); i++)
			if(lastName.charAt(i) >= 'a' && lastName.charAt(i) <= 'z')
				temp += lastName.charAt(i);
		//capitalize first letter
		lastName = Character.toUpperCase(temp.charAt(0)) + temp.substring(1);
		
		//create post parameters to send to server
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("firstName", firstName));
		parameters.add(new BasicNameValuePair("lastName", lastName));
		parameters.add(new BasicNameValuePair("email", email));
		parameters.add(new BasicNameValuePair("password", password));
		parameters.add(new BasicNameValuePair("secretQuestion", secretQuestion));
		parameters.add(new BasicNameValuePair("secretAnswer", secretAnswer));
		new HttpPostTask("CreateUser.php", parameters, new CallBack(){
			public void Invoke(String result)
			{
				Client.GetInstance().Login(email, password);
			}
		}).execute();
	}
	
	protected void AddFriend(int senderUid, String recieverEmail)
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("senderUid", Integer.toString(senderUid)));
		parameters.add(new BasicNameValuePair("recieverEmail", recieverEmail));
		new HttpPostTask("AddFriend.php", parameters).execute();
	}
	
	protected void RemoveFriend(int clientUid, int friendUid)
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		parameters.add(new BasicNameValuePair("friendUid", Integer.toString(friendUid)));
		new HttpPostTask("RemoveFriend.php", parameters).execute();
	}
	
	protected void BlockUser(int clientUid, int blockedUid)
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		parameters.add(new BasicNameValuePair("blockedUid", Integer.toString(blockedUid)));
		new HttpPostTask("BlockUser.php", parameters).execute();
	}
	
	protected void UnblockUser(int clientUid, int blockedUid)
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		parameters.add(new BasicNameValuePair("blockedUid", Integer.toString(blockedUid)));
		new HttpPostTask("UnblockUser.php", parameters).execute();
	}
	
	protected void RemoveFriendRequest(int senderUid, int recieverUid)
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("senderUid", Integer.toString(senderUid)));
		parameters.add(new BasicNameValuePair("recieverUid", Integer.toString(recieverUid)));
		new HttpPostTask("RemoveFriendRequest.php", parameters).execute();
	}
	
	protected void SetShareLocation(int clientUid, int friendUid, boolean value, CallBack callBack)
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		parameters.add(new BasicNameValuePair("friendUid", Integer.toString(friendUid)));
		parameters.add(new BasicNameValuePair("value",     Boolean.toString(value)));
		ArrayList<CallBack> callBacks = new ArrayList<CallBack>();
		callBacks.add(callBack);
		new HttpPostTask("SetShareLocation.php", parameters, callBacks).execute();
	}
	
	protected boolean allowRequestFriends = true;
	protected void RequestUpdateFriends(int clientUid)
	{	
		//don't create multiple requests
		if( !allowRequestFriends )
			return;
					

		allowRequestFriends = false;
		//query server requesting users
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		
		new HttpPostTask("GetFriendData.php", parameters, new CallBack(){
			public void Invoke(String result)
			{
				ArrayList<User> users = _ParseUsers(result);
				
				//publish event
				ServerEvents serverEvents = ServerEvents.GetInstance();
				serverEvents._InvokeFriendsUpdated(users);
				
				Server.GetInstance().allowRequestFriends = true;
			}
		}).execute();		
	}
	
	protected boolean allowRequestBlocked = true;
	protected void RequestUpdateBlockedUsers(int clientUid)
	{
		//don't create multiple requests
		if( !allowRequestBlocked )
			return;
				
		//query server requesting users
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		
		new HttpPostTask("GetBlockedUserData.php", parameters, new CallBack(){
			public void Invoke(String result)
			{
				ArrayList<User> users = _ParseUsers(result);
				
				//publish event
				ServerEvents serverEvents = ServerEvents.GetInstance();
				serverEvents._InvokeBlockedUsersUpdated(users);
				
				Server.GetInstance().allowRequestBlocked = true;
			}
		}).execute();
	}
	
	protected boolean allowRequestRequests = true;
	protected void RequestUpdateFriendRequests(int clientUid)
	{
		//don't create multiple requests
		if( !allowRequestRequests )
			return;
				
		//query server requesting users
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		
		new HttpPostTask("GetFriendRequestData.php", parameters, new CallBack(){
			public void Invoke(String result)
			{
				ArrayList<User> users = _ParseUsers(result);
				
				//publish event
				ServerEvents serverEvents = ServerEvents.GetInstance();
				serverEvents._InvokeFriendRequestsUpdated(users);
				
				Server.GetInstance().allowRequestRequests = true;
			}
		}).execute();
	}
	
	protected void Login(String email, String password)
	{
		//TODO: Hash password here
		
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("email", email));
		parameters.add(new BasicNameValuePair("password", password));
		new HttpPostTask("Login.php", parameters, new CallBack(){
			public void Invoke(String result)
			{
				ServerEvents serverEvents = ServerEvents.GetInstance();
				
				//if login unsuccessful
				if(result == "")
					serverEvents._InvokeLoginFailure();
				
				else //parse client info, save it to client, invoke ServerEvents.LoginSuccess()
				{
					JSONObject json;
					try 
					{
						//parse client info and store it
						Client client = Client.GetInstance();
						result = _SeparateUsers(result).get(0);
						json = new JSONObject(result);
						client._firstName 		 = json.getString("firstName");
						client._lastName 		 = json.getString("lastName");
						client._email			 = json.getString("email");
						client._secretQuestion	 = json.getString("secretQuestion");
						client._latitude 	     = json.getDouble("latitude");
					 	client._longitude	     = json.getDouble("longitude");
						client._timestamp 		 = json.getLong("time");
						client._clientUid		 = json.getInt("uid");
						client._globalVisibility = json.getInt("visible") == 1;
						
						serverEvents._InvokeLoginSuccess();
					} 
					
					catch (JSONException e) 
					{
						serverEvents._InvokeLoginFailure();
					}		
				}
			}
		}).execute();
	}
	
	/* Updates the client's data with the given variables */
	protected void UpdateClientData(
			int clientUid,
			String firstName,
			String lastName,
			String email, 
			String secretQuestion,
			boolean visible )
	{
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		parameters.add(new BasicNameValuePair("firstName", firstName));
		parameters.add(new BasicNameValuePair("lastName", lastName));
		parameters.add(new BasicNameValuePair("email", email));
		parameters.add(new BasicNameValuePair("secretQuestion", secretQuestion));
		parameters.add(new BasicNameValuePair("visible", Boolean.toString(visible)));
		new HttpPostTask("UpdateClientData.php", parameters).execute();
	}
	
	/* Updates the location of the client with the given lat/long and the current time */
	protected void UpdateLocation(int clientUid, double latitude, double longitude)
	{
		//Calendar object used to get current time
		Calendar now = Calendar.getInstance();
		
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("clientUid", Integer.toString(clientUid)));
		parameters.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
		parameters.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
		parameters.add(new BasicNameValuePair("time", Long.toString(now.getTimeInMillis())));
		new HttpPostTask("UpdateLocation.php", parameters).execute();
	}
	
	
	/* Takes a raw server response, and parses it into an array of Users */
	private ArrayList<User> _ParseUsers(String httpResult)
	{
		//parse results into list of users' json info
		ArrayList<String> userStrings = _SeparateUsers(httpResult);
		
		
		//parse user json to create User objects
		ArrayList<User> users = new ArrayList<User>();
		for(String userJson : userStrings)
		{
			JSONObject json;
			//parse the user's information
			try
			{ 
				json = new JSONObject(userJson);
				
				String firstName 		= json.getString("firstName");
				String lastName 		= json.getString("lastName");
				String email			= json.getString("email");
				double latitude 	    = json.getDouble("latitude");
			    double longitude 	    = json.getDouble("longitude");
				long timestamp 			= json.getLong("time");
				int uid 				= json.getInt("uid");
				boolean shareLocation	= json.getInt("shareLocation") == 1;
				
				//determine whether or not this user will be visible
				boolean locationShared 		 = json.getInt("locationShared") == 1;
				boolean globalLocationShared = json.getInt("visible") == 1;
				locationShared = locationShared && globalLocationShared;
				
				//create user
				User user = new User(uid,
						firstName,
						lastName,
						email,
						longitude,
						latitude,
						timestamp,
						locationShared,
						shareLocation);
				
				users.add(user);
			}
			catch(Exception e){ }
		}
		
		return users;
	}
	
	/*
	 * separates a string containing many users' json information clumped together
	 *into a list of strings, each containing 1 user's information.
	 *Returned strings are easily parsed as json.
	 */
	private ArrayList<String> _SeparateUsers(String result)
	{
		ArrayList<String> retList = new ArrayList<String>();
		
		int start = 0;
		for(int i = 0; i < result.length(); i++)
		{
			if(result.charAt(i) == '{') //start of user
				start = i;
			if(result.charAt(i) == '}') //end of user
				retList.add(result.substring(start, i+1));
		}
		
		return retList;
	}
	
	
	
}
