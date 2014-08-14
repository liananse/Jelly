package com.medialab.jelly.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medialab.jelly.LoginAndRegisterActivity;
import com.medialab.jelly.R;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class LoginAndRegisterView extends RelativeLayout implements
		OnClickListener {

	private Context mContext;

	public LoginAndRegisterView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.login_and_register, this);
		initView();
	}

	private ImageView mLoginAndRegLogo;
	private TextView mLoginAndRegAppName;
	private TextView mLoginAndRegSlogan;
	private TextView mLoginAndRegLogin;
	private TextView mLoginAndRegRegister;

	private void initView() {
		mLoginAndRegLogo = (ImageView) findViewById(R.id.login_and_register_logo);
		
		mLoginAndRegAppName = (TextView) findViewById(R.id.login_and_register_app_name);
		mLoginAndRegSlogan = (TextView) findViewById(R.id.login_and_register_slogan);
		mLoginAndRegLogin = (TextView) findViewById(R.id.login_and_register_login);
		mLoginAndRegRegister = (TextView) findViewById(R.id.login_and_register_register);

		// 使用第三方字库
		styleTextView(mLoginAndRegAppName);
		styleTextView(mLoginAndRegSlogan);
		styleTextView(mLoginAndRegLogin);
		styleTextView(mLoginAndRegRegister);
		
		this.setOnClickListener(this);
		mLoginAndRegLogo.setOnClickListener(this);
		mLoginAndRegLogin.setOnClickListener(this);
		mLoginAndRegRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mLoginAndRegLogin) {
			if (mContext instanceof LoginAndRegisterActivity) {
				((LoginAndRegisterActivity) mContext).loginBtnClick();
			}
			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_AND_REGISTER_VIEW_LOGIN);
		} else if (v == mLoginAndRegRegister) {
			// 点击注册按钮
			if (mContext instanceof LoginAndRegisterActivity) {
				((LoginAndRegisterActivity) mContext).registerBtnClick();
			}
			MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_AND_REGISTER_VIEW_REGISTER);
		} else if (v == mLoginAndRegLogo) {
		}
	}

	protected void styleTextView(TextView paramCustomTextView) {
		FontManager
				.setTypeface(paramCustomTextView, FontManager.Weight.HUAKANG);
	}

}
