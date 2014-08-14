package com.medialab.jelly.ui.adapter.util;

import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.rebound.SpringSystem;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.ui.adapter.AnswerCardsAdapter;
import com.medialab.jelly.ui.event.ForwardRequested;
import com.medialab.jelly.ui.view.QuestionAndAnswerCardView;
import com.medialab.jelly.ui.view.QuestionCard;
import com.medialab.jelly.ui.widget.SlidingDetailCards;
import com.medialab.jelly.ui.widget.SlidingDetailCardsAdapter;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.squareup.otto.Bus;

public class SingleQuestionControllerUtils {

	private final Bus bus;
	private final JunkDrawerUtils junkDrawerUtils;
	private final StarfishScreenUtils screenUtils;
	private final SpringSystem springSystem;
	private final ExecutorService uiExecutor;

	public SingleQuestionControllerUtils(JunkDrawerUtils paramJunkDrawerUtils,
			StarfishScreenUtils paramStarfishScreenUtils,
			ExecutorService paramExecutorService) {
		this.bus = JellyApplication.getBus();
		this.junkDrawerUtils = paramJunkDrawerUtils;
		this.screenUtils = paramStarfishScreenUtils;
		this.springSystem = SpringSystem.create();
		this.uiExecutor = paramExecutorService;
	}

	public QuestionAndAnswerCardView createEmptyView(Activity paramActivity) {
		QuestionCard localQuestionCard = new QuestionCard(paramActivity, bus,
				springSystem);
		Object localObject = new AnswerCardsAdapter(paramActivity,
				this.junkDrawerUtils, this.bus, this.springSystem,
				this.uiExecutor);

		localObject = new SlidingDetailCards(paramActivity,
				(SlidingDetailCardsAdapter) localObject, this.springSystem);

		return (QuestionAndAnswerCardView) new QuestionAndAnswerCardView(
				paramActivity, this.screenUtils, this.springSystem,
				localQuestionCard, (SlidingDetailCards) localObject);
	}

	public void setQuestionAndAnswerForView(final Activity paramActivity,
			QuestionCard.DisplayMode paramDisplayMode,
			QuestionAndAnswerCardView paramQuestionAndAnswerCardView,
			Question paramQuestion, Answer paramAnswer,
			View.OnClickListener answerOnClickListener) {
		JellyUser localJellyUser = JellyApplication.getMineInfo(paramActivity);
		
		
		SharedPreferences sp = UTools.Storage.getSharedPreferences(paramActivity, UConstants.BASE_PREFS_NAME);
		boolean firstQuestion = sp.getBoolean(UConstants.FIRST_LOAD_QUESTION, false);

		if (!firstQuestion) {
			SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(paramActivity,UConstants.BASE_PREFS_NAME);
			mEditor.putBoolean(UConstants.FIRST_LOAD_QUESTION, true);
			mEditor.commit();
		}
		paramQuestionAndAnswerCardView.setQuestion(paramDisplayMode,
				paramQuestion, paramAnswer, this.junkDrawerUtils
						.getJunkDrawerClickerForQuestion(paramQuestion,
								localJellyUser), new forwardOnClickListener(
						paramQuestion), answerOnClickListener, firstQuestion);
		
	}

	class forwardOnClickListener implements OnClickListener {

		Question question;

		public forwardOnClickListener(Question paramQuestion) {
			this.question = paramQuestion;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SingleQuestionControllerUtils.this.bus.post(new ForwardRequested(
					question));
		}

	}
}
