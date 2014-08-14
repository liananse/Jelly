package com.medialab.jelly.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.medialab.jelly.LoginAndRegisterActivity;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.resultmodel.BaseResultModel;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class RegisterStep1View extends LinearLayout implements OnClickListener {

	private Context mContext;
	
	private Handler handle;
	private Timer timer;
	private boolean isCountDown = false;
	
	public RegisterStep1View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.register_step_1, this);
		initView();
	}
	
	private EditText mMobile;
	private EditText mPassword;
	private TextView mNextBtn;
	
	private TextView mRegisterTitle;
	private TextView mRegisterBack;
	private TextView mRegisterTips;
	
	private EditText mRegisterCodeCodeEt;
	private Button mRegisterGetCodeBtn;

	private void initView() {
		mMobile = (EditText) findViewById(R.id.register_mobile_et);
		mPassword = (EditText) findViewById(R.id.register_password_et);
		mNextBtn = (TextView) findViewById(R.id.register_next_btn);
		mRegisterTitle = (TextView) findViewById(R.id.register_title);
		mRegisterBack = (TextView) findViewById(R.id.register_back);
		mRegisterTips = (TextView) findViewById(R.id.register_step1_tips);
		
		mRegisterCodeCodeEt = (EditText) findViewById(R.id.register_code_et);
		mRegisterGetCodeBtn = (Button) findViewById(R.id.register_get_code_btn);
		
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
					mNextBtn.setEnabled(false);
					if (!isCountDown) {
						mRegisterGetCodeBtn.setEnabled(false);
					}
					return;
				}
				
				if (!isCountDown) {
					mRegisterGetCodeBtn.setEnabled(true);
				}
				
				if (mRegisterCodeCodeEt.getText().toString().trim().equals("")) {
					mNextBtn.setEnabled(false);
					return;
				}
				
				if (mPassword.getText().toString().trim().equals("") || !isPassword(mPassword.getText().toString().trim())) {
					mNextBtn.setEnabled(false);
					return;
				}
				
				mNextBtn.setEnabled(true);
			}
		});
		
		mRegisterCodeCodeEt.addTextChangedListener(new TextWatcher() {
			
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
					mNextBtn.setEnabled(false);
					if (!isCountDown) {
						mRegisterGetCodeBtn.setEnabled(false);
					}
					return;
				}
				
				if (!isCountDown) {
					mRegisterGetCodeBtn.setEnabled(true);
				}
				
				if (mRegisterCodeCodeEt.getText().toString().trim().equals("")) {
					mNextBtn.setEnabled(false);
					return;
				}
				
				if (mPassword.getText().toString().trim().equals("") || !isPassword(mPassword.getText().toString().trim())) {
					mNextBtn.setEnabled(false);
					return;
				}
				
				mNextBtn.setEnabled(true);
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
					mNextBtn.setEnabled(false);
					if (!isCountDown) {
						mRegisterGetCodeBtn.setEnabled(false);
					}
					return;
				}
				
				if (!isCountDown) {
					mRegisterGetCodeBtn.setEnabled(true);
				}
				
				if (mRegisterCodeCodeEt.getText().toString().trim().equals("")) {
					mNextBtn.setEnabled(false);
					return;
				}
				
				if (mPassword.getText().toString().trim().equals("") || !isPassword(mPassword.getText().toString().trim())) {
					mNextBtn.setEnabled(false);
					return;
				}
				
				mNextBtn.setEnabled(true);
			}
		});
		// 设置字体
		FontManager.setTypeface(mNextBtn, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mMobile, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mPassword, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterTitle, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterBack, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterTips, FontManager.Weight.HUAKANG);
		
		FontManager.setTypeface(mRegisterCodeCodeEt, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mRegisterGetCodeBtn, FontManager.Weight.HUAKANG);
		
		handle = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what > 0) {
					mRegisterGetCodeBtn.setText(mContext.getResources().getText(
							R.string.login_and_register_login_code)
							+ "(" + msg.what + ")");
				} else {
					mRegisterGetCodeBtn.setText(mContext.getResources().getText(
							R.string.login_and_register_login_code));
					
					if (mMobile.getText().toString().trim().equals("") || !isMobile(mMobile.getText().toString().trim())) {
						mRegisterGetCodeBtn.setEnabled(false);
					} else {
						mRegisterGetCodeBtn.setEnabled(true);
					}
					isCountDown = false;
					timer.cancel();
				}
			}

		};
		
		mNextBtn.setOnClickListener(this);
		mRegisterBack.setOnClickListener(this);
		
		mRegisterGetCodeBtn.setOnClickListener(this);
		
		mNextBtn.setEnabled(false);
		mRegisterGetCodeBtn.setEnabled(false);
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mNextBtn) {
			
			// if mobile is empty
			if (mMobile.getText().toString().trim().equals("")) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_no_mobile_tips));
				return;
			}
			// if password is empty
			if (mPassword.getText().toString().trim().equals("")) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_no_password_tips));
				return;
			}
			
			if (mRegisterCodeCodeEt.getText().toString().trim().equals("")) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_no_code));
				return;
			}
			// if it were not a mobile
			if (!isMobile(mMobile.getText().toString().trim())) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_not_mobile_tips));
				return;
			}
			// if it were not password
			if (!isPassword(mPassword.getText().toString().trim())) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_not_password_tips));
				return;
			}
			
			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_REGISTER_STEP1_NEXT);
			checkCodeMethod();
		} else if (v == mRegisterBack) {
			if (mContext instanceof LoginAndRegisterActivity) {
				((LoginAndRegisterActivity) mContext).onBackPressed();
			}
		} else if (v == mRegisterGetCodeBtn) {
			((LoginAndRegisterActivity) mContext).hideKeyBoard();
			
			if (mMobile.getText().toString().trim().equals("")) {
				UToast.showShortToast(
						mContext,
						getResources().getString(
								R.string.register_no_mobile_tips));
				return;
			}
			
			// if it were not a mobile
			if (!isMobile(mMobile.getText().toString().trim())) {
				UToast.showShortToast(mContext, mContext.getResources().getString(R.string.register_not_mobile_tips));
				return;
			}

			isCountDown = true;
			mRegisterGetCodeBtn.setEnabled(false);
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

			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_REGISTER_STEP1_GET_CODE);
			sendCodeMethod();
		}
	}
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	private void sendCodeMethod() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mMobile.getText().toString().trim());
		params.put("type", "0");

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
							} else if (mModel != null && mModel.result == UConstants.FAILURE) {
								UToast.showShortToast(
										mContext,
										mModel.message);
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
	
	private void checkCodeMethod() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mMobile.getText().toString().trim());
		params.put("code", mRegisterCodeCodeEt.getText().toString().trim());
		
		mDataLoader.postData(UConfig.CHECK_CODE_URL, params, mContext, new HDataListener() {
			
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
						
						// 跳到下一个页面
						
						if (mContext instanceof LoginAndRegisterActivity) {
							((LoginAndRegisterActivity) mContext).hideKeyBoard();
							((LoginAndRegisterActivity) mContext).registerNextBtnClick();
							((LoginAndRegisterActivity) mContext).setRegMobileStr(mMobile.getText().toString().trim());
							((LoginAndRegisterActivity) mContext).setRegPasswordStr(mPassword.getText().toString().trim());
						}
						
					} else if (mModel != null && mModel.result == UConstants.FAILURE){
						UToast.showShortToast(
								mContext,
								mModel.message);
					} else {
						UToast.showShortToast(
								mContext,
								getResources().getString(
										R.string.register_check_code_fail));
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					UToast.showShortToast(
							mContext,
							getResources().getString(
									R.string.register_check_code_fail));
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
