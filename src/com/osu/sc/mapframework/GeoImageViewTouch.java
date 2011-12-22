package com.osu.sc.mapframework;

import com.osu.sc.meadows.GeoMapActivity;
import com.osu.sc.meadows.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GeoImageViewTouch extends ImageViewTouch
{
	protected GeoMapActivity geoMapActivity;
	private static final int MARKER_OFFSET_X = 14;
	private static final int MARKER_OFFSET_Y = 33;
	
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
		Bitmap meetingBmp = BitmapFactory.decodeResource(getResources(), R.drawable.meeting_icon);
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
		Bitmap userBmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
		int wd = userBmp.getWidth();
		int ht = userBmp.getHeight();
		
		//Shift the meeting point back by the bitmap size.
		float origX = this.geoMapActivity.getMapLocation().x - MARKER_OFFSET_X;
		float origY = this.geoMapActivity.getMapLocation().y - MARKER_OFFSET_Y;
		
		if(imageLoc.x < origX || imageLoc.x > (origX + wd) || imageLoc.y < origY || imageLoc.y > (origY + ht))
			return false;
		
		//Found the user, notify the activity and return true
		this.geoMapActivity.userSelected();
		return true;
		
	}
	
	protected void drawAt(Canvas canvas, Point screenLoc, int drawable_id)
	{
		if(screenLoc.x < 0 || screenLoc.x > mThisWidth || screenLoc.y < 0 || screenLoc.y > mThisHeight)
			return;
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawable_id);
		
		//Draw the user icon at the screen location.
		canvas.drawBitmap(bmp, screenLoc.x, screenLoc.y, null);
	}
	
	protected void drawMeetingPoints(Canvas canvas)
	{
		for(MeetingPoint mPoint : this.geoMapActivity.getMeetingPoints())
		{
			Point screen = imageToScreen(mPoint.mapLoc);
			screen.x -= MARKER_OFFSET_X;
			screen.y -= MARKER_OFFSET_Y;
			
			drawAt(canvas, screen, R.drawable.meeting_icon);
			
		}
	}
	
	protected void drawUserLocation(Canvas canvas)
	{
		PointF mapLoc = this.geoMapActivity.getMapLocation();
		if(mapLoc == null)
			return;
		
		Point screen = imageToScreen(mapLoc);
		screen.x -= MARKER_OFFSET_X;
	    screen.y -= MARKER_OFFSET_Y;
		
		drawAt(canvas, screen, R.drawable.user_icon);
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
		super.onDraw(canvas);
		drawUserLocation(canvas);
		drawMeetingPoints(canvas);
	}
	
	public void setGeoMapActivity(GeoMapActivity act)
	{
		this.geoMapActivity = act;
	}
}

