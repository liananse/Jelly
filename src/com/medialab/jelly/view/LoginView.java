package com.medialab.jelly.view;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.medialab.jelly.resultmodel.BaseResultModel;
import com.medialab.jelly.resultmodel.LoginAndRegisterResultModel;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.MD5;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class LoginView extends LinearLayout implements OnClickListener {

	private Context mContext;

	public LoginView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.login, this);
		initView();
	}

	private EditText mMobile;
	private EditText mPassword;
	private TextView mLoginBtn;

	private TextView mLoginTitle;
	private TextView mLoginBack;
	private TextView mLoginForgetPassword;

	private void initView() {
		mMobile = (EditText) findViewById(R.id.login_mobile_et);
		mPassword = (EditText) findViewById(R.id.login_password_et);
		mLoginBtn = (TextView) findViewById(R.id.login_btn);
		mLoginTitle = (TextView) findViewById(R.id.login_title);
		mLoginBack = (TextView) findViewById(R.id.login_back);
		mLoginForgetPassword = (TextView) findViewById(R.id.login_forget_password);

		mMobile.addTextChangedListener(new TextWatcher() {
			
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
				if (mMobile.getText().toString().trim().equals("") || !isMobile(mMobile.getText().toString().trim())) {
					mLoginBtn.setEnabled(false);
					return;
				}
				if (mPassword.getText().toString().trim().equals("") || !isPassword(mPassword.getText().toString().trim())) {
					mLoginBtn.setEnabled(false);
					return;
				}
				
				mLoginBtn.setEnabled(true);
			}
		});
		mPassword.addTextChangedListener(new TextWatcher() {
			
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
				if (mMobile.getText().toString().trim().equals("") || !isMobile(mMobile.getText().toString().trim())) {
					mLoginBtn.setEnabled(false);
					return;
				}
				if (mPassword.getText().toString().trim().equals("") || !isPassword(mPassword.getText().toString().trim())) {
					mLoginBtn.setEnabled(false);
					return;
				}
				
				mLoginBtn.setEnabled(true);
			}
		});
		// 设置字体
		FontManager.setTypeface(mLoginBtn, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mMobile, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mPassword, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginTitle, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginBack, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginForgetPassword,
				FontManager.Weight.HUAKANG);
		mLoginBtn.setOnClickListener(this);
		mLoginBtn.setEnabled(false);
		mLoginBack.setOnClickListener(this);
		mLoginForgetPassword.setOnClickListener(this);
		this.setOnClickListener(this);
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mLoginBtn) {
			if (mMobile.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_mobile_tips));
				return;
			}
			
			if (!isMobile(mMobile.getText().toString().trim())) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_not_mobile_tips));
				return;
			}
			if (mPassword.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_password_tips));
				return;
			}
			
			if (!isPassword(mPassword.getText().toString().trim())) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_not_password_tips));
				return;
			}

			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_VIEW_LOGIN);
			loginMethod();
		} else if (v == mLoginBack) {
			// 返回
			if (mContext instanceof LoginAndRegisterActivity) {
				mMobile.setText("");
				mPassword.setText("");
				((LoginAndRegisterActivity) mContext).onBackPressed();
			}
		} else if (v == mLoginForgetPassword) {
			// 忘记密码
			if (mMobile.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_mobile_tips));
				return;
			}
			
			if (!isMobile(mMobile.getText().toString().trim())) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_not_mobile_tips));
				return;
			}

			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_VIEW_FORGET_PASSWORD);
			if (mContext instanceof LoginAndRegisterActivity) {
				((LoginAndRegisterActivity) mContext).hideKeyBoard();
				((LoginAndRegisterActivity) mContext).loginWithCodeBtnClick();
				((LoginAndRegisterActivity) mContext).setRegMobileStr(mMobile.getText().toString().trim());
				
				sendCodeMethod();
			}
		}
	}

	private void sendCodeMethod() {
		mPassword.setText("");

		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mMobile.getText().toString().trim());
		params.put("type", "1");

		mDataLoader.postData(UConfig.SEND_CODE_URL, params, mContext,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showSocketTimeoutToast(mContext);
					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							BaseResultModel mModel = gson.fromJson(source,
									new TypeToken<BaseResultModel>() {
									}.getType());

							if (mModel != null
									&& mModel.result == UConstants.SUCCESS) {
								UToast.showShortToast(
										mContext,
										getResources()
												.getString(
														R.string.login_send_code_success));
							} else {
								UToast.showShortToast(
										mContext,
										getResources().getString(
												R.string.login_send_code_fail));
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							UToast.showShortToast(
									mContext,
									getResources().getString(
											R.string.login_send_code_fail));
						}
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						UToast.showOnFail(mContext);
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showConnectTimeoutToast(mContext);
					}
				});

	}

	private void loginMethod() {

		// dialog show
		final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
		FragmentTransaction ft = ((LoginAndRegisterActivity) mContext)
				.getSupportFragmentManager().beginTransaction();
		mLoadingProgressBarFragment.show(ft, "dialog");

		// 设置loginBtn 不可点击
		mLoginBtn.setEnabled(false);
		// 参数
		SharedPreferences sp = UTools.Storage.getSharedPreferences(mContext,
				UConstants.BASE_PREFS_NAME);

		String time = String.valueOf(System.currentTimeMillis());
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mMobile.getText().toString().trim());
		params.put("digest",
				MD5.encode(mPassword.getText().toString().trim() + time));
		params.put("time", time);
		params.put("xiaomiUserId", sp.getString(UConstants.XIAOMI_REGID, ""));
		params.put("x", sp.getString(UConstants.LOCATION_LATITUDE, "0.0"));
		params.put("y", sp.getString(UConstants.LOCATION_LONGITUDE, "0.0"));
		mDataLoader.postData(UConfig.LOGIN_URL, params, mContext,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showSocketTimeoutToast(mContext);
						mLoadingProgressBarFragment.dismiss();
						mLoginBtn.setEnabled(true);
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

										// 2014-05-13 wangpei : 将推送的设置的状态保存
										if (mModel.data.user.getPushSetting() != null) {

											if (UConstants.OFF
													.equals(mModel.data.user
															.getPushSetting()
															.getPushSwitch())) {
												mEditor.putBoolean(
														UConstants.NOTICE_PUSH_SWITCH,
														false);
											} else if (UConstants.ON
													.equals(mModel.data.user
															.getPushSetting()
															.getPushSwitch())) {
												mEditor.putBoolean(
														UConstants.NOTICE_PUSH_SWITCH,
														true);
											}

											if (UConstants.OFF
													.equals(mModel.data.user
															.getPushSetting()
															.getSoundSwitch())) {
												mEditor.putBoolean(
														UConstants.NOTICE_SOUNDS_SWITCH,
														false);
											} else if (UConstants.ON
													.equals(mModel.data.user
															.getPushSetting()
															.getSoundSwitch())) {
												mEditor.putBoolean(
														UConstants.NOTICE_SOUNDS_SWITCH,
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
						mLoginBtn.setEnabled(true);
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						UToast.showOnFail(mContext);
						mLoadingProgressBarFragment.dismiss();
						mLoginBtn.setEnabled(true);
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showConnectTimeoutToast(mContext);
						mLoadingProgressBarFragment.dismiss();
						mLoginBtn.setEnabled(true);
					}
				});
	}
	
	public static boolean isMobile(String mobile)
	{
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");  
		Matcher m = p.matcher(mobile);
		return m.matches();
	}
	
	public static boolean isPassword(String password) {
		
		if (!password.equals("") && password.length() >= 6 && password.length() <= 16) {
			return true;
		} else {
			return false;
		}
	}

}
