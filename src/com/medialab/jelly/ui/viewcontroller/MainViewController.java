package com.medialab.jelly.ui.viewcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.controller.SimpleStackController;
import com.medialab.jelly.controller.SimpleStackListener;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.controller.ViewController;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.Notification;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.model.ThankYou;
import com.medialab.jelly.resultmodel.MainQuestionListResultModel;
import com.medialab.jelly.ui.SocialContextUtils;
import com.medialab.jelly.ui.adapter.AnswerCompositionController;
import com.medialab.jelly.ui.adapter.ComposeController;
import com.medialab.jelly.ui.adapter.ComposeInviteController;
import com.medialab.jelly.ui.adapter.NotificationWithTabController;
import com.medialab.jelly.ui.adapter.util.SingleQuestionControllerUtils;
import com.medialab.jelly.ui.adapter.util.ThankYouControllerUtils;
import com.medialab.jelly.ui.event.AnswerComposedNotSent;
import com.medialab.jelly.ui.event.AnswerNotificationSelected;
import com.medialab.jelly.ui.event.ComposeInviteOverlaySelectedEvent;
import com.medialab.jelly.ui.event.ComposeOverlaySelectedEvent;
import com.medialab.jelly.ui.event.ForwardRequested;
import com.medialab.jelly.ui.event.NewFriendAndFriendNumEvent;
import com.medialab.jelly.ui.event.NewUnReadTipsEvent;
import com.medialab.jelly.ui.event.NotificationOverlaySelectedEvent;
import com.medialab.jelly.ui.event.QuestionComposed;
import com.medialab.jelly.ui.event.ShowToastEvent;
import com.medialab.jelly.ui.event.SpinnerHideEvent;
import com.medialab.jelly.ui.event.SpinnerShowEvent;
import com.medialab.jelly.ui.event.StarredNotificationSelected;
import com.medialab.jelly.ui.event.ThankYouNotificationSelected;
import com.medialab.jelly.ui.event.ViewAnswerSelected;
import com.medialab.jelly.ui.view.NotificationWithTabOverlay;
import com.medialab.jelly.ui.view.OutOfQuestionsView;
import com.medialab.jelly.ui.view.Pawrappa;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UImageManager;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

public class MainViewController implements ViewController {

	private static final String TAG = MainViewController.class.getSimpleName();
	private final SimpleStackController stackController;
	private final Bus bus;
	private final Activity context;
	private final Handler handler;

	private NotificationWithTabController notificationController;
	private DismissableViewController overlayControllerToSet;
	private QuestionViewController singleBaseView;

	private SingleQuestionControllerUtils singleQuestionControllerUtils;
	// private AnswerCompositionController answerCompositionController;

	private StarfishScreenUtils screenUtils;

	public MainViewController(Activity paramContext,
			StarfishScreenUtils paramScreenUtils,
			SingleQuestionControllerUtils paramSingleQuestionControllerUtils) {
		this.stackController = new SimpleStackController(paramContext,
				paramScreenUtils);
		this.bus = JellyApplication.getBus();
		this.context = paramContext;
		this.handler = new Handler();
		this.screenUtils = paramScreenUtils;
		this.singleQuestionControllerUtils = paramSingleQuestionControllerUtils;
		this.stackController.setListener(new SimpleStackListener() {

			@Override
			public void didDismiss(
					DismissableViewController paramDismissableViewController,
					boolean paramBoolean) {
				// TODO Auto-generated method stub
				Log.v(MainViewController.TAG, "dismissed viewController");
				
				if (mQuestionList != null && mQuestionList.size() > 0) {
					mQuestionList.remove(0);
					
					if (mQuestionList.size() <= limitCount && hasMoreQuestion && !isLoading && loadOnce) {
						loadMoreQuestion();
					}
				}
				
				addViewsToStack(paramDismissableViewController);
			}
		});

		this.stackController.getView().setBackgroundColor(
				paramContext.getResources().getColor(R.color.activity_bg));

		loadQuestionList();
	}

