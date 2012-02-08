package com.osu.sc.mapframework;

import com.osu.sc.meadows.GeoMapActivity;
import com.osu.sc.meadows.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GeoImageViewTouch extends ImageViewTouch
{
	protected GeoMapActivity geoMapActivity;
	private static final int MARKER_OFFSET_X = 14;
	private static final int MARKER_OFFSET_Y = 33;
	private final Bitmap userBmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
    private final Bitmap meetingBmp = BitmapFactory.decodeResource(getResources(), R.drawable.meeting_icon);
    private final Bitmap friendBmp = BitmapFactory.decodeResource(getResources(), R.drawable.friend_icon);

	public GeoImageViewTouch(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	protected boolean checkIfTappedFriend(PointF imageLoc)
	{
		return false;
	}
	
	protected boolean checkIfTappedMeeting(PointF imageLoc)
	{
		int wd = meetingBmp.getWidth();
		int ht = meetingBmp.getHeight();
		//Convert the screen location to image coordinates.
		for(MeetingPoint mPoint : this.geoMapActivity.getMeetingPoints())
		{
			//Shift the meeting point back by the bitmap size.
			float origX = mPoint.mapLoc.x - MARKER_OFFSET_X;
			float origY = mPoint.mapLoc.y - MARKER_OFFSET_Y;
			
			if(imageLoc.x < origX || imageLoc.x > (origX + wd) || imageLoc.y < origY || imageLoc.y > (origY + ht))
				continue;
			
			//Found a meeting point, notify the Activity and return true.
			this.geoMapActivity.meetingSelected(mPoint);
			return true;
		}
		
		return false;
	}
	
	protected boolean checkIfTappedUser(PointF imageLoc)
	{
	    //Ensure that there is a valid map location point.
		PointF userMapLoc = this.geoMapActivity.getUserMapLoc();
		if(userMapLoc == null)
			return false;
		
		int wd = userBmp.getWidth();
		int ht = userBmp.getHeight();
		
		//Shift the meeting point back by the bitmap size.
		float origX = userMapLoc.x - MARKER_OFFSET_X;
		float origY = userMapLoc.y - MARKER_OFFSET_Y;
		
		if(imageLoc.x < origX || imageLoc.x > (origX + wd) || imageLoc.y < origY || imageLoc.y > (origY + ht))
			return false;
		
		//Found the user, notify the activity and return true
		this.geoMapActivity.userSelected();
		return true;
		
	}
	
	protected void drawAt(Canvas canvas, Point screenLoc, Bitmap bmp)
	{
		if(screenLoc.x < 0 || screenLoc.x > mThisWidth || screenLoc.y < 0 || screenLoc.y > mThisHeight)
			return;
		
		//Draw the user icon at the screen location.
		canvas.drawBitmap(bmp, screenLoc.x, screenLoc.y, null);
	}
	
	protected void drawFriendLocations(Canvas canvas)
	{
		for(PointF friendMapLoc : this.geoMapActivity.getFriendsMapLoc())
		{
			Point screen = imageToScreen(friendMapLoc);
			screen.x -= MARKER_OFFSET_X;
			screen.y -= MARKER_OFFSET_Y;
			
			drawAt(canvas, screen, friendBmp);
		}
	}
	
	protected void drawMeetingPoints(Canvas canvas)
	{
		for(MeetingPoint mPoint : this.geoMapActivity.getMeetingPoints())
		{
			Point screen = imageToScreen(mPoint.mapLoc);
			screen.x -= MARKER_OFFSET_X;
			screen.y -= MARKER_OFFSET_Y;
			
			drawAt(canvas, screen, meetingBmp);
			
		}
	}
	
	protected void drawUserLocation(Canvas canvas)
	{
		PointF mapLoc = this.geoMapActivity.getUserMapLoc();
		if(mapLoc == null)
			return;
		
		Point screen = imageToScreen(mapLoc);
		screen.x -= MARKER_OFFSET_X;
	    screen.y -= MARKER_OFFSET_Y;
		
		drawAt(canvas, screen, userBmp);
	}
	
	@Override
	protected void longPressed(Point loc)
	{
		if(mBitmapDisplayed == null)
			return;
		
		PointF imageLoc = screenToImage(loc);
		
		//Return if the long press was outside the bounds of the image.
		if(imageLoc.x < 0 || imageLoc.y < 0 || imageLoc.x > mBitmapDisplayed.getWidth() || imageLoc.y > mBitmapDisplayed.getHeight())
			return;
		
		if(this.geoMapActivity == null)
			return;
		
		//Notify the activity that the long press has occurred.
		this.geoMapActivity.startCreateMeeting(imageLoc);
	}
	
	@Override
	protected void singleTapped(Point loc)
	{
		PointF imageLoc = screenToImage(loc);
		if(checkIfTappedUser(imageLoc))
			return;
		if(checkIfTappedMeeting(imageLoc))
			return;
		if(checkIfTappedFriend(imageLoc))
			return;
	}
	
	protected void onDraw(Canvas canvas)
	{
		//Ensure a valid bitmap.
		if(mBitmapDisplayed == null || mBitmapDisplayed.isRecycled())
			return;
		
		super.onDraw(canvas);
		drawUserLocation(canvas);
		drawFriendLocations(canvas);
		drawMeetingPoints(canvas);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
		super.onLayout( changed, left, top, right, bottom );
		
		if(mBitmapDisplayed == null)
			return;
		
		//Zoom to the user location on the map, or the center if there's no valid user location within the map bounds.
		PointF userMapLoc = this.geoMapActivity.getUserMapLoc();
		Point toPoint;
		float currentMidX = mThisWidth / 2;
		float currentMidY = mThisHeight / 2;
		if(userMapLoc == null || userMapLoc.x < 0 || userMapLoc.y < 0 || userMapLoc.x > getBitmapWidth() || userMapLoc.y > getBitmapHeight() )
			toPoint = imageToScreen(this.geoMapActivity.getHomePointLoc());
		else
			toPoint = imageToScreen(new PointF(userMapLoc.x, userMapLoc.y));
		
		//Center the starting point.
		scrollBy(-(toPoint.x - currentMidX), -(toPoint.y - currentMidY));
		
		//Zoom in on the starting point.
		zoomTo(4.0f);
		
    }
	
	public void setGeoMapActivity(GeoMapActivity act)
	{
		this.geoMapActivity = act;
	}
}

