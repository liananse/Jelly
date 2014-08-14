package com.medialab.jelly.ui.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.controller.StarfishScreenUtils;

public class Pawrappa extends ViewGroup {

	private final int topLine;
	private View view;

	public Pawrappa(Context context,
			StarfishScreenUtils paramStarfishScreenUtils) {
		super(context);
		// TODO Auto-generated constructor stub
		this.topLine = paramStarfishScreenUtils.getTopNavHeight();
	}

	public void addView(View paramView) {
		if (this.view != null)
			removeView(this.view);
		this.view = paramView;
		if (this.view.getParent() != this)
			super.addView(this.view);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (this.view != null)
			this.view.layout(0, this.topLine, this.view.getMeasuredWidth(),
					this.topLine + this.view.getMeasuredHeight());
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		if (this.view != null) {
			int j = View.MeasureSpec.getSize(paramInt1);
			int i = View.MeasureSpec.getSize(paramInt2) - this.topLine;
			this.view.measure(
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY));
		}
		super.onMeasure(paramInt1, paramInt2);
	}

}
