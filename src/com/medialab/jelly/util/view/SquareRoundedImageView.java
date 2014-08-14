package com.medialab.jelly.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.medialab.jelly.util.RoundedImageView;

public class SquareRoundedImageView extends RoundedImageView {
	public SquareRoundedImageView(Context context) {
		super(context);
	}

	public SquareRoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int i = View.MeasureSpec.getSize(widthMeasureSpec);
		int k = View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY);

		setMeasuredDimension(k, k);

		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