	private void addViewsToStack(
			DismissableViewController paramDismissableViewController) {
		if (((!this.stackController.hasCurrentBaseController()) || ((this.stackController
				.getCurrentBaseController() instanceof OutOfQuestionsViewController)))) {

			Question localQuestion;
			if (mQuestionList != null && mQuestionList.size() > 0) {
				localQuestion = mQuestionList.get(0);
			} else {
				localQuestion = null;
			}

			if (localQuestion != null) {
				QuestionViewController localQuestionViewController;
				if (this.singleBaseView != null) {
					localQuestionViewController = this.singleBaseView;
					Log.v(TAG, "Using recycled controller "
							+ this.singleBaseView.toString());
				} else {

					localQuestionViewController = new BaseQuestionViewController(
							context, bus, new Pawrappa(context, screenUtils),
							singleQuestionControllerUtils,
							new AnswerCompositionController(context, bus));
					this.singleBaseView = localQuestionViewController;
				}
				localQuestionViewController.setQuestionAndAnswer(localQuestion,
						null);
				if (!(this.stackController.getCurrentBaseController() instanceof OutOfQuestionsViewController)) {
					this.stackController
							.addBaseController(localQuestionViewController);
				} else {
					this.stackController.dismissCurrentBaseView();
					this.stackController
							.addBaseController(localQuestionViewController);
				}
			} else {
				perhapsShowOutOfQuestions();
			}
		}
		if ((paramDismissableViewController != null)
				&& (paramDismissableViewController != this.singleBaseView))
			this.stackController
					.removeController(paramDismissableViewController);
	}

	private void checkQuestionAgainstCurrentBaseControllerAndDismissIfNecessary(
			long paramQuestionId) {
		DismissableViewController localDismissableViewController = this.stackController
				.getCurrentBaseController();
		if ((localDismissableViewController != null)
				&& ((localDismissableViewController instanceof BaseQuestionViewController))
				&& (((BaseQuestionViewController) localDismissableViewController)
						.getQuestion().qid == (paramQuestionId)))
			this.stackController.dismissCurrentBaseView();
	}

	private void openQuestionAndAnswerOverlay(Question paramQuestion,
			Answer paramAnswer) {

		QuestionViewController localQuestionViewController = new OverlayQuestionViewController(
				context, bus, new Pawrappa(context, screenUtils),
				singleQuestionControllerUtils, new AnswerCompositionController(
						context, bus));
		localQuestionViewController.setQuestionAndAnswer(paramQuestion,
				paramAnswer);
		this.stackController.setOverlayController(localQuestionViewController,
				SimpleStackController.AnimationType.SLIDE);
		checkQuestionAgainstCurrentBaseControllerAndDismissIfNecessary(paramQuestion.qid);
	}

	private void openThankYouOverlay(ThankYou paramThankYou) {
		ThankYouControllerUtils paramThankYouControllerUtils = new ThankYouControllerUtils(
				context, bus, screenUtils);
		ThankYouCardController localThankYouCardController = new ThankYouCardController(
				bus, context.getResources(), paramThankYouControllerUtils);

		localThankYouCardController.setThankYou(paramThankYou);
		this.stackController.setOverlayController(localThankYouCardController,
				SimpleStackController.AnimationType.SLIDE);
	}

	private View.OnClickListener getInviteClickListener() {
		return new View.OnClickListener() {
			public void onClick(View paramView) {
				Intent localIntent = new Intent();
				localIntent.setAction("android.intent.action.SEND");
				localIntent.putExtra(
						"android.intent.extra.TEXT",
						((Activity) context).getResources().getString(
								R.string.out_of_questions_invite_main_text));
				localIntent.putExtra(
						"android.intent.extra.SUBJECT",
						((Activity) context).getResources().getString(
								R.string.out_of_questions_invite_subject));
				localIntent.setType("text/plain");
				((Activity) context)
						.startActivity(Intent
								.createChooser(
										localIntent,
										((Activity) context)
												.getResources()
												.getString(
														R.string.out_of_questions_invite_chooser_title)));
			}
		};
	}

	public View.OnClickListener getFeelingHelpfulClickListener() {
		return new View.OnClickListener() {
			public void onClick(View paramView) {
			}
		};
	}

	private void perhapsShowOutOfQuestions() {
		if (!this.stackController.hasCurrentBaseController()) {
			OutOfQuestionsView paramOutOfQuestionsView = new OutOfQuestionsView(
					context, screenUtils);
			OutOfQuestionsViewController localOutOfQuestionsViewController = new OutOfQuestionsViewController(
					bus, context.getResources(), paramOutOfQuestionsView);
			this.stackController
					.addBaseController(localOutOfQuestionsViewController);
		}
	}

