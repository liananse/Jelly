package com.medialab.jelly.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medialab.jelly.R;

public class DrawLinkProgressBar extends ViewGroup {

	private final ImageView drawButton;
	private final ImageView linkButton;
	private final ProgressButton progressButton;
	private final int progressIndicatorHeight;
	private int spacingBetweenDrawAndLink;

	public DrawLinkProgressBar(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		Resources localResources = paramContext.getResources();
		this.progressIndicatorHeight = localResources
				.getDimensionPixelSize(R.dimen.progress_indicator_height);
		this.spacingBetweenDrawAndLink = localResources
				.getDimensionPixelSize(R.dimen.top_section_draw_to_link_spacing);
		this.progressButton = new ProgressButton(paramContext);
		this.progressButton.setCircleColorAndProgressColor(localResources
				.getColor(R.color.top_card_not_editing_circle_color),
				localResources
						.getColor(R.color.top_card_not_editing_progress_color));
		this.progressButton.setCircleColorAndProgressColor(localResources
				.getColor(R.color.top_card_not_editing_circle_color),
				localResources
						.getColor(R.color.top_card_not_editing_progress_color));
		this.drawButton = new ImageView(paramContext);
		this.drawButton.setImageResource(R.drawable.draw_icon);
		this.linkButton = new ImageView(paramContext);
		this.linkButton.setImageResource(R.drawable.link_icon);
		addView(this.progressButton);
		addView(this.drawButton);
		addView(this.linkButton);
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int k = paramInt3 - paramInt1;
		int j = k - this.drawButton.getMeasuredWidth();
		int i = this.drawButton.getMeasuredHeight();
		this.drawButton.layout(j, 0, k, i);
		j -= this.spacingBetweenDrawAndLink;
		k = j - this.linkButton.getMeasuredWidth();
		this.linkButton.layout(k, 0, j, i);
		j = 0 + this.progressButton.getMeasuredWidth();
		i = 0 + this.progressButton.getMeasuredHeight();
		this.progressButton.layout(0, 0, j, i);
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		this.progressButton.measure(View.MeasureSpec.makeMeasureSpec(
				this.progressIndicatorHeight, MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(this.progressIndicatorHeight,
						MeasureSpec.EXACTLY));
		this.drawButton.measure(View.MeasureSpec.makeMeasureSpec(
				this.progressIndicatorHeight, MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(this.progressIndicatorHeight,
						MeasureSpec.EXACTLY));
		this.linkButton.measure(View.MeasureSpec.makeMeasureSpec(
				this.progressIndicatorHeight, MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(this.progressIndicatorHeight,
						MeasureSpec.EXACTLY));
		super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(
				this.progressIndicatorHeight, MeasureSpec.EXACTLY));
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		float f1 = paramMotionEvent.getX();
		float f2 = this.linkButton.getX() - this.spacingBetweenDrawAndLink / 2;
		float f3 = this.linkButton.getX() + this.linkButton.getMeasuredWidth()
				+ this.spacingBetweenDrawAndLink / 2;
		boolean bool;
		if ((f1 <= f2) || (f1 >= f3)) {
			if (f1 <= f3) {
				bool = super.onTouchEvent(paramMotionEvent);
			} else {
				paramMotionEvent.setLocation(1.0F, 1.0F);
				bool = this.drawButton.dispatchTouchEvent(paramMotionEvent);
			}
		} else {
			paramMotionEvent.setLocation(1.0F, 1.0F);
			bool = this.linkButton.dispatchTouchEvent(paramMotionEvent);
		}
		return bool;
	}

	public void setDrawAndLinkOnClickListener(
			View.OnClickListener drawOnClickListener,
			View.OnClickListener linkOnClickListener) {
		this.drawButton.setOnClickListener(drawOnClickListener);
		this.linkButton.setOnClickListener(linkOnClickListener);
	}

	public void setProgress(int paramInt) {
		this.progressButton.setProgress(paramInt);
	}

}
