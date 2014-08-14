package com.medialab.jelly.ui.view;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.data.image.ComposingImage;

public class EditImageView extends ViewGroup {

	private ComposingImage composingImage;
	private final FrameLayout frameView;
	private final PhotoView mainImageView;
	private PhotoViewAttacher mainImageViewAttacher;
	private final ImageView rotateButton;
	private final int smallIconInset;
	private final int smallIconSize;

	public EditImageView(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.rotateButton = new ImageView(paramContext);
		this.rotateButton.setScaleType(ImageView.ScaleType.FIT_XY);
		this.rotateButton.setImageResource(R.drawable.rotation_icon_3x);
		this.rotateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				float f = (360.0F + (90.0F + EditImageView.this.mainImageViewAttacher
						.getPhotoViewRotation())) % 360.0F;
				if ((EditImageView.this.composingImage != null)
						&& (EditImageView.this.composingImage.bitmap != null)) {
					EditImageView.this.mainImageViewAttacher
							.setPhotoViewRotation(f);
					EditImageView.this.mainImageViewAttacher
							.setScaleAndUpdate();
				}
			}
		});
		this.mainImageView = new PhotoView(paramContext);
		this.frameView = new FrameLayout(paramContext);
		this.frameView.setBackgroundResource(R.drawable.camera_overlay);
		this.smallIconInset = paramContext.getResources()
				.getDimensionPixelSize(R.dimen.camera_small_icon_inset);
		this.smallIconSize = paramContext.getResources().getDimensionPixelSize(
				R.dimen.camera_small_icon_size);
		addView(this.mainImageView);
		addView(this.frameView);
		addView(this.rotateButton);
	}

	public void clear() {
		this.composingImage = null;
		this.mainImageView.setImageBitmap(null);
		if (this.mainImageViewAttacher != null)
			this.mainImageViewAttacher.setPhotoViewRotation(0.0F);
	}

	// @DebugLog
	public ComposingImage getImageTrimmedAndTransformed() {
		Matrix localMatrix = this.mainImageViewAttacher.getDisplayMatrix();
		return new ComposingImage(trimDownBitmap(
				localMatrix, this.mainImageView.getMeasuredWidth(),
				this.mainImageView.getMeasuredHeight(),
				this.composingImage.bitmap), this.composingImage.rotation,
				ComposingImage.Source.DRAW);
	}

	public Bitmap trimDownBitmap(Matrix paramMatrix, int paramInt1,
			int paramInt2, Bitmap paramBitmap) {
		float f = Math.max(1088.0F / paramInt1, 1280.0F / paramInt2);
		paramMatrix.postScale(f, f);
		paramMatrix.postTranslate((1088.0F - f * paramInt1) / 2.0F,
				(1280.0F - f * paramInt2) / 2.0F);
		Bitmap localBitmap = Bitmap.createBitmap(1088, 1280,
				Bitmap.Config.ARGB_8888);
		new Canvas(localBitmap).drawBitmap(paramBitmap, paramMatrix, null);
		return localBitmap;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (this.composingImage != null) {
			int j = this.mainImageView.getMeasuredHeight();
			int i = this.mainImageView.getMeasuredWidth();
			this.mainImageView.layout(0, 0, i, j);
			this.frameView.layout(0, 0, i, j);
			int m = j - this.smallIconInset;
			i = this.smallIconInset;
			int k = m - this.rotateButton.getMeasuredHeight();
			j = i + this.rotateButton.getMeasuredWidth();
			this.rotateButton.layout(i, k, j, m);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if (this.composingImage != null) {
			int j = View.MeasureSpec.getSize(widthMeasureSpec);
			int i = View.MeasureSpec.getSize(heightMeasureSpec);
			j = View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY);
			i = View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY);
			this.mainImageView.measure(j, i);
			this.frameView.measure(j, i);
			this.rotateButton.measure(View.MeasureSpec.makeMeasureSpec(
					this.smallIconSize, MeasureSpec.EXACTLY), View.MeasureSpec
					.makeMeasureSpec(this.smallIconSize, MeasureSpec.EXACTLY));
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setImage(ComposingImage paramComposingImage) {
		if (paramComposingImage != null) {
			this.composingImage = paramComposingImage;
			this.mainImageView.setImageBitmap(paramComposingImage.bitmap);
			this.mainImageViewAttacher = new PhotoViewAttacher(
					this.mainImageView);
			this.mainImageViewAttacher
					.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
	}
}