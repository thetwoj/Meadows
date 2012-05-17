package com.osu.sc.meadows;

import java.util.List;

import server.CallBack;
import server.Client;
import server.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

@SuppressWarnings("rawtypes")
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
		// Set up a new View to hold the friend at 'position' in the list
		View rowView = convertView;
		FriendView fView = null;

		// Create a listener for the "Visible" button unique to each friend
		OnCheckedChangeListener fVisible;

		// Get the user at the current 'position' in the list
		final User user = (User)friends.get(position);

		if(rowView == null)
		{
			// Get a new instance of the row layout view
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.friend_list_item, null);

			// Hold the view objects in an object, so they don't need to be re-fetched
			fView = new FriendView();
			fView.friendNameText = (TextView) rowView.findViewById(R.id.friendNameTV);
			fView.friendVisible = (ToggleButton) rowView.findViewById(R.id.friendVisibleToggle);

			// Cache the view objects in the tag, so they can be re-accessed later
			rowView.setTag(fView);
		} 
		else 
		{
			fView = (FriendView) rowView.getTag();
		}

		// When visibility to a certain friend is toggled
		fVisible = new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
				// Disable togglebutton until callBack is received
				buttonView.setClickable(false);

				CallBack callBack = new CallBack(){
					public void Invoke(String result)
					{
						// Enable togglebutton after callBack received
						buttonView.setClickable(true);
					}
				};
				// SetShareLocation will execute callBack after server transaction is complete
				Client.GetInstance().SetShareLocation(user, isChecked, callBack);
			}
		};

		// Initialize alert that will confirm the user wishes to remove given friend
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		// Set a listener for long clicks on friends so that friends can be removed
		OnLongClickListener rowLongClick = new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// Initialize alert, confirm that the user wants to remove the friend they have selected
				builder.setMessage("Remove " + user.GetFirstName() + " " + user.GetLastName() + " from friends?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						// If removal is confirmed, remove given user from friends
						Client.GetInstance().RemoveFriend(user);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						// If remove is declined, close dialog and do nothing else
						dialog.cancel();
					}
				});
				// Create and show alert
				final AlertDialog alert = builder.create();
				alert.show();

				// Return true to inform onLongClick that the callback was consumed
				return true;
			}

		};

		// Set onLongClickListener to each rowView so that friends can be removed
		rowView.setOnLongClickListener(rowLongClick);

		// Transfer the stock data from the data object to the view objects
		fView.friendNameText.setText("");
		fView.friendNameText.append(user.GetFirstName() + " " + user.GetLastName());

		// Set the checked state of friend visibility to match current Client info
		fView.friendVisible.setChecked(user.GetShareWithUser());
		fView.friendVisible.setOnCheckedChangeListener(fVisible);

		// Return the completed view
		return rowView;
	}

	protected static class FriendView
	{
		protected TextView friendNameText;
		protected ToggleButton friendVisible;
	}
}