package com.medialab.jelly.ui.viewcontroller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.controller.SpinnerController;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.controller.ViewController;
import com.medialab.jelly.ui.event.SpinnerHideEvent;
import com.medialab.jelly.ui.event.SpinnerShowEvent;
import com.medialab.jelly.util.view.SpinnerView;
import com.medialab.jelly.view.JellyViewFrame;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class JellyViewController implements ViewController {

	private final Bus bus;

	// private final MetricsLogger metrics;
	private boolean networkIsDown = false;
	private final SpinnerController spinnerController;
	private final TopNavController topNavController;
	private final JellyViewFrame view;
	private final MainViewController mainViewController;

	public JellyViewController(Context paramContext,
			MainViewController paramMainViewController) {
		this.view = new JellyViewFrame(paramContext, new StarfishScreenUtils(
				(Activity) paramContext,
				((Activity) paramContext).getWindowManager()));
		// this.activityFactory = paramProvider;
		this.bus = JellyApplication.getBus();
		this.spinnerController = new SpinnerController(new SpinnerView(
				paramContext));
		this.topNavController = new TopNavController(paramContext);
		// need
		mainViewController = paramMainViewController;
		this.view.setNavBarAndMainContent(this.topNavController.getView(),
				mainViewController.getView());

		this.topNavController.getView().setBackgroundColor(
				paramContext.getResources().getColor(R.color.activity_bg));
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.view;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean bool = this.mainViewController.onBackPressed();
		if (this.view.showingSpinner())
			this.view.hideSpinner();
		return bool;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.mainViewController.onHide(paramViewGroup);
		this.topNavController.onHide(paramViewGroup);
		this.bus.unregister(this);
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		this.topNavController.onShow(paramViewGroup);
		this.mainViewController.onShow(paramViewGroup);
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		this.mainViewController.resumeState(paramBundle);
	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		this.mainViewController.saveState(paramBundle);
	}

	@Subscribe
	public void spinnerHide(SpinnerHideEvent paramSpinnerHideEvent) {
		this.spinnerController.onHide((ViewGroup) getView());
		this.view.hideSpinner();
	}

	@Subscribe
	public void spinnerShow(SpinnerShowEvent paramSpinnerShowEvent) {
		this.spinnerController.onShow((ViewGroup) getView());
		this.view.showSpinner(this.spinnerController.getView());
	}

}
