package com.medialab.jelly;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.controller.FullCardView;
import com.medialab.jelly.controller.LoginUiController;
import com.medialab.jelly.controller.SimpleStackController;
import com.medialab.jelly.controller.SimpleStackListener;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.controller.WelcomeCardController;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UImageManager;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.view.LoginAndRegisterView;
import com.medialab.jelly.view.LoginView;
import com.medialab.jelly.view.LoginWithCodeView;
import com.medialab.jelly.view.RegisterStep1View;
import com.medialab.jelly.view.RegisterStep2View;

public class LoginAndRegisterActivity extends BaseActivity {

	StarfishScreenUtils screenUtils;

	SimpleStackController localSimpleStackController;
	WelcomeCardController mWelcomeCardController;
	LoginUiController mLoginAndRegisterUiController;
	LoginUiController mLoginUiController;
	LoginUiController mRegisterStep1UiController;
	LoginUiController mRegisterStep2UiController;
	LoginUiController mLoginWithCodeUiController;

	// 下一个要展示的View
	private ShowViewType nextShowType;
//	private ShowViewType currentShowType;
	private ShowViewType preShowType;

	private String regMobileStr;
	private String regPasswordStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		gotoActivity();
	}

	private void gotoActivity() {
		JellyUser mSelf = JellyApplication.getMineInfo(this);

		if (mSelf != null) {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			this.startActivity(intent);
			this.finish();
		} else {

			getLocation();
			// setContentView
			screenUtils = new StarfishScreenUtils(this, getWindowManager());
			localSimpleStackController = new SimpleStackController(this,
					screenUtils);
			this.setContentView(localSimpleStackController.getView());

			// 设置View的背景颜色
			localSimpleStackController.getView().setBackgroundColor(
					this.getResources().getColor(R.color.activity_bg));

			initView();

			SharedPreferences sp = UTools.Storage.getSharedPreferences(this,
					UConstants.BASE_PREFS_NAME);
			boolean isFirstLogin = sp.getBoolean(UConstants.FIRST_LOGIN, true);

			if (isFirstLogin) {
				// 首先加载welcome card
				setShowViewType(ShowViewType.OTHER, ShowViewType.WELCOMECARD,
						ShowViewType.LOGINANDREGISTER);
				localSimpleStackController
						.addBaseController(mWelcomeCardController);

				SharedPreferences.Editor mEditor = UTools.Storage
						.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
				mEditor.putBoolean(UConstants.FIRST_LOGIN, false);
				mEditor.commit();
			} else {
				// 否则直接加在登录注册
				localSimpleStackController
						.addBaseController(mLoginAndRegisterUiController);

				setShowViewType(ShowViewType.WELCOMECARD,
						ShowViewType.LOGINANDREGISTER, ShowViewType.OTHER);
			}

			localSimpleStackController.setListener(new SimpleStackListener() {

				@Override
				public void didDismiss(
						DismissableViewController paramDismissableViewController,
						boolean paramBoolean) {
					// TODO Auto-generated method stub

					if (nextShowType == ShowViewType.LOGINANDREGISTER) {
						localSimpleStackController
								.addBaseController(mLoginAndRegisterUiController);

						setShowViewType(ShowViewType.WELCOMECARD,
								ShowViewType.LOGINANDREGISTER,
								ShowViewType.OTHER);
					} else if (nextShowType == ShowViewType.LOGIN) {
						localSimpleStackController
								.addBaseController(mLoginUiController);
						setShowViewType(ShowViewType.LOGINANDREGISTER,
								ShowViewType.LOGIN, ShowViewType.OTHER);
					} else if (nextShowType == ShowViewType.REGISTER1) {
						localSimpleStackController
								.addBaseController(mRegisterStep1UiController);
						setShowViewType(ShowViewType.LOGINANDREGISTER,
								ShowViewType.REGISTER1, ShowViewType.OTHER);
					} else if (nextShowType == ShowViewType.REGISTER2) {
						localSimpleStackController
								.addBaseController(mRegisterStep2UiController);
						setShowViewType(ShowViewType.REGISTER1,
								ShowViewType.REGISTER2, ShowViewType.OTHER);
					} else if (nextShowType == ShowViewType.LOGINWITHCODE) {
						localSimpleStackController
								.addBaseController(mLoginWithCodeUiController);
						setShowViewType(ShowViewType.LOGIN,
								ShowViewType.LOGINWITHCODE, ShowViewType.OTHER);
					}

				}
			});
		}
	}

	private void initView() {
		// WelcomeCard View

		final FullCardView localFullCardView = new FullCardView(this,
				this.screenUtils);
		ImageView localTouchHappyImageView = new ImageView(this);
		localTouchHappyImageView.setImageResource(R.drawable.welcome_card);

		localTouchHappyImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		localFullCardView.setCurrentView(localTouchHappyImageView);

		mWelcomeCardController = new WelcomeCardController(localFullCardView);
		// WelcomeCard view end

		// loginAndRegisterUiView
		LoginAndRegisterView mLoginAndRegisterView = new LoginAndRegisterView(
				this);

		mLoginAndRegisterUiController = new LoginUiController(
				mLoginAndRegisterView);
		// loginAndRegisterUiView view end

		// loginUiView
		LoginView mLoginView = new LoginView(this);

		mLoginUiController = new LoginUiController(mLoginView);
		// loginUiView view end

		// loginWithCodeView
		LoginWithCodeView mLoginWithCodeView = new LoginWithCodeView(this);

		mLoginWithCodeUiController = new LoginUiController(mLoginWithCodeView);
		// loginWithCodeView End

		// registerStep1UiView

		RegisterStep1View mRegisterStep1View = new RegisterStep1View(this);

		mRegisterStep1UiController = new LoginUiController(mRegisterStep1View);

		// registerStep1UiView end

		// registerStep2UiView
		RegisterStep2View mRegisterStep2View = new RegisterStep2View(this);

		mRegisterStep2UiController = new LoginUiController(mRegisterStep2View);

		// registerStep2UiView end
	}

	// onClickListener 方法

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (preShowType == ShowViewType.LOGINANDREGISTER) {
			nextShowType = ShowViewType.LOGINANDREGISTER;
			localSimpleStackController.dismissCurrentBaseView();
		} else if (preShowType == ShowViewType.WELCOMECARD
				|| preShowType == ShowViewType.OTHER) {
			this.finish();
		} else if (preShowType == ShowViewType.REGISTER1) {
			nextShowType = ShowViewType.REGISTER1;
			localSimpleStackController.dismissCurrentBaseView();
		} else if (preShowType == ShowViewType.LOGIN) {
			nextShowType = ShowViewType.LOGIN;
			localSimpleStackController.dismissCurrentBaseView();
		}
	}

	private void setShowViewType(ShowViewType preViewType,
			ShowViewType currentViewType, ShowViewType nextViewType) {
		this.preShowType = preViewType;
//		this.currentShowType = currentViewType;
		this.nextShowType = nextViewType;
	}

	public void loginBtnClick() {
		nextShowType = ShowViewType.LOGIN;
		localSimpleStackController.dismissCurrentBaseView();
	}

	public void registerBtnClick() {
		nextShowType = ShowViewType.REGISTER1;
		localSimpleStackController.dismissCurrentBaseView();
	}

	public void registerNextBtnClick() {
		nextShowType = ShowViewType.REGISTER2;
		localSimpleStackController.dismissCurrentBaseView();
	}

	public void loginWithCodeBtnClick() {
		nextShowType = ShowViewType.LOGINWITHCODE;
		localSimpleStackController.dismissCurrentBaseView();
	}

	public String getRegMobileStr() {
		return regMobileStr;
	}

	public void setRegMobileStr(String regMobileStr) {
		this.regMobileStr = regMobileStr;
	}

	public String getRegPasswordStr() {
		return regPasswordStr;
	}

	public void setRegPasswordStr(String regPasswordStr) {
		this.regPasswordStr = regPasswordStr;
	}

	public void hideKeyBoard() {
		// WindowManager.LayoutParams params = getWindow().getAttributes();
		// if (params.softInputMode ==
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
		// // 隐藏软键盘
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// params.softInputMode =
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
		// }
		((InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private boolean isHeadPicSet = false;

	public boolean isHeadPicSet() {
		return isHeadPicSet;
	}

	public void setHeadPicSet(boolean isHeadPicSet) {
		this.isHeadPicSet = isHeadPicSet;
	}

	@SuppressWarnings("deprecation")
	public void ShowImageDialog() {
		showDialog(DIALOG_YES_NO_LONG_MESSAGE);
	}

	// 上传头像
	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
			return new AlertDialog.Builder(LoginAndRegisterActivity.this,
					AlertDialog.THEME_HOLO_LIGHT)
					.setTitle(getText(R.string.login_select_dialog_tips))
					.setMessage(getText(R.string.login_select_dialog_context))
					.setPositiveButton(getText(R.string.login_select_dialog_from_photo_album),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
									startPhotoAlbum();
								}
							})
					.setNeutralButton(getText(R.string.login_select_dialog_from_camera),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked Something so do some stuff */
									startCamera();
								}
							}).create();
		}

		return null;
	}

	/**
	 * @author liananse 2013-7-21
	 */
	private void startPhotoAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(intent, UConstants.GETIMAGE);
	}

	private void startCamera() {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(UTools.Storage.getHeadPicImagePath())));
		startActivityForResult(i, UConstants.CAPUTRE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {

		case RESULT_OK:

			if (requestCode == UConstants.CAPUTRE) {
				// Load up the image's dimensions not the image itself
				BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
				bmpFactoryOptions.inJustDecodeBounds = true;
				Bitmap bmp = BitmapFactory
						.decodeFile(UTools.Storage.getHeadPicImagePath(),
								bmpFactoryOptions);

				int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
						/ (float) 1080);

				if (widthRatio > 1) {
					bmpFactoryOptions.inSampleSize = widthRatio;
				}

				bmpFactoryOptions.inJustDecodeBounds = false;
				bmp = BitmapFactory
						.decodeFile(UTools.Storage.getHeadPicImagePath(),
								bmpFactoryOptions);

				String imagePath = UTools.Storage.getHeadPicSmallImagePath();

				UImageManager.saveBtimapToFile(bmp, imagePath);

				if (mRegisterStep2UiController != null) {
					((RoundedImageView) (mRegisterStep2UiController.getView())
							.findViewById(R.id.register_avatar))
							.setImageBitmap(bmp);
					
					if (!((EditText) mRegisterStep2UiController.getView().findViewById(R.id.register_username_et)).getText().toString().trim().equals("")) {
						((TextView) mRegisterStep2UiController.getView().findViewById(R.id.register_btn)).setEnabled(true);
					}
				}
				setHeadPicSet(true);
			} else if (requestCode == UConstants.GETIMAGE) {
				Uri imageFileUri = data.getData();

				try {
					BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
					bmpFactoryOptions.inJustDecodeBounds = true;

					Bitmap bmp = BitmapFactory.decodeStream(
							getContentResolver().openInputStream(imageFileUri),
							null, bmpFactoryOptions);

					int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
							/ (float) 1080);
					if (widthRatio > 1) {
						bmpFactoryOptions.inSampleSize = widthRatio;
					}

					bmpFactoryOptions.inJustDecodeBounds = false;
					bmp = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(imageFileUri), null,
							bmpFactoryOptions);

					String imagePath = UTools.Storage
							.getHeadPicSmallImagePath();

					if (UImageManager.saveBtimapToFile(bmp, imagePath)) {

					}

					if (mRegisterStep2UiController != null) {
						((RoundedImageView) (mRegisterStep2UiController
								.getView()).findViewById(R.id.register_avatar))
								.setImageBitmap(bmp);
						
						if (!((EditText) mRegisterStep2UiController.getView().findViewById(R.id.register_username_et)).getText().toString().trim().equals("")) {
							((TextView) mRegisterStep2UiController.getView().findViewById(R.id.register_btn)).setEnabled(true);
						}
					}
					setHeadPicSet(true);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			break;

		default:
			break;
		}
	}

	private void getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				SharedPreferences.Editor mEditor = UTools.Storage
						.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
				mEditor.putString(UConstants.LOCATION_LATITUDE,
						String.valueOf(location.getLatitude()));
				mEditor.putString(UConstants.LOCATION_LONGITUDE,
						String.valueOf(location.getLongitude()));
				mEditor.commit();
			}
		} else {
			LocationListener locationListener = new LocationListener() {

				// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {

				}

				// Provider被enable时触发此函数，比如GPS被打开
				@Override
				public void onProviderEnabled(String provider) {

				}

				// Provider被disable时触发此函数，比如GPS被关闭
				@Override
				public void onProviderDisabled(String provider) {

				}

				// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
				@Override
				public void onLocationChanged(Location location) {
					if (location != null) {
						Log.e("Map",
								"Location changed : Lat: "
										+ location.getLatitude() + " Lng: "
										+ location.getLongitude());
					}
				}
			};
			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							1000, 0, locationListener);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				SharedPreferences.Editor mEditor = UTools.Storage
						.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
				mEditor.putString(UConstants.LOCATION_LATITUDE,
						String.valueOf(location.getLatitude()));
				mEditor.putString(UConstants.LOCATION_LONGITUDE,
						String.valueOf(location.getLongitude()));
				mEditor.commit();
			}
		}
	}

	public static enum ShowViewType {
		WELCOMECARD, LOGINANDREGISTER, LOGIN, REGISTER1, REGISTER2, LOGINWITHCODE, OTHER
	}
}