	/**
	 * @param paramComposeOverlaySelectedEvent
	 *            打开发问题的页面
	 */
	@Subscribe
	public void composeOverlaySelected(
			ComposeOverlaySelectedEvent paramComposeOverlaySelectedEvent) {
		if (!(this.stackController.getCurrentOverlayController() instanceof ComposeController)) {
			ComposeController localComposeController = new ComposeController(
					context, screenUtils, bus);
			localComposeController
					.setLaunchedFromOpen(paramComposeOverlaySelectedEvent.launchedFromOpen);
			// if (!paramComposeOverlaySelectedEvent.launchedFromOpen)
			// this.metrics.logProperty("main nav: press camera");
			this.stackController.setOverlayController(localComposeController,
					SimpleStackController.AnimationType.SLIDE);
		} else {
			this.stackController
					.dismissCurrentOverlay(SimpleStackController.AnimationType.SLIDE);
		}

		// if (!(this.stackController.getCurrentOverlayController() instanceof
		// ComposeQuestionController)) {
		// ComposeQuestionController localComposeQuestionController = new
		// ComposeQuestionController(
		// context, screenUtils, bus);
		//
		// this.stackController.setOverlayController(
		// localComposeQuestionController,

		// SimpleStackController.AnimationType.SLIDE);
		// } else {
		// this.stackController
		// .dismissCurrentOverlay(SimpleStackController.AnimationType.SLIDE);
		// }

	}

	@Subscribe
	public void composeInviteOverLaySelected(
			ComposeInviteOverlaySelectedEvent paramComposeInviteOverlaySelectedEvent) {
		if (!(this.stackController.getCurrentOverlayController() instanceof OutOfQuestionsViewController)) {

			ComposeInviteController localComposeInviteController = new ComposeInviteController(
					context, screenUtils, bus);

			this.stackController.setOverlayController(
					localComposeInviteController,
					SimpleStackController.AnimationType.SLIDE);
		} else {
			this.stackController
					.dismissCurrentOverlay(SimpleStackController.AnimationType.SLIDE);
		}
	}

	@Subscribe
	public void forwardRequested(ForwardRequested paramForwardRequested) {
		
		MobclickAgent.onEvent(context, UMengEventID.QUESTION_CARD_FOR_HELP);
		
		Question localQuestion = paramForwardRequested.question;
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.SEND");
		localIntent.putExtra("android.intent.extra.TEXT",
				localQuestion.metaData.shareTextWithUrl);
		localIntent.setType("text/plain");
		this.context.startActivity(Intent.createChooser(localIntent,
				context.getText(R.string.forward_button_text)));
	}

