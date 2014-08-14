package com.medialab.jelly.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.view.ComposeInviteView;
import com.squareup.otto.Bus;

public class ComposeInviteController implements DismissableViewController{

	private final Activity activity;
	private final StarfishScreenUtils starfishScreenUtils;
	private final Bus bus;
	
	private final ComposeInviteView composeInviteView;
	public ComposeInviteController(Activity paramActivity,
			StarfishScreenUtils paramStarfishScreenUtils, Bus paramBus) {
		this.activity = paramActivity;
		this.starfishScreenUtils = paramStarfishScreenUtils;
		this.bus = paramBus;
		
		this.composeInviteView = new ComposeInviteView(activity, starfishScreenUtils);
	}
	
	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.composeInviteView;
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
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		this.bus.post(new TopNavModalSwitch(this.activity.getResources()
				.getString(R.string.top_nav_compose_invite_text), this.activity
				.getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						ComposeInviteController.this.bus
								.post(new BackPressedEvent());
					}
				}, null, null));
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
