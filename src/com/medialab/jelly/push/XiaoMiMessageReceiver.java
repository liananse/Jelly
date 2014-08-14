package com.medialab.jelly.push;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

public class XiaoMiMessageReceiver extends PushMessageReceiver {

	private static final String TAG = XiaoMiMessageReceiver.class
			.getSimpleName();

	private String mRegId;
	private String mTopic;
	private String mAlias;
	private String mStartTime;
	private String mEndTime;
	private long mResultCode = -1;
	private String mReason;

	@Override
	public void onCommandResult(Context context, MiPushCommandMessage message) {
		// TODO Auto-generated method stub
		Log.d(TAG, message.toString());
		String command = message.getCommand();
		List<String> arguments = message.getCommandArguments();
		if (arguments != null) {
			if (MiPushClient.COMMAND_REGISTER.equals(command)
					&& arguments.size() == 1) {
				mRegId = arguments.get(0);

				// 将小米Id放到SharedPreferences
				SharedPreferences.Editor mEditor = UTools.Storage
						.getSharedPreEditor(context, UConstants.BASE_PREFS_NAME);
				mEditor.putString(UConstants.XIAOMI_REGID, mRegId);
				mEditor.commit();
			} else if ((MiPushClient.COMMAND_SET_ALIAS.equals(command) || MiPushClient.COMMAND_UNSET_ALIAS
					.equals(command)) && arguments.size() == 1) {
				mAlias = arguments.get(0);
			} else if ((MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command) || MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC
					.equals(command)) && arguments.size() == 1) {
				mTopic = arguments.get(0);
			} else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)
					&& arguments.size() == 2) {
				mStartTime = arguments.get(0);
				mEndTime = arguments.get(1);
			}
		}
		mResultCode = message.getResultCode();
		mReason = message.getReason();
	}

	@Override
	public void onReceiveMessage(Context context, MiPushMessage message) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();

		try {
			NotifyMessage mes = gson.fromJson(message.getContent(),
					new TypeToken<NotifyMessage>() {
					}.getType());

			if (mes != null) {
				// 消息的用户自定义内容读取方式
				AndroidNotify notify = new AndroidNotify();
				notify.notify(context, mes);
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
