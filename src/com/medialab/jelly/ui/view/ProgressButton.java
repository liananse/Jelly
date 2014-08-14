package com.medialab.jelly.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.medialab.jelly.R;

public class ProgressButton extends Button {
	private int mCircleColor;
	private Paint mCirclePaint;
	private int mMax;
	private int mProgress;
	private int mProgressColor;
	private Paint mProgressPaint;
	private RectF mTempRect = new RectF();

	public ProgressButton(Context paramContext) {
		super(paramContext);
		init(paramContext, null, 0);
	}

	public ProgressButton(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext, paramAttributeSet, 0);
	}

	public ProgressButton(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext, paramAttributeSet, paramInt);
	}

	private void init(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		setBackgroundColor(0);
		this.mMax = 100;
		this.mProgress = 0;
		if (paramAttributeSet != null) {
			TypedArray localTypedArray = paramContext.obtainStyledAttributes(
					paramAttributeSet, R.styleable.PinProgressButton, paramInt,
					0);
			this.mMax = localTypedArray.getInteger(1, this.mMax);
			this.mProgress = localTypedArray.getInteger(0, this.mProgress);
			this.mCircleColor = localTypedArray.getColor(2, 17170444);
			this.mProgressColor = localTypedArray.getColor(3, 17170443);
			localTypedArray.recycle();
		}
		setupPaints();
	}

	private void setupPaints() {
		this.mCirclePaint = new Paint();
		this.mCirclePaint.setColor(this.mCircleColor);
		this.mCirclePaint.setAntiAlias(true);
		this.mProgressPaint = new Paint();
		this.mProgressPaint.setColor(this.mProgressColor);
		this.mProgressPaint.setAntiAlias(true);
	}

	protected void onDraw(Canvas paramCanvas) {
		int i = getMeasuredHeight();
		this.mTempRect.set(0.0F, 0.0F, i, i);
		paramCanvas.drawArc(this.mTempRect, 0.0F, 360.0F, true,
				this.mCirclePaint);
		paramCanvas.drawArc(this.mTempRect, -90.0F, 360 * this.mProgress
				/ this.mMax, true, this.mProgressPaint);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt2), 1073741824);
		super.onMeasure(i, i);
	}

	public void setCircleColorAndProgressColor(int paramInt1, int paramInt2) {
		this.mCircleColor = paramInt1;
		this.mProgressColor = paramInt2;
		setupPaints();
	}

	public void setProgress(int paramInt) {
		this.mProgress = paramInt;
		postInvalidate();
	}
}
