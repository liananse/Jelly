package com.medialab.jelly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.Contact;
import com.medialab.jelly.resultmodel.DeviceInfoResultModel;
import com.medialab.jelly.ui.adapter.util.JunkDrawerUtils;
import com.medialab.jelly.ui.adapter.util.SingleQuestionControllerUtils;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.GalleryPictureSelectedEvent;
import com.medialab.jelly.ui.event.LinkChoosenEvent;
import com.medialab.jelly.ui.event.NotificationOverlaySelectedEvent;
import com.medialab.jelly.ui.event.UpdateBadgeEvent;
import com.medialab.jelly.ui.viewcontroller.JellyViewController;
import com.medialab.jelly.ui.viewcontroller.MainViewController;
import com.medialab.jelly.util.MD5;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * @author zenghui
 * 
 *         这个是主界面
 * 
 */
public class MainActivity extends BaseActivity {

	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (getIntent().getStringExtra(UConstants.FROM_WHERE) != null
				&& getIntent().getStringExtra(UConstants.FROM_WHERE).equals(
						UConstants.FROM_NOTIFICATION)) {
			isFromNotification = true;
		} else {
			isFromNotification = false;
		}

		initJellyViewController();
		uploadDeviceInfo();
		initLocation();
	}
	
	private void initLocation() {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	// 变量
	Bus bus;
	ExecutorService executorService;

	// jelly view frame
	StarfishScreenUtils screenUtils;

	MainViewController mainViewController;
	JellyViewController jellyViewController;

	private void initJellyViewController() {
		// 初始化JellyViewFrame
		screenUtils = new StarfishScreenUtils(this, getWindowManager());

		// bus = new Bus();
		bus = JellyApplication.getBus();

		executorService = Executors.newCachedThreadPool();

		// ///////////////////////////////////////////

		JunkDrawerUtils paramJunkDrawerUtils = new JunkDrawerUtils(this);

		SingleQuestionControllerUtils paramSingleQuestionControllerUtils = new SingleQuestionControllerUtils(
				paramJunkDrawerUtils, screenUtils, executorService);

		// ////////////////////////////////////////////////

		mainViewController = new MainViewController(this, screenUtils,
				paramSingleQuestionControllerUtils);

		jellyViewController = new JellyViewController(this, mainViewController);

		this.setContentView(jellyViewController.getView());
	}

	private Object postMe;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 132:
			if (resultCode == RESULT_OK)
				this.postMe = new LinkChoosenEvent(data.getData());
			break;
		case 129:
			if ((data == null) || (data.getData() == null))
				break;
			this.postMe = new GalleryPictureSelectedEvent(data.getData());
			break;
		case 130:
			if ((data == null) || (data.getData() == null))
				break;
			this.postMe = new GalleryPictureSelectedEvent(data.getData());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.bus.register(this);
		this.jellyViewController.onShow(null);
		if (this.postMe != null) {
			this.bus.post(this.postMe);
			this.postMe = null;
		}

		SharedPreferences sp = UTools.Storage.getSharedPreferences(this,
				UConstants.BASE_PREFS_NAME);
		boolean hasNewActivity = sp.getBoolean(UConstants.HAS_NEW_ACTIVITY,
				false);

		if (hasNewActivity) {
			this.bus.post(new UpdateBadgeEvent(true));
		}

		if (isFromNotification) {
			this.bus.post(new NotificationOverlaySelectedEvent());

			// clear all notifications
			NotificationManager mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();

			isFromNotification = false;
		}
	}

	@Override
	protected void onDestroy() {
		mLocClient.stop();
		super.onDestroy();
	}
	
	private boolean isFromNotification = false;

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		// 从通知栏跳过来，打开notification View
		isFromNotification = true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		jellyViewController.onHide(null);
		this.bus.unregister(this);
	}

	@Subscribe
	public void backPressedEvent(BackPressedEvent paramBackPressedEvent) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!this.jellyViewController.onBackPressed())
			super.onBackPressed();
	}

	// 上传设备信息
	HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private void uploadDeviceInfo() {
		SharedPreferences sp = UTools.Storage.getSharedPreferences(this,
				UConstants.BASE_PREFS_NAME);
		String xiaomiRegId = sp.getString(UConstants.XIAOMI_REGID, "");

		Map<String, String> params = new HashMap<String, String>();
		params.put("xiaomiUserId", xiaomiRegId);
		params.put("x", sp.getString(UConstants.LOCATION_LATITUDE, "0.0"));
		params.put("y", sp.getString(UConstants.LOCATION_LONGITUDE, "0.0"));

		mDataLoader.postData(UConfig.UPLOAD_DEVICE_INFO_URL, params, this,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							DeviceInfoResultModel mModel = gson.fromJson(
									source,
									new TypeToken<DeviceInfoResultModel>() {
									}.getType());

							if (mModel != null
									&& mModel.result == UConstants.SUCCESS
									&& mModel.data != null) {
								
								SharedPreferences.Editor mEditor = UTools.Storage
										.getSharedPreEditor(MainActivity.this,
												UConstants.BASE_PREFS_NAME);
								mEditor.putInt(UConstants.FRIENDS_NUM, mModel.data.friendNum);
								mEditor.putInt(UConstants.UNLOCKED_FRIENDS_NUM, mModel.data.unLockFriendNum);
								mEditor.putString(UConstants.INVITE_CARD_URL, mModel.data.inviteCard);
								mEditor.putString(UConstants.OUT_OF_QUESTION_INVITE_CARD_URL, mModel.data.outOfQuestionInviteCard);
								mEditor.commit();

								String contactsString = gson
										.toJson(getPhoneContacts());
								String localMD5 = MD5.encode(contactsString);

								// 如果通讯录有改变
								if (!localMD5.equals(mModel.data.mobileListMd5)) {
									uploadContactsMethod(localMD5,
											contactsString);
								}

							} else if (mModel != null && mModel.result == UConstants.INVALID) {
								// 等于退出登录
								UToast.showShortToast(MainActivity.this, mModel.message);
								JellyApplication.relogin(MainActivity.this);
							}

						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void uploadContactsMethod(String localMd5, String contactsString) {
		Map<String, String> params = new HashMap<String, String>();

		params.put("mobileList", contactsString);
		params.put("mobileListMd5", localMd5);

		mDataLoader.postData(UConfig.MOBILE_LIST_URL, params, this, null);
	}

	private List<Contact> getPhoneContacts() {

		List<Contact> list = new ArrayList<Contact>();

		ContentResolver resolver = getContentResolver();

		String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME,
				Phone.NUMBER };
		/** 联系人显示名称 **/
		int PHONES_DISPLAY_NAME_INDEX = 0;

		/** 电话号码 **/
		int PHONES_NUMBER_INDEX = 1;

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				phoneNumber = phoneNumber.trim();
				phoneNumber = phoneNumber.replaceAll(" ", "");

				if (isMobile(phoneNumber)) {
					Contact man = new Contact();
					man.setName(contactName);
					man.setMobile(String.valueOf(phoneNumber.trim()
							.replace(" ", "").replace("+", "")));

					list.add(man);
				}
			}

			phoneCursor.close();
		}

		return list;
	}

	public static boolean isMobile(String mobile) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");  
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			SharedPreferences.Editor mEditor = UTools.Storage
					.getSharedPreEditor(MainActivity.this,
							UConstants.BASE_PREFS_NAME);
			mEditor.putString(UConstants.LOCATION_LATITUDE,
					String.valueOf(location.getLatitude()));
			mEditor.putString(UConstants.LOCATION_LONGITUDE,
					String.valueOf(location.getLongitude()));
			
			if (location.hasAddr() && location.getCity() != null) {
				mEditor.putString(UConstants.LOCATION_CITY, location.getCity());
			}
			mEditor.commit();

			Map<String, String> params = new HashMap<String, String>();
			params.put("x", String.valueOf(location.getLatitude()));
			params.put("y", String.valueOf(location.getLongitude()));

			mDataLoader.postData(UConfig.UPLOAD_DEVICE_INFO_URL, params,
					MainActivity.this, null);
			
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}
