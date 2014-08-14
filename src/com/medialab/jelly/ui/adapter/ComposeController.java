package com.medialab.jelly.ui.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.controller.FullCardView;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.ui.drawing.DrawingController;
import com.medialab.jelly.ui.drawing.DrawingFrame;
import com.medialab.jelly.ui.event.DrawingCompleteEvent;
import com.medialab.jelly.ui.event.PictureConfirmedEvent;
import com.medialab.jelly.ui.event.PictureTakenEvent;
import com.medialab.jelly.ui.event.StartDrawingOnImage;
import com.medialab.jelly.ui.view.AddTextView;
import com.medialab.jelly.ui.view.CameraLowerView;
import com.medialab.jelly.ui.view.CameraView;
import com.medialab.jelly.ui.view.EditImageView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ComposeController implements DismissableViewController {

	private static final String TAG = ComposeController.class.getSimpleName();
	private final AddTextController addTextController;
	private final Bus bus;
	private final CameraController cameraController;
	private final CameraLowerView cameraLowerView;
	private final FullCardView composeWrapper;
	private DismissableViewController currentController;
	private final DrawingController drawingController;
	private final EditImageController editImageController;
	private ComposingImage lastTakenPicture;
	private boolean launchedFromOpen = false;
	private final Resources resources;
	private boolean showing = false;

	public ComposeController(Activity paramContext,
			StarfishScreenUtils paramStarfishScreenUtils, Bus paramBus) {
		this.bus = paramBus;
		this.resources = paramContext.getResources();
		this.editImageController = new EditImageController(paramContext, bus,
				new EditImageView(paramContext));
		this.drawingController = new DrawingController(resources, bus,
				new DrawingFrame(paramContext));
		this.cameraController = new CameraController(paramContext,
				new CameraView(paramContext, bus, 0, 1), bus);
		this.cameraLowerView = new CameraLowerView(paramContext);
		this.addTextController = new AddTextController(bus, paramContext,
				this.resources, new AddTextView(paramContext));
		this.currentController = this.cameraController;
		this.composeWrapper = new FullCardView(paramContext,
				paramStarfishScreenUtils);
	}

	private void swapViews(
			DismissableViewController paramDismissableViewController1,
			DismissableViewController paramDismissableViewController2) {
		if (this.showing) {
			if (paramDismissableViewController2 == this.editImageController)
				this.cameraLowerView.setVisibility(View.GONE);
			paramDismissableViewController2.onShow(getView());
			this.currentController = paramDismissableViewController2;
			paramDismissableViewController1.onHide(getView());
			this.composeWrapper.setCurrentView(paramDismissableViewController2
					.getView());
		}
	}

	@Subscribe
	public void drawCompositionFinished(
			DrawingCompleteEvent paramDrawingCompleteEvent) {

		Log.d(TAG, "drawCompositionFinished completeEvent");
		this.drawingController.reset(this.composeWrapper);
		this.addTextController.setComposingImage(
				paramDrawingCompleteEvent.image,
				AddTextController.ComposeType.QUESTION);
		swapViews(this.currentController, this.addTextController);
	}

	@Subscribe
	public void drawCompositionStarted(
			StartDrawingOnImage paramStartDrawingOnImage) {
		Log.v(TAG, "Draw compose selected");
		this.drawingController.setBaseImageContentAndColor(
				paramStartDrawingOnImage.image, "question compose: show draw",
				this.resources.getColor(R.color.drawing_question_color));
		swapViews(this.currentController, this.drawingController);
	}

	@Override
	public ViewGroup getView() {
		// TODO Auto-generated method stub
		return this.composeWrapper;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean i = true;
		if (this.currentController != this.editImageController) {
			if (this.currentController != this.addTextController) {
				if (this.currentController != this.drawingController) {
					i = false;
				} else {
					this.addTextController.setComposingImage(
							this.drawingController.getBaseImage(),
							AddTextController.ComposeType.QUESTION);
					swapViews(this.currentController, this.addTextController);
					this.drawingController.reset(this.composeWrapper);
				}
			} else {
				this.editImageController.setCurrentImage(this.lastTakenPicture);
				swapViews(this.currentController, this.editImageController);
				this.addTextController.reset(this.composeWrapper);
			}
		} else {
			swapViews(this.currentController, this.cameraController);
			this.editImageController.reset(this.composeWrapper);
			this.lastTakenPicture = null;
		}
		return i;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.showing) {
			this.showing = false;
			this.launchedFromOpen = false;
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
		if (this.launchedFromOpen) {
		}
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
		this.composeWrapper.hideLowerView();
	}

	@Override
	public void onStopDragging() {
		// TODO Auto-generated method stub
		if ((this.launchedFromOpen)
				&& (this.currentController == this.cameraController))
			this.composeWrapper.showLowerView();
	}

	@Subscribe
	public void pictureConfirmed(
			PictureConfirmedEvent paramPictureConfirmedEvent) {
		Log.v(TAG, "Picture confirmed");
		this.addTextController.setComposingImage(
				paramPictureConfirmedEvent.data,
				AddTextController.ComposeType.QUESTION);
		swapViews(this.currentController, this.addTextController);
	}

	@Subscribe
	public void pictureTaken(PictureTakenEvent paramPictureTakenEvent) {
		Log.v(TAG, "Picture successfully selected/taken");
		swapViews(this.currentController, this.editImageController);
		this.editImageController.setCurrentImage(paramPictureTakenEvent.data);
		this.lastTakenPicture = paramPictureTakenEvent.data;
	}

	@Override
	public void reset(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.lastTakenPicture = null;
		this.editImageController.reset(paramViewGroup);
		this.addTextController.reset(paramViewGroup);
		this.cameraController.reset(paramViewGroup);
		this.drawingController.reset(paramViewGroup);
	}

	public void setLaunchedFromOpen(boolean paramBoolean) {
		this.launchedFromOpen = paramBoolean;
		this.cameraController.setLaunchedFromOpen(this.launchedFromOpen);
	}

	// @Subscribe
	// public void stockImageSelected(StockImageSelected
	// paramStockImageSelected) {
	// if (this.showing) {
	// Log.v(TAG, "Stock image selected");
	// this.bus.post(new SpinnerShowEvent());
	// this.imageLoader.get(paramStockImageSelected.uri.toString(),
	// new ImageLoader.ImageListener() {
	// public void onErrorResponse(VolleyError paramVolleyError) {
	// ComposeController.this.bus.post(new UserActionFailure(
	// "stock image download: failure",
	// ComposeController.this.resources
	// .getString(R.string.error_stock_image_download),
	// true, paramVolleyError));
	// ComposeController.this.bus
	// .post(new SpinnerHideEvent());
	// }
	//
	// public void onResponse(
	// ImageLoader.ImageContainer paramImageContainer,
	// boolean paramBoolean) {
	// if (paramImageContainer.getBitmap() != null) {
	// ComposingImage localComposingImage = new ComposingImage(
	// paramImageContainer.getBitmap(), 0,
	// ComposingImage.Source.CAMERA);
	// ComposeController.this
	// .swapViews(
	// ComposeController.this.currentController,
	// ComposeController.this.editImageController);
	// ComposeController.this.editImageController
	// .setCurrentImage(localComposingImage);
	// ComposeController.access$602(
	// ComposeController.this,
	// localComposingImage);
	// ComposeController.this.bus
	// .post(new SpinnerHideEvent());
	// }
	// }
	// });
	// }
	// }

}
