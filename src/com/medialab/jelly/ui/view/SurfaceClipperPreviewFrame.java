package com.medialab.jelly.ui.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

public class SurfaceClipperPreviewFrame extends ViewGroup {

	private TextureView child;
	private float percentPreviewHeightShowing;
	private float percentPreviewWidthShowing;
	private Camera.Size previewSize;

	public void clearTextureView() {
		if (this.child != null)
			removeView(this.child);
		this.child = null;
	}

	public Pair<Float, Float> getPreviewWidthAndHeightShowing() {
		return new Pair(Float.valueOf(this.percentPreviewWidthShowing),
				Float.valueOf(this.percentPreviewHeightShowing));
	}

	public SurfaceClipperPreviewFrame(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		if ((this.child != null) && (this.previewSize != null)) {
			int i = paramInt4 - paramInt2;
			int j = this.child.getMeasuredHeight();
			i = (i - j) / 2;
			j = i + j;
			int m = paramInt3 - paramInt1;
			int k = this.child.getMeasuredWidth();
			m = (m - k) / 2;
			k = m + k;
			this.child.layout(m, i, k, j);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if ((this.child != null) && (this.previewSize != null)) {
			int k = View.MeasureSpec.getSize(widthMeasureSpec);
			int j = View.MeasureSpec.getSize(heightMeasureSpec);

			float f2 = Math.max(
					(float) ((float) k / (float) this.previewSize.height),
					((float) (float) j / (float) this.previewSize.width));

			float f1 = f2 * this.previewSize.height;
			f2 *= this.previewSize.width;
			this.percentPreviewHeightShowing = ((float) k / f1);
			this.percentPreviewWidthShowing = ((float) j / f2);
			
			int i = View.MeasureSpec.makeMeasureSpec((int) f1,
					MeasureSpec.EXACTLY);
			j = View.MeasureSpec.makeMeasureSpec((int) f2, MeasureSpec.EXACTLY);
			this.child.measure(i, j);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	public void setTextureView(TextureView paramTextureView,
			Camera.Size paramSize) {
		this.child = paramTextureView;
		this.previewSize = paramSize;
		addView(this.child);
	}

}
