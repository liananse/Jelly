package com.medialab.jelly.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

	public SquareImageView(Context context) {
		super(context);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
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
