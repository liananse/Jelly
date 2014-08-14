package com.medialab.jelly.ui.viewcontroller;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.ui.adapter.AnswerCompositionController;
import com.medialab.jelly.ui.adapter.util.SingleQuestionControllerUtils;
import com.medialab.jelly.ui.event.BackPressedEvent;
import com.medialab.jelly.ui.event.TopNavModalSwitch;
import com.medialab.jelly.ui.view.Pawrappa;
import com.medialab.jelly.ui.view.QuestionCard;
import com.medialab.jelly.ui.view.QuestionCard.DisplayMode;
import com.squareup.otto.Bus;

public class OverlayQuestionViewController extends QuestionViewController {

	public OverlayQuestionViewController(Activity paramActivity, Bus paramBus,
			Pawrappa paramPawrappa,
			SingleQuestionControllerUtils paramSingleQuestionControllerUtils,
			AnswerCompositionController paramAnswerCompositionController) {
		super(paramActivity, paramBus, paramPawrappa,
				paramSingleQuestionControllerUtils,
				paramAnswerCompositionController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void childOnShow() {
		// TODO Auto-generated method stub
		String str1;
		if (!this.question.user.getNickName().isEmpty())
			str1 = this.question.user.getNickName();
		else
			str1 = getStringForResId(R.string.top_nav_user_no_name_name);

		String topTitle;
		if (this.question.user.equals(JellyApplication
				.getMineInfo(this.paramActivity))) {
			topTitle = getStringForResId(R.string.top_nav_single_question_title_yours);
		} else {
//			String str2 = getStringForResId(R.string.top_nav_single_question_title);
//			Object[] localObject = new Object[1];
//			localObject[0] = str1;
//			topTitle = String.format(str2, localObject);
			topTitle = this.question.context + getStringForResId(R.string.top_nav_someones_question);
		}
		this.bus.post(new TopNavModalSwitch(topTitle,
				getStringForResId(R.string.top_nav_done),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						OverlayQuestionViewController.this.bus
								.post(new BackPressedEvent());
					}
				}, null, null));
	}

	@Override
	public DisplayMode getDisplayMode() {
		// TODO Auto-generated method stub
		return QuestionCard.DisplayMode.OVERLAY;
	}

	public void reset(ViewGroup paramViewGroup) {
		super.reset(paramViewGroup);
		if (this.view != null)
			this.view.destroy();
	}

}
