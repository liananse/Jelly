package com.medialab.jelly.ui.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.ui.drawing.DrawingController;
import com.medialab.jelly.ui.drawing.DrawingFrame;
import com.medialab.jelly.ui.event.DrawingCompleteEvent;
import com.medialab.jelly.ui.event.StartDrawingOnImage;
import com.medialab.jelly.ui.view.AddTextView;
import com.medialab.jelly.ui.view.AnswerCompositionContainer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AnswerCompositionController implements DismissableViewController {

	private final AddTextController addTextController;
	private final Bus bus;
	private final FrameLayout container;
	private DismissableViewController currentController;
	private Question currentQuestion;
	private final DrawingController drawingController;
	private final Resources resources;
	private boolean showing = false;
	private final AnswerTextCompositionController textCompositionController;

	public AnswerCompositionController(Activity paramContext, Bus paramBus) {
		this.bus = paramBus;
		this.resources = paramContext.getResources();
		this.container = new AnswerCompositionContainer(paramContext);
		this.addTextController = new AddTextController(bus, paramContext,
				resources, new AddTextView(paramContext));
		this.textCompositionController = new AnswerTextCompositionController(
				paramContext, paramBus, this.resources);
		this.currentController = this.textCompositionController;
		this.drawingController = new DrawingController(resources, bus,
				new DrawingFrame(paramContext));
	}

	private void swapViews(
			DismissableViewController paramDismissableViewController1,
			DismissableViewController paramDismissableViewController2) {
		if (this.showing) {
			this.currentController = paramDismissableViewController2;
			paramDismissableViewController1.onHide(getView());
			this.container.removeAllViews();
			this.currentController.onShow(getView());
			this.container.addView(this.currentController.getView());
		}
	}

	@Override
	public ViewGroup getView() {
		// TODO Auto-generated method stub
		return this.container;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean bool = true;
		if (this.currentController != this.drawingController) {
			if (this.currentController != this.addTextController)
				bool = false;
			else
				swapViews(this.addTextController, this.drawingController);
		} else
			swapViews(this.drawingController, this.textCompositionController);
		return bool;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.showing) {
			this.showing = false;
			this.bus.unregister(this);
			this.currentController.onHide(getView());
		}
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.showing = true;
		this.bus.register(this);
		this.currentController.onShow(getView());
		if (this.container.getChildCount() <= 0)
			this.container.addView(this.currentController.getView());
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		// int i = paramBundle.getInt("answer_comp_current_controller", -1);
		// AnswerTextCompositionController localAnswerTextCompositionController
		// = null;
		// if (i >= 0) {
		// CurrentController localCurrentController = CurrentController
		// .values()[i];
		// switch
		// (AnswerCompositionController.CurrentController[localCurrentController
		// .ordinal()]) {
		// case 1:
		// this.textCompositionController.resumeState(paramBundle);
		// localAnswerTextCompositionController =
		// this.textCompositionController;
		// }
		// }
		// if ((localAnswerTextCompositionController != null)
		// && (this.currentController != localAnswerTextCompositionController))
		// swapViews(this.currentController,
		// localAnswerTextCompositionController);
	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		// if (this.currentController == this.textCompositionController) {
		// paramBundle.putInt("answer_comp_current_controller",
		// CurrentController.ANSWER_TEXT.ordinal());
		// System.out.println("saveState " +
		// CurrentController.ANSWER_TEXT.ordinal());
		// }
		// this.currentController.saveState(paramBundle);
	}

	public void setQuestion(Question paramQuestion) {
		this.currentQuestion = paramQuestion;
		this.textCompositionController.setQuestion(paramQuestion);
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
		this.addTextController.reset(paramViewGroup);
		this.drawingController.reset(paramViewGroup);
		this.textCompositionController.reset(paramViewGroup);
		this.currentQuestion = null;
		this.currentController = this.textCompositionController;
		this.container.removeAllViews();
		this.showing = false;
	}

	@Subscribe
	public void drawCompositionFinished(
			DrawingCompleteEvent paramDrawingCompleteEvent) {
		if (this.showing) {
			this.addTextController.setComposingImage(
					paramDrawingCompleteEvent.image,
					AddTextController.ComposeType.ANSWER);
			this.addTextController.setQuestion(
					this.currentQuestion,
					new SpannableString(this.textCompositionController
							.getText()));
			swapViews(this.currentController, this.addTextController);
		}
	}

	@Subscribe
	public void drawSelected(StartDrawingOnImage paramStartDrawingOnImage) {
		this.drawingController.setBaseImageContentAndColor(
				paramStartDrawingOnImage.image, "answer compose: show draw",
				this.resources.getColor(R.color.drawing_answer_color));
		swapViews(this.currentController, this.drawingController);
	}

	private static enum CurrentController {
		ANSWER_TEXT
	}

}
