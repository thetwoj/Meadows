package com.osu.sc.meadows;

import java.util.List;

import server.Client;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class FriendAdapter extends ArrayAdapter{
	public final Activity activity;
	public final List friends;

	@SuppressWarnings("unchecked")
	public FriendAdapter(Activity activity, List objects)
	{
		super(activity, R.layout.friend_list_item , objects);
		this.activity = activity;
		this.friends = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		FriendView fView = null;
		OnClickListener acceptListener;
		final User user = (User)friends.get(position);

		if(rowView == null)
		{
			// Get a new instance of the row layout view
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.friend_list_item, null);

			// Hold the view objects in an object,
			// so they don't need to be re-fetched
			fView = new FriendView();
			fView.friendButton = (Button) rowView.findViewById(R.id.friendListButton);

			// Cache the view objects in the tag,
			// so they can be re-accessed later
			rowView.setTag(fView);
		} 
		else 
		{
			fView = (FriendView) rowView.getTag();
		}
		
		acceptListener = new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Client.GetInstance().AcceptFriendRequest(user);
			}
		};

		// Transfer the stock data from the data object
		// to the view objects
		User currentFriend = (User) friends.get(position);
		fView.friendButton.append(currentFriend.GetFirstName() + " " + currentFriend.GetLastName());
		fView.friendButton.setOnClickListener(acceptListener);

		return rowView;
	}


	protected static class FriendView
	{
		protected Button friendButton;
	}
}