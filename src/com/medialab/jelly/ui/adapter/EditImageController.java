package com.medialab.jelly.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.PictureConfirmedEvent;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.view.EditImageView;
import com.squareup.otto.Bus;

public class EditImageController implements DismissableViewController {

	private final Bus bus;
	private final Resources resources;
	private final EditImageView view;

	public EditImageController(Context paramContext, Bus paramBus,
			EditImageView paramEditImageView) {
		this.resources = paramContext.getResources();
		this.bus = paramBus;
		this.view = paramEditImageView;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return view;
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
				.getString(R.string.top_nav_crop_photo), this.resources
				.getString(R.string.top_nav_camera),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						EditImageController.this.bus
								.post(new BackPressedEvent());
					}
				}, this.resources.getString(R.string.top_nav_use_photo),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						// need
						EditImageController.this.bus.post(new PictureConfirmedEvent(
								EditImageController.this.view
										.getImageTrimmedAndTransformed()));
					}
				}));
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
		this.view.clear();
	}

	public void setCurrentImage(ComposingImage paramComposingImage) {
		this.view.setImage(paramComposingImage);
	}

}
