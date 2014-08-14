package com.medialab.jelly.ui.adapter.util;

import android.app.Activity;
import android.content.Intent;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.model.ThankYou;
import com.medialab.jelly.ui.event.ViewAnswerSelected;
import com.medialab.jelly.ui.view.ThankYouCard;
import com.medialab.jelly.ui.view.ThankYouCard.ThankYouViewListener;
import com.squareup.otto.Bus;

public class ThankYouControllerUtils {

	private final Bus bus;
	private final Activity context;
	private final StarfishScreenUtils screenUtils;
	private final String shareDialogTitlie;

	public ThankYouControllerUtils(Activity paramActivity, Bus paramBus,
			StarfishScreenUtils paramStarfishScreenUtils) {
		this.context = paramActivity;
		this.bus = paramBus;
		this.screenUtils = paramStarfishScreenUtils;
		this.shareDialogTitlie = this.context.getResources().getString(
				R.string.thank_you_share_dialog_title);

	}

	private String generateThankYouShareMessage(ThankYou paramThankYou) {
		String str;
		if ((paramThankYou.shareTextWithUrl == null)
				|| (paramThankYou.shareTextWithUrl.isEmpty()))
			str = "";
		else
			str = paramThankYou.shareTextWithUrl;
		return str;
	}

	public ThankYouCard getThankYouView(ThankYou paramThankYou) {
		boolean bool;
		if ((paramThankYou.shareTextWithUrl == null)
				|| (paramThankYou.shareTextWithUrl.isEmpty()))
			bool = false;
		else
			bool = true;

		return new ThankYouCard(this.context, this.screenUtils, paramThankYou,
				bool, new ParamThankYouViewListener(paramThankYou));
	}

	private class ParamThankYouViewListener implements ThankYouViewListener {

		ThankYou paramThankYou;

		public ParamThankYouViewListener(ThankYou paramThankYou) {
			this.paramThankYou = paramThankYou;
		}

		@Override
		public void shareClicked() {
			// TODO Auto-generated method stub
			Intent localIntent = new Intent();
			localIntent.setAction("android.intent.action.SEND");
			localIntent.putExtra("android.intent.extra.TEXT",
					ThankYouControllerUtils.this
							.generateThankYouShareMessage(paramThankYou));
			localIntent.setType("text/plain");
			ThankYouControllerUtils.this.context.startActivity(Intent
					.createChooser(localIntent,
							ThankYouControllerUtils.this.shareDialogTitlie));
		}

		@Override
		public void viewAnswerClicked() {
			// TODO Auto-generated method stub
			ThankYouControllerUtils.this.bus.post(new ViewAnswerSelected(
					paramThankYou.question, paramThankYou.question
							.getAnswerById(paramThankYou.aid)));
		}
	}
}
