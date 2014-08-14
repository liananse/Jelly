package com.medialab.jelly.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

public class NavBarSwapper extends ViewGroup {

	private ViewGroup current;
	private boolean firstMeasure = true;
	private final Spring iconModalSpring;
	private final IconNavBar iconNavBar;
	private final ModalNavBar modalNavBar;
	private int offscreenTranslation;
	private final SimpleSpringListener springListener = new SimpleSpringListener() {
		public void onSpringUpdate(Spring paramSpring) {
			float f2 = (float) SpringUtil.mapValueFromRangeToRange(
					paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D,
					NavBarSwapper.this.offscreenTranslation);
			float f1 = (float) SpringUtil.mapValueFromRangeToRange(
					paramSpring.getCurrentValue(), 0.0D, 1.0D,
					NavBarSwapper.this.offscreenTranslation, 0.0D);
			NavBarSwapper.this.iconNavBar.setTranslationY(f2);
			NavBarSwapper.this.modalNavBar.setTranslationY(f1);
		}
	};

	public NavBarSwapper(Context paramContext, SpringSystem paramSpringSystem) {
		super(paramContext);
		this.iconModalSpring = paramSpringSystem.createSpring()
				.setCurrentValue(0.0D).addListener(this.springListener)
				.setAtRest();
		this.iconNavBar = new IconNavBar(paramContext);
		this.modalNavBar = new ModalNavBar(paramContext);
		this.iconNavBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		this.modalNavBar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		addView(this.iconNavBar);
		addView(this.modalNavBar);
	}

	public IconNavBar getIconNavBar() {
		return this.iconNavBar;
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		this.iconNavBar.measure(paramInt1, paramInt2);
		this.modalNavBar.measure(paramInt1, paramInt2);
		super.onMeasure(paramInt1, paramInt2);
		if (this.firstMeasure) {
			this.firstMeasure = false;
			this.offscreenTranslation = (-getMeasuredHeight());
			if ((this.current != null) && (this.current != this.iconNavBar)) {
				this.iconModalSpring.setCurrentValue(1.0D).setAtRest();
			} else {
				this.current = this.iconNavBar;
				this.iconModalSpring.setCurrentValue(0.0D).setAtRest();
			}
		}
	}

	public void setIconNavBar(String paramString, boolean paramBoolean) {
		this.iconNavBar.setTitle(paramString);
		this.iconNavBar.setDisplay(paramBoolean);
		if (this.current != this.iconNavBar) {
			this.current = this.iconNavBar;
			this.iconModalSpring.setEndValue(0.0D);
		}
	}

	public void setModalNavBar(String paramString1, String paramString2,
			View.OnClickListener paramOnClickListener1, String paramString3,
			View.OnClickListener paramOnClickListener2) {
		this.modalNavBar.setTitle(paramString1);
		this.modalNavBar.setTextAndClicks(paramString2, paramOnClickListener1,
				paramString3, paramOnClickListener2);
		if (this.current != this.modalNavBar)
			this.iconModalSpring.setEndValue(1.0D);
		this.current = this.modalNavBar;
	}

	public void setNetworkDown() {
		this.iconNavBar.displayNetworkDown();
		this.modalNavBar.displayNetworkDown();
	}

	public void setNetworkUp() {
		this.iconNavBar.displayNetworkUp();
		this.modalNavBar.displayNetworkUp();
	}

	@Override
	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		int i = paramInt3 - paramInt1;
		int j = paramInt4 - paramInt2;
		
		this.iconNavBar.layout(0, 0, i, j);
		this.modalNavBar.layout(0, 0, i, j);
	}

}