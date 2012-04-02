package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;

/*
 * An HTTPPost task will execute an Http request in a new thread,
 * then, upon Http request completion, will execute the given CallBack 
 * in the UI thread.
 */
public class HttpPostTask extends AsyncTask<String, Boolean, String>
{
	//This contains the URL of the server we'll be interacting with
	final String baseUrl = "http://people.oregonstate.edu/~schmitje/Meadows/";
	final String _url;
	final ArrayList<NameValuePair> _params;
	final ArrayList<CallBack> _callBacks;
	
	public HttpPostTask(String location, ArrayList<NameValuePair> params)
	{
		this(location, params, (ArrayList<CallBack>)null);
	}
	
	@SuppressWarnings("serial")
	public HttpPostTask(String location, ArrayList<NameValuePair> params, final CallBack callBack)
	{
		this(location, params, new ArrayList<CallBack>(){{add(callBack);}});
	}
	
	/*
	 * This constructor stores the given variables
	 */
	public HttpPostTask(String location, ArrayList<NameValuePair> params, ArrayList<CallBack> callBacks)
	{
		_url = baseUrl + location;
		_params = params;
		_callBacks = callBacks;
	}


	@Override
	protected String doInBackground(String... garbage) 
	{
		//This is the result which will be populated with whatever
		//JSON is returned from our HTTP Post request.
		String result = "";
		
		//set HTTP connection to timeout after 3 seconds
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		
		//create HTTP connection
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(_url);

		boolean connectionMade = false;
		while(!connectionMade)
		{
			try
			{			
				//set post parameters			
				post.setEntity(new UrlEncodedFormEntity(_params));
	
				//execute http request
				HttpResponse response = client.execute(post);
				
				//parse response
				
				try 
				{
					StringBuilder stringBuilder = new StringBuilder();
					String line = "";
					BufferedReader reader
						= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					while((line = reader.readLine()) != null)
						stringBuilder.append(line+"\n");
					result = stringBuilder.toString();	
				} 
				catch (Exception e) 
				{
					result = "";
				}
				
				
				
					
				connectionMade = true;
			}
			//if connection could not be made, try again. Is this dangerous?
			catch(SocketTimeoutException e)
			{
				try {Thread.sleep(15000);} 
				catch (InterruptedException e1) { }
			} 
			catch (ClientProtocolException e) { } catch (UnsupportedEncodingException e) {
				try {Thread.sleep(15000);} 
				catch (InterruptedException e1) { }
			} catch (IOException e) {
				try {Thread.sleep(15000);} 
				catch (InterruptedException e1) { }
			} 
		}
		
		return result;		
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		if(_callBacks != null)
			for(CallBack callBack : _callBacks)
				callBack.Invoke(result);
	}
	
}
