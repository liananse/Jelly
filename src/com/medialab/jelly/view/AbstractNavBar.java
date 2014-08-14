package com.medialab.jelly.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.view.CustomTextView;

public abstract class AbstractNavBar extends ViewGroup {

	private static final float ICON_RATIO = 0.7971014F;
	private final int buttonHeight;
	private final View[] clickableButtons = new View[2];
	private final int largeTextSize;
	private String lastText;
	private final int leftPaddingPixels;
	private boolean networkDown = false;
	private final CustomTextView titleText;
	private final int touchTargetPixelExpansion;

	public AbstractNavBar(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		Resources localResources = paramContext.getResources();
		this.largeTextSize = localResources
				.getDimensionPixelSize(R.dimen.large_text_size);
		this.leftPaddingPixels = localResources
				.getDimensionPixelSize(R.dimen.top_nav_left_right_padding);
		this.touchTargetPixelExpansion = localResources
				.getDimensionPixelSize(R.dimen.top_nav_touch_target_increase);
		this.buttonHeight = localResources
				.getDimensionPixelSize(R.dimen.top_nav_button_height);
		this.titleText = new CustomTextView(paramContext);
		this.titleText.setGravity(17);
		this.titleText.setTextColor(getResources().getColor(
				R.color.top_nav_title_color));
		styleTextView(this.titleText);
		this.clickableButtons[0] = getLeftView();
		this.clickableButtons[1] = getRightView();
		if (this.clickableButtons[0] != null)
			addView(this.clickableButtons[0]);
		if (this.clickableButtons[1] != null)
			addView(this.clickableButtons[1]);
		addView(this.titleText);
	}

	public void displayNetworkDown() {
		this.networkDown = true;
		String str = getResources().getString(R.string.network_down);
		Drawable localDrawable = getContext().getResources().getDrawable(
				R.drawable.nav_bar_offline_icon);
		localDrawable.setBounds(0, 0, this.largeTextSize, this.largeTextSize);
		SpannableString localSpannableString = new SpannableString("   " + str);
		localSpannableString.setSpan(new ImageSpan(localDrawable, 1), 0, 1, 33);
		localSpannableString.setSpan(new ForegroundColorSpan(getResources()
				.getColor(R.color.complementary_orange)), 3, 3 + str.length(),
				33);
		this.titleText.setText(localSpannableString);
	}

	public void displayNetworkUp() {
		this.networkDown = false;
		this.titleText.setText(this.lastText);
	}

	protected abstract View getLeftView();

	protected abstract View getRightView();

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int j = 0;
		int k = View.MeasureSpec.makeMeasureSpec(
				(int) (2.0F * this.buttonHeight), MeasureSpec.AT_MOST);
		int i = View.MeasureSpec.makeMeasureSpec(this.buttonHeight,
				MeasureSpec.EXACTLY);
		if (this.clickableButtons[0] != null) {
			this.clickableButtons[0]
					.setMinimumWidth((int) (this.buttonHeight / ICON_RATIO));
			this.clickableButtons[0].measure(k, i);
		}
		if (this.clickableButtons[1] != null) {
			this.clickableButtons[1]
					.setMinimumWidth((int) (this.buttonHeight / ICON_RATIO));
			this.clickableButtons[1].measure(k, i);
		}
		k = 2 * this.leftPaddingPixels;
		int m;
		if (this.clickableButtons[0] != null)
			m = this.clickableButtons[0].getMeasuredWidth();
		else
			m = 0;
		if (this.clickableButtons[1] != null)
			j = this.clickableButtons[1].getMeasuredWidth();
		j = k + 2 * Math.max(m, j);
		j = View.MeasureSpec.getSize(widthMeasureSpec) - j;
		this.titleText.measure(
				View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY), i);

		super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
				(int) (2.0F * this.buttonHeight), MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean paramBoolean, int l, int t, int r, int b) {

		// 子View在竖直方向居中
		int childViewTop = (b - t - this.buttonHeight) / 2;
		int childViewBottom = childViewTop + this.buttonHeight;

		if (this.clickableButtons[0] != null) {

			int childViewLeft = l + this.leftPaddingPixels;
			int childViewRight = childViewLeft
					+ this.clickableButtons[0].getMeasuredWidth();
			this.clickableButtons[0].layout(childViewLeft, childViewTop,
					childViewRight, childViewBottom);
		}

		if (this.clickableButtons[1] != null) {
			int childViewLeft = r - this.leftPaddingPixels
					- this.clickableButtons[1].getMeasuredWidth();
			int childViewRight = r - this.leftPaddingPixels;
			this.clickableButtons[1].layout(childViewLeft, childViewTop,
					childViewRight, childViewBottom);
		}

		int k = (r - l - this.titleText.getMeasuredWidth()) / 2;
		this.titleText.layout(k, childViewTop,
				k + this.titleText.getMeasuredWidth(), childViewBottom);
	}

	public final void setTitle(String paramString) {
		if (!this.networkDown)
			this.titleText.setText(paramString);
		this.lastText = paramString;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	protected void styleTextView(CustomTextView paramCustomTextView) {
		FontManager
				.setTypeface(paramCustomTextView, FontManager.Weight.HUAKANG);
		paramCustomTextView.setTextSize(0, this.largeTextSize);
		paramCustomTextView.setVisibility(View.VISIBLE);
		paramCustomTextView.setPivotX(0.0F);
	}

}
