package com.medialab.jelly.view;

import android.content.Context;
import android.view.View;

import com.medialab.jelly.R;
import com.medialab.jelly.util.view.CustomTextView;

public class ModalNavBar extends AbstractNavBar {

	private final int disabledTextColor = getResources().getColor(
			R.color.top_nav_modal_disabled_text_color);
	private final int enabledTextColor = getResources().getColor(
			R.color.top_nav_modal_text_color);
	private CustomTextView leftText;
	private CustomTextView rightText;

	public ModalNavBar(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getLeftView() {
		// TODO Auto-generated method stub
		this.leftText = new CustomTextView(getContext());
		this.leftText.setGravity(19);
		styleTextView(this.leftText);
		return this.leftText;
	}

	@Override
	public View getRightView() {
		// TODO Auto-generated method stub
		this.rightText = new CustomTextView(getContext());
		this.rightText.setGravity(21);
		styleTextView(this.rightText);
		return this.rightText;
	}

	public void setTextAndClicks(String paramString1,
			View.OnClickListener paramOnClickListener1, String paramString2,
			View.OnClickListener paramOnClickListener2) {
		if (paramString1 == null) {
			this.leftText.setVisibility(8);
			this.leftText.setClickable(false);
		} else {
			this.leftText.setVisibility(0);
			this.leftText.setText(paramString1);
			this.leftText.setOnClickListener(paramOnClickListener1);
			if (paramOnClickListener1 != null)
				this.leftText.setTextColor(this.enabledTextColor);
			else
				this.leftText.setTextColor(this.disabledTextColor);
		}
		if (paramString2 == null) {
			this.rightText.setVisibility(8);
			this.rightText.setClickable(false);
		} else {
			this.rightText.setVisibility(0);
			this.rightText.setText(paramString2);
			this.rightText.setOnClickListener(paramOnClickListener2);
			if (paramOnClickListener2 != null)
				this.rightText.setTextColor(this.enabledTextColor);
			else
				this.rightText.setTextColor(this.disabledTextColor);
		}
	}

}