package com.medialab.jelly.ui.viewcontroller;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.resultmodel.QuestionInfoResultModel;
import com.medialab.jelly.ui.adapter.AnswerCompositionController;
import com.medialab.jelly.ui.adapter.util.SingleQuestionControllerUtils;
import com.medialab.jelly.ui.event.TopNavBarIconDisplay;
import com.medialab.jelly.ui.view.Pawrappa;
import com.medialab.jelly.ui.view.QuestionAndAnswerCardView;
import com.medialab.jelly.ui.view.QuestionCard;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public abstract class QuestionViewController implements
		DismissableViewController {
	private final AnswerCompositionController answerCompositionController;
	protected final Bus bus;
	protected Question question;
	private final Resources resources;
	private final SingleQuestionControllerUtils singleQuestionUtils;
	protected QuestionAndAnswerCardView view;
	private final Pawrappa wrapper;
	protected final Activity paramActivity;

	public QuestionViewController(Activity paramActivity, Bus paramBus,
			Pawrappa paramPawrappa,
			SingleQuestionControllerUtils paramSingleQuestionControllerUtils,
			AnswerCompositionController paramAnswerCompositionController) {
		this.paramActivity = paramActivity;
		this.bus = paramBus;
		this.wrapper = paramPawrappa;
		this.resources = paramActivity.getResources();
		this.answerCompositionController = paramAnswerCompositionController;
		this.singleQuestionUtils = paramSingleQuestionControllerUtils;
	}

	private void startAnswerComposition() {
		if (!this.view.isDisplayingAnswerComposition()) {
			this.answerCompositionController.setQuestion(this.question);
			this.view.startNewAnswerCreation(this.answerCompositionController);
		}
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.wrapper;
	}

	public abstract void childOnShow();

	public abstract QuestionCard.DisplayMode getDisplayMode();

	protected Question getQuestion() {
		return this.question;
	}

	protected Resources getResources() {
		return this.resources;
	}

	protected String getStringForResId(int paramInt) {
		return this.resources.getString(paramInt);
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean bool = true;
		if (!this.view.isDisplayingAnswerComposition()) {
			bool = false;
		} else if (!this.answerCompositionController.onBackPressed()) {
			this.view.endNewAnswerCreation();
			this.bus.post(new TopNavBarIconDisplay(
					getStringForResId(R.string.top_nav_help_text), bool));
		}
		return bool;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.view.isDisplayingAnswerComposition()) {
			this.answerCompositionController.onHide(this.view);
		}
		this.bus.unregister(this);
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		childOnShow();
		if (this.view.isDisplayingAnswerComposition()) {
			this.answerCompositionController.onShow(this.view);
		}

//		if (this.question != null) {
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("qid", String.valueOf(this.question.qid));
//
//			mDataLoader.postData(UConfig.QUESTION_GET_URL, params,
//					paramActivity, new HDataListener() {
//
//						@Override
//						public void onSocketTimeoutException(String msg) {
//							// TODO Auto-generated method stub
//
//						}
//
//						@Override
//						public void onFinish(String source) {
//							// TODO Auto-generated method stub
//							Gson gson = new Gson();
//
//							try {
//								QuestionInfoResultModel mModel = gson
//										.fromJson(
//												source,
//												new TypeToken<QuestionInfoResultModel>() {
//												}.getType());
//
//								if (mModel != null
//										&& mModel.result == UConstants.SUCCESS
//										&& mModel.data != null) {
//
//									QuestionViewController.this.view
//											.refreshSlidingObjects(mModel.data,
//													mModel.data.getAnswers());
//
//								}
//							} catch (JsonSyntaxException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//
//						@Override
//						public void onFail(String msg) {
//							// TODO Auto-generated method stub
//
//						}
//
//						@Override
//						public void onConnectTimeoutException(String msg) {
//							// TODO Auto-generated method stub
//
//						}
//					});
//		}

	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		if (paramBundle.getBoolean("question_is_composing", false)) {
			this.answerCompositionController.resumeState(paramBundle);
			startAnswerComposition();
		}
	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		if (this.view.isDisplayingAnswerComposition()) {
			paramBundle.putBoolean("question_is_composing", true);
			this.answerCompositionController.saveState(paramBundle);
		}
	}

	@Override
	public boolean canBeDismissed() {
		// TODO Auto-generated method stub
		return view.isDismissable();
	}

	@Override
	public void onStartDragging() {
		// TODO Auto-generated method stub
		this.view.hideDetailCards();
	}

	@Override
	public void onStopDragging() {
		// TODO Auto-generated method stub
		if (!this.view.isDisplayingAnswerComposition())
			this.view.showDetailCards();
	}

	@Override
	public void reset(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.view.endNewAnswerCompositionImmediately();
		this.answerCompositionController.reset(paramViewGroup);
	}

	public void setQuestionAndAnswer(Question paramQuestion, Answer paramAnswer) {
		this.question = paramQuestion;
		if ((this.view == null) || (this.wrapper.getChildCount() <= 0)) {
			this.view = this.singleQuestionUtils
					.createEmptyView(this.paramActivity);
			this.wrapper.addView(this.view);
		}
		this.singleQuestionUtils.setQuestionAndAnswerForView(
				this.paramActivity, getDisplayMode(), this.view, paramQuestion,
				paramAnswer, new View.OnClickListener() {
					public void onClick(View paramView) {
						QuestionViewController.this.startAnswerComposition();
						
						MobclickAgent.onEvent(paramActivity, UMengEventID.QUESTION_CARD_ANSWER);
					}
				});
		
		if (this.question != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("qid", String.valueOf(this.question.qid));

			mDataLoader.postData(UConfig.QUESTION_GET_URL, params,
					paramActivity, new HDataListener() {

						@Override
						public void onSocketTimeoutException(String msg) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							Gson gson = new Gson();

							try {
								QuestionInfoResultModel mModel = gson
										.fromJson(
												source,
												new TypeToken<QuestionInfoResultModel>() {
												}.getType());

								if (mModel != null
										&& mModel.result == UConstants.SUCCESS
										&& mModel.data != null) {

									QuestionViewController.this.view
											.refreshSlidingObjects(mModel.data,
													mModel.data.getAnswers());

								}
							} catch (JsonSyntaxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onConnectTimeoutException(String msg) {
							// TODO Auto-generated method stub

						}
					});
		}
	}

}
