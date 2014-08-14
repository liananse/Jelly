package com.medialab.jelly.ui.adapter;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.LinkChooserActivity;
import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.ui.ComposingTextListener;
import com.medialab.jelly.ui.event.AnswerComposedNotSent;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.LinkChoosenEvent;
import com.medialab.jelly.ui.event.StartDrawingOnImage;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.view.AnswerTextCompositionView;
import com.medialab.jelly.util.UDisplayWidth;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AnswerTextCompositionController implements
		DismissableViewController {

	private static final String STATE_LINK_KEY = "answer_composition_link";
	private static final String STATE_TEXT_KEY = "answer_composition_text";
	private final Activity activity;
	private final Bus bus;
	private final ComposingTextListener composingTextListener;
	private AnswerTextCompositionView currentView;
	private final Resources resources;
	private boolean showing;

	public AnswerTextCompositionController(Activity paramActivity,
			Bus paramBus, Resources paramResources) {
		this.activity = paramActivity;
		this.bus = paramBus;
		this.resources = paramResources;
		this.composingTextListener = new ComposingTextListener() {
			public void hasLink(Uri paramUri) {
				if (paramUri == null)
					AnswerTextCompositionController.this.currentView
							.setLinkText(null);
				else
					AnswerTextCompositionController.this.currentView
							.setLinkText(paramUri);
				if (AnswerTextCompositionController.this.showing)
					AnswerTextCompositionController.this.updateTopNav();
			}

			public void hasText(boolean paramBoolean) {
				if (AnswerTextCompositionController.this.showing)
					AnswerTextCompositionController.this.updateTopNav();
			}
		};
	}

	private void postShowingDisabled() {
		this.bus.post(new TopNavModalSwitch(this.resources
				.getString(R.string.top_nav_compose_answer), this.resources
				.getString(R.string.cancel), new View.OnClickListener() {
			public void onClick(View paramView) {
				AnswerTextCompositionController.this.bus
						.post(new BackPressedEvent());
			}
		}, this.resources.getString(R.string.top_nav_send_question), null));
	}

	private void postShowingEnabled() {
		this.bus.post(new TopNavModalSwitch(this.resources
				.getString(R.string.top_nav_compose_answer), this.resources
				.getString(R.string.cancel), new View.OnClickListener() {
			public void onClick(View paramView) {
				AnswerTextCompositionController.this.bus
						.post(new BackPressedEvent());
			}
		}, this.resources.getString(R.string.top_nav_send_question),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						AnswerTextCompositionController.this.bus
								.post(new AnswerComposedNotSent(
										AnswerTextCompositionController.this
												.getView().getQuestion(),
										AnswerTextCompositionController.this
												.getView().getText(),
										AnswerTextCompositionController.this
												.getView().getLinkText(), null));
					}
				}));
	}

	private void updateTopNav() {
		if ((this.currentView.getText().toString().isEmpty())
				&& (this.currentView.getLinkText().isEmpty()))
			postShowingDisabled();
		else
			postShowingEnabled();
	}

	@Subscribe
	public void answerComposed(AnswerComposedNotSent paramAnswerComposedNotSent) {
		getView().hideKeyboard();
	}

	public CharSequence getText() {
		return this.currentView.getText();
	}

	@Subscribe
	public void linkComposed(LinkChoosenEvent paramLinkChoosenEvent) {
		this.composingTextListener.hasLink(paramLinkChoosenEvent.linkPicked);
	}

	@Override
	public AnswerTextCompositionView getView() {
		// TODO Auto-generated method stub
		if (this.currentView == null)
			this.currentView = new AnswerTextCompositionView(this.activity);
		return this.currentView;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.showing = false;
		this.bus.unregister(this);
		getView().hideKeyboard();
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.showing = true;

		this.bus.register(this);
		getView().showKeyboardAndFocus();
		updateTopNav();
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		getView().setText(paramBundle.getString("answer_composition_text", ""));
		String str = paramBundle.getString("answer_composition_link", "");
		if ((str != null) && (!str.isEmpty()))
			getView().setLinkText(Uri.parse(str));
	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		paramBundle.putString("answer_composition_text", getView().getText()
				.toString());
		String str = getView().getLinkText();
		if (!str.isEmpty())
			paramBundle.putString("answer_composition_link", str);
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
		if (this.currentView != null)
			this.currentView.reset();
	}

	public void setQuestion(final Question paramQuestion) {
		getView().setQuestion(paramQuestion, new View.OnClickListener() {
			public void onClick(View paramView) {
				Intent localIntent = new Intent(
						AnswerTextCompositionController.this.activity,
						LinkChooserActivity.class);
				localIntent.putExtra("event_name_on_launch",
						"answer compose: show link browser");
				AnswerTextCompositionController.this.activity
						.startActivityForResult(localIntent, 132);
				AnswerTextCompositionController.this.getView()
						.setLinkText(null);
			}
		}, new View.OnClickListener() {
			public void onClick(View paramView) {

				Bitmap mBitmap = FinalBitmap.create(activity)
						.getBitmapFromCache(
								UDisplayWidth.getPicUrlByWidth(
										UDisplayWidth.PIC_WIDTH_680,
										paramQuestion.questionImage.picName));
				AnswerTextCompositionController.this.bus
						.post(new StartDrawingOnImage(new ComposingImage(
								mBitmap, 0, ComposingImage.Source.CAMERA)));
			}
		}, this.composingTextListener);
	}

}
