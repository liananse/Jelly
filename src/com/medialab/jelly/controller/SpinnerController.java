package com.medialab.jelly.controller;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.util.view.SpinnerView;

public class SpinnerController implements DismissableViewController {

	private final SpinnerView spinnerView;

	public SpinnerController(SpinnerView paramSpinnerView) {
		this.spinnerView = paramSpinnerView;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.spinnerView;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.spinnerView.stopSpin();
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.spinnerView.startSpin();
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canBeDismissed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onStartDragging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopDragging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub

	}

}
