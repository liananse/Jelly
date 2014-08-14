package com.medialab.jelly.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

import com.facebook.rebound.SpringSystem;
import com.medialab.jelly.R;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.ui.adapter.AnswerCardsAdapter;
import com.medialab.jelly.ui.widget.CardView;
import com.medialab.jelly.ui.widget.SlidingDetailCards;

public class QuestionAndAnswerCardView extends
		CardView<Answer, QuestionCard, AnswerCard> {

	private final int detailCardsPxFromBottom = getResources()
			.getDimensionPixelSize(R.dimen.detail_cards_bottom_spacing);
	private Question question;
	private Answer answer;

	public QuestionAndAnswerCardView(Context paramContext,
			StarfishScreenUtils paramStarfishScreenUtils,
			SpringSystem paramSpringSystem, QuestionCard paramQuestionCard,
			SlidingDetailCards<Answer, AnswerCard> paramSlidingDetailCards) {
		super(paramContext, paramStarfishScreenUtils, paramSpringSystem);
		// TODO Auto-generated constructor stub
		setMainViewAndSlidingCards(paramQuestionCard, paramSlidingDetailCards);
	}

	@Override
	protected int getAnswerCompositionTopLine(int paramInt) {
		// TODO Auto-generated method stub
		return ((QuestionCard) getCurrentMainView()).getTopOfSocialWidget();
	}

	@Override
	protected int getDetailClosedTopLine(int paramInt) {
		// TODO Auto-generated method stub
		return paramInt - this.detailCardsPxFromBottom;
	}

	@Override
	protected int getDetailOpenTopLine(int paramInt) {
		// TODO Auto-generated method stub
		return Math.max(0, Math.min(getHeight() - paramInt,
				((QuestionCard) getCurrentMainView()).getTopOfSocialWidget()));
	}

	public void refreshSlidingObjects(Question paramQuestion,
			List<Answer> paramList) {
		if (paramQuestion.equals(this.question)) {

			// need 暂时先这么处理，看有没有bug

			List<Answer> tempList = (new ArrayList<Answer>());

			((AnswerCardsAdapter) getCurrentDetailCards().getAdapter())
					.setQuestionAndAnswer(this.question, tempList, null);

			((AnswerCardsAdapter) getCurrentDetailCards().getAdapter())
					.setQuestionAndAnswer(this.question, paramList, this.answer);

			// ((AnswerCardsAdapter) getCurrentDetailCards().getAdapter())
			// .updateAnswers(this.question, paramList);
			((QuestionCard) getCurrentMainView()).refreshUiState();
		}
	}

	public void refreshUiState() {
		((QuestionCard) getCurrentMainView()).refreshUiState();
	}

	public void setQuestion(QuestionCard.DisplayMode paramDisplayMode,
			Question paramQuestion, Answer paramAnswer,
			View.OnClickListener junkDrawerOnClickListener,
			View.OnClickListener forwardOnClickListener,
			View.OnClickListener answerOnClickListener, boolean paramBoolean) {
		this.question = paramQuestion;
		((QuestionCard) getCurrentMainView()).setQuestion(paramDisplayMode,
				this.question, junkDrawerOnClickListener);
		((QuestionCard) getCurrentMainView())
				.setForwardOnClickListener(forwardOnClickListener);
		((QuestionCard) getCurrentMainView())
				.setAnswerOnClickListener(answerOnClickListener);
		if ((!paramBoolean)
				&& (paramDisplayMode == QuestionCard.DisplayMode.BASE))
			((QuestionCard) getCurrentMainView()).showCanSwipeDownNux();
		// 如果确定的answer不为空，表示需要弹出来
		this.answer = paramAnswer;
		if (paramAnswer != null)
			popUpDetailCards();
		resetToStartState();
		// need 需要先置空
		((AnswerCardsAdapter) getCurrentDetailCards().getAdapter())
				.setQuestionAndAnswer(this.question, new ArrayList<Answer>(),
						null);
		((AnswerCardsAdapter) getCurrentDetailCards().getAdapter())
				.setQuestionAndAnswer(this.question,
						this.question.getAnswers(), paramAnswer);
	}

}
