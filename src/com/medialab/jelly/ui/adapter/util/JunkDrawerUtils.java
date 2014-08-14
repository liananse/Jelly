package com.medialab.jelly.ui.adapter.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.resultmodel.BaseResultModel;
import com.medialab.jelly.ui.event.ShowToastEvent;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class JunkDrawerUtils {

	private final Activity activity;

	public JunkDrawerUtils(Activity paramActivity) {
		this.activity = paramActivity;
	}

	public View.OnClickListener getJunkDrawerClickerForAnswer(
			Answer paramAnswer, JellyUser paramJellyUser) {
		return new JunkDrawerClickListenerForAnswer(paramAnswer, paramJellyUser);
	}

	private class JunkDrawerClickListenerForAnswer implements OnClickListener {

		Answer answer;
		JellyUser currentUser;

		public JunkDrawerClickListenerForAnswer(Answer paramAnswer,
				JellyUser paramJellyUser) {
			this.answer = paramAnswer;
			this.currentUser = paramJellyUser;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			MobclickAgent.onEvent(activity, UMengEventID.ANSWER_CARD_OPERATE);

			new JunkDrawerUtils.AnswerJunkDrawerDialog(this.answer,
					this.currentUser).show(activity.getFragmentManager(),
					"Answer junk drawer");
		}

	}

	public View.OnClickListener getJunkDrawerClickerForQuestion(
			Question paramQuestion, JellyUser paramJellyUser) {
		return new JunkDrawerClickListenerForQuestion(paramQuestion,
				paramJellyUser);
	}

	private class JunkDrawerClickListenerForQuestion implements OnClickListener {

		Question question;
		JellyUser currentUser;

		public JunkDrawerClickListenerForQuestion(Question paramQuestion,
				JellyUser paramJellyUser) {
			this.question = paramQuestion;
			this.currentUser = paramJellyUser;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			MobclickAgent.onEvent(activity, UMengEventID.QUESTION_CARD_OPERATE);

			new JunkDrawerUtils.QuestionJunkDrawerDialog(this.question,
					this.currentUser).show(activity.getFragmentManager(),
					"Question junk drawer");
		}

	}

	public class QuestionJunkDrawerDialog extends DialogFragment {
		private final boolean forCurrentUser;
		private final Question question;

		private HHttpDataLoader mDataLoader = new HHttpDataLoader();

		public QuestionJunkDrawerDialog(Question paramJellyUser, JellyUser arg3) {
			this.question = paramJellyUser;
			boolean bool;
			if ((paramJellyUser.user == null)
					|| (!paramJellyUser.user.equals(arg3)))
				bool = false;
			else
				bool = true;
			this.forCurrentUser = bool;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			// common actions
			CharSequence[] items1 = getResources().getTextArray(
					R.array.question_junk_drawer_array);

			// if the question's owner is not self
			if (!forCurrentUser) {

			} else {
				// if the question's owner is self
				// CharSequence[] items2 = new CharSequence[1 + items1.length];
				// System.arraycopy(items1, 0, items2, 0, items1.length);
				//
				// items1 = items2;
				//
				// items1[items1.length - 1] = getResources().getString(
				// R.string.junk_drawer_delete_answer);
			}
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(
					getActivity(), AlertDialog.THEME_HOLO_LIGHT);
			localBuilder.setTitle(R.string.question_junk_drawer_title);

			localBuilder.setItems(items1,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 0:
								// report question

								MobclickAgent.onEvent(getActivity(),
										UMengEventID.QUESTION_CARD_REPORT);

								Map<String, String> params = new HashMap<String, String>();
								params.put("qid", question.qid + "");
								params.put("content", "");
								mDataLoader.postData(
										UConfig.QUESTION_REPORT_URL, params,
										getActivity(), new HDataListener() {

											@Override
											public void onSocketTimeoutException(
													String msg) {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onFinish(String source) {
												// TODO Auto-generated method
												// stub
												Gson gson = new Gson();

												try {
													BaseResultModel mModel = gson
															.fromJson(
																	source,
																	new TypeToken<BaseResultModel>() {
																	}.getType());

													if (mModel != null
															&& mModel.result == UConstants.SUCCESS) {
														JellyApplication.getBus().post(new ShowToastEvent(R.string.report_question_success));
													}
													
												} catch (JsonSyntaxException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}

											}

											@Override
											public void onFail(String msg) {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onConnectTimeoutException(
													String msg) {
												// TODO Auto-generated method
												// stub

											}
										});
								break;
							case 1:
								// don't like this question

								MobclickAgent.onEvent(getActivity(),
										UMengEventID.QUESTION_CARD_DO_NOT_LIKE);

								Map<String, String> paramsDisLike = new HashMap<String, String>();
								paramsDisLike.put("qid", question.qid + "");

								mDataLoader.postData(
										UConfig.QUESTION_DISLIKE_URL,
										paramsDisLike, getActivity(), null);
								break;
							case 2:
								// mute person or delete answer
								if (!forCurrentUser) {
									// mute persons

								} else {
									// delete question
								}
								break;
							default:
								break;
							}
						}
					});

			localBuilder.setNegativeButton(
					getText(R.string.junk_drawer_dialog_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked Cancel so do some stuff */
							dialog.cancel();
						}
					});

			return localBuilder.create();
		}

	}

	public class AnswerJunkDrawerDialog extends DialogFragment {
		private final Answer answer;
		private final boolean forCurrentUser;

		private HHttpDataLoader mDataLoader = new HHttpDataLoader();

		public AnswerJunkDrawerDialog(Answer paramJellyUser, JellyUser arg3) {
			this.answer = paramJellyUser;
			boolean bool;
			if ((paramJellyUser.user == null)
					|| (!paramJellyUser.user.equals(arg3)))
				bool = false;
			else
				bool = true;
			this.forCurrentUser = bool;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			// common actions
			CharSequence[] items1 = getResources().getTextArray(
					R.array.answer_junk_drawer_array);

			// if the answer's owner is not self, if we find someone was
			// Disturbed we will open it
			if (!forCurrentUser) {

				/**
				 * CharSequence[] items2 = new CharSequence[1 + items1.length];
				 * System.arraycopy(items1, 0, items2, 0, items1.length);
				 * 
				 * items1 = items2;
				 * 
				 * String userNickName; if (this.answer.user.getNickName() !=
				 * null && !this.answer.user.getNickName().equals("")) {
				 * userNickName = this.answer.user.getNickName(); } else {
				 * userNickName = ""; }
				 * 
				 * System.out.println("answer user nickname " + userNickName);
				 * Object[] arrayOfObject = new Object[1]; arrayOfObject[0] =
				 * userNickName; items1[items1.length - 1] =
				 * getResources().getString(R.string.junk_drawer_mute_user,
				 * arrayOfObject);
				 **/
			} else {
				// if the answer's owner is self
				// CharSequence[] items2 = new CharSequence[1 + items1.length];
				// System.arraycopy(items1, 0, items2, 0, items1.length);
				//
				// items1 = items2;
				//
				// items1[items1.length - 1] = getResources().getString(
				// R.string.junk_drawer_delete_answer);
			}

			AlertDialog.Builder localBuilder = new AlertDialog.Builder(
					getActivity(), AlertDialog.THEME_HOLO_LIGHT);

			localBuilder.setTitle(R.string.answer_junk_drawer_title);
			localBuilder.setItems(items1,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 0:
								// report answer

								MobclickAgent.onEvent(getActivity(),
										UMengEventID.ANSWER_CARD_REPORT);

								Map<String, String> params = new HashMap<String, String>();
								params.put("aid", answer.aid + "");
								params.put("content", "");
								mDataLoader.postData(UConfig.ANSWER_REPORT_URL,
										params, getActivity(), new HDataListener() {
											
											@Override
											public void onSocketTimeoutException(String msg) {
												// TODO Auto-generated method stub
												
											}
											
											@Override
											public void onFinish(String source) {
												// TODO Auto-generated method stub
												Gson gson = new Gson();

												try {
													BaseResultModel mModel = gson
															.fromJson(
																	source,
																	new TypeToken<BaseResultModel>() {
																	}.getType());

													if (mModel != null
															&& mModel.result == UConstants.SUCCESS) {
														JellyApplication.getBus().post(new ShowToastEvent(R.string.report_answer_success));
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
								break;
							case 1:
								// don't like this answer

								MobclickAgent.onEvent(getActivity(),
										UMengEventID.ANSWER_CARD_DO_NOT_LIKE);

								Map<String, String> paramsDisLike = new HashMap<String, String>();
								paramsDisLike.put("aid", answer.aid + "");

								mDataLoader.postData(
										UConfig.ANSWER_DISLIKE_URL,
										paramsDisLike, getActivity(), null);
								break;
							case 2:
								// mute person or delete answer
								if (!forCurrentUser) {
									// mute persons

								} else {
									// delete answer
								}
								break;
							default:
								break;
							}
						}
					});
			localBuilder.setNegativeButton(
					getText(R.string.junk_drawer_dialog_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked Cancel so do some stuff */
							dialog.cancel();
						}
					});

			return localBuilder.create();
		}

	}
}
