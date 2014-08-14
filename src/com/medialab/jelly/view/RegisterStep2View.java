package com.medialab.jelly.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.LoginAndRegisterActivity;
import com.medialab.jelly.MainActivity;
import com.medialab.jelly.R;
import com.medialab.jelly.fragment.FLoadingProgressBarFragment;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.resultmodel.LoginAndRegisterResultModel;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class RegisterStep2View extends LinearLayout implements OnClickListener {

	private Context mContext;

	public RegisterStep2View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.register_step_2, this);
		initView();
	}

	private EditText mUserName;
	private TextView mRegisterBtn;
	private TextView mRegisterTitle;
	private TextView mRegisterBack;
	private TextView mRegisterTips;

	private RoundedImageView mRegisterAvatar;

	private void initView() {
		mUserName = (EditText) findViewById(R.id.register_username_et);
		mRegisterBtn = (TextView) findViewById(R.id.register_btn);
		mRegisterTitle = (TextView) findViewById(R.id.register_title);
		mRegisterBack = (TextView) findViewById(R.id.register_back);
		mRegisterTips = (TextView) findViewById(R.id.register_step2_tips);
		mRegisterAvatar = (RoundedImageView) findViewById(R.id.register_avatar);
		// 设置字体
		FontManager.setTypeface(mRegisterBtn, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mUserName, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterTitle, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterBack, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterTips, FontManager.Weight.HUAKANG);

		mUserName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// 如果没有设置头像
				if (!((LoginAndRegisterActivity) mContext).isHeadPicSet()) {
					mRegisterBtn.setEnabled(false);
					return;
				}

				// 如果姓名为空
				if (mUserName.getText().toString().trim().equals("")) {
					mRegisterBtn.setEnabled(false);
					return;
				}
				
				mRegisterBtn.setEnabled(true);
			}
		});
		mRegisterBtn.setOnClickListener(this);
		mRegisterBack.setOnClickListener(this);
		mRegisterAvatar.setOnClickListener(this);
		
		mRegisterBtn.setEnabled(false);
		this.setOnClickListener(this);
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mRegisterBtn) {
			if (mContext instanceof LoginAndRegisterActivity) {
				// 如果没有设置头像
				if (!((LoginAndRegisterActivity) mContext).isHeadPicSet()) {
					UToast.showShortToast(
							mContext,
							getResources().getString(
									R.string.register_no_avatar));
					return;
				}

				// 如果姓名为空
				if (mUserName.getText().toString().trim().equals("")) {
					UToast.showShortToast(mContext,
							getResources().getString(R.string.register_no_name));
					return;
				}

				MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_REGISTER_STEP2_REGISTER);
				registerMethod();
			}
		} else if (v == mRegisterBack) {
			if (mContext instanceof LoginAndRegisterActivity) {
				((LoginAndRegisterActivity) mContext).onBackPressed();
			}
		} else if (v == mRegisterAvatar) {
			if (mContext instanceof LoginAndRegisterActivity) {
				((LoginAndRegisterActivity) mContext).ShowImageDialog();
			}
			
			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_REGISTER_STEP2_UPLOAD_HEAD);
		}
	}

	private void registerMethod() {
		// dialog show
		final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
		FragmentTransaction ft = ((LoginAndRegisterActivity) mContext)
				.getSupportFragmentManager().beginTransaction();
		mLoadingProgressBarFragment.show(ft, "dialog");

		// 设置不可点击
		mRegisterBtn.setEnabled(false);

		// 参数
		SharedPreferences sp = UTools.Storage.getSharedPreferences(mContext,
				UConstants.BASE_PREFS_NAME);

		Map<String, String> params = new HashMap<String, String>();
		params.put("avatar",
				"avatar:" + UTools.Storage.getHeadPicSmallImagePath());
		params.put("nickName", mUserName.getText().toString().trim());
		params.put("mobile",
				((LoginAndRegisterActivity) mContext).getRegMobileStr());
		params.put("password",
				((LoginAndRegisterActivity) mContext).getRegPasswordStr());
		params.put("xiaomiUserId", sp.getString(UConstants.XIAOMI_REGID, ""));
		params.put("x", sp.getString(UConstants.LOCATION_LATITUDE, "0.0"));
		params.put("y", sp.getString(UConstants.LOCATION_LONGITUDE, "0.0"));
		mDataLoader.postData(UConfig.REGISTER_URL, params, mContext,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showSocketTimeoutToast(mContext);
						mLoadingProgressBarFragment.dismiss();
						mRegisterBtn.setEnabled(true);
					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							LoginAndRegisterResultModel mModel = gson
									.fromJson(
											source,
											new TypeToken<LoginAndRegisterResultModel>() {
											}.getType());

							if (mModel != null) {
								if (mModel.result == UConstants.SUCCESS) {
									// 登录成功

									if (mModel.data != null
											&& mModel.data.user != null) {
										// 将用户个人信息存数据库
										JellyApplication.initMineInfo(mContext,
												mModel.data.user);
										// 将userid和accesstoken同时放到sharedpreferences中
										SharedPreferences.Editor mEditor = UTools.Storage
												.getSharedPreEditor(
														mContext,
														UConstants.BASE_PREFS_NAME);
										mEditor.putString(
												UConstants.SELF_USER_ID,
												String.valueOf(mModel.data.user
														.getUid()));
										mEditor.putString(
												UConstants.SELF_ACCESS_TOKEN,
												mModel.data.user
														.getAccessToken());

										//2014-05-13 wangpei : 将推送的设置的状态保存
										if(mModel.data.user.getPushSetting() != null){
											
											if(UConstants.OFF.equals(mModel.data.user
													.getPushSetting().getPushSwitch())){
												mEditor.putBoolean(UConstants.NOTICE_PUSH_SWITCH,
														false);
											}else{
												mEditor.putBoolean(UConstants.NOTICE_PUSH_SWITCH,
														true);
											}
											
											if(UConstants.OFF.equals(mModel.data.user
													.getPushSetting().getSoundSwitch())){
												mEditor.putBoolean(UConstants.NOTICE_SOUNDS_SWITCH,
														false);
											}else{
												mEditor.putBoolean(UConstants.NOTICE_SOUNDS_SWITCH,
														true);
											}
											
										}
										
										mEditor.commit();
										// 跳转到主页
										Intent intent = new Intent();
										intent.setClass(mContext,
												MainActivity.class);
										((LoginAndRegisterActivity) mContext)
												.startActivity(intent);
										((LoginAndRegisterActivity) mContext)
												.finish();
									} else {
										UToast.showDataParsingError(mContext);
									}
								} else {
									// 登录失败
									UToast.showShortToast(mContext,
											mModel.message);
								}
							} else {
								// 数据返回失败
								UToast.showDataParsingError(mContext);
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// 数据解析失败
							UToast.showDataParsingError(mContext);
						}
						mLoadingProgressBarFragment.dismiss();
						mRegisterBtn.setEnabled(true);
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						UToast.showOnFail(mContext);
						mLoadingProgressBarFragment.dismiss();
						mRegisterBtn.setEnabled(true);
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showConnectTimeoutToast(mContext);
						mLoadingProgressBarFragment.dismiss();
						mRegisterBtn.setEnabled(true);
					}
				});
	}

}
