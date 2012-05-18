package com.osu.sc.meadows;

import java.util.List;

import server.CallBack;
import server.Client;
import server.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class FriendRequestAdapter extends ArrayAdapter
{
	public final Activity activity;
	public final List friendRequests;

	OnClickListener accept;
	OnClickListener deny;

	ProgressDialog acceptFriend;
	ProgressDialog denyFriend;

	@SuppressWarnings("unchecked")
	public FriendRequestAdapter(Activity activity, List objects)
	{
		super(activity, R.layout.friend_request_list_item , objects);
		this.activity = activity;
		this.friendRequests = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		FriendView fRView = null;
		final User user = (User)friendRequests.get(position);

		if(rowView == null)
		{
			// Get a new instance of the row layout view
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.friend_request_list_item, null);

			// Hold the view objects in an object,
			// so they don't need to be re-fetched
			fRView = new FriendView();
			fRView.friendNameText = (TextView) rowView.findViewById(R.id.friendRequestName);

			fRView.acceptFriend = (ImageButton) rowView.findViewById(R.id.acceptFriendRequestButton);
			fRView.denyFriend = (ImageButton) rowView.findViewById(R.id.denyFriendRequestButton);

			// Cache the view objects in the tag,
			// so they can be re-accessed later
			rowView.setTag(fRView);
		} 
		else 
		{
			fRView = (FriendView) rowView.getTag();
		}

		// Create dialog on friend request denial to allow user the option to block requesting user
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// If yes, block user
				if(which == DialogInterface.BUTTON_POSITIVE)
					Client.GetInstance().BlockUser(user);
				// If no, simply deny the friend request
				else if(which == DialogInterface.BUTTON_NEGATIVE)
					Client.GetInstance().DenyFriendRequest(user);
			}
		};

		// When a friend request is accepted
		accept = new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				// Create loading message that lets the user know the friend request acceptance is pending
				////acceptFriend = ProgressDialog.show(getContext(), "", "Accepting friend request, please wait...", true);

				// Accept the friend request
				Client.GetInstance().AcceptFriendRequest(user);

				/*
				CallBack callBack = new CallBack()
				{
					public void Invoke(String result)
					{
						// Dismiss the loading message on a successful 
						// callback from setting visibility to new friend
						acceptFriend.dismiss();
					}
				};
				// Make sure that visibility to new friend is set to true
				Client.GetInstance().SetShareLocation(user, true, callBack);
				 */
			}
		};

		// When a friend request is denied
		deny = new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				final AlertDialog.Builder alert = new AlertDialog.Builder(activity);

				alert.setIcon(android.R.drawable.ic_dialog_alert);
				alert.setTitle("Deny Request");
				alert.setMessage("Do you want to block this user from sending you further requests?");
				alert.setPositiveButton("Yes", dialogClickListener);
				alert.setNegativeButton("No", dialogClickListener);
				alert.setNeutralButton("Cancel", dialogClickListener);

				alert.show();
			}
		};

		fRView.friendNameText.setText("");
		fRView.friendNameText.append(user.GetFirstName() + " " + user.GetLastName());

		fRView.acceptFriend.setOnClickListener(accept);
		fRView.denyFriend.setOnClickListener(deny);

		// Return the populated list element
		return rowView;
	}


	protected static class FriendView
	{
		protected TextView friendNameText;
		protected ImageButton acceptFriend;
		protected ImageButton denyFriend;
	}
}