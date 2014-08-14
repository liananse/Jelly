package com.medialab.jelly.ui.event;


import android.view.View;

public class TopNavModalSwitch extends TopNavBarEvent {

	public final View.OnClickListener leftOnClickListener;
	public final String leftText;

	public final View.OnClickListener rightOnClickListener;
	public final String rightText;

	public TopNavModalSwitch(String paramString1, String paramString2,
			View.OnClickListener paramOnClickListener1, String paramString3,
			View.OnClickListener paramOnClickListener2) {
		super(paramString1);
		this.leftText = paramString2;
		this.leftOnClickListener = paramOnClickListener1;
		this.rightText = paramString3;
		this.rightOnClickListener = paramOnClickListener2;
	}
}
