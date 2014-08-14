package com.medialab.jelly.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
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
import com.medialab.jelly.ui.event.QuestionComposed;
import com.medialab.jelly.ui.event.StartDrawingOnImage;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.view.AddTextView;
import com.medialab.jelly.util.LinkUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AddTextController implements DismissableViewController {

	private static final String TAG = AddTextController.class.getSimpleName();
	
	private final Activity activity;
	private final AddTextView addTextView;
	private final Bus bus;
	private ComposeType composeType;
	private final ComposingTextListener composingTextListener;
	private boolean hasText;

	private Question question;
	private final Resources resources;
	private boolean showing;

	public AddTextController(Bus paramBus, Activity paramActivity,
			Resources paramResources, AddTextView paramAddTextView) {
		this.resources = paramResources;
		this.bus = paramBus;
		this.addTextView = paramAddTextView;
		this.activity = paramActivity;
		this.composingTextListener = new ComposingTextListener() {
			public void hasLink(Uri paramUri) {
				if (paramUri == null)
					AddTextController.this.addTextView.setLink(null);
				else
					AddTextController.this.addTextView.setLink(LinkUtils
							.noUnderlineLinksFromString(paramUri.toString()));
				if (AddTextController.this.showing)
					AddTextController.this.updateTopNav();
			}

			public void hasText(boolean paramBoolean) {
				AddTextController.this.hasText = paramBoolean;
				if (AddTextController.this.showing)
					AddTextController.this.updateTopNav();
			}
		};
	}

	private void postWithSend() {
		Bus localBus = this.bus;
		String str;
		if (this.composeType != ComposeType.QUESTION)
			str = this.resources.getString(R.string.top_nav_compose_answer);
		else
			str = this.resources.getString(R.string.top_nav_ask_question);
		localBus.post(new TopNavModalSwitch(
				str,
				this.resources.getString(R.string.top_nav_crop_back),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						AddTextController.this.bus.post(new BackPressedEvent());
					}
				}, this.resources.getString(R.string.top_nav_send_question),
				new View.OnClickListener() {
					public void onClick(View paramView) {

						if (AddTextController.this.composeType != AddTextController.ComposeType.QUESTION) {
							if (AddTextController.this.composeType == AddTextController.ComposeType.ANSWER)
								AddTextController.this.bus
										.post(new AnswerComposedNotSent(
												AddTextController.this.question,
												AddTextController.this.addTextView
														.getText(),
												AddTextController.this.addTextView
														.getLinkText(),
												AddTextController.this.addTextView
														.getImage()));
						} else {
							AddTextController.this.bus
									.post(new QuestionComposed(
											AddTextController.this.addTextView
													.getImage(),
											AddTextController.this.addTextView
													.getText(),
											AddTextController.this.addTextView
													.getLinkText()));
						}
					}
				}));
	}

	private void postWithoutSend() {
		Bus localBus = this.bus;
		String str;
		if (this.composeType != ComposeType.QUESTION)
			str = this.resources.getString(R.string.top_nav_compose_answer);
		else
			str = this.resources.getString(R.string.top_nav_ask_question);
		localBus.post(new TopNavModalSwitch(
				str,
				this.resources.getString(R.string.top_nav_crop_back),
				new View.OnClickListener() {
					public void onClick(View paramView) {
						AddTextController.this.bus.post(new BackPressedEvent());
					}
				}, this.resources.getString(R.string.top_nav_send_question),
				null));
	}

	private void updateTopNav() {
		if (this.composeType != ComposeType.QUESTION) {
			if (this.composeType == ComposeType.ANSWER)
				if ((!this.hasText)
						&& (this.addTextView.getLinkText().isEmpty())
						&& ((this.addTextView.getImage() == null) || (this.addTextView
								.getImage().source != ComposingImage.Source.DRAW)))
					postWithoutSend();
				else
					postWithSend();
		} else if (!this.hasText)
			postWithoutSend();
		else
			postWithSend();
	}

	public ComposingImage getComposingImage() {
		return getView().getImage();
	}

	@Override
	public AddTextView getView() {
		// TODO Auto-generated method stub
		return this.addTextView;
	}

	@Subscribe
	public void linkAdded(LinkChoosenEvent paramLinkChoosenEvent) {
		this.composingTextListener.hasLink(paramLinkChoosenEvent.linkPicked);
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
		this.addTextView.hideKeyboard();
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.showing = true;
		this.bus.register(this);
		updateTopNav();
		this.addTextView.showKeyboardAndFocus();
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
		setComposingImage(null, this.composeType);
		this.question = null;
		this.showing = false;
		this.hasText = false;
	}

	public void setComposingImage(final ComposingImage paramComposingImage,
			ComposeType paramComposeType) {
		View.OnClickListener linkOnClickListener;
		this.composeType = paramComposeType;
		AddTextView localAddTextView = this.addTextView;
		View.OnClickListener drawOnClickListener;
		if (paramComposingImage != null)
			drawOnClickListener = new View.OnClickListener() {
				public void onClick(View paramView) {
					AddTextController.this.bus.post(new StartDrawingOnImage(
							paramComposingImage));
					
					Log.d(TAG, "AddTextController paramComposingImage");
				}
			};
		else
			drawOnClickListener = null;
		if (paramComposingImage != null) {
			linkOnClickListener = new View.OnClickListener() {
				public void onClick(View paramView) {
					Intent localIntent = new Intent(
							AddTextController.this.activity,
							LinkChooserActivity.class);
					String str;
					if (AddTextController.this.composeType != AddTextController.ComposeType.QUESTION)
						str = "answer compose: show link browser";
					else
						str = "question compose: show link browser";
					localIntent.putExtra("event_name_on_launch", str);
					AddTextController.this.activity.startActivityForResult(
							localIntent, 132);
				}
			};
		} else
			linkOnClickListener = null;
		localAddTextView.setImageDrawAndLinkListeners(paramComposingImage,
				drawOnClickListener, linkOnClickListener, this.composingTextListener);
	}

	public void setQuestion(Question paramQuestion, Spannable paramSpannable) {
		this.question = paramQuestion;
		if (paramSpannable != null)
			this.addTextView.setText(paramSpannable);
	}

	static enum ComposeType {
		QUESTION, ANSWER
	}

}
