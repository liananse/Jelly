package com.medialab.jelly.ui.view;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.model.QuestionStatusType;
import com.medialab.jelly.ui.widget.SlideableMainView;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.CustomTextView;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class QuestionCard extends ViewGroup implements SlideableMainView {

	private final Button answerButton;
	private final Rect borderRect = new Rect();
	private final int bottomRowTouchExtension;
	private final Bus bus;
	private final LinearLayout buttonRow;
	private final int checkHeight;
	private JellyUser currentUser;
	private DisplayMode displayMode;
	private final int dogEarDimen;
	private final View.OnClickListener fadeClickListener;
	private Spring fadeSpring;
	private final ImageView followButton;
	private final int followButtonTouchExtension;
	private final Button forwardButton;
	private final ToggleButton goodButton;
	private final RoundedImageView imageView;
	private Question question;
	private final int questionButtonRowHeight;

	private LinearLayout swipeDownNux;
	private final TopOfCardTextAndIndicators topRow;

	private FinalBitmap fb;

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private final Activity activity;

	public QuestionCard(Activity paramActivity, Bus paramBus,
			SpringSystem paramSpringSystem) {
		super(paramActivity);
		// TODO Auto-generated constructor stub
		Context localContext = (Context) paramActivity;
		activity = paramActivity;
		this.bus = paramBus;
		setBackgroundResource(R.drawable.hollowed_card_overlay);

		this.setClickable(true);
		fb = FinalBitmap.create(localContext);
		this.topRow = new TopOfCardTextAndIndicators(localContext);
		this.topRow.init(false, false, null);

		this.fadeSpring = paramSpringSystem.createSpring()
				.setCurrentValue(0.0D).setAtRest()
				.addListener(getFadeSpringListener());

		this.followButton = new ImageView(localContext);
		this.followButton.setImageResource(R.drawable.dog_ear);

		this.buttonRow = ((LinearLayout) LinearLayout.class
				.cast(((LayoutInflater) paramActivity
						.getSystemService("layout_inflater")).inflate(
						R.layout.question_button_row_layout, this, false)));

		this.answerButton = ((Button) Button.class.cast(this.buttonRow
				.findViewById(R.id.answer_button)));
		this.forwardButton = ((Button) Button.class.cast(this.buttonRow
				.findViewById(R.id.forward_button)));
		this.goodButton = ((ToggleButton) ToggleButton.class
				.cast(this.buttonRow.findViewById(R.id.good_button)));

		FontManager.setTypeface(this.answerButton, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(this.forwardButton, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(this.goodButton, FontManager.Weight.HUAKANG);

		this.followButtonTouchExtension = getResources().getDimensionPixelSize(
				R.dimen.follow_button_touch_extension);

		this.imageView = new RoundedImageView(localContext);
		this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.imageView.setCornerRadius(R.dimen.card_top_background_radius);

		this.fadeClickListener = new View.OnClickListener() {
			public void onClick(View paramView) {
				int i;
				if (QuestionCard.this.fadeSpring.getEndValue() == 0.0D)
					i = 0;
				else
					i = 1;
				if (i == 0) {
					QuestionCard.this.fadeSpring.setEndValue(1.0D);
					QuestionCard.this.followButton.setClickable(false);
					QuestionCard.this.answerButton.setClickable(false);
					QuestionCard.this.followButton.setClickable(false);
					QuestionCard.this.forwardButton.setClickable(false);
					QuestionCard.this.goodButton.setClickable(false);
					QuestionCard.this.topRow.setClickable(false);
				} else {
					QuestionCard.this.fadeSpring.setEndValue(0.0D);
					QuestionCard.this.followButton.setClickable(true);
					QuestionCard.this.answerButton.setClickable(true);
					QuestionCard.this.followButton.setClickable(true);
					QuestionCard.this.forwardButton.setClickable(true);
					QuestionCard.this.goodButton.setClickable(true);
					QuestionCard.this.topRow.setClickable(true);
				}
			}
		};

		this.bottomRowTouchExtension = getResources().getDimensionPixelSize(
				R.dimen.card_bottom_row_touch_extension);
		this.checkHeight = getResources().getDimensionPixelSize(
				R.dimen.question_card_button_text_size);
		this.questionButtonRowHeight = getResources().getDimensionPixelSize(
				R.dimen.question_card_button_row_height);
		this.dogEarDimen = getResources().getDimensionPixelSize(
				R.dimen.question_card_dog_ear_dimen);

		this.currentUser = JellyApplication.getMineInfo(paramActivity);

		addView(this.imageView);
		addView(this.topRow);
		addView(this.buttonRow);
		addView(this.followButton);
	}

	private SpringListener getFadeSpringListener() {
		return new SimpleSpringListener() {
			public void onSpringUpdate(Spring paramSpring) {
				float f = 1.0F - (float) paramSpring.getCurrentValue();
				QuestionCard.this.topRow.setAlpha(f);
				QuestionCard.this.buttonRow.setAlpha(f);
				QuestionCard.this.followButton.setAlpha(f);
			}
		};
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.fadeSpring.destroy();
		this.fadeSpring = null;
	}

	public int getTopOfSocialWidget() {
		return this.topRow.getTopOfSocialWidget();
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int k = paramInt4 - paramInt2;
		int m = paramInt3 - paramInt1;
		int j = this.borderRect.top;
		int i = this.borderRect.left;
		int n = i + this.imageView.getMeasuredWidth();
		int i1 = j + this.imageView.getMeasuredHeight();
		this.imageView.layout(i, j, n, i1);
		i1 = j + this.topRow.getMeasuredHeight();
		n = i + this.topRow.getMeasuredWidth();
		this.topRow.layout(i, j, n, i1);
		n = m - this.borderRect.right;
		m = n - this.followButton.getMeasuredWidth();
		i1 = this.followButton.getMeasuredHeight();
		this.followButton.layout(m, j, n, i1);
		m = k - this.borderRect.bottom;
		k = m - this.buttonRow.getMeasuredHeight();
		j = i + this.buttonRow.getMeasuredWidth();
		this.buttonRow.layout(i, k, j, m);
		if (this.swipeDownNux != null) {
			m = k - this.swipeDownNux.getMeasuredHeight();
			this.swipeDownNux.layout(i, m, j, k);
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		getBackground().getPadding(this.borderRect);
		int i = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt1)
						- (this.borderRect.left + this.borderRect.right),
				MeasureSpec.EXACTLY);
		this.buttonRow.measure(i, View.MeasureSpec.makeMeasureSpec(
				this.questionButtonRowHeight, MeasureSpec.EXACTLY));
		int j = View.MeasureSpec.getSize(paramInt2)
				- (this.borderRect.top + this.borderRect.bottom);
		int k = View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY);
		this.imageView.measure(i, k);
		j -= this.questionButtonRowHeight;
		this.topRow.measure(i,
				View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		j = View.MeasureSpec.makeMeasureSpec(this.dogEarDimen,
				MeasureSpec.EXACTLY);
		this.followButton.measure(j, j);
		if (this.swipeDownNux != null) {
			j = getResources().getDimensionPixelOffset(
					R.dimen.swipe_down_nux_height);
			this.swipeDownNux.measure(i,
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY));
		}
		super.onMeasure(paramInt1, paramInt2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		float f3 = paramMotionEvent.getX();
		float f2 = paramMotionEvent.getY();
		float f1 = this.followButton.getY()
				+ this.followButton.getMeasuredHeight();
		boolean bool;
		if ((f3 <= this.followButton.getX() - this.followButtonTouchExtension)
				|| (f2 >= f1 + this.followButtonTouchExtension)) {
			bool = super.onTouchEvent(paramMotionEvent);
		} else {
			paramMotionEvent.setLocation(1.0F, 1.0F);
			bool = this.followButton.dispatchTouchEvent(paramMotionEvent);
		}
		return bool;
	}

	public void refreshUiState() {
		if (!this.currentUser.equals(this.question.user)) {
			if (this.question.metaData.viewerStarred == 0)
				this.followButton.setImageResource(R.drawable.dog_ear);
			else
				this.followButton.setImageResource(R.drawable.dog_ear_selected);
		} else if (this.question.status == 0)
			this.followButton.setImageResource(R.drawable.resolved);
		else
			this.followButton.setImageResource(R.drawable.resolve_selected);
	}

	public void resetMainViewClickListener() {
		setOnClickListener(this.fadeClickListener);
		this.topRow.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
			}
		});
	}

	public void setAnswerOnClickListener(
			View.OnClickListener answerOnClickListener) {
		if (this.answerButton.getVisibility() == View.VISIBLE)
			this.answerButton.setOnClickListener(answerOnClickListener);
	}

	public void setForwardOnClickListener(
			View.OnClickListener paramOnClickListener) {
		this.forwardButton.setOnClickListener(paramOnClickListener);
	}

	public void setMainViewClickListener(
			View.OnClickListener paramOnClickListener) {
		setOnClickListener(paramOnClickListener);
		this.topRow.setOnClickListener(paramOnClickListener);
	}

	public void setQuestion(DisplayMode paramDisplayMode,
			Question paramQuestion,
			View.OnClickListener junkDrawerOnClickListener) {
		if (this.swipeDownNux != null) {
			removeView(this.swipeDownNux);
			this.swipeDownNux = null;
		}
		if (this.fadeSpring.getCurrentValue() > 0.0D)
			this.fadeSpring.setCurrentValue(0.0D).setAtRest();
		this.displayMode = paramDisplayMode;
		this.question = paramQuestion;

		// Set QuestionCard Image
		if (question.questionImage != null) {
			fb.display(imageView, UDisplayWidth.getPicUrlByWidth(
					UDisplayWidth.PIC_WIDTH_680,
					this.question.questionImage.picName));
		}

		// Set QuestionCard SocialContext
		this.topRow.loadQuestionSocialContext(this.question.context,
				junkDrawerOnClickListener, this.question.createdAt,
				this.question.user, this.question.middleUser);

		// Set QuestionCard Content
		this.topRow.setQuestionText(this.question.getPregeneratedText());

		// Set QuestionCard Link Content
		Spannable spannableLink;
		if (!this.question.link.equals("")) {
			spannableLink = this.question.getPregeneratedLinkText();
		} else {
			spannableLink = null;
		}
		this.topRow.setTextLink(spannableLink);

		this.followButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sp = UTools.Storage.getSharedPreferences(
						activity, UConstants.BASE_PREFS_NAME);
				boolean firstFollowQuestion = sp.getBoolean(
						UConstants.FIRST_FOLLOW_QUESTION, true);

				if (firstFollowQuestion) {
					SharedPreferences.Editor mEditor = UTools.Storage
							.getSharedPreEditor(activity,
									UConstants.BASE_PREFS_NAME);
					mEditor.putBoolean(UConstants.FIRST_FOLLOW_QUESTION, false);
					mEditor.commit();

					new QuestionCard.FollowDialog().show(
							activity.getFragmentManager(), "follow question");

				} else {

					Map<String, String> params = new HashMap<String, String>();

					String url;
					params.put("qid", QuestionCard.this.question.qid + "");

					if (!currentUser.equals(question.user)) {
						if (QuestionCard.this.question.metaData.viewerStarred == 1) {
							params.put("type", "0"); // 标星
						} else {
							params.put("type", "1"); // 取消标星
						}

						url = UConfig.QUESTION_STAR_URL;
						
						MobclickAgent.onEvent(getContext(), UMengEventID.QUESTION_CARD_STARRED);
					} else {
						if (question.status == QuestionStatusType.UNRESOLVED) {
							params.put("type", "1");
						} else {
							params.put("type", "0");
						}

						url = UConfig.QUESTION_CLOSE_URL;
						
						MobclickAgent.onEvent(getContext(), UMengEventID.QUESTION_CARD_SOLVED);
					}

					mDataLoader.postData(url, params, getContext(),
							new HDataListener() {

								@Override
								public void onSocketTimeoutException(String msg) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onFinish(String source) {
									// TODO Auto-generated method stub

									if (!currentUser.equals(question.user)) {
										if (QuestionCard.this.question.metaData.viewerStarred == 1) {
											QuestionCard.this.question.metaData.viewerStarred = 0;
										} else {
											QuestionCard.this.question.metaData.viewerStarred = 1;
										}
									} else {
										if (QuestionCard.this.question.status == QuestionStatusType.UNRESOLVED) {
											QuestionCard.this.question.status = QuestionStatusType.RESOLVED;
										} else {
											QuestionCard.this.question.status = QuestionStatusType.UNRESOLVED;
										}
									}

									refreshUiState();
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
		});

		// if localUser is Question Owner, then goodButton gone
		if (!this.currentUser.equals(this.question.user)) {
			this.goodButton.setVisibility(View.VISIBLE);
			this.goodButton.setOnCheckedChangeListener(null);
			this.goodButton
					.setChecked(this.question.metaData.viewerGooded == 1);

			this.goodButton
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							// ask for good or cancel good
							
							HashMap<String,String> map = new HashMap<String,String>();
							map.put("type",isChecked + "");
							MobclickAgent.onEvent(getContext(), UMengEventID.QUESTION_CARD_LIKE, map);
							
							Map<String, String> params = new HashMap<String, String>();
							params.put("qid", QuestionCard.this.question.qid
									+ "");

							if (isChecked)
								params.put("type", "1"); // 赞
							else
								params.put("type", "0"); // 取消
							mDataLoader.postData(UConfig.QUESTION_GOOD_URL,
									params, getContext(), null);
						}
					});

		} else {
			this.goodButton.setVisibility(View.GONE);
		}
		this.goodButton.setTextOff(getResources().getString(
				R.string.good_button_text));

		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = getResources().getString(R.string.good_button_text);

		Spannable goodTextOn = new SpannableString(String.format("   %s",
				arrayOfObject));

		Drawable goodTextOnDrawable = getResources().getDrawable(
				R.drawable.action_check);

		goodTextOnDrawable.setBounds(0, 0, this.checkHeight, this.checkHeight);
		goodTextOn.setSpan(new ImageSpan(goodTextOnDrawable, 1), 0, 1, 33);
		this.goodButton.setTextOn(goodTextOn);
		this.goodButton.setChecked(this.goodButton.isChecked());
		refreshUiState();
	}

	public void showCanSwipeDownNux() {
		this.swipeDownNux = new LinearLayout(getContext());
		int i = getResources().getDimensionPixelOffset(
				R.dimen.swipe_down_nux_top_padding);
		this.swipeDownNux.setPadding(0, i, 0, 0);
		this.swipeDownNux.setBackgroundColor(getResources().getColor(
				R.color.swipe_down_nux_background_color));
		this.swipeDownNux.setOrientation(1);
		Object localObject = new ImageView(getContext());
		((ImageView) localObject).setImageResource(R.drawable.swipe_down_icon);
		((ImageView) localObject).setScaleType(ImageView.ScaleType.FIT_CENTER);
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				-1, -1, 1.0F);
		this.swipeDownNux.addView((View) localObject, localLayoutParams);
		localObject = new CustomTextView(getContext());
		FontManager.setTypeface((TextView) localObject,
				FontManager.Weight.MEDIUM);
		((CustomTextView) localObject).setTextSize(0, getResources()
				.getDimensionPixelSize(R.dimen.nux_text_size));
		((CustomTextView) localObject).setTextColor(getResources().getColor(
				R.color.nux_text_color));
		((CustomTextView) localObject).setGravity(17);
		((CustomTextView) localObject).setText(getResources().getString(
				R.string.swipe_down_nux_text));
		localLayoutParams = new LinearLayout.LayoutParams(-1, -1, 1.0F);
		this.swipeDownNux.addView((View) localObject, localLayoutParams);
		addView(this.swipeDownNux);
	}

	public String toString() {
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[1] = String.valueOf(this.question.qid);
		return String.format("for question id %s", arrayOfObject);
	}

	public static enum DisplayMode {
		OVERLAY, BASE
	}

	public class FollowDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			// common actions
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(
					getActivity(), AlertDialog.THEME_HOLO_LIGHT);
			localBuilder.setTitle(R.string.first_follow_dialog_title);
			localBuilder
					.setMessage(getText(R.string.first_follow_dialog_message));
			localBuilder.setNegativeButton(getText(R.string.i_know_tips),
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
