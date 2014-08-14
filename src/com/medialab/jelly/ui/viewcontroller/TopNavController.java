package com.medialab.jelly.ui.viewcontroller;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SpringSystem;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.controller.ViewController;
import com.medialab.jelly.ui.event.TopNavBarIconDisplay;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.event.UpdateBadgeEvent;
import com.medialab.jelly.view.NavBarSwapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class TopNavController implements ViewController {

	private final Bus bus;
	private final NavBarSwapper swapper;

	public TopNavController(Context paramContext) {
		this.bus = JellyApplication.getBus();
		this.swapper = new NavBarSwapper(paramContext, SpringSystem.create());
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.swapper;
	}

	public void networkDown() {
		this.swapper.setNetworkDown();
	}

	public void networkUp() {
		this.swapper.setNetworkUp();
	}

	@Subscribe
	public void normalTopNavBarDisplay(
			TopNavBarIconDisplay paramTopNavBarIconDisplay) {
		this.swapper.setIconNavBar(paramTopNavBarIconDisplay.title,
				paramTopNavBarIconDisplay.showRightIcon);
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
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Subscribe
	public void setModalTopNav(TopNavModalSwitch paramTopNavModalSwitch) {
		
		this.swapper.setModalNavBar(paramTopNavModalSwitch.title,
				paramTopNavModalSwitch.leftText,
				paramTopNavModalSwitch.leftOnClickListener,
				paramTopNavModalSwitch.rightText,
				paramTopNavModalSwitch.rightOnClickListener);
	}

	@Subscribe
	public void unreadNotificationsUpdated(
			UpdateBadgeEvent paramUpdateBadgeEvent) {
		this.swapper.getIconNavBar().updateProfileIsBadged(
				paramUpdateBadgeEvent.shouldBadge);
	}

}
