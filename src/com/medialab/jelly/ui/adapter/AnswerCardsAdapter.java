package com.medialab.jelly.ui.adapter;

import java.util.List;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SpringSystem;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.ui.adapter.util.JunkDrawerUtils;
import com.medialab.jelly.ui.view.AnswerCard;
import com.medialab.jelly.ui.widget.SlidingDetailCardsAdapter;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.view.CustomTextView;
import com.squareup.otto.Bus;

public class AnswerCardsAdapter extends
		SlidingDetailCardsAdapter<Answer, AnswerCard> {

	private static final String TAG = AnswerCardsAdapter.class.getSimpleName();
	private final Bus bus;
	private final JellyUser currentUser;
	private final CustomTextView detailView;
	private final JunkDrawerUtils junkDrawerUtils;
	private View maskOne;
	private View maskTwo;
	private Question question;
	private final Resources resources;
	private final SpringSystem springSystem;
	private final ExecutorService uiExecutor;
	private final Context context;

	public AnswerCardsAdapter(Context paramContext,
			JunkDrawerUtils paramJunkDrawerUtils, Bus paramBus,
			SpringSystem paramSpringSystem, ExecutorService paramExecutorService) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		Context localContext = getContext();
		context = paramContext;
		this.springSystem = paramSpringSystem;
		this.uiExecutor = paramExecutorService;
		this.bus = paramBus;
		this.currentUser = JellyApplication.getMineInfo(paramContext);
		this.junkDrawerUtils = paramJunkDrawerUtils;
		this.resources = localContext.getResources();
		this.detailView = new CustomTextView(getContext());
		FontManager.setTypeface(this.detailView, FontManager.Weight.MEDIUM);
		this.detailView.setTextSize(0, this.resources
				.getDimensionPixelSize(R.dimen.question_card_main_text_size));
		this.detailView.setTextColor(this.resources
				.getColor(R.color.sliding_cards_answer_number));
		this.detailView.setGravity(17);
	}

	private void refreshDetailTextView() {
		int j = getCount();
		Resources localResources = this.resources;
		int i = R.plurals.sliding_cards_answers_string;
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(j);
		String str = localResources.getQuantityString(i, j, arrayOfObject);
		this.detailView.setText(str);
	}

	@Override
	protected AnswerCard convertObjectToNewView(Answer paramAnswer,
			ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "convertObjectToNewView answerCardAdapter");
		AnswerCard localAnswerCard = new AnswerCard((Activity) context,
				springSystem);
		localAnswerCard.setAnswer(paramAnswer, this.question,
				this.junkDrawerUtils.getJunkDrawerClickerForAnswer(paramAnswer,
						this.currentUser), this.currentUser.getUid(), this.bus,
				this.uiExecutor);
		return localAnswerCard;
	}

	@Override
	protected void convertObjectToRecycledView(Answer paramAnswer,
			ViewGroup paramViewGroup, AnswerCard paramAnswerCard) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "convertObjectToTecycleView answerCardAdapter");
		paramAnswerCard.setAnswer(paramAnswer, this.question,
				this.junkDrawerUtils.getJunkDrawerClickerForAnswer(paramAnswer,
						this.currentUser), this.currentUser.getUid(), this.bus,
				this.uiExecutor);
	}

	@Override
	protected View getDetailView() {
		// TODO Auto-generated method stub
		return this.detailView;
	}

	@Override
	public View getMaskingView(int paramInt) {
		// TODO Auto-generated method stub
		View localView;
		if (paramInt != 1) {
			if (paramInt != 2) {
				localView = null;
			} else {
				if (this.maskTwo == null) {
					this.maskTwo = new View(getContext());
					this.maskTwo
							.setBackgroundResource(R.drawable.answer_card_background_mask_two);
				}
				localView = this.maskTwo;
			}
		} else {
			if (this.maskOne == null) {
				this.maskOne = new View(getContext());
				this.maskOne
						.setBackgroundResource(R.drawable.answer_card_background_mask_one);
			}
			localView = this.maskOne;
		}
		return localView;
	}

	@Override
	protected boolean isExpandable() {
		// TODO Auto-generated method stub
		boolean i;
		if (getCount() <= 0)
			i = false;
		else
			i = true;
		return i;
	}

	public void setQuestionAndAnswer(Question paramQuestion,
			List<Answer> paramList, Answer paramAnswer) {
		this.question = paramQuestion;
		super.setNewObjects(paramList, paramAnswer);
		refreshDetailTextView();
	}

	public void updateAnswers(Question paramQuestion, List<Answer> paramList) {
		this.question = paramQuestion;
		super.refreshObjects(paramList);
		refreshDetailTextView();
	}

}
