package com.osu.sc.meadows;

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
	protected Point mapLoc;
	private static final int MARKER_OFFSET_X = 16;
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
		
		Point screen = imageToScreen(this.mapLoc);
		screen.x -= MARKER_OFFSET_X;
		screen.y -= MARKER_OFFSET_Y;
		if(screen.x < 0 || screen.x > mThisWidth || screen.y < 0 || screen.y > mThisHeight)
			return;
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
		
		//Draw the user icon at the screen location.
		canvas.drawBitmap(bmp, screen.x, screen.y, null);
		
	}
}

