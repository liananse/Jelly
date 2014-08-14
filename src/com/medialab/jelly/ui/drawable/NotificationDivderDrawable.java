package com.medialab.jelly.ui.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class NotificationDivderDrawable extends Drawable {
	private Rect bounds;
	private final int leftSpace;
	private final Paint paint = new Paint();

	public NotificationDivderDrawable(int paramInt1, int paramInt2) {
		this.paint.setColor(paramInt1);
		this.paint.setStyle(Paint.Style.STROKE);
		this.leftSpace = paramInt2;
	}

	public void draw(Canvas paramCanvas) {
		float f = (this.bounds.bottom + this.bounds.top) / 2.0F;
		paramCanvas.drawLine(this.leftSpace, f, this.bounds.right, f,
				this.paint);
	}

	public int getOpacity() {
		return -1;
	}

	public void setAlpha(int paramInt) {
	}

	public void setBounds(Rect paramRect) {
		super.setBounds(paramRect);
		this.bounds = paramRect;
		this.paint.setStrokeWidth(paramRect.bottom - paramRect.top);
	}

	public void setColorFilter(ColorFilter paramColorFilter) {
	}
}