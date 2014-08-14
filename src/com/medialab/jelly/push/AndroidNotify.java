package com.medialab.jelly.push;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.MainActivity;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.ui.event.NewUnReadTipsEvent;
import com.medialab.jelly.ui.event.UpdateBadgeEvent;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;

public class AndroidNotify implements Notify {

	private final Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public void notify(Context context, NotifyMessage mes) {
		// TODO Auto-generated method stub

		SharedPreferences sp = UTools.Storage.getSharedPreferences(context,
				UConstants.BASE_PREFS_NAME);
		boolean noticeSound = sp.getBoolean(UConstants.NOTICE_SOUNDS_SWITCH,
				true);
		boolean noticePush = sp.getBoolean(UConstants.NOTICE_PUSH_SWITCH, true);

		NotificationCompat.Builder mBuilder;
		if (noticeSound) {
			mBuilder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(
							context.getText(R.string.notification_title))
					.setContentText(mes.content)
					.setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true);
		} else {
			mBuilder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(
							context.getText(R.string.notification_title))
					.setContentText(mes.content)
					.setDefaults(Notification.DEFAULT_VIBRATE)
					.setAutoCancel(true);
		}
		// Use Notifiaction.Default_all (sound light and so on)
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		resultIntent.putExtra(UConstants.FROM_WHERE,
				UConstants.FROM_NOTIFICATION);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		if (noticePush) {
			mNotificationManager.notify(mes.type, mBuilder.build());
		}

		// 有新的保存到文件
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				context, UConstants.BASE_PREFS_NAME);
		mEditor.putBoolean(UConstants.HAS_NEW_ACTIVITY, true);

		// 如果是收到朋友申请或者是通讯录匹配到新朋友
		if (mes.type == 11 || mes.type == 13) {
			mEditor.putBoolean(UConstants.HAS_NEW_FRIEND_ACTIVITY, true);
		} else if (mes.type == 12) {
			// 审核朋友通过
			mEditor.putBoolean(UConstants.HAS_FRIEND_ACTIVITY, true);
		} else {
			// 有新的个人动态保存到文件
			mEditor.putBoolean(UConstants.HAS_NEW_SELF_ACTIVITY, true);
		}

		mEditor.commit();

		final Context validContext = context;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// 更新小红点
				JellyApplication.getBus().post(new UpdateBadgeEvent(true));

				JellyApplication.getBus().post(new NewUnReadTipsEvent(true));

				if (isYesterDay(validContext)) {
					validMethod(validContext);
				}
			}
		});

	}

	/**
	 * 这个接口一天最多请求一次
	 * 
	 * @param context
	 * @return
	 */
	private boolean isYesterDay(Context context) {

		SharedPreferences sp = UTools.Storage.getSharedPreferences(context,
				UConstants.BASE_PREFS_NAME);
		long lastValidTime = sp.getLong(UConstants.LAST_VALID_TIME, 0);

		if (lastValidTime == 0) {
			return true;
		}

		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date());
		int nowYear = nowCalendar.get(Calendar.YEAR);
		int nowDay = nowCalendar.get(Calendar.DAY_OF_YEAR);

		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTimeInMillis(lastValidTime);
		int beforeYear = timeCalendar.get(Calendar.YEAR);
		int beforeDay = timeCalendar.get(Calendar.DAY_OF_YEAR);

		if (nowYear == beforeYear && nowDay == beforeDay) {
			return false;
		} else {
			return true;
		}
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private void validMethod(Context context) {

		// 上次验证的时间保存到本地
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				context, UConstants.BASE_PREFS_NAME);
		mEditor.putLong(UConstants.LAST_VALID_TIME, System.currentTimeMillis());
		mEditor.commit();

		Map<String, String> params = new HashMap<String, String>();

		mDataLoader.postData(UConfig.HEART_BEAT_URL, params, context, null);
	}

}
