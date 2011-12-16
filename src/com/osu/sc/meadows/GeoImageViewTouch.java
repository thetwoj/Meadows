package com.osu.sc.meadows;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GeoImageViewTouch extends ImageViewTouch
{
	protected Point mapLoc;
	private static final int MARKER_OFFSET_X = 16; //temp 
	private static final int MARKER_OFFSET_Y = 35;
	
	public GeoImageViewTouch(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void setLoc(Point loc)
	{
		this.mapLoc = loc;
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(this.mapLoc == null)
			return;
		
		//Draw the user icon at the screen location.
		int screenX = this.mOrigin.x + this.mapLoc.x - MARKER_OFFSET_X;
		int screenY = this.mOrigin.y + this.mapLoc.y - MARKER_OFFSET_Y;
		if(screenX < 0 || screenX > this.mThisWidth || screenY < 0 || screenY > this.mThisHeight)
			return;
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
		
		canvas.drawBitmap(bmp, screenX, screenY, null);
		
	}
}

