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
		
		//Map the point into its world coordinates, accounting for the zoom scale.
		int worldX = (int)(this.mapLoc.x * (Math.min(mThisWidth, mThisHeight)) * getScale() / (this.mBitmapDisplayed.getWidth()));
		int worldY = (int)(this.mapLoc.y * (Math.min(mThisWidth, mThisHeight)) * getScale() / (this.mBitmapDisplayed.getHeight()));
		
		Matrix m = getImageViewMatrix();
		float transX = getValue(m, Matrix.MTRANS_X);
		float transY = getValue(m, Matrix.MTRANS_Y);
		
		//Shift by the translation matrix to convert to screen coordinates.
		int screenX = (int)(transX + worldX );
		int screenY = (int)(transY + worldY );
		
		//Account for the icon point offset.
		screenX -= MARKER_OFFSET_X;
		screenY -= MARKER_OFFSET_Y;
		if(screenX < 0 || screenX > mThisWidth || screenY < 0 || screenY > mThisHeight)
			return;
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
		
		//Draw the user icon at the screen location.
		canvas.drawBitmap(bmp, screenX, screenY, null);
		
	}
}

