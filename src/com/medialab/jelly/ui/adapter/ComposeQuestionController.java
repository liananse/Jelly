package com.medialab.jelly.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.controller.FullCardView;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.squareup.otto.Bus;

public class ComposeQuestionController implements DismissableViewController {

	private final Activity activity;
	private final Bus bus;
	private boolean showing = false;
	
	private final FullCardView composeWrapper;
	private DismissableViewController currentController;
	
	private final AllTypesController allTypesController;
	
	public ComposeQuestionController(Activity paramActivity,
			StarfishScreenUtils paramStarfishScreenUtils, Bus paramBus) {
		
		this.activity = paramActivity;
		this.bus = paramBus;
		
		this.composeWrapper = new FullCardView(paramActivity,
				paramStarfishScreenUtils);
		
		this.allTypesController = new AllTypesController(paramActivity, paramBus);
		
		this.currentController = this.allTypesController;
	}
	
	
	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.composeWrapper;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.showing) {
			this.showing = false;
			this.currentController.onHide(paramViewGroup);
			this.composeWrapper.hideLowerView();
			this.bus.unregister(this);
		}
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.showing = true;
		this.bus.register(this);
		this.composeWrapper.showLowerView();
		this.composeWrapper.setCurrentView(this.currentController.getView());
		this.currentController.onShow(paramViewGroup);
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
		return this.currentController.canBeDismissed();
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
