package com.medialab.jelly.ui.viewcontroller;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.ui.event.TopNavBarIconDisplay;
import com.medialab.jelly.ui.view.OutOfQuestionsView;
import com.squareup.otto.Bus;

public class OutOfQuestionsViewController implements DismissableViewController {

	private final Bus bus;
	private final Resources resources;
	private final OutOfQuestionsView view;

	public OutOfQuestionsViewController(Bus paramBus, Resources paramResources,
			OutOfQuestionsView paramOutOfQuestionsView) {
		this.bus = paramBus;
		this.resources = paramResources;
		this.view = paramOutOfQuestionsView;
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

	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.post(new TopNavBarIconDisplay(this.resources
				.getString(R.string.top_nav_out_of_questions_text), true));
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
