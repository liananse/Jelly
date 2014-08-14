package com.medialab.jelly;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	private static final String TAG = SettingActivity.class.getSimpleName();
	
	private TextView settingBack; // 设置返回
	private LinearLayout logout; // 退出登录

	private CheckBox noticeSoundsCb; // 消息提示音选择框
	private CheckBox noticePushCb; // 推送选择框

	private boolean noticeSoundsSwitch = true;
	private boolean noticePushSwitch = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.setting);

		settingBack = (TextView) this.findViewById(R.id.setting_back);
		logout = (LinearLayout) this.findViewById(R.id.setting_logout);

		noticeSoundsCb = (CheckBox) this.findViewById(R.id.setting_sounds_cb);
		noticePushCb = (CheckBox) this.findViewById(R.id.setting_push_cb);

		settingBack.setOnClickListener(this);
		logout.setOnClickListener(this);

		noticeSoundsCb.setOnCheckedChangeListener(this);
		noticePushCb.setOnCheckedChangeListener(this);

		SharedPreferences sp = UTools.Storage.getSharedPreferences(this,
				UConstants.BASE_PREFS_NAME);

		noticeSoundsSwitch = sp.getBoolean(UConstants.NOTICE_SOUNDS_SWITCH,
				true);
		noticePushSwitch = sp.getBoolean(UConstants.NOTICE_PUSH_SWITCH, true);

		if (noticeSoundsSwitch) {
			noticeSoundsCb.setChecked(true);
		} else {
			noticeSoundsCb.setChecked(false);
		}

		if (noticePushSwitch) {
			noticePushCb.setChecked(true);
		} else {
			noticePushCb.setChecked(false);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_back:
			settingBack();
			break;
		case R.id.setting_logout:
			showDialog(DIALOG_YES_NO_LONG_MESSAGE);
			break;
		}
	}

	private void settingBack() {
		this.finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.setting_sounds_cb:
			changeNoticeSounds(isChecked);
			break;
		case R.id.setting_push_cb:
			changeNoticePush(isChecked);
			break;
		}
	}

	// 向服务器发送修改请求
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private void changeNoticeSounds(boolean isChecked) {

		noticeSoundsSwitch = isChecked;

		Log.d(TAG, "changeNoticeSounds " + isChecked);
		Map<String, String> params = new HashMap<String, String>();
		if (noticeSoundsSwitch) {
			params.put("soundSwitch", "1");
		} else {
			params.put("soundSwitch", "0");
		}

		HashMap<String,String> map = new HashMap<String,String>();
		map.put("checked",isChecked + "");
		MobclickAgent.onEvent(this, UMengEventID.SETTING_SOUND_ON_OFF, map);    
		
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(SettingActivity.this,UConstants.BASE_PREFS_NAME);
		mEditor.putBoolean(UConstants.NOTICE_SOUNDS_SWITCH, noticeSoundsSwitch);
		mEditor.commit();
	}

	private void changeNoticePush(boolean isChecked) {

		noticePushSwitch = isChecked;

		Log.d(TAG, "changeNoticePush " + isChecked);
		Map<String, String> params = new HashMap<String, String>();
		if (noticePushSwitch) {
			params.put("pushSwitch", "1");
		} else {
			params.put("pushSwitch", "0");
		}

		HashMap<String,String> map = new HashMap<String,String>();
		map.put("checked",isChecked + "");
		MobclickAgent.onEvent(this, UMengEventID.SETTING_PUSH_ON_OFF, map);    
		
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(SettingActivity.this,UConstants.BASE_PREFS_NAME);
		mEditor.putBoolean(UConstants.NOTICE_PUSH_SWITCH, noticePushSwitch);
		mEditor.commit();
	}

	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
			return new AlertDialog.Builder(SettingActivity.this,
					AlertDialog.THEME_HOLO_LIGHT)
					.setTitle(getString(R.string.logout_dialog_title))
					.setMessage(getString(R.string.logout_dialog_content))
					.setPositiveButton(
							getString(R.string.logout_dialog_comfirm),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									logOut();
								}
							})
					.setNegativeButton(
							getString(R.string.logout_dialog_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked Cancel so do some stuff */
									dialog.cancel();
								}
							}).create();
		}

		return null;
	}
	
	private void logOut() {
		MobclickAgent.onEvent(this, UMengEventID.SETTING_LOGOUT);
		requestLogout();
		JellyApplication.relogin(this);
	}

	/** logout告诉服务器，停止百度推送 */
	private void requestLogout() {
		Map<String, String> params = new HashMap<String, String>();

		mDataLoader.postData(UConfig.LOGOUT_URL, params, this, null);
	}

}
