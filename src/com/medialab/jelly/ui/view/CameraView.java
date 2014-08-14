package com.medialab.jelly.ui.view;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.medialab.jelly.AskActivity;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.SearchImageActivity;
import com.medialab.jelly.ui.event.UserActionFailure;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.CustomTextView;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class CameraView extends ViewGroup {

	private final Context mContext;
	private static Camera currentCamera;
	private static boolean needsCleanup = false;
	private TextureView currentTextureView;

	private final int bigIconInset;
	private final int bigIconSize;
	private final Bus bus;
	private final ImageView captureButton;
	private final int captureButtonInset;
	private final int captureIconSize;

	private int cardHeight;
	private int cardWidth;
	private CameraDirection currentDirection;

	private final FrameLayout overlayFrame;

	private final ImageView rotateCameraButton;
	private final int smallIconInset;
	private final int smallIconSize;
	private SurfaceClipperPreviewFrame textureContainer;

	private final int rearCameraId;
	private final Handler handler = new Handler();
	private boolean hasBeenMeasured = false;
	private final int frontCameraId;

	private final Runnable firstMeasureRunnable;
	private final ImageView flashIconButton;
	private final FlashState flashState;
	private final ImageView galleryButton;

	private final Handler uiHandler = new Handler();
	private PictureCallback pictureCallback;

	private Camera.Size currentPictureSize;
	private Camera.Size currentPreviewSize;
	
	/////////////////////////新增图层
	// 文字层
	private final TextView textQuestionView;
	private int topTextPadding;
	// 地图层
	private final ImageView mapQuestionView;
	
	private final TextView askPhotoTextView;
	private final TextView askMapTextView;

	public CameraView(Context context, Bus paramBus, int paramRearCameraId,
			int paramFrontCameraId) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.bus = paramBus;
		this.rearCameraId = paramRearCameraId;
		this.frontCameraId = paramFrontCameraId;

		this.overlayFrame = new FrameLayout(getContext());
		this.overlayFrame.setBackgroundResource(R.drawable.camera_overlay);

		this.flashState = new FlashState();
		textureContainer = new SurfaceClipperPreviewFrame(context);

		if (this.rearCameraId < 0) {
			if (this.frontCameraId >= 0)
				this.currentDirection = CameraDirection.FRONT;
		} else
			this.currentDirection = CameraDirection.REAR;

		this.firstMeasureRunnable = new Runnable() {
			public void run() {
				setUpCamera(CameraView.this.currentDirection);
				resetSurfaceView();
				requestLayout();
			}
		};

		Resources localResources = getResources();
		this.bigIconInset = localResources
				.getDimensionPixelSize(R.dimen.camera_big_icon_inset);
		this.bigIconSize = localResources
				.getDimensionPixelSize(R.dimen.camera_big_icon_size);
		this.captureButtonInset = localResources
				.getDimensionPixelSize(R.dimen.camera_capture_button_inset);
		this.captureIconSize = localResources
				.getDimensionPixelSize(R.dimen.camera_capture_icon_size);
		this.smallIconSize = localResources
				.getDimensionPixelSize(R.dimen.camera_small_icon_size);
		this.smallIconInset = localResources
				.getDimensionPixelSize(R.dimen.camera_small_icon_inset);

		this.rotateCameraButton = new ImageView(getContext());
		this.rotateCameraButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.rotateCameraButton
				.setImageResource(R.drawable.camera_rotate_button);
		this.rotateCameraButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				if (CameraView.currentCamera != null)
					if (CameraView.this.currentDirection != CameraView.CameraDirection.FRONT) {
						CameraView.this.currentDirection = CameraView.CameraDirection.FRONT;
						CameraView.this
								.setUpCamera(CameraView.this.currentDirection);
						CameraView.this.resetSurfaceView();
					} else {
						CameraView.this.currentDirection = CameraView.CameraDirection.REAR;
						CameraView.this
								.setUpCamera(CameraView.this.currentDirection);
						CameraView.this.resetSurfaceView();
					}
			}
		});

		if ((frontCameraId < 0) || (rearCameraId < 0)) {
			this.rotateCameraButton.setVisibility(View.GONE);
			this.rotateCameraButton.setClickable(false);
		}

		this.galleryButton = new ImageView(getContext());
		this.galleryButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.galleryButton.setImageResource(R.drawable.camera_gallery_button);
		this.galleryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				new CameraView.ImageSearchTypeDialog().show(
						((Activity) mContext).getFragmentManager(),
						"pick image");
			}
		});

		this.flashIconButton = new ImageView(getContext());
		this.flashIconButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.flashIconButton.setImageResource(this.flashState
				.getResourceIdForFlashState());
		this.flashIconButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				if (CameraView.currentCamera != null) {
					CameraView.this.flashState
							.nextFlashState(CameraView.currentCamera);
					CameraView.this.flashIconButton
							.setImageResource(CameraView.this.flashState
									.getResourceIdForFlashState());
				}
			}
		});

		this.captureButton = new ImageView(getContext());
		this.captureButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.captureButton.setImageResource(R.drawable.camera_capture_button);
		this.captureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				if (CameraView.currentCamera == null)
					return;
				try {
					CameraView.currentCamera
							.autoFocus(new Camera.AutoFocusCallback() {
								public void onAutoFocus(boolean paramBoolean,
										Camera paramCamera) {
									CameraView.this
											.tryToTakePicture(paramCamera);
								}
							});
				} catch (Throwable localThrowable) {
					CameraView.this.tryToTakePicture(CameraView.currentCamera);
				}
			}
		});

		this.captureButton.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
				int i = MotionEventCompat.getActionMasked(paramMotionEvent);
				if (i != 0) {
					if (i == 1)
						CameraView.this.captureButton
								.setImageResource(R.drawable.camera_capture_button);
				} else
					CameraView.this.captureButton
							.setImageResource(R.drawable.camera_capture_button_down);
				return false;
			}
		});

		this.textureContainer.setBackgroundColor(getResources().getColor(
				R.color.black));
		
		// 新增图层
		this.topTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_bottom_padding);

		// 另外两层view
		askPhotoTextView = new CustomTextView(mContext);
		askPhotoTextView.setText(mContext
				.getText(R.string.compose_question_photo_view_hint));
		askPhotoTextView.setBackgroundColor(mContext.getResources().getColor(
				R.color.white_apha));
		askPhotoTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		askPhotoTextView.setGravity(Gravity.TOP | Gravity.LEFT);
		askPhotoTextView.setTextSize(0, localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_size));
		askPhotoTextView.setTextColor(localResources
				.getColor(R.color.question_card_main_text_color));
		askPhotoTextView.setPadding(topTextPadding, topTextPadding, topTextPadding, topTextPadding);
		FontManager.setTypeface(askPhotoTextView, FontManager.Weight.HUAKANG);
		askPhotoTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flashIconButton.setVisibility(View.VISIBLE);
				rotateCameraButton.setVisibility(View.VISIBLE);
				galleryButton.setVisibility(View.VISIBLE);
				captureButton.setVisibility(View.VISIBLE);
				
				askPhotoTextView.setVisibility(View.GONE);
				askMapTextView.setVisibility(View.GONE);
				textQuestionView.setVisibility(View.GONE);
				mapQuestionView.setVisibility(View.GONE);
				
				JellyApplication.setIsMapQuestion(false);
				
				MobclickAgent.onEvent(mContext, UMengEventID.EDIT_QUESTION_PHOTO);
			}
		});
		
		askMapTextView = new CustomTextView(mContext);
		askMapTextView.setText(mContext
				.getText(R.string.compose_question_map_view_hint));
		askMapTextView.setBackgroundColor(mContext.getResources().getColor(
				R.color.white_apha));
		askMapTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		askMapTextView.setGravity(Gravity.TOP | Gravity.LEFT);
		askMapTextView.setTextSize(0, localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_size));
		askMapTextView.setTextColor(localResources
				.getColor(R.color.question_card_main_text_color));
		askMapTextView.setPadding(topTextPadding, topTextPadding, topTextPadding, topTextPadding);
		FontManager.setTypeface(askMapTextView, FontManager.Weight.HUAKANG);
		
		askMapTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent localIntent;
				localIntent = new Intent(mContext,
						AskActivity.class);
				((Activity) mContext)
						.startActivityForResult(
								localIntent, 130);
				
				JellyApplication.setIsMapQuestion(true);
				
				MobclickAgent.onEvent(mContext, UMengEventID.EDIT_QUESTION_LOCATION);
			}
		});
		
		// 文字层
		textQuestionView = new CustomTextView(mContext);
		textQuestionView.setText(mContext
				.getText(R.string.compose_question_text_view_hint));
		textQuestionView.setBackgroundColor(mContext.getResources().getColor(
				R.color.white));
		textQuestionView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		textQuestionView.setGravity(Gravity.TOP | Gravity.LEFT);
		textQuestionView.setTextSize(0, localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_size));
		textQuestionView.setTextColor(localResources
				.getColor(R.color.question_card_main_text_color));
		textQuestionView.setPadding(topTextPadding, topTextPadding, 0, 0);
		FontManager.setTypeface(textQuestionView, FontManager.Weight.HUAKANG);
		
		textQuestionView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent localIntent;
				localIntent = new Intent(mContext,
						SearchImageActivity.class);
				((Activity) mContext)
						.startActivityForResult(
								localIntent, 130);
				
				JellyApplication.setIsMapQuestion(false);
				
				MobclickAgent.onEvent(mContext, UMengEventID.EDIT_QUESTION_ANYTHING);
			}
		});
		
		// 地图层
		mapQuestionView = new ImageView(mContext);
		mapQuestionView.setScaleType(ScaleType.CENTER_CROP);

		SharedPreferences sp = UTools.Storage.getSharedPreferences(context,
				UConstants.BASE_PREFS_NAME);

		FinalBitmap.create(mContext).display(mapQuestionView, UDisplayWidth.getBaiDuMapAPIUrl(Double.parseDouble(sp.getString(
				UConstants.LOCATION_LONGITUDE, "0.0")), Double.parseDouble(sp.getString(
				UConstants.LOCATION_LATITUDE, "0.0")), 300, 200));

		addView(textureContainer);
		addView(this.flashIconButton);
		addView(this.rotateCameraButton);
		addView(this.galleryButton);
		addView(this.captureButton);
		addView(this.askPhotoTextView);
		addView(this.mapQuestionView);
		addView(this.askMapTextView);
		addView(this.textQuestionView);
		addView(this.overlayFrame);
		
		this.flashIconButton.setVisibility(View.GONE);
		this.rotateCameraButton.setVisibility(View.GONE);
		this.galleryButton.setVisibility(View.GONE);
		this.captureButton.setVisibility(View.GONE);
	}

	private void cleanUpEventually() {
		needsCleanup = true;
		this.handler.postDelayed(new Runnable() {
			public void run() {
				if (CameraView.needsCleanup)
					CameraView.this.cleanupNow();
			}
		}, 1400L);
	}

	private void cleanupNow() {
		needsCleanup = false;
		resetCamera();
		clearSurfaceView();
	}

	private void clearSurfaceView() {
		if (this.textureContainer.getChildCount() > 0)
			this.textureContainer.clearTextureView();
	}

	private int getRotationForCurrentCameraAndDirection() {
		int j;
		if (this.currentDirection != CameraDirection.FRONT)
			j = this.rearCameraId;
		else
			j = this.frontCameraId;
		Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(j, localCameraInfo);
		int k = ((Activity) this.getContext()).getWindowManager()
				.getDefaultDisplay().getRotation();
		j = 0;
		switch (k) {
		case 0:
			j = 0;
			break;
		case 1:
			j = 90;
			break;
		case 2:
			j = 180;
			break;
		case 3:
			j = 270;
		}
		int i;
		if (localCameraInfo.facing != 1)
			i = (360 + (localCameraInfo.orientation - j)) % 360;
		else {
			i = (360 - (j + localCameraInfo.orientation) % 360) % 360;
		}
		return i;
	}

	private void setUpCamera(CameraDirection paramCameraDirection) {

		resetCamera();
		if (paramCameraDirection == CameraDirection.FRONT) {
			if (this.frontCameraId >= 0) {
				currentCamera = Camera.open(this.frontCameraId);
				this.currentDirection = CameraDirection.FRONT;
			}
		} else {
			if (this.rearCameraId >= 0) {
				currentCamera = Camera.open(this.rearCameraId);
				this.currentDirection = CameraDirection.REAR;
			}
		}

		currentCamera
				.setDisplayOrientation(getRotationForCurrentCameraAndDirection());

		Object localObject2;

		localObject2 = currentCamera.getParameters();
		((Camera.Parameters) localObject2).setFocusMode("continuous-picture");
		Pair<Camera.Size, Camera.Size> localPair = pickBestPreviewAndPictureSize(
				currentCamera.getParameters().getSupportedPreviewSizes(),
				currentCamera.getParameters().getSupportedPictureSizes());
		this.currentPreviewSize = ((Camera.Size) localPair.first);
		((Camera.Parameters) localObject2).setPreviewSize(
				this.currentPreviewSize.width, this.currentPreviewSize.height);

		this.currentPictureSize = ((Camera.Size) localPair.second);
		((Camera.Parameters) localObject2).setPictureSize(
				this.currentPictureSize.width, this.currentPictureSize.height);

		// 部分机型设置有问题，比如魅族MX3
		try {
			currentCamera.setParameters((Camera.Parameters) localObject2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setUpContinuousFocusStuff(currentCamera);
	}

	private Pair<Camera.Size, Camera.Size> pickBestPreviewAndPictureSize(
			List<Camera.Size> paramPreviewSizes,
			List<Camera.Size> paramPictureSizes) {

		Camera.Size resultPreviewSize = null;
		for (Camera.Size tempSize : paramPreviewSizes) {
			Camera.Size localPreviewSize = tempSize;
			if ((localPreviewSize.width >= 2.0F * this.cardHeight)
					|| (((localPreviewSize.width <= 0.8F * this.cardHeight) || (localPreviewSize.height <= 0.8F * this.cardWidth)) && ((localPreviewSize.width <= 1152.0F) || (localPreviewSize.height <= 979.19995F)))) {

			} else {
				resultPreviewSize = localPreviewSize;
			}
		}

		if (resultPreviewSize == null) {
			int i = 0;
			for (Camera.Size tempSize : paramPreviewSizes) {

				if (((tempSize).height * (tempSize).width <= i)) {

				} else {
					i = tempSize.height * tempSize.width;
					resultPreviewSize = tempSize;
				}
			}
		}

		Camera.Size resultPictureSize = null;

		resultPictureSize = pickPictureSizeBasedOnPreviewSize(
				resultPreviewSize, paramPictureSizes);

		return new Pair<Camera.Size, Camera.Size>(resultPreviewSize,
				resultPictureSize);
	}

	private Camera.Size pickPictureSizeBasedOnPreviewSize(
			Camera.Size paramPreviewSize, List<Camera.Size> paramPictureSizes) {
		Camera.Size resultPictureSize = null;
		for (Camera.Size tempSize : paramPictureSizes) {
			resultPictureSize = tempSize;
			if (Math.abs(tempSize.height / tempSize.width
					- paramPreviewSize.height / paramPreviewSize.width) >= 0.05D) {
			} else {
				break;
			}
		}
		
//		if (resultPictureSize == null) {
//			int i = 0;
//			for (Camera.Size tempSize : paramPictureSizes) {
//
//				if (((tempSize).height * (tempSize).width <= i)) {
//
//				} else {
//					i = tempSize.height * tempSize.width;
//					resultPictureSize = tempSize;
//				}
//			}
//		}

		return resultPictureSize;
	}

	private void resetCamera() {
		this.currentPreviewSize = null;
		this.currentPictureSize = null;
		try {
			currentCamera.stopPreview();
			currentCamera.release();
			currentCamera = null;
		} catch (Exception localException) {
		}
	}

	private void resetSurfaceView() {
		clearSurfaceView();
		this.currentTextureView = new TextureView(mContext);
		this.currentTextureView
				.setSurfaceTextureListener(getSurfaceTextureListener());

		this.textureContainer.setTextureView(this.currentTextureView,
				this.currentPreviewSize);
	}

	private void setUpContinuousFocusStuff(Camera paramCamera) {
		paramCamera
				.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
					public void onAutoFocusMoving(boolean paramBoolean,
							Camera paramCamera) {
					}
				});
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int k = paramInt4 - paramInt2;
		int i = paramInt3 - paramInt1;
		this.textureContainer.layout(0, 0, i, k);
		this.overlayFrame.layout(0, 0, i, k);
		this.askPhotoTextView.layout(0, 0, i, this.askPhotoTextView.getMeasuredHeight());
		this.mapQuestionView.layout(0, k - this.mapQuestionView.getMeasuredHeight() * 2, i, k
				- this.mapQuestionView.getMeasuredHeight());
		this.askMapTextView.layout(0, k - this.mapQuestionView.getMeasuredHeight() * 2, i, k
				- this.mapQuestionView.getMeasuredHeight() * 2 + this.askMapTextView.getMeasuredHeight());
		this.textQuestionView.layout(0,
				k - this.textQuestionView.getMeasuredHeight(), i, k);
		this.flashIconButton.layout(0 + this.smallIconInset,
				0 + this.smallIconInset, 0 + this.smallIconInset
						+ this.smallIconSize, 0 + this.smallIconInset
						+ this.smallIconSize);
		this.rotateCameraButton.layout(i
				- (this.smallIconSize + this.smallIconInset),
				0 + this.smallIconInset, i - this.smallIconInset, 0
						+ this.smallIconInset + this.smallIconSize);
		int m = k - this.bigIconInset;
		int j = m - this.bigIconSize;
		this.galleryButton.layout(0 + this.bigIconInset, j, 0
				+ this.bigIconInset + this.smallIconSize, m);
		k -= this.captureButtonInset;
		j = k - this.captureIconSize;
		i = (i + 0) / 2 - this.captureIconSize / 2;
		m = i + this.captureIconSize;
		this.captureButton.layout(i, j, m, k);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		this.cardWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		this.cardHeight = View.MeasureSpec.getSize(heightMeasureSpec);
		int i = View.MeasureSpec
				.makeMeasureSpec(this.smallIconSize, 1073741824);
		this.flashIconButton.measure(i, i);
		this.rotateCameraButton.measure(i, i);
		this.galleryButton.measure(i, i);
		this.captureButton.measure(View.MeasureSpec.makeMeasureSpec(
				this.captureIconSize, 1073741824), View.MeasureSpec
				.makeMeasureSpec(this.captureIconSize, 1073741824));
		this.textureContainer.measure(
				View.MeasureSpec.makeMeasureSpec(this.cardWidth, 1073741824),
				View.MeasureSpec.makeMeasureSpec(this.cardHeight, 1073741824));
		this.overlayFrame.measure(
				View.MeasureSpec.makeMeasureSpec(this.cardWidth, 1073741824),
				View.MeasureSpec.makeMeasureSpec(this.cardHeight, 1073741824));
		this.askPhotoTextView.measure(View.MeasureSpec.makeMeasureSpec(
				this.cardWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.cardHeight / 3, MeasureSpec.AT_MOST));
		this.askMapTextView.measure(View.MeasureSpec.makeMeasureSpec(
				this.cardWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.cardHeight / 3, MeasureSpec.AT_MOST));
		this.mapQuestionView.measure(View.MeasureSpec.makeMeasureSpec(this.cardWidth,
				MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
				this.cardHeight / 3, MeasureSpec.EXACTLY));
		this.textQuestionView.measure(View.MeasureSpec.makeMeasureSpec(
				this.cardWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.cardHeight / 3, MeasureSpec.EXACTLY));
		if (!this.hasBeenMeasured) {
			uiHandler.post(firstMeasureRunnable);
			this.hasBeenMeasured = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private TextureView.SurfaceTextureListener getSurfaceTextureListener() {
		return new TextureView.SurfaceTextureListener() {

			private void setUpSurfaceView(SurfaceTexture paramSurfaceTexture) {
				if (CameraView.currentCamera == null) {
					// CameraView.this.bus
					// .post(new UserActionFailure(
					// "camera setup: failure",
					// ((Activity) CameraView.this.mContext)
					// .getString(R.string.error_setting_up_camera),
					// false, new RuntimeException(
					// "Current camera is null")));
				}
				// while (true) {
				// return;

				try {
					CameraView.currentCamera
							.setPreviewTexture(paramSurfaceTexture);
					CameraView.currentCamera.startPreview();
				} catch (Throwable localThrowable) {
					// CameraView.this.bus
					// .post(new UserActionFailure(
					// "camera setup: failure",
					// ((Activity) CameraView.this.mContext)
					// .getString(R.string.error_setting_up_camera),
					// false, localThrowable));
				}
				// }
			}

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
					int width, int height) {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface,
					int width, int height) {
				// TODO Auto-generated method stub
				final SurfaceTexture mSurface = surface;
				CameraView.this.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (CameraView.needsCleanup) {
							CameraView.needsCleanup = false;
						}
						setUpSurfaceView(mSurface);
					}
				}, 600L);
			}
		};
	}

	private void tryToTakePicture(Camera paramCamera) {
		try {
			paramCamera.takePicture(null, null, new Camera.PictureCallback() {
				public void onPictureTaken(byte[] paramArrayOfByte,
						Camera paramCamera) {
					if (CameraView.this.pictureCallback != null) {
						int i = CameraView.this
								.getRotationForCurrentCameraAndDirection();
						if (CameraView.this.currentDirection == CameraView.CameraDirection.FRONT)
							i = (360 - i) % 360;

						boolean bool;
						if (CameraView.this.currentDirection != CameraView.CameraDirection.FRONT)
							bool = false;
						else
							bool = true;
						Pair<Float, Float> localPair = CameraView.this.textureContainer
								.getPreviewWidthAndHeightShowing();
						float f1 = ((Float) localPair.first).floatValue();
						float f3 = ((Float) localPair.second).floatValue();

						float f2 = f1
								* CameraView.this.currentPictureSize.width;
						float f4 = f3
								* CameraView.this.currentPictureSize.height;

						int j = (int) ((CameraView.this.currentPictureSize.width - f2) / 2.0F);
						int k = j + (int) f2;
						int m = (int) ((CameraView.this.currentPictureSize.height - f4) / 2.0F);
						int n = m + (int) f4;

						CameraView.PictureCallback localPictureCallback = CameraView.this.pictureCallback;
						localPictureCallback.onPictureTaken(bool,
								paramArrayOfByte, i, j, m, k, n);
					}
				}
			});
			return;
		} catch (Throwable localThrowable) {
			while (true) {
				this.bus.post(new UserActionFailure("take photo: failure",
						localThrowable.getMessage(), false, localThrowable));
			}
		}
	}

	public void initializeCamera(PictureCallback paramPictureCallback) {
		this.pictureCallback = paramPictureCallback;
		this.hasBeenMeasured = false;
	}

	public void pause() {
		cleanUpEventually();
	}

	public void resume() {
		this.hasBeenMeasured = false;
		if (needsCleanup)
			cleanupNow();
	}

	public class ImageSearchTypeDialog extends DialogFragment {
		public ImageSearchTypeDialog() {
		}

		public Dialog onCreateDialog(Bundle paramBundle) {
			Activity localActivity = (Activity) mContext;
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(
					localActivity,AlertDialog.THEME_HOLO_LIGHT);
			localBuilder.setTitle(R.string.image_search_type_picker_title)
					.setItems(R.array.image_search_types_array,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent localIntent;
									if (which != 0) {
										if (which == 1) {
											
											MobclickAgent.onEvent(mContext, UMengEventID.IMAGE_SEARCH_REMOTE);
											
											localIntent = new Intent(mContext,
													SearchImageActivity.class);
											((Activity) mContext)
													.startActivityForResult(
															localIntent, 130);
										}
									} else {
										
										MobclickAgent.onEvent(mContext, UMengEventID.IMAGE_SEARCH_GALLERY);
										
										localIntent = new Intent(
												"android.intent.action.PICK",
												MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
										localIntent
												.putExtra(
														"android.intent.extra.LOCAL_ONLY",
														true);
										
										Intent intent = new Intent(Intent.ACTION_PICK,
												android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
										
										((Activity) mContext)
												.startActivityForResult(
														intent, 129);
									}
								}
							});
			
			localBuilder.setNegativeButton(
					getText(R.string.junk_drawer_dialog_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked Cancel so do some stuff */
							dialog.cancel();
						}
					});
			return localBuilder.create();
		}
	}

	private class FlashState {
		private int currentIndex = 0;
		private final Integer[] resourceIds;

		private FlashState() {
			Integer[] arrayOfInteger = new Integer[3];
			arrayOfInteger[0] = Integer.valueOf(R.drawable.camera_flash_auto);
			arrayOfInteger[1] = Integer.valueOf(R.drawable.camera_flash_off);
			arrayOfInteger[2] = Integer.valueOf(R.drawable.camera_flash_on);
			this.resourceIds = arrayOfInteger;
		}

		public int getResourceIdForFlashState() {
			return this.resourceIds[this.currentIndex].intValue();
		}

		public void nextFlashState(Camera paramCamera) {
			this.currentIndex = ((1 + this.currentIndex) % this.resourceIds.length);
			Camera.Parameters localParameters;
			localParameters = paramCamera.getParameters();
			if (currentIndex == 0) {
				localParameters.setFlashMode("auto");
				paramCamera.setParameters(localParameters);
			} else if (currentIndex == 1) {
				localParameters.setFlashMode("off");
				paramCamera.setParameters(localParameters);
			} else {
				localParameters.setFlashMode("on");
				paramCamera.setParameters(localParameters);
			}
		}
	}

	public static abstract interface PictureCallback {
		public abstract void onPictureTaken(boolean paramBoolean,
				byte[] paramArrayOfByte, int paramInt1, int paramInt2,
				int paramInt3, int paramInt4, int paramInt5);
	}

	static enum CameraDirection {
		REAR, FRONT
	}

}
