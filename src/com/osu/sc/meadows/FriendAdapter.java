package com.osu.sc.meadows;

import java.util.List;

import server.CallBack;
import server.Client;
import server.User;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
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