	@Subscribe
	public void newAnswerComposed(
			AnswerComposedNotSent paramAnswerComposedNotSent) {

		ComposingImage paramImage = paramAnswerComposedNotSent.image;

		if (paramImage != null) {

			String answerImageUrl = UTools.Storage
					.getQuestionPicSaveImagePath();

			if (UImageManager.saveBtimapToFile(paramImage.bitmap,
					answerImageUrl)) {
				MainViewController.this.bus.post(new SpinnerShowEvent());

				HashMap<String,String> map = new HashMap<String,String>();
				map.put("type","with_picture");
				MobclickAgent.onEvent(context, UMengEventID.ANSWER_POST, map);
				
				Map<String, String> params = new HashMap<String, String>();

				params.put("qid", paramAnswerComposedNotSent.question.qid + "");
				params.put("title", paramAnswerComposedNotSent.text + "");
				params.put("link", paramAnswerComposedNotSent.linkText + "");
				params.put("answerPic", "answerPic:" + answerImageUrl);

				mDataLoader.postData(UConfig.ANSWER_PUT_URL, params, context,
						new HDataListener() {

							@Override
							public void onSocketTimeoutException(String msg) {
								// TODO Auto-generated method stub
								MainViewController.this.bus
										.post(new SpinnerHideEvent());
							}

							@Override
							public void onFinish(String source) {
								// TODO Auto-generated method stub
								MainViewController.this.bus
										.post(new SpinnerHideEvent());

								if (MainViewController.this.stackController
										.getCurrentOverlayController() == null)
									MainViewController.this.stackController
											.dismissCurrentBaseView();
								else
									MainViewController.this.stackController
											.dismissCurrentOverlay(SimpleStackController.AnimationType.SLIDE);
								
								// 有新的保存到文件
								SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(context,UConstants.BASE_PREFS_NAME);
								mEditor.putBoolean(UConstants.HAS_NEW_ACTIVITY,true);
								
								// 有新的个人动态保存到文件
								mEditor.putBoolean(UConstants.HAS_NEW_SELF_ACTIVITY, true);
								
								mEditor.commit();
								
								MainViewController.this.bus.post(new NewUnReadTipsEvent(true));
							}

							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								MainViewController.this.bus
										.post(new SpinnerHideEvent());
							}

							@Override
							public void onConnectTimeoutException(String msg) {
								// TODO Auto-generated method stub
								MainViewController.this.bus
										.post(new SpinnerHideEvent());
							}
						});
			}

		} else {
			MainViewController.this.bus.post(new SpinnerShowEvent());

			HashMap<String,String> map = new HashMap<String,String>();
			map.put("type","without_picture");
			MobclickAgent.onEvent(context, UMengEventID.ANSWER_POST, map);
			
			Map<String, String> params = new HashMap<String, String>();

			params.put("qid", paramAnswerComposedNotSent.question.qid + "");
			params.put("title", paramAnswerComposedNotSent.text + "");
			params.put("link", paramAnswerComposedNotSent.linkText + "");

			mDataLoader.postData(UConfig.ANSWER_PUT_URL, params, context,
					new HDataListener() {

						@Override
						public void onSocketTimeoutException(String msg) {
							// TODO Auto-generated method stub
							MainViewController.this.bus
									.post(new SpinnerHideEvent());
						}

						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							MainViewController.this.bus
									.post(new SpinnerHideEvent());

							if (MainViewController.this.stackController
									.getCurrentOverlayController() == null)
								MainViewController.this.stackController
										.dismissCurrentBaseView();
							else
								MainViewController.this.stackController
										.dismissCurrentOverlay(SimpleStackController.AnimationType.SLIDE);
							
							// 有新的保存到文件
							SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(context,UConstants.BASE_PREFS_NAME);
							mEditor.putBoolean(UConstants.HAS_NEW_ACTIVITY,true);
							
							// 有新的个人动态保存到文件
							mEditor.putBoolean(UConstants.HAS_NEW_SELF_ACTIVITY, true);
							
							mEditor.commit();
							
							MainViewController.this.bus.post(new NewUnReadTipsEvent(true));
						}

						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
							MainViewController.this.bus
									.post(new SpinnerHideEvent());
						}

						@Override
						public void onConnectTimeoutException(String msg) {
							// TODO Auto-generated method stub
							MainViewController.this.bus
									.post(new SpinnerHideEvent());
						}
					});

		}
	}

	@Subscribe
	public void newQuestionComposed(QuestionComposed paramQuestionComposed) {

		ComposingImage paramImage = paramQuestionComposed.image;

		MobclickAgent.onEvent(context, UMengEventID.EDIT_QUESTION_POST);
		
		String questionImageUrl = UTools.Storage.getQuestionPicSaveImagePath();

		if (UImageManager.saveBtimapToFile(paramImage.bitmap, questionImageUrl)) {
			MainViewController.this.bus.post(new SpinnerShowEvent());
			Map<String, String> params = new HashMap<String, String>();

			params.put("title", paramQuestionComposed.text + "");
			params.put("link", paramQuestionComposed.linkText + "");
			params.put("questionPic", "questionPic:" + questionImageUrl);

			if (JellyApplication.IsMapQuestion()) {
				params.put("x", String.valueOf(JellyApplication
						.getMapQuestionLatitude()));
				params.put("y", String.valueOf(JellyApplication
						.getMapQuestionLongitude()));
			}

			mDataLoader.postData(UConfig.PUT_QUESTION_URL, params, context,
					new HDataListener() {

						@Override
						public void onSocketTimeoutException(String msg) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							MainViewController.this.bus
									.post(new SpinnerHideEvent());
							if ((MainViewController.this.stackController
									.getCurrentOverlayController() instanceof ComposeController))
								MainViewController.this.stackController
										.dismissCurrentOverlay(SimpleStackController.AnimationType.SLIDE);
							
							// 有新的保存到文件
							SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(context,UConstants.BASE_PREFS_NAME);
							mEditor.putBoolean(UConstants.HAS_NEW_ACTIVITY,true);
							
							// 有新的个人动态保存到文件
							mEditor.putBoolean(UConstants.HAS_NEW_SELF_ACTIVITY, true);
							
							mEditor.commit();
							
							MainViewController.this.bus.post(new NewUnReadTipsEvent(true));
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

	@Subscribe
	public void openNotificationOverlay(
			NotificationOverlaySelectedEvent paramNotificationOverlaySelectedEvent) {

		if (!(this.stackController.getCurrentOverlayController() instanceof NotificationWithTabController)) {
			this.stackController.setOverlayController(
					this.notificationController,
					SimpleStackController.AnimationType.NOTIFICATION);
		} else {
			this.stackController
					.dismissCurrentOverlay(SimpleStackController.AnimationType.NOTIFICATION);
		}
	}

	@Subscribe
	public void justStarredNotificationSelected(
			StarredNotificationSelected paramJustStarredNotificationSelected) {
		openQuestionAndAnswerOverlay(
				(paramJustStarredNotificationSelected.getNotification()).question,
				null);
	}

	@Subscribe
	public void answerNotificationClicked(
			AnswerNotificationSelected paramAnswerNotificationSelected) {
		openQuestionAndAnswerOverlay(
				(paramAnswerNotificationSelected.getNotification()).question,
				(paramAnswerNotificationSelected.getNotification()).question
						.getAnswerById((paramAnswerNotificationSelected
								.getNotification()).aid));
	}

	@Subscribe
	public void thankYouNotificationClicked(
			ThankYouNotificationSelected paramThankYouNotificationSelected) {

		ThankYou mThankYou = new ThankYou();

		Notification mNotification = paramThankYouNotificationSelected
				.getNotification();
		mThankYou = mNotification.thanksCard;
		mThankYou.question = mNotification.question;
		openThankYouOverlay(mThankYou);
	}

	@Subscribe
	public void viewAnswerClicked(ViewAnswerSelected paramViewAnswerSelected) {
		openQuestionAndAnswerOverlay(paramViewAnswerSelected.question,
				paramViewAnswerSelected.answer);
	}
	
	@Subscribe
	public void unreadNewFriendOrActivityUpdated(
			NewUnReadTipsEvent paramNewUnReadTipsEvent) {
		if (this.notificationController != null) {
			this.notificationController.showUnReadTips();
		}
	}
	
	@Subscribe
	public void showToastMethod(ShowToastEvent paramShowToastEvent) {
		UToast.showShortToast(context, context.getString(paramShowToastEvent.textId));
	}
	
	@Subscribe
	public void setNumOfNewFriendAndFriend(NewFriendAndFriendNumEvent paramNewFriendAndFriendNumEvent) {
		if (this.notificationController != null) {
			this.notificationController.setNumOfNewFriendAndFriend();
		}
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return stackController.getView();
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return this.stackController.onBackPressed();
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.unregister(this);
		this.stackController.onHide(paramViewGroup);
		
		// 退出时保存数据
		if (mQuestionList != null) {
			Gson gson = new Gson();
			
			String questionListString = gson.toJson(mQuestionList);
			
			SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(context,UConstants.BASE_PREFS_NAME);
			mEditor.putString(UConstants.LOCAL_QUESTION_LIST, questionListString);
			mEditor.commit();
		}
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		this.stackController.onShow(paramViewGroup);
		if (this.notificationController == null) {

			// NotificationListView paramNotificationListView = new
			// NotificationListView(
			// context);
			//
			// NotificationOverlay paramNotificationOverlay = new
			// NotificationOverlay(
			// context, screenUtils, paramNotificationListView);

			NotificationWithTabOverlay paramNotificationWithFriendsOverlay = new NotificationWithTabOverlay(
					bus, context);
			this.notificationController = new NotificationWithTabController(
					context, bus, new SocialContextUtils(context),
					paramNotificationWithFriendsOverlay);
			// this.notificationController.initialize();
		}
		// this.notificationController.fetchNewStuff();
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		Object localObject;
		if (paramBundle.getBoolean("compose_open", false)) {
			localObject = new ComposeController(context, screenUtils, bus);
			((ComposeController) localObject).setLaunchedFromOpen(false);
			((ComposeController) localObject).resumeState(paramBundle);
			this.overlayControllerToSet = ((DismissableViewController) localObject);
		} else {
			// if (paramBundle.getLong("question_answer__open", -1L) != -1L) {
			// localObject = paramBundle.getLong("question_answer__open");
			// this.singleBaseView = new BaseQuestionViewController(context,
			// bus, new Pawrappa(context, screenUtils),
			// singleQuestionControllerUtils,
			// new AnswerCompositionController(context, bus));
			// try {
			// localObject = (Pair) this.dataController.fetchQuestion(
			// (QuestionId) localObject, UseCache.YES).get(5000L,
			// TimeUnit.MILLISECONDS);
			// this.singleBaseView.setQuestionAndAnswer(
			// (Question) ((Pair) localObject).first, null);
			// this.singleBaseView.resumeState(paramBundle);
			// this.stackController.addBaseController(this.singleBaseView);
			// } catch (Exception localException) {
			// Log.e(TAG, "Error fetching question when resuming state",
			// localException);
			// }
			// continue;
			// }
		}
	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub
		Object localObject;
		if (!this.stackController.hasCurrentOverlayController()) {
			// localObject = this.stackController.getCurrentBaseController();
			// if ((localObject != null)
			// && ((localObject instanceof BaseQuestionViewController))) {
			// localObject = (BaseQuestionViewController) localObject;
			// ((BaseQuestionViewController) localObject)
			// .saveState(paramBundle);
			// paramBundle.putLong("question_answer__open",
			// ((BaseQuestionViewController) localObject)
			// .getQuestion().qid);
			// }
		} else {
			localObject = this.stackController.getCurrentOverlayController();
			if ((localObject instanceof ComposeController)) {
				((DismissableViewController) localObject)
						.saveState(paramBundle);
				paramBundle.putBoolean("compose_open", true);
			}
		}
	}

	private List<Question> mQuestionList;
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private boolean hasMoreQuestion = true;
	private boolean isLoading = false;
	
	private int loadCount = 10;
	private int limitCount = 3;
	
	private boolean loadOnce = true;

	private void loadQuestionList() {
		isLoading = true;
		
		SharedPreferences sp = UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME);
		String localQuestionListString = sp.getString(UConstants.LOCAL_QUESTION_LIST, "");
		
		Gson mGson = new Gson();
		
		try {
			List<Question> localQuestionList = mGson.fromJson(localQuestionListString, new TypeToken<List<Question>>() {
												}.getType());
			
			if (localQuestionList != null && localQuestionList.size() > 0) {
				
				mQuestionList = localQuestionList;
				MainViewController.this.addViewsToStack(null);
				isLoading = false;
				return;
			}
		} catch (JsonSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("forward", "1");
		params.put("count", "" + loadCount);

		mDataLoader.postData(UConfig.QUESTION_LIST_URL, params, context,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						MainViewController.this.addViewsToStack(null);
						hasMoreQuestion = false;
						isLoading = false;
					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							MainQuestionListResultModel mModel = gson
									.fromJson(
											source,
											new TypeToken<MainQuestionListResultModel>() {
											}.getType());

							if (mModel != null) {
								if (mModel.data != null
										&& mModel.data.size() > 0) {
									mQuestionList = mModel.data;
									
									if (mModel.data.size() < loadCount) {
										hasMoreQuestion = false;
									} else {
										hasMoreQuestion = true;
									}
								} else {
									hasMoreQuestion = false;
								}
							} else {
								hasMoreQuestion = false;
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							hasMoreQuestion = false;
						}

						isLoading = false;
						MainViewController.this.addViewsToStack(null);
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						MainViewController.this.addViewsToStack(null);
						hasMoreQuestion = false;
						isLoading = false;
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						MainViewController.this.addViewsToStack(null);
						hasMoreQuestion = false;
						isLoading = false;
					}
				});
	}
	
	private void loadMoreQuestion() {
		isLoading = true;
		loadOnce = false;
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("forward", "0");
		params.put("count", "" + loadCount);

		mDataLoader.postData(UConfig.QUESTION_LIST_URL, params, context,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						hasMoreQuestion = false;
						isLoading = false;
					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							MainQuestionListResultModel mModel = gson
									.fromJson(
											source,
											new TypeToken<MainQuestionListResultModel>() {
											}.getType());

							if (mModel != null) {
								if (mModel.data != null
										&& mModel.data.size() > 0) {
									
									if (mQuestionList != null) {
										mQuestionList.addAll(mModel.data);
									} else {
										mQuestionList = mModel.data;
									}
									
									if (mModel.data.size() < loadCount) {
										hasMoreQuestion = false;
									} else {
										hasMoreQuestion = true;
									}
								} else {
									hasMoreQuestion = false;
								}
							} else {
								hasMoreQuestion = false;
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							hasMoreQuestion = false;
						}
						
						if (mQuestionList != null && mQuestionList.size() > limitCount) {
							loadOnce = true;
						}

						isLoading = false;
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						hasMoreQuestion = false;
						isLoading = false;
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						hasMoreQuestion = false;
						isLoading = false;
					}
				});
	}

}
