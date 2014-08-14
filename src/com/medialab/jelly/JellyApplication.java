package com.medialab.jelly;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;

import com.baidu.mapapi.SDKInitializer;
import com.medialab.jelly.db.DDBOpenHelper;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UDataCleanManager;
import com.medialab.jelly.util.UTools;
import com.squareup.otto.Bus;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.mipush.sdk.MiPushClient;

public class JellyApplication extends Application {

	// user your appid the key.
	public static final String APP_ID = "2882303761517189884";
	// user your appid the key.
	public static final String APP_KEY = "5251718932884";

	// 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
	// com.xiaomi.mipushdemo
	public static final String TAG = "com.xiaomi.mipushdemo";

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		// 字体初始化
		FontManager.initializeTypefaces(this);
		bus = new Bus();

		if (UConstants.isOfficial) {
			Constants.useOfficial();
		} else {
			Constants.useSandbox();
		}

		// 注册push服务，注册成功后会向DemoMessageReceiver发送广播
		// 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
		if (shouldInit()) {
			MiPushClient.registerPush(this, APP_ID, APP_KEY);
		}
	}

	private boolean shouldInit() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	private static JellyUser mine;

	private static Bus bus;

	public static Bus getBus() {

		if (bus == null) {
			bus = new Bus();
		}
		return bus;
	}

	public static void initMineInfo(Context ctx, JellyUser model) {
		mine = model;
		if (mine != null) {
			DDBOpenHelper db = DDBOpenHelper.getInstance(ctx);
			db.insertOnlyClassData(model, DDBOpenHelper.USER_TABLE_NAME);
		}
	}

	public static void clearMineInfo() {
		mine = null;
	}

	@SuppressWarnings("unchecked")
	public static JellyUser getMineInfo(Context ctx) {
		if (mine == null) {
			DDBOpenHelper db = DDBOpenHelper.getInstance(ctx);
			Object o = db.query(DDBOpenHelper.USER_TABLE_NAME, JellyUser.class,
					null, null, null);
			List<JellyUser> mList = (ArrayList<JellyUser>) o;

			if (mList != null && mList.size() > 0) {
				mine = mList.get(0);
			}
		}
		return mine;
	}

	public static void relogin(Activity act) {
		SharedPreferences sp = UTools.Storage.getSharedPreferences(act,
				UConstants.BASE_PREFS_NAME);
		String xiaomiRegId = sp.getString(UConstants.XIAOMI_REGID, "");

		// 清空数据
		clearMineInfo();
		UDataCleanManager.cleanApplicationData(act);

		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				act, UConstants.BASE_PREFS_NAME);
		mEditor.putString(UConstants.XIAOMI_REGID, xiaomiRegId);
		mEditor.commit();

		// 跳转
		UTools.activityhelper.clearAllBut(act);
		Intent intent = new Intent();
		intent.setClass(act, LoginAndRegisterActivity.class);
		act.startActivity(intent);
		act.finish();
	}

	// 与发问题相关
	public static boolean isMapQuestion = false;

	public static double mapQuestionLatitude = 0.0;

	public static double mapQuestionLongitude = 0.0;

	public static void setIsMapQuestion(boolean paramIsMapQuestion) {
		isMapQuestion = paramIsMapQuestion;
	}

	public static void setMapQuestionLatitude(double paramMapQuestionLatitude) {
		mapQuestionLatitude = paramMapQuestionLatitude;
	}

	public static void setMapQuesitonLongitude(double paramMapQuestionLongitude) {
		mapQuestionLongitude = paramMapQuestionLongitude;
	}

	public static boolean IsMapQuestion() {
		return isMapQuestion;
	}

	public static double getMapQuestionLatitude() {
		return mapQuestionLatitude;
	}

	public static double getMapQuestionLongitude() {
		return mapQuestionLongitude;
	}

	// 与发问题相关 end
}
