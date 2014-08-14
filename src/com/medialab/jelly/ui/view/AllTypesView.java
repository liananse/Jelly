package com.medialab.jelly.ui.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.SupportMapFragment;
import com.medialab.jelly.R;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.view.CustomTextView;

public class AllTypesView extends ViewGroup {

	private final Context mContext;
	private static Camera currentCamera;
	private static boolean needsCleanup = false;
	private TextureView currentTextureView;

	private int cardHeight;
	private int cardWidth;
	private CameraDirection currentDirection;

	private final FrameLayout overlayFrame;

	private final int smallIconInset;
	private final int smallIconSize;
	private SurfaceClipperPreviewFrame textureContainer;

	private final int rearCameraId;
	private final Handler handler = new Handler();
	private boolean hasBeenMeasured = false;
	private final int frontCameraId;

	private final Runnable firstMeasureRunnable;

	private final Handler uiHandler = new Handler();

	private Camera.Size currentPictureSize;
	private Camera.Size currentPreviewSize;

	// 文字层
	private final TextView textQuestionView;
	private int topTextPadding;

	// 地图层
	private final LinearLayout mapQuestionView;
//	MapView mMapView;
	SupportMapFragment map;
	public AllTypesView(Context context, int paramRearCameraId,
			int paramFrontCameraId) {
		super(context);
		// TODO Auto-generated constructor stub

		this.mContext = context;
		this.rearCameraId = paramRearCameraId;
		this.frontCameraId = paramFrontCameraId;

		this.overlayFrame = new FrameLayout(getContext());
		this.overlayFrame.setBackgroundResource(R.drawable.camera_overlay);

		textureContainer = new SurfaceClipperPreviewFrame(context);

		if (this.rearCameraId < 0) {
			if (this.frontCameraId >= 0)
				this.currentDirection = CameraDirection.FRONT;
		} else
			this.currentDirection = CameraDirection.REAR;

		this.firstMeasureRunnable = new Runnable() {
			public void run() {
				setUpCamera(AllTypesView.this.currentDirection);
				resetSurfaceView();
				requestLayout();
			}
		};

		Resources localResources = getResources();
		this.smallIconSize = localResources
				.getDimensionPixelSize(R.dimen.camera_small_icon_size);
		this.smallIconInset = localResources
				.getDimensionPixelSize(R.dimen.camera_small_icon_inset);

		this.textureContainer.setBackgroundColor(getResources().getColor(
				R.color.black));
		this.topTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_bottom_padding);

		// 另外两层view
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

		// 地图层
		mapQuestionView = ((LinearLayout) LinearLayout.class
				.cast(((LayoutInflater) mContext
						.getSystemService("layout_inflater")).inflate(
						R.layout.alltypes_view_map_view, this, false)));
		
		MapStatus ms = new MapStatus.Builder().overlook(-20).zoom(15).build();
		BaiduMapOptions bo = new BaiduMapOptions().mapStatus(ms)
				.compassEnabled(false).zoomControlsEnabled(false);
		map = SupportMapFragment.newInstance(bo);
		FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
		manager.beginTransaction().add(R.id.map, map, "map_fragment").commit();
		
//		mMapView = new MapView(mContext);
		
		System.out.println("allTypesView all");

//		SharedPreferences sp = UTools.Storage.getSharedPreferences(context,
//				UConstants.BASE_PREFS_NAME);
//
//		mMapView.getController().animateTo(
//				new GeoPoint((int) (Double.parseDouble(sp.getString(
//						UConstants.LOCATION_LATITUDE, "0.0")) * 1e6),
//						(int) (Double.parseDouble(sp.getString(
//								UConstants.LOCATION_LONGITUDE, "0.0")) * 1e6)));

		addView(textureContainer);
		addView(mapQuestionView);
		addView(textQuestionView);
		addView(this.overlayFrame);
	}

	private void cleanUpEventually() {
		needsCleanup = true;
		this.handler.postDelayed(new Runnable() {
			public void run() {
				if (AllTypesView.needsCleanup)
					AllTypesView.this.cleanupNow();
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

		return resultPictureSize;
	}

	private void resetCamera() {
		this.currentPreviewSize = null;
		this.currentPictureSize = null;
		if (currentCamera != null) {
			System.out.println("Current camera is still valid");
		}
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

		this.mapQuestionView.layout(0, k - this.mapQuestionView.getMeasuredHeight() * 2, i, k
				- this.mapQuestionView.getMeasuredHeight());
		this.textQuestionView.layout(0,
				k - this.textQuestionView.getMeasuredHeight(), i, k);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		this.cardWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		this.cardHeight = View.MeasureSpec.getSize(heightMeasureSpec);
		int i = View.MeasureSpec.makeMeasureSpec(this.smallIconSize,
				MeasureSpec.EXACTLY);
		this.textureContainer.measure(View.MeasureSpec.makeMeasureSpec(
				this.cardWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.cardHeight, MeasureSpec.EXACTLY));
		this.overlayFrame.measure(View.MeasureSpec.makeMeasureSpec(
				this.cardWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.cardHeight, MeasureSpec.EXACTLY));
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
				if (AllTypesView.currentCamera == null) {
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
					AllTypesView.currentCamera
							.setPreviewTexture(paramSurfaceTexture);
					AllTypesView.currentCamera.startPreview();
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
				AllTypesView.this.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (AllTypesView.needsCleanup) {
							AllTypesView.needsCleanup = false;
						}
						setUpSurfaceView(mSurface);
					}
				}, 600L);
			}
		};
	}

	public void pause() {
		cleanUpEventually();
//		mMapView.onPause();
		System.out.println("allTypesView pause");
	}

	public void resume() {
		this.hasBeenMeasured = false;
		if (needsCleanup)
			cleanupNow();
//		mMapView.onResume();
		System.out.println("allTypesView resume");
	}

	static enum CameraDirection {
		REAR, FRONT
	}

}
