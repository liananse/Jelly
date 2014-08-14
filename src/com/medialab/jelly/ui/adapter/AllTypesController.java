package com.medialab.jelly.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.ui.view.AllTypesView;
import com.squareup.otto.Bus;

public class AllTypesController implements DismissableViewController {

	private final Activity activity;
	private final Bus bus;
	
	private final AllTypesView allTypesView;
	
	public AllTypesController(Activity paramActivity, Bus paramBus) {
		this.activity = paramActivity;
		this.bus = paramBus;
		this.allTypesView = new AllTypesView(activity, 0, 1);
	}
	
	
	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.allTypesView;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.unregister(this);
		this.allTypesView.pause();
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		this.allTypesView.resume();
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
		return true;
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
