package com.medialab.jelly.controller;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class WelcomeCardController implements DismissableViewController {
	private final View view;

	public WelcomeCardController(View paramView) {
		this.view = paramView;
	}

	public boolean canBeDismissed() {
		return true;
	}

	public View getView() {
		return this.view;
	}

	public boolean onBackPressed() {
		return false;
	}

	public void onHide(ViewGroup paramViewGroup) {
	}

	public void onShow(ViewGroup paramViewGroup) {
	}

	public void onStartDragging() {
	}

	public void onStopDragging() {
	}

	public void reset(ViewGroup paramViewGroup) {
	}

	public void resumeState(Bundle paramBundle) {
	}

	public void saveState(Bundle paramBundle) {
	}
}
