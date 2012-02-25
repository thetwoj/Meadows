package com.osu.sc.mapframework;

import java.util.Calendar;

import server.Client;
import server.User;

import com.osu.sc.meadows.GeoMapActivity;
import com.osu.sc.meadows.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GeoImageViewTouch extends ImageViewTouch
{
	protected GeoMapActivity geoMapActivity;

	public GeoImageViewTouch(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	/*
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
			float origX = mPoint.mapLoc.x;
			float origY = mPoint.mapLoc.y;
			
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
		
		float origX = userMapLoc.x;
		float origY = userMapLoc.y;
		
		if(imageLoc.x < origX || imageLoc.x > (origX + wd) || imageLoc.y < origY || imageLoc.y > (origY + ht))
			return false;
		
		//Found the user, notify the activity and return true
		this.geoMapActivity.userSelected();
		return true;
		
	}
	*/
	
	protected void drawNameplateAt(Canvas canvas, Paint paint, Point screenLoc, String name, int color)
	{
		if(name.length() <= 0 )
			return;
		
		//Draw the user icon at the screen location.
		int rectStartX = screenLoc.x + 15;
		int rectStartY = screenLoc.y + 15;
		Rect rect = new Rect();
		paint.getTextBounds(name, 0, name.length(), rect);
		rect.offsetTo(rectStartX, rectStartY);
		
		//Increase the size of the rectangle by the padding so the text is not right at the edges.
		rect.inset(-6, -6);
		paint.setColor(color);
		paint.setStrokeWidth(2);
		canvas.drawRect(rect, paint);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rect, paint);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.CENTER);
		FontMetrics fm = new FontMetrics();
		paint.getFontMetrics(fm);
		canvas.drawLine(screenLoc.x, screenLoc.y, rect.left, rect.top, paint);
		canvas.drawText(name, rect.exactCenterX(), rect.exactCenterY() + -(fm.ascent + fm.descent) / 2, paint);
	}
	
	protected void drawFriendLocations(Canvas canvas, Paint paint)
	{
		Calendar now = Calendar.getInstance();
		long time = now.getTimeInMillis();
		
		for(User friend : Client.GetInstance().GetVisibleFriends())
		{
			PointF mapLoc = friend.GetMapLocation();
			if(mapLoc == null)
				continue;
			
			if(mapLoc.x < 0 || mapLoc.y < 0 || mapLoc.x > getBitmapWidth() || mapLoc.y > getBitmapHeight())
				continue;
			
			int color = getNameplateColor(time, friend.GetTimestamp());
			Point screen = imageToScreen(mapLoc);
			
			drawNameplateAt(canvas, paint, screen, friend.GetFirstName(), color);
		}
	}
	
	protected void drawMeetingPoints(Canvas canvas, Paint paint)
	{
		for(MeetingPoint mPoint : this.geoMapActivity.getMeetingPoints())
		{
			Point screen = imageToScreen(mPoint.mapLoc);
			
			drawNameplateAt(canvas, paint, screen, "12:05 P.M", Color.WHITE);
			
		}
	}
	
	protected void drawUserLocation(Canvas canvas, Paint paint)
	{
		Client client = Client.GetInstance();
		PointF mapLoc = client.GetMapLocation();
		if(mapLoc == null)
			return;
		
		if(mapLoc.x < 0 || mapLoc.y < 0 || mapLoc.x > getBitmapWidth() || mapLoc.y > getBitmapHeight())
			return;
		
		int color = getNameplateColor(Calendar.getInstance().getTimeInMillis(), client.GetTimestamp());
		
		Point screen = imageToScreen(mapLoc);
		
		String name = client.GetFirstName();
		if(name == null || name.length() <= 0)
			name = "You";
		
		drawNameplateAt(canvas, paint, screen, name, color);
	}
	
	protected int getNameplateColor(long now, long timestamp)
	{
		long diff = now - timestamp;
		//Return green if the user was updated within a minute.
		if(diff < 60000)
			return Color.GREEN;
		//Return yellow if the user was updated within 10 minutes.
		if(diff < 600000)
			return Color.YELLOW;
		
		//Return red if the user was updated over 10 minutes ago.
		return Color.RED;
	}
	
	@Override
	protected void longPressed(Point loc)
	{
		/*
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
		*/
	}
	
	@Override
	protected void singleTapped(Point loc)
	{
		/*
		PointF imageLoc = screenToImage(loc);
		if(checkIfTappedUser(imageLoc))
			return;
		if(checkIfTappedMeeting(imageLoc))
			return;
		if(checkIfTappedFriend(imageLoc))
			return;
		*/
	}
	
	protected void onDraw(Canvas canvas)
	{
		//Ensure a valid bitmap.
		if(mBitmapDisplayed == null || mBitmapDisplayed.isRecycled())
			return;
		
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		paint.setTextSize(16);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		drawUserLocation(canvas, paint);
		drawFriendLocations(canvas, paint);
		//drawMeetingPoints(canvas, paint);
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

