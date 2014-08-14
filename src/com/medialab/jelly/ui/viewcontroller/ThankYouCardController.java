package com.medialab.jelly.ui.viewcontroller;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.model.ThankYou;
import com.medialab.jelly.ui.adapter.util.ThankYouControllerUtils;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.view.ThankYouCard;
import com.squareup.otto.Bus;

public class ThankYouCardController implements DismissableViewController {

	private final Bus bus;

	private final Resources resources;

	private ThankYou thankYou;
	private final ThankYouControllerUtils thankYouControllerUtils;
	private ThankYouCard thankYouView;

	public ThankYouCardController(Bus paramBus, Resources paramResources,
			ThankYouControllerUtils paramThankYouControllerUtils) {
		this.bus = paramBus;
		this.resources = paramResources;
		this.thankYouControllerUtils = paramThankYouControllerUtils;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.thankYouView;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.post(new TopNavModalSwitch(this.resources
				.getString(R.string.top_nav_thank_you_title), this.resources
				.getString(R.string.top_nav_done), new View.OnClickListener() {
			public void onClick(View paramView) {
				ThankYouCardController.this.bus.post(new BackPressedEvent());
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

	public void setThankYou(ThankYou paramThankYou) {
		this.thankYou = paramThankYou;
		this.thankYouView = this.thankYouControllerUtils
				.getThankYouView(paramThankYou);
	}

}
