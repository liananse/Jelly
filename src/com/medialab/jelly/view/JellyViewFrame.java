package com.medialab.jelly.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.controller.StarfishScreenUtils;

public class JellyViewFrame extends ViewGroup {

	private View mainContent;
	private View navBar;
	private View spinnerView;
	private final int topNavHeight;

	public JellyViewFrame(Context paramContext,
			StarfishScreenUtils paramStarfishScreenUtils) {
		super(paramContext);
		this.setClipChildren(false);
		this.topNavHeight = paramStarfishScreenUtils.getTopNavHeight();
		// TODO Auto-generated constructor stub
	}

	public void hideSpinner() {
		if ((this.spinnerView != null)
				&& (this.spinnerView.getParent() == this))
			removeView(this.spinnerView);
		this.spinnerView = null;
	}

	@Override
	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int j = paramInt3 - paramInt1;
		int k = paramInt4 - paramInt2;
		if (this.navBar != null) {
			int i = this.navBar.getMeasuredHeight();
			this.navBar.layout(0, 0, j, i);
			this.mainContent.layout(0, i, j, k);
		}
		if (this.spinnerView != null)
			this.spinnerView.layout(0, 0, j, k);
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int i = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt1), MeasureSpec.EXACTLY);
		int j;
		if (this.navBar != null) {
			j = View.MeasureSpec.makeMeasureSpec(this.topNavHeight * 2,
					MeasureSpec.EXACTLY);
			this.navBar.measure(i, j);
			j = View.MeasureSpec
					.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2)
							- this.topNavHeight * 2, MeasureSpec.EXACTLY);
			this.mainContent.measure(i, j);
		}
		if (this.spinnerView != null) {
			j = View.MeasureSpec.makeMeasureSpec(
					View.MeasureSpec.getSize(paramInt2), MeasureSpec.EXACTLY);
			this.spinnerView.measure(i, j);
		}
		super.onMeasure(paramInt1, paramInt2);
	}

	public void setNavBarAndMainContent(View paramView1, View paramView2) {
		this.navBar = paramView1;
		this.mainContent = paramView2;
		addView(paramView1);
		addView(paramView2);
	}

	public void showSpinner(View paramView) {
		if (this.spinnerView == null) {
			this.spinnerView = paramView;
			addView(this.spinnerView);
		}
	}

	public boolean showingSpinner() {
		if (this.spinnerView == null)
			return false;
		else
			return true;
	}

}
