package com.medialab.jelly.controller;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;

public class FullCardView extends ViewGroup {
	private int cardBottom;
	private int cardTop;

	private View currentView;
	private View lowerView;
	private final int padding;
	private final int screenHeight;
	private final StarfishScreenUtils screenUtils;

	public FullCardView(Activity paramActivity,
			StarfishScreenUtils paramStarfishScreenUtils) {
		super(paramActivity);
		this.padding = paramActivity.getResources().getDimensionPixelSize(
				R.dimen.activity_horizontal_margin);
		this.screenUtils = paramStarfishScreenUtils;
		this.screenHeight = getHeightMinusStatusBar(paramActivity);
	}

	private static int getHeightMinusStatusBar(Activity paramActivity) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		paramActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(localDisplayMetrics);
		int k = localDisplayMetrics.heightPixels;
		int j = 0;
		int i = paramActivity.getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (i > 0)
			j = paramActivity.getResources().getDimensionPixelSize(i);
		return k - j;
	}

	public void hideLowerView() {
		if (this.lowerView != null)
			this.lowerView.setVisibility(View.GONE);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		int k = paramInt4 - (paramInt2 + 0 + this.padding);
		if (this.currentView != null) {
			int j = this.currentView.getMeasuredWidth();
			int i = this.padding;
			j = i + j;
			this.currentView.layout(i, this.cardTop, j, this.cardBottom);
			if (this.lowerView != null) {
				int m = k - this.padding;
				k = m - this.lowerView.getMeasuredHeight();
				this.lowerView.layout(i, k, j, m);
			}
		}
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = View.MeasureSpec.getSize(paramInt1);
		int j = View.MeasureSpec.getSize(paramInt2);
		i -= this.padding + this.padding;
		int k = (int) (1.176471F * i);
		if (this.currentView != null)
			this.currentView.measure(
					View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(k, MeasureSpec.EXACTLY));
		int n = this.screenHeight;
		int m = (n - k) / 2;
		n -= j;
		this.cardTop = Math.max(this.screenUtils.getTopNavHeight(), m - n);
		this.cardBottom = (k + this.cardTop);
		j -= this.cardBottom + this.padding;
		if (this.lowerView != null)
			this.lowerView.measure(
					View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY));
		super.onMeasure(paramInt1, paramInt2);
	}

	public void setCurrentView(View paramView) {
		if (this.currentView != null)
			removeView(this.currentView);
		this.currentView = paramView;
		if (this.currentView != null)
			addView(this.currentView);
	}

	public void setLowerView(View paramView,
			View.OnClickListener paramOnClickListener) {
		if (this.lowerView != null)
			removeView(this.lowerView);
		this.lowerView = paramView;
		this.lowerView.setOnClickListener(paramOnClickListener);
		this.lowerView.setVisibility(View.GONE);
		addView(this.lowerView);
	}

	public void showLowerView() {
		if (this.lowerView != null)
			this.lowerView.setVisibility(View.VISIBLE);
	}
}
