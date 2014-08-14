package com.medialab.jelly.ui.viewcontroller;

import android.app.Activity;

import com.medialab.jelly.R;
import com.medialab.jelly.ui.adapter.AnswerCompositionController;
import com.medialab.jelly.ui.adapter.util.SingleQuestionControllerUtils;
import com.medialab.jelly.ui.event.TopNavBarIconDisplay;
import com.medialab.jelly.ui.view.Pawrappa;
import com.medialab.jelly.ui.view.QuestionCard;
import com.medialab.jelly.ui.view.QuestionCard.DisplayMode;
import com.squareup.otto.Bus;

public class BaseQuestionViewController extends QuestionViewController {

	public BaseQuestionViewController(Activity paramActivity, Bus paramBus,
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
		this.bus.post(new TopNavBarIconDisplay(
				getStringForResId(R.string.top_nav_help_text), true));
	}

	@Override
	public DisplayMode getDisplayMode() {
		// TODO Auto-generated method stub
		return QuestionCard.DisplayMode.BASE;
	}

}
