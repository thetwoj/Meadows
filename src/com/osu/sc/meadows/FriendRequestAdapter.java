package com.osu.sc.meadows;

import java.util.List;
import server.Client;
import server.User;
import server.UsersUpdatedEvent;
import server.UsersUpdatedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FriendRequestAdapter extends ArrayAdapter{
	public final Activity activity;
	public final List friendRequests;

	OnClickListener accept;
	OnClickListener deny;

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

		accept = new OnClickListener(){

			@Override
			public void onClick(View v) {
				Client.GetInstance().AcceptFriendRequest(user);
			}
		};

		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == DialogInterface.BUTTON_POSITIVE)
					Client.GetInstance().BlockUser(user);

				else if(which == DialogInterface.BUTTON_NEGATIVE)
					Client.GetInstance().DenyFriendRequest(user);
			}
		};

		deny = new OnClickListener(){

			@Override
			public void onClick(View v) {
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

		return rowView;
	}


	protected static class FriendView
	{
		protected TextView friendNameText;
		protected ImageButton acceptFriend;
		protected ImageButton denyFriend;
	}
}