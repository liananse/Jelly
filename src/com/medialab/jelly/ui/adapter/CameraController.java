package com.medialab.jelly.ui.adapter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.GalleryPictureSelectedEvent;
import com.medialab.jelly.ui.event.PictureTakenEvent;
import com.medialab.jelly.ui.event.TopNavBarIconDisplay;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.event.UserActionFailure;
import com.medialab.jelly.ui.view.CameraView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class CameraController implements DismissableViewController {

	private final Bus bus;
	private final CameraView cameraView;
	private final Activity context;
	private boolean launchedFromOpen = false;
	private final CameraView.PictureCallback pictureCallback;

	public CameraController(Activity paramActivity, CameraView paramCameraView,
			Bus paramBus) {
		this.context = paramActivity;
		this.cameraView = paramCameraView;
		this.bus = paramBus;
		this.pictureCallback = new CameraView.PictureCallback() {
			public void onPictureTaken(boolean paramBoolean,
					byte[] paramArrayOfByte, int paramInt1, int paramInt2,
					int paramInt3, int paramInt4, int paramInt5) {
				try {

					ComposingImage mImage = convertBytesToInMemoryImage(
							paramBoolean, paramArrayOfByte, paramInt1,
							paramInt2, paramInt3, paramInt4, paramInt5);
					CameraController.this.bus
							.post(new PictureTakenEvent(mImage));
					return;
				} catch (Throwable localThrowable) {
//					CameraController.this.bus
//							.post(new UserActionFailure(
//									"take photo: failure",
//									CameraController.this.context
//											.getString(R.string.picture_processing_error_display),
//									false, localThrowable));
				}
			}
		};
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.cameraView;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.unregister(this);
		this.cameraView.pause();
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		if (!this.launchedFromOpen)
			this.bus.post(new TopNavModalSwitch(this.context.getResources()
					.getString(R.string.top_nav_composition_text), this.context
					.getResources().getString(R.string.cancel),
					new View.OnClickListener() {
						public void onClick(View paramView) {
							CameraController.this.bus
									.post(new BackPressedEvent());
						}
					}, null, null));
		else
			this.bus.post(new TopNavBarIconDisplay(this.context.getResources()
					.getString(R.string.top_nav_composition_text), false));
		this.cameraView.initializeCamera(this.pictureCallback);
		this.cameraView.resume();
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canBeDismissed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Subscribe
	public void galleryPictureSelected(
			GalleryPictureSelectedEvent paramGalleryPictureSelectedEvent) {
		try {
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			bmpFactoryOptions.inJustDecodeBounds = true;

			Bitmap paramBitmap = BitmapFactory.decodeStream(
					context.getContentResolver().openInputStream(
							paramGalleryPictureSelectedEvent.photoUri), null,
					bmpFactoryOptions);

			int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
					/ (float) 1080);
			if (widthRatio > 1) {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}

			bmpFactoryOptions.inJustDecodeBounds = false;
			paramBitmap = BitmapFactory.decodeStream(
					context.getContentResolver().openInputStream(
							paramGalleryPictureSelectedEvent.photoUri), null,
					bmpFactoryOptions);

			ComposingImage paramComposingImage = new ComposingImage(
					paramBitmap, 0, ComposingImage.Source.CAMERA);
			CameraController.this.bus.post(new PictureTakenEvent(
					paramComposingImage));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStartDragging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopDragging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub

	}

	public void setLaunchedFromOpen(boolean paramBoolean) {
		this.launchedFromOpen = paramBoolean;
	}

	public ComposingImage convertBytesToInMemoryImage(boolean paramBoolean,
			byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4, int paramInt5) throws Exception {
		long l = System.currentTimeMillis();
		Object localObject1 = new BitmapFactory.Options();
		((BitmapFactory.Options) localObject1).inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(paramArrayOfByte, 0,
				paramArrayOfByte.length, (BitmapFactory.Options) localObject1);
		int i = downsampleFactorFromOptions((BitmapFactory.Options) localObject1);
		Object localObject3 = new BitmapFactory.Options();
		((BitmapFactory.Options) localObject3).inSampleSize = i;
		((BitmapFactory.Options) localObject3).inPurgeable = true;
		((BitmapFactory.Options) localObject3).inInputShareable = true;
		localObject1 = BitmapFactory.decodeByteArray(paramArrayOfByte, 0,
				paramArrayOfByte.length, (BitmapFactory.Options) localObject3);
		localObject3 = new Matrix();
		((Matrix) localObject3).preRotate(paramInt1);
		if (paramBoolean)
			((Matrix) localObject3).postScale(-1.0F, 1.0F);
		Object localObject2 = Bitmap.createBitmap((Bitmap) localObject1,
				paramInt2 / i, paramInt3 / i, (paramInt4 - paramInt2) / i,
				(paramInt5 - paramInt3) / i, (Matrix) localObject3, true);
		Object localObject4 = new ByteArrayOutputStream();
		((Bitmap) localObject2).compress(Bitmap.CompressFormat.JPEG, 65,
				(OutputStream) localObject4);
		((Bitmap) localObject1).recycle();
		((Bitmap) localObject2).recycle();
		localObject1 = ((ByteArrayOutputStream) localObject4).toByteArray();
		localObject3 = BitmapFactory.decodeByteArray(((byte[]) localObject1),
				0, ((byte[]) localObject1).length);
		return (ComposingImage) (ComposingImage) (ComposingImage) (ComposingImage) new ComposingImage(
				(Bitmap) localObject3, paramInt1, ComposingImage.Source.CAMERA);
	}

	private int downsampleFactorFromOptions(BitmapFactory.Options paramOptions) {
		float f = Math.min(paramOptions.outHeight / 1280.0F,
				paramOptions.outWidth / 1088.0F);
		int i;
		if (f >= 1.4D)
			i = Math.max(2, Math.round(f));
		else
			i = 1;
		return i;
	}

}
