package com.samsung.sra.tutorial.draganddraw;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
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
	
}
