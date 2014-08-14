package com.medialab.jelly.util.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.medialab.jelly.R;

public class SpinnerView extends ViewGroup {

	private RotateAnimation animation;
	private boolean shouldBeAnimating;
	private final ImageView spinner;
	private final int spinnerSize;

	public SpinnerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
			}
		});
		// setBackgroundColor(getResources().getColor(
		// R.color.spinner_view_background_color));
		this.spinner = new ImageView(context);
		this.spinner.setImageResource(R.drawable.spinner);
		addView(this.spinner);
		this.spinnerSize = getResources().getDimensionPixelSize(
				R.dimen.spinner_size);
	}

	@Override
	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int k = paramInt3 - paramInt1;
		int i = paramInt4 - paramInt2;
		int j = this.spinner.getMeasuredHeight();
		i = (i - j) / 2;
		j = i + j;
		int m = this.spinner.getMeasuredWidth();
		k = (k - m) / 2;
		m = k + m;
		this.spinner.layout(k, i, m, j);
		if (this.animation == null) {
			this.animation = new RotateAnimation(0.0F, 3600.0F,
					this.spinner.getMeasuredWidth() / 2,
					this.spinner.getMeasuredHeight() / 2);
			this.animation.setRepeatCount(-1);
			this.animation.setRepeatMode(-1);
			this.animation.setDuration(12000L);
			this.animation.setInterpolator(new LinearInterpolator());
			this.animation.setRepeatMode(1);
		}
		if (this.shouldBeAnimating)
			this.spinner.startAnimation(this.animation);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = View.MeasureSpec.makeMeasureSpec(this.spinnerSize, 1073741824);
		this.spinner.measure(i, i);
		super.onMeasure(paramInt1, paramInt2);
	}

	public void startSpin() {
		this.shouldBeAnimating = true;
		requestLayout();
	}

	public void stopSpin() {
		this.spinner.clearAnimation();
	}

}
