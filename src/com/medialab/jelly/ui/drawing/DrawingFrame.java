package com.medialab.jelly.ui.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.util.RoundedImageView;

public class DrawingFrame extends ViewGroup {

	private Bitmap baseBitmap;
	private final RoundedImageView baseImage;
	private final int buttonSize;
	private final ImageView clearButton;
	private final DrawingView drawingView;
	private final int leftRightTopPadding;

	public DrawingFrame(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.clearButton = new ImageView(paramContext);
		this.clearButton.setImageResource(R.drawable.drawing_clear_icon);
		this.clearButton.setClickable(true);
		this.clearButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				DrawingFrame.this.drawingView.clear();
			}
		});
		this.drawingView = new DrawingView(paramContext);
		this.baseImage = new RoundedImageView(paramContext);
		this.baseImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.baseImage.setCornerRadius(R.dimen.card_top_background_radius);
		this.buttonSize = getResources().getDimensionPixelSize(
				R.dimen.drawing_button_size);
		this.leftRightTopPadding = getResources().getDimensionPixelSize(
				R.dimen.drawing_left_right_padding);
		addView(this.baseImage);
		addView(this.drawingView);
		addView(this.clearButton);
	}

	public void clear() {
		this.baseBitmap = null;
		this.baseImage.setImageBitmap(null);
		this.drawingView.clear();
	}

	public Bitmap getCompositeBitmap() {
		Matrix localMatrix = new Matrix();
		float j = (float) this.baseBitmap.getWidth();
		float i = (float) this.baseBitmap.getHeight();
		float m = (float) getMeasuredWidth();
		float k = (float) getMeasuredHeight();

		float f2 = 0.0F;
		float f3 = 0.0F;
		float f1;
		if (j * k <= m * i) {
			f1 = m / j;
			f3 = 0.5F * (k - f1 * i);
		} else {
			f1 = k / i;
			f2 = 0.5F * (m - f1 * j);
		}
		float f4 = 1.0F / f1;
		localMatrix.setScale(f4, f4);
		localMatrix.postTranslate((int) (-1.0F * ((0.5F + f2) / f1)),
				(int) (-1.0F * ((0.5F + f3) / f1)));
		Bitmap localBitmap = Bitmap.createBitmap(this.baseBitmap.getWidth(),
				this.baseBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		localCanvas.drawBitmap(this.baseBitmap, new Matrix(), null);
		localCanvas.drawBitmap(this.drawingView.getOutputBitmap(), localMatrix,
				null);

		return localBitmap;
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int m = paramInt3 - paramInt1;
		int i = paramInt4 - paramInt2;
		int n = this.leftRightTopPadding + this.clearButton.getMeasuredWidth();
		int k = i - this.leftRightTopPadding;
		int j = k - this.clearButton.getMeasuredHeight();
		this.clearButton.layout(this.leftRightTopPadding, j, n, k);
		this.drawingView.layout(0, 0, m, i);
		this.baseImage.layout(0, 0, m, i);
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int i = View.MeasureSpec.makeMeasureSpec(this.buttonSize, 1073741824);
		this.clearButton.measure(i, i);
		i = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt1), 1073741824);
		int j = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt2), 1073741824);
		this.drawingView.measure(i, j);
		this.baseImage.measure(i, j);
		super.onMeasure(paramInt1, paramInt2);
	}

	public void setBaseImageBitmap(Bitmap paramBitmap) {
		this.baseBitmap = paramBitmap;
		this.baseImage.setImageBitmap(paramBitmap);
	}

	public void setDrawingColor(int paramInt) {
		this.drawingView.setStrokeColor(paramInt);
	}

}
