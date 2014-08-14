package com.medialab.jelly.ui.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	private Bitmap bitmapCache;
	private Canvas cachedCanvas;

	private final Matrix identity = new Matrix();
	private final Paint paint = new Paint();
	private final Path path = new Path();

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	public DrawingView(Context c) {
		super(c);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(12);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmapCache != null) {
			canvas.drawBitmap(bitmapCache, identity, null);
			canvas.drawPath(path, paint);
		}
	}

	public void clear() {
		this.path.reset();
		if (this.bitmapCache != null) {
			this.bitmapCache.recycle();
			this.bitmapCache = null;
		}
		this.cachedCanvas = null;
		invalidate();
	}

	public Bitmap getOutputBitmap() {
		if (this.bitmapCache == null) {
			this.bitmapCache = Bitmap.createBitmap(getMeasuredWidth(),
					getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			this.cachedCanvas = new Canvas(this.bitmapCache);
		}
		return this.bitmapCache;
	}

	public void setStrokeColor(int paramInt) {
		this.paint.setColor(paramInt);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bitmapCache = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		cachedCanvas = new Canvas(bitmapCache);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	private void touch_start(float x, float y) {
		path.reset();
		path.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		path.lineTo(mX, mY);
		if (bitmapCache == null) {
			bitmapCache = Bitmap.createBitmap(getMeasuredWidth(),
					getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			cachedCanvas = new Canvas(bitmapCache);
		}
		cachedCanvas.drawPath(path, paint);
		path.reset();
	}

}
