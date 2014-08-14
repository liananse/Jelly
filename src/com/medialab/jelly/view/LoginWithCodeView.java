package com.medialab.jelly.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;

public class LoginWithCodeView extends LinearLayout implements OnClickListener {

	private Context mContext;

	private Handler handle;
	private Timer timer;
	private boolean isCountDown = true;

	public LoginWithCodeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.login_with_code, this);
		initView();
	}

	private TextView mLoginCodeBack;
	private TextView mLoginCodeTitle;
	private EditText mLoginCodeMobileEt;
	private EditText mLoginCodeCodeEt;
	private Button mLoginGetCodeBtn;
	private EditText mLoginCodeNewPasswordEt;
	private TextView mLoginCodeLoginBtn;

	private void initView() {
		mLoginCodeBack = (TextView) findViewById(R.id.login_code_back);
		mLoginCodeTitle = (TextView) findViewById(R.id.login_code_title);
		mLoginCodeMobileEt = (EditText) findViewById(R.id.login_code_mobile_et);
		mLoginCodeCodeEt = (EditText) findViewById(R.id.login_code_et);
		mLoginGetCodeBtn = (Button) findViewById(R.id.login_get_code_btn);
		mLoginCodeNewPasswordEt = (EditText) findViewById(R.id.login_code_password_et);
		mLoginCodeLoginBtn = (TextView) findViewById(R.id.login_code_btn);

		mLoginCodeMobileEt.addTextChangedListener(new TextWatcher() {
			
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
				if (mLoginCodeMobileEt.getText().toString().trim().equals("") || !isMobile(mLoginCodeMobileEt.getText().toString().trim())) {
					
					if (!isCountDown) {
						mLoginGetCodeBtn.setEnabled(false);
					}
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				}
				
				if (!isCountDown) {
					mLoginGetCodeBtn.setEnabled(true);
				}
				
				if (mLoginCodeCodeEt.getText().toString().trim().equals("")) {
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				}
				
				if (mLoginCodeNewPasswordEt.getText().toString().trim().equals("") || !isPassword(mLoginCodeNewPasswordEt.getText().toString().trim())) {
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				} 
				
				mLoginCodeLoginBtn.setEnabled(true);
			}
		});
		
		mLoginCodeCodeEt.addTextChangedListener(new TextWatcher() {
			
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
				if (mLoginCodeMobileEt.getText().toString().trim().equals("") || !isMobile(mLoginCodeMobileEt.getText().toString().trim())) {
					
					if (!isCountDown) {
						mLoginGetCodeBtn.setEnabled(false);
					}
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				}
				
				if (!isCountDown) {
					mLoginGetCodeBtn.setEnabled(true);
				}
				
				if (mLoginCodeCodeEt.getText().toString().trim().equals("")) {
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				}
				
				if (mLoginCodeNewPasswordEt.getText().toString().trim().equals("") || !isPassword(mLoginCodeNewPasswordEt.getText().toString().trim())) {
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				} 
				
				mLoginCodeLoginBtn.setEnabled(true);
			}
		});
		
		mLoginCodeNewPasswordEt.addTextChangedListener(new TextWatcher() {
			
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
				if (mLoginCodeMobileEt.getText().toString().trim().equals("") || !isMobile(mLoginCodeMobileEt.getText().toString().trim())) {
					
					if (!isCountDown) {
						mLoginGetCodeBtn.setEnabled(false);
					}
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				}
				
				if (!isCountDown) {
					mLoginGetCodeBtn.setEnabled(true);
				}
				
				if (mLoginCodeCodeEt.getText().toString().trim().equals("")) {
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				}
				
				if (mLoginCodeNewPasswordEt.getText().toString().trim().equals("") || !isPassword(mLoginCodeNewPasswordEt.getText().toString().trim())) {
					mLoginCodeLoginBtn.setEnabled(false);
					return;
				} 
				
				mLoginCodeLoginBtn.setEnabled(true);
			}
		});
		
		FontManager.setTypeface(mLoginCodeBack, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginCodeTitle, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginCodeMobileEt, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginCodeCodeEt, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginGetCodeBtn, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginCodeNewPasswordEt,
				FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mLoginCodeLoginBtn, FontManager.Weight.HUAKANG);

		handle = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what > 0) {
					mLoginGetCodeBtn.setText(mContext.getResources().getText(
							R.string.login_and_register_login_code)
							+ "(" + msg.what + ")");
				} else {
					mLoginGetCodeBtn.setText(mContext.getResources().getText(
							R.string.login_and_register_login_code));
					mLoginGetCodeBtn.setEnabled(true);
					
					isCountDown = false;
					timer.cancel();
				}
			}

		};

		mLoginCodeLoginBtn.setEnabled(false);
		
		mLoginCodeBack.setOnClickListener(this);
		mLoginGetCodeBtn.setOnClickListener(this);
		mLoginCodeLoginBtn.setOnClickListener(this);
	}

	public void onShow() {
		if (mContext instanceof LoginAndRegisterActivity) {
			mLoginCodeMobileEt.setText(((LoginAndRegisterActivity) mContext)
					.getRegMobileStr());

			mLoginCodeMobileEt
					.setSelection(((LoginAndRegisterActivity) mContext)
							.getRegMobileStr().length());

			mLoginGetCodeBtn.setEnabled(false);

			isCountDown = true;
			timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				int i = 60;

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = i--;
					handle.sendMessage(msg);
				}
			};
			timer.schedule(timerTask, 1000, 1000);
		}
	}

	public void onHide() {
		mLoginGetCodeBtn.setText(mContext.getResources().getText(
				R.string.login_and_register_login_code));
		timer.cancel();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mLoginCodeBack) {
			// 返回
			if (mContext instanceof LoginAndRegisterActivity) {
				mLoginCodeMobileEt.setText("");
				mLoginCodeCodeEt.setText("");
				mLoginCodeNewPasswordEt.setText("");
				((LoginAndRegisterActivity) mContext).onBackPressed();
			}

		} else if (v == mLoginGetCodeBtn) {
			if (mLoginCodeMobileEt.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_mobile_tips));
				return;
			}

			mLoginGetCodeBtn.setEnabled(false);
			
			isCountDown = true;
			timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				int i = 60;

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = i--;
					handle.sendMessage(msg);
				}
			};
			timer.schedule(timerTask, 1000, 1000);

			sendCodeMethod();
		} else if (v == mLoginCodeLoginBtn) {

			if (mLoginCodeMobileEt.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_mobile_tips));
				return;
			}

			if (mLoginCodeCodeEt.getText().toString().trim().equals("")) {
				UToast.showShortToast(mContext,
						getResources().getString(R.string.login_no_code));
				return;
			}
			if (mLoginCodeNewPasswordEt.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_new_password_tips));
				return;
			}

			loginMethod();
		}

	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private void loginMethod() {

		// dialog show
		final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
		FragmentTransaction ft = ((LoginAndRegisterActivity) mContext)
				.getSupportFragmentManager().beginTransaction();
		mLoadingProgressBarFragment.show(ft, "dialog");

		// 设置loginBtn 不可点击
		mLoginCodeLoginBtn.setEnabled(false);

		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mLoginCodeMobileEt.getText().toString().trim());
		params.put("code", mLoginCodeCodeEt.getText().toString().trim());
		params.put("password", mLoginCodeNewPasswordEt.getText().toString()
				.trim());

		mDataLoader.postData(UConfig.RESET_PASSWORD_URL, params, mContext,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showSocketTimeoutToast(mContext);
						mLoadingProgressBarFragment.dismiss();
						mLoginCodeLoginBtn.setEnabled(true);
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
						mLoginCodeLoginBtn.setEnabled(true);
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						UToast.showOnFail(mContext);
						mLoadingProgressBarFragment.dismiss();
						mLoginCodeLoginBtn.setEnabled(true);
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showConnectTimeoutToast(mContext);
						mLoadingProgressBarFragment.dismiss();
						mLoginCodeLoginBtn.setEnabled(true);
					}
				});
	}

	private void sendCodeMethod() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mLoginCodeMobileEt.getText().toString().trim());
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
							UToast.showShortToast(mContext, getResources()
									.getString(R.string.login_send_code_fail));
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
