package com.medialab.jelly.controller;

import com.medialab.jelly.view.LoginWithCodeView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class LoginUiController implements DismissableViewController {

	private final View view;
	
	public LoginUiController(View paramView) {
		this.view = paramView;
	}
	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.view;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.view instanceof LoginWithCodeView) {
			((LoginWithCodeView) this.view).onHide();
		}
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.view instanceof LoginWithCodeView) {
			((LoginWithCodeView) this.view).onShow();
		}
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
