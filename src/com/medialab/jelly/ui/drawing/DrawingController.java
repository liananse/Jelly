package com.medialab.jelly.ui.drawing;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.DrawingCompleteEvent;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.squareup.otto.Bus;

public class DrawingController implements DismissableViewController {

	private final Bus bus;

	private ComposingImage composingImage;
	private String eventName;
	private final Resources resources;
	private int rotation;
	private final DrawingFrame view;

	public DrawingController(Resources paramResources, Bus paramBus,
			DrawingFrame paramDrawingFrame) {
		this.view = paramDrawingFrame;
		this.bus = paramBus;
		this.resources = paramResources;
	}

	public ComposingImage getBaseImage() {
		return this.composingImage;
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
		this.bus.unregister(this);
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		this.bus.post(new TopNavModalSwitch(this.resources
				.getString(R.string.top_nav_draw_title), this.resources
				.getString(R.string.top_nav_back), new View.OnClickListener() {
			public void onClick(View paramView) {
				DrawingController.this.bus.post(new BackPressedEvent());
			}
		}, this.resources.getString(R.string.top_nav_use_drawing),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						DrawingController.this.bus.post(new DrawingCompleteEvent(
								new ComposingImage(DrawingController.this.view.getCompositeBitmap(),
										DrawingController.this.rotation,
										ComposingImage.Source.DRAW)));
					}
				}));
		// this.metrics.logProperty(this.eventName);
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
		this.composingImage = null;
		this.view.clear();
	}

	public void setBaseImageContentAndColor(ComposingImage paramComposingImage,
			String paramString, int paramInt) {
		this.composingImage = paramComposingImage;
		this.view.setBaseImageBitmap(paramComposingImage.bitmap);
		this.rotation = paramComposingImage.rotation;
		this.eventName = paramString;
		this.view.setDrawingColor(paramInt);
	}

}
