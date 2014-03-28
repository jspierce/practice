package com.samsung.sra.tutorial.draganddraw;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BoxDrawingView extends View {
	private static final String TAG = "BoxDrawingView";
	
	private Box mCurrentBox;
	private ArrayList<Box> mBoxes;
	private Paint mBoxPaint;
	private Paint mBackgroundPaint;

	// Used when creating the view in code
	public BoxDrawingView(Context context) {
		this(context, null);
	}
	
	// Used when inflating the view from XML
	public BoxDrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mBoxes = new ArrayList<Box>();
		
		// Paint the boxes a semi-transparent red (ARGB)
		mBoxPaint = new Paint();
		mBoxPaint.setColor(0x22ff0000);
		
		// Paint the background off-white
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(0xfff8efe0);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		PointF currentPos = new PointF(event.getX(), event.getY());
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Reset drawing state
				mCurrentBox = new Box(currentPos);
				mBoxes.add(mCurrentBox);
				break;
				
			case MotionEvent.ACTION_UP:
				mCurrentBox = null;
				break;
				
			case MotionEvent.ACTION_MOVE:
				if (mCurrentBox != null) {
					mCurrentBox.setCurrent(currentPos);
					invalidate();
				}
				break;
				
			case MotionEvent.ACTION_CANCEL:
				mCurrentBox = null;
				break;
		}
		
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Fill the background
		canvas.drawPaint(mBackgroundPaint);
		
		for (Box box: mBoxes) {
			PointF origin = box.getOrigin();
			PointF current = box.getCurrent();
			float left = Math.min(origin.x, current.x);
			float right = Math.max(origin.x, current.x);
			float top = Math.min(origin.y, current.y);
			float bottom = Math.max(origin.y, current.y);
			
			canvas.drawRect(left, top, right, bottom, mBoxPaint);
		}
	}
	
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Log.d(TAG, "Saving box data");
		
		Parcelable parentState = super.onSaveInstanceState();
		
		Bundle state = new Bundle();
		
		ArrayList<Bundle> boxData = new ArrayList<Bundle>();
		for (Box box: mBoxes) {
			Bundle boxBundle = new Bundle();
			
			PointF origin = box.getOrigin();
			boxBundle.putFloat("originX", origin.x);
			boxBundle.putFloat("originY", origin.y);
			PointF current = box.getCurrent();
			boxBundle.putFloat("currentX", current.x);
			boxBundle.putFloat("currentY", current.y);
			
			boxData.add(boxBundle);
		}
		
		state.putParcelableArrayList("boxData", boxData);
		state.putParcelable("parentState", parentState);
		
		return state;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Log.d(TAG, "Restoring box data");
		Bundle stateBundle = (Bundle) state;
		
		Parcelable parentState = stateBundle.getParcelable("parentState");
		super.onRestoreInstanceState(parentState);
		
		ArrayList<Parcelable> boxData = stateBundle.getParcelableArrayList("boxData");
		
		for (Parcelable boxParcelable: boxData) {
			Bundle boxBundle = (Bundle) boxParcelable;
			
			PointF origin = new PointF(boxBundle.getFloat("originX"), boxBundle.getFloat("originY"));
			PointF current = new PointF(boxBundle.getFloat("currentX"), boxBundle.getFloat("currentY"));
			Box box = new Box(origin);
			box.setCurrent(current);
			
			mBoxes.add(box);
		}
	}
	
}
