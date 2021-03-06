package it.sephiroth.android.library.imagezoom;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;

public class ImageViewTouch extends ImageViewTouchBase
{
	protected static final float            MIN_ZOOM        = 0.7f;
	protected static final float            MAX_ZOOM        = 4.0f;
	static final long                       SINGLE_TAP_THRESHOLD = 306;
	protected ScaleGestureDetector  	    mScaleDetector;
	protected GestureDetector               mGestureDetector;
	protected int                           mTouchSlop;
	protected float                         mCurrentScaleFactor;
	protected float                         mScaleFactor;
	protected GestureListener               mGestureListener;
	protected ScaleListener                 mScaleListener;
	protected OnClickListener               mOnClickListener;
	protected long                          mDownTime = 0;
	protected boolean                       mLongPressed = false;
	protected boolean						mScrolled = false;
	protected boolean                       mZoomed = false;
	
	public OnClickListener getOnClickListener()
	{
		return mOnClickListener;
	}

	@Override
	public void setOnClickListener(OnClickListener listener)
	{
		mOnClickListener = listener;
	}

	public ImageViewTouch(Context context, AttributeSet attrs)
	{
		super( context, attrs );
	}

	@Override
	protected void init()
	{
		super.init();
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mGestureListener = new GestureListener();
		mScaleListener = new ScaleListener();

		mScaleDetector = new ScaleGestureDetector( getContext(), mScaleListener );
		mGestureDetector = new GestureDetector( getContext(), mGestureListener, null, true );
		mCurrentScaleFactor = 1f;
	}
	
	protected void longPressed(Point loc)
	{
	}
	
	protected void singleTapped(Point loc)
	{
	}

	@Override
	public void setImageBitmapReset(Bitmap bitmap, boolean reset)
	{
		super.setImageBitmapReset( bitmap, reset );
		mScaleFactor = getMaxZoom() / 2.0f;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		mScaleDetector.onTouchEvent( event );
		if ( !mScaleDetector.isInProgress())
			mGestureDetector.onTouchEvent( event );

		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			mDownTime = Calendar.getInstance().getTimeInMillis();
			cancelScroll();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
            if (getScale() < 1f)
            	zoomTo( 1f, 500 );
            if (getScale() > getMaxZoom())
               zoomTo( getMaxZoom(), 500 );
               center( true, true, 500 );
               
            //Massive hack to send a singleTapped event since there's a time
            //between onSingleTapConfirmed and onLongPress that the GestureDetector
            //doesn't pick up.
            if(mDownTime > 0)
            {
            	long elapsedTime = Calendar.getInstance().getTimeInMillis() - mDownTime;
            	if(elapsedTime > SINGLE_TAP_THRESHOLD && !mLongPressed && !mScrolled && !mZoomed)
            	{
            		singleTapped(new Point((int)event.getX(), (int)event.getY()));
            	}
            }
            
            //Reset the longpressed state and downtime.
    		mGestureDetector.setIsLongpressEnabled(true);
            mDownTime = 0;
            mLongPressed = false;
            mScrolled = false;
            mZoomed = false;
			 
			break;
		}
		return true;
	}

	@Override
	protected void onZoom(float scale)
	{
		mZoomed = true;
		super.onZoom( scale );
		if ( !mScaleDetector.isInProgress())
			mCurrentScaleFactor = scale;
	}

	class GestureListener
	extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			if ( mOnClickListener != null )
			{
				mOnClickListener.onClick( ImageViewTouch.this );
				return true;
			}
			
			singleTapped(new Point((int)e.getX(), (int)e.getY()));
			
			return super.onSingleTapConfirmed( e );
		}

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			mGestureDetector.setIsLongpressEnabled(false);
			float scale = getScale();
			float targetScale = scale;
			targetScale = (scale >= mScaleFactor) ? 1f : scale + mScaleFactor;
			targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
			mCurrentScaleFactor = targetScale;
			zoomTo( targetScale, e.getX(), e.getY(), 500 );
			invalidate();
			return super.onDoubleTap( e );
		}
		
		@Override
		public void onLongPress(MotionEvent e)
		{
			if(mScaleDetector.isInProgress())
				return;
			mLongPressed = true;
			longPressed(new Point((int)e.getX(), (int)e.getY()));
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			
			if (e1 == null || e2 == null)
				return false;
			if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
				return false;
			if (mScaleDetector.isInProgress())
				return false;
			
			mScrolled = true;

			scrollBy( -distanceX, -distanceY );

			invalidate();
			return super.onScroll( e1, e2, distanceX, distanceY );
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
				return false;
			if (mScaleDetector.isInProgress())
				return false;

			scrollBy( velocityX / 3, velocityY / 3, 500);
			invalidate();

			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}

	class ScaleListener
	extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			mZoomed = true;
			mCurrentScaleFactor = Math.min( getMaxZoom() * MAX_ZOOM, Math.max( mCurrentScaleFactor * detector.getScaleFactor(), MIN_ZOOM ) );
			zoomTo( mCurrentScaleFactor, detector.getFocusX(), detector.getFocusY() );
			invalidate();
			return true;
		}
	}
}