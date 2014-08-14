package com.medialab.jelly.ui.view;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.ui.widget.SlideableView;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.LinkUtils;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.view.CustomTextView;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class AnswerCard extends ViewGroup implements SlideableView {

	private long answerId;
	private RoundedImageView answerImageView;
	private final CustomTextView answerText;
	private final Rect borderPadding;
	private final LinearLayout bottomRow;
	private final int bottomRowTouchExtension;
	private final int buttonRowHeight;
	private final int checkHeight;
	private Spring fadeSpring;

	private final ToggleButton goodOrThanksButton;
	private boolean hasImage = false;
	private boolean hasLink;
	private final int junkDrawerExpansion;
	private final int leftTextPadding;

	private final CustomTextView linkText;
	private final Button shareButton;
	private final SocialContextWidget socialContextWidget;
	private final SpringSystem springSystem;
	private final FrameLayout textAndSocialBackground;
	private final int textToSocialContextSpacing;
	private final int topTextPadding;

	private FinalBitmap fb;

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	public AnswerCard(Activity paramActivity, SpringSystem paramSpringSystem) {
		super(paramActivity);
		// TODO Auto-generated constructor stub
		Context localContext = getContext();
		Resources localResources = localContext.getResources();
		setClickable(true);
		setFocusable(true);

		fb = FinalBitmap.create(localContext);
		this.springSystem = paramSpringSystem;
		this.answerText = new CustomTextView(localContext);
		this.answerText.setLineSpacing(
				getResources().getDimension(R.dimen.extra_line_height), 1.0F);
		FontManager.setTypeface(this.answerText, FontManager.Weight.HUAKANG);
		this.answerText.setTextColor(localResources
				.getColor(R.color.answer_card_text_color));
		this.answerText.setTextIsSelectable(true);
		this.answerText.setTextSize(0, localResources
				.getDimension(R.dimen.question_card_main_text_size));
		this.answerText.setGravity(Gravity.CENTER_VERTICAL);

		this.linkText = new CustomTextView(localContext);
		FontManager.setTypeface(this.linkText, FontManager.Weight.MEDIUM);
		this.linkText.setTextColor(localResources
				.getColor(R.color.link_text_color));
		this.linkText.setLinkTextColor(localResources
				.getColor(R.color.link_text_color));
		this.linkText.setTextSize(0, localResources
				.getDimension(R.dimen.question_card_main_text_size));
		this.linkText.setGravity(Gravity.CENTER_VERTICAL);
		this.linkText.setEllipsize(TextUtils.TruncateAt.END);
		this.linkText.setSingleLine();
		this.linkText.setMovementMethod(LinkMovementMethod.getInstance());

		this.answerImageView = new RoundedImageView(localContext);
		this.answerImageView
				.setCornerRadius(R.dimen.card_top_background_radius);
		this.answerImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.textAndSocialBackground = new FrameLayout(localContext);
		this.socialContextWidget = new SocialContextWidget(paramActivity);

		this.bottomRow = ((LinearLayout) LinearLayout.class
				.cast(((LayoutInflater) paramActivity
						.getSystemService("layout_inflater")).inflate(
						R.layout.answer_button_row_layout, this, false)));

		this.goodOrThanksButton = ((ToggleButton) ToggleButton.class
				.cast(this.bottomRow.findViewById(R.id.good_button)));
		FontManager.setTypeface(this.goodOrThanksButton,
				FontManager.Weight.HUAKANG);

		this.shareButton = ((Button) Button.class.cast(this.bottomRow
				.findViewById(R.id.share_button)));
		FontManager.setTypeface(this.shareButton, FontManager.Weight.HUAKANG);

		this.bottomRowTouchExtension = getResources().getDimensionPixelSize(
				R.dimen.card_bottom_row_touch_extension);
		this.textToSocialContextSpacing = localResources
				.getDimensionPixelSize(R.dimen.text_to_social_context_spacing_vertical);
		this.leftTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_left_right_padding);
		this.topTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_top_padding);
		this.buttonRowHeight = localResources
				.getDimensionPixelSize(R.dimen.question_card_button_row_height);
		this.borderPadding = new Rect();
		this.junkDrawerExpansion = getResources().getDimensionPixelSize(
				R.dimen.junk_icon_touch_expansion);
		this.checkHeight = getResources().getDimensionPixelSize(
				R.dimen.question_card_button_text_size);

		addView(this.socialContextWidget);
		addView(this.answerText);
		addView(this.bottomRow);
		addView(this.linkText);
	}

	private void createFadeSpring() {
		if (this.fadeSpring == null)
			this.fadeSpring = this.springSystem.createSpring()
					.setCurrentValue(0.0D).setAtRest()
					.addListener(getFadeSpringListener());
	}

	private SpringListener getFadeSpringListener() {
		return new SimpleSpringListener() {
			public void onSpringEndStateChange(Spring paramSpring) {
				if (paramSpring.getEndValue() < 1.0D) {
					AnswerCard.this.linkText.setClickable(true);
					AnswerCard.this.socialContextWidget.setIsClickable(true);
				} else {
					AnswerCard.this.linkText.setClickable(false);
					AnswerCard.this.socialContextWidget.setIsClickable(false);
				}
			}

			public void onSpringUpdate(Spring paramSpring) {
				if (AnswerCard.this.hasImage) {
					float f = 1.0F - (float) paramSpring.getCurrentValue();
					AnswerCard.this.bottomRow.setAlpha(f);
					AnswerCard.this.linkText.setAlpha(f);
					AnswerCard.this.textAndSocialBackground.setAlpha(f);
					AnswerCard.this.socialContextWidget.setAlpha(f);
					AnswerCard.this.answerText.setAlpha(f);
				}
			}
		};
	}

	public boolean equals(Object paramObject) {
		boolean bool;
		if ((paramObject instanceof AnswerCard)) {
			if (paramObject != this)
				bool = (((AnswerCard) paramObject).answerId == this.answerId);
			else
				bool = true;
		} else
			bool = false;
		return bool;
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		createFadeSpring();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		this.fadeSpring.destroy();
		this.fadeSpring = null;
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int k = paramInt4 - paramInt2;
		int j = this.leftTextPadding + this.borderPadding.left;
		int i = j + this.socialContextWidget.getMeasuredWidth();
		int m = k - this.borderPadding.bottom;
		int i1 = m - this.buttonRowHeight;
		int n = 0 + this.bottomRow.getMeasuredWidth();
		this.bottomRow.layout(0, i1, n, m);
		if (this.hasImage) {
			k = this.textAndSocialBackground.getMeasuredHeight();
			this.textAndSocialBackground.layout(this.borderPadding.left,
					this.borderPadding.top, this.borderPadding.left
							+ this.textAndSocialBackground.getMeasuredWidth(),
					k);
			k = this.borderPadding.top;
			i1 = this.borderPadding.left;
			m = i1 + this.answerImageView.getMeasuredWidth();
			n = k + this.answerImageView.getMeasuredHeight();
			this.answerImageView.layout(i1, k, m, n);
			m = this.topTextPadding;
			k = m + this.answerText.getMeasuredHeight();
		} else {
			m = this.socialContextWidget.getMeasuredHeight()
					+ this.answerText.getMeasuredHeight();
			m = (k
					- (this.buttonRowHeight + this.borderPadding.bottom + this.borderPadding.top) - m)
					/ 2 + this.borderPadding.top;
			k = m + this.answerText.getMeasuredHeight();
		}
		if (!this.hasLink) {
			n = k;
		} else {
			i1 = k + this.textToSocialContextSpacing;
			n = i1 + this.linkText.getMeasuredHeight();
			this.linkText.layout(j, i1, i, n);
		}
		i1 = n + this.textToSocialContextSpacing;
		n = i1 + this.socialContextWidget.getMeasuredHeight();
		this.answerText.layout(j, m, i, k);
		this.socialContextWidget.layout(j, i1, i, n);
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int m = 0;
		// getBackground().getPadding(this.borderPadding);
		int k = View.MeasureSpec.getSize(paramInt1);
		int n = k - (this.borderPadding.left + this.borderPadding.right);
		int i1 = View.MeasureSpec.getSize(paramInt2)
				- (this.borderPadding.top + this.borderPadding.bottom);
		int j = i1 - this.buttonRowHeight;
		int i = View.MeasureSpec.makeMeasureSpec(n - 2 * this.leftTextPadding,
				MeasureSpec.EXACTLY);
		this.bottomRow.measure(View.MeasureSpec.makeMeasureSpec(k,
				MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
				this.buttonRowHeight, MeasureSpec.EXACTLY));
		if ((this.answerText.getText() == null)
				|| (this.answerText.getText().length() <= 0))
			this.answerText.measure(i,
					View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
		else
			this.answerText.measure(i,
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		this.socialContextWidget.measure(i,
				View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		if (this.hasLink)
			this.linkText.measure(i,
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		if (this.hasImage) {
			this.answerImageView.measure(
					View.MeasureSpec.makeMeasureSpec(n, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(i1, MeasureSpec.EXACTLY));
			n = this.answerText.getMeasuredHeight()
					+ this.socialContextWidget.getMeasuredHeight()
					+ this.textToSocialContextSpacing + 2 * this.topTextPadding;
			if (this.hasLink) {
				m = this.linkText.getMeasuredHeight()
						+ this.textToSocialContextSpacing;
			}
			m = n + m;
			this.textAndSocialBackground
					.measure(
							View.MeasureSpec
									.makeMeasureSpec(
											k
													- (this.borderPadding.left + this.borderPadding.right),
											MeasureSpec.EXACTLY),
							View.MeasureSpec.makeMeasureSpec(m,
									MeasureSpec.EXACTLY));
		}

		super.onMeasure(paramInt1, paramInt2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		float f1 = paramMotionEvent.getY();
		float f3 = paramMotionEvent.getX();
		boolean bool;
		if ((f1 >= this.socialContextWidget.getY()
				+ this.socialContextWidget.getMeasuredHeight()
				+ this.junkDrawerExpansion)
				|| (f1 <= this.socialContextWidget.getY()
						- this.junkDrawerExpansion)) {
			bool = super.onTouchEvent(paramMotionEvent);
		} else {
			float f2;
			if (f3 >= this.socialContextWidget.getX()) {
				if (f3 <= this.socialContextWidget.getX()
						+ this.socialContextWidget.getMeasuredWidth())
					f2 = f3;
				else
					f2 = -1 + this.socialContextWidget.getMeasuredWidth();
			} else
				f2 = 5.0F;
			paramMotionEvent.setLocation(f2, 1.0F);
			this.socialContextWidget.dispatchTouchEvent(paramMotionEvent);
			bool = true;
		}
		return bool;
	}

	public void setAnswer(Answer paramAnswer, Question paramQuestion,
			View.OnClickListener junkDrawerOnClickListener, int paramUserId,
			Bus paramBus, ExecutorService paramExecutorService) {

		createFadeSpring();

		this.fadeSpring.setCurrentValue(0.0D).setAtRest();
		boolean bool = this.hasImage;
		this.hasImage = paramAnswer.hasAnswerImage();

		if ((!bool) || (this.hasImage)) {
			if (this.hasImage)
				this.answerImageView
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View paramView) {
								int i;
								if (AnswerCard.this.fadeSpring.getEndValue() == 0.0D)
									i = 0;
								else
									i = 1;
								if (i == 0) {
									AnswerCard.this.fadeSpring
											.setEndValue(1.0D);
									AnswerCard.this.bottomRow
											.setClickable(false);
								} else {
									AnswerCard.this.fadeSpring
											.setEndValue(0.0D);
									AnswerCard.this.bottomRow
											.setClickable(true);
								}
							}
						});
		} else {
			this.textAndSocialBackground.setBackgroundResource(0);
			this.answerImageView.setImageBitmap(null);
			removeView(this.answerImageView);
			removeView(this.textAndSocialBackground);
		}

		if ((paramAnswer.link == null) || (paramAnswer.link.isEmpty()))
			bool = false;
		else
			bool = true;
		this.hasLink = bool;

		if (!this.hasLink) {
			this.linkText.setVisibility(View.GONE);
		} else {
			this.linkText.setClickable(true);
			this.linkText.setVisibility(View.VISIBLE);
			if (paramAnswer.getPregeneratedLinkText() == null)
				throw new RuntimeException(
						"Failed to generate answer link text with link "
								+ paramAnswer.link.toString());
			this.linkText.setText(LinkUtils
					.noUnderlineLinksFromString(paramAnswer
							.getPregeneratedLinkText()));
		}

		final boolean isQuestionOwner = paramQuestion.user.getUid() == (paramUserId);
		this.answerId = paramAnswer.aid;
		this.answerText.setText(paramAnswer.getPregeneratedText());
		this.socialContextWidget.loadAnswerSocialContext(paramAnswer.context,
				junkDrawerOnClickListener, paramAnswer.createdAt,
				paramAnswer.user, null, paramAnswer.metaData);

		if ((paramAnswer.user != null)
				&& (!(paramAnswer.user.getUid() == (paramUserId)))) {
			this.goodOrThanksButton.setVisibility(View.VISIBLE);
			this.goodOrThanksButton.setOnCheckedChangeListener(null);
			if (isQuestionOwner)
				this.goodOrThanksButton
						.setChecked(paramAnswer.metaData.viewerThanked == 1);
			else
				this.goodOrThanksButton
						.setChecked(paramAnswer.metaData.viewerGooded == 1);
			this.goodOrThanksButton
					.setOnCheckedChangeListener(new GoodOrThankedOnCheckedChangeListener(
							isQuestionOwner, paramAnswer));
		} else {
			this.goodOrThanksButton.setVisibility(View.GONE);
		}

		this.socialContextWidget.updateWidgetText(paramAnswer.createdAt,
				paramAnswer.metaData);
		Object localObject2;
		Object localObject1;

		if (!isQuestionOwner) {
			localObject2 = getResources().getString(R.string.good_button_text);
			this.goodOrThanksButton.setTextOff((CharSequence) localObject2);
			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = String.valueOf(localObject2);

			localObject2 = new SpannableString(String.format("   %s",
					arrayOfObject));
			localObject1 = getContext().getResources().getDrawable(
					R.drawable.action_check);
			((Drawable) localObject1).setBounds(0, 0, this.checkHeight,
					this.checkHeight);
			((Spannable) localObject2).setSpan(new ImageSpan(
					(Drawable) localObject1, 1), 0, 1, 33);
			this.goodOrThanksButton.setTextOn((CharSequence) localObject2);
			this.goodOrThanksButton.setChecked(this.goodOrThanksButton
					.isChecked());
		} else {
			localObject1 = getResources()
					.getString(R.string.thanks_button_text);
			this.goodOrThanksButton.setTextOff((CharSequence) localObject1);

			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = String.valueOf(localObject1);

			localObject1 = new SpannableString(String.format("   %s",
					arrayOfObject));
			localObject2 = getContext().getResources().getDrawable(
					R.drawable.action_check);
			((Drawable) localObject2).setBounds(0, 0, this.checkHeight,
					this.checkHeight);
			((Spannable) localObject1).setSpan(new ImageSpan(
					(Drawable) localObject2, 1), 0, 1, 33);
			this.goodOrThanksButton.setTextOn((CharSequence) localObject1);
			this.goodOrThanksButton.setChecked(this.goodOrThanksButton
					.isChecked());
		}

		this.shareButton.setOnClickListener(new ForwardAnswerOnClickListener(
				paramAnswer));

		if (this.hasImage) {
			setBackgroundResource(R.drawable.hollowed_card_overlay);
			this.textAndSocialBackground
					.setBackgroundResource(R.drawable.card_top_background);

			fb.display(this.answerImageView, UDisplayWidth.getPicUrlByWidth(
					UDisplayWidth.PIC_WIDTH_480,
					paramAnswer.answerImage.picName));
			if (this.answerImageView.getParent() == null)
				addView(this.answerImageView);
			if (this.textAndSocialBackground.getParent() == null)
				addView(this.textAndSocialBackground);
		} else {
			setBackgroundResource(R.drawable.answer_card_background_with_borders);
			if (this.answerImageView.getParent() == this)
				removeView(this.answerImageView);
			if (this.textAndSocialBackground.getParent() == this)
				removeView(this.textAndSocialBackground);
		}

		bringChildToFront(this.answerText);
		bringChildToFront(this.socialContextWidget);
		bringChildToFront(this.linkText);
		bringChildToFront(this.bottomRow);
		setSlidingAlpha(1.0F);
		invalidate();
	}

	class ForwardAnswerOnClickListener implements OnClickListener {

		Answer answer;

		public ForwardAnswerOnClickListener(Answer paramAnswer) {
			this.answer = paramAnswer;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			MobclickAgent.onEvent(getContext(), UMengEventID.ANSWER_CARD_FOR_FRIEND);
			
			Intent localIntent = new Intent();
			localIntent.setAction("android.intent.action.SEND");
			localIntent.putExtra("android.intent.extra.TEXT",
					answer.metaData.shareTextWithUrl);
			localIntent.setType("text/plain");
			getContext().startActivity(
					Intent.createChooser(localIntent,
							getContext().getText(R.string.share_button_text)));
		}

	}

	private class GoodOrThankedOnCheckedChangeListener implements
			OnCheckedChangeListener {

		boolean bool;
		Answer answer;

		public GoodOrThankedOnCheckedChangeListener(boolean mbool,
				Answer mAnswer) {
			this.bool = mbool;
			this.answer = mAnswer;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			String url;
			
			Map<String, String> params = new HashMap<String, String>();

			params.put("aid", AnswerCard.this.answerId + "");

			if (isChecked)
				params.put("type", "1"); // 赞
			else
				params.put("type", "0"); // 取消
			
			if (bool) {
				// 感谢接口
				url = UConfig.ANSWER_THANKYOU_URL;
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("type",isChecked + "");
				MobclickAgent.onEvent(getContext(), UMengEventID.ANSWER_CARD_THANKED, map);
				
			} else {
				// 赞接口
				url = UConfig.ANSWER_GOOD_URL;
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("type",isChecked + "");
				MobclickAgent.onEvent(getContext(), UMengEventID.ANSWER_CARD_LIKE, map);
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
							if (bool) {
								if (answer.metaData.viewerThanked == 1) {
									answer.metaData.viewerThanked = 0;
								} else {
									answer.metaData.viewerThanked = 1;
								}
							} else {
								if (answer.metaData.viewerGooded == 1) {
									answer.metaData.viewerGooded = 0;
									answer.metaData.numberOfGoods = answer.metaData.numberOfGoods - 1;
								} else {
									answer.metaData.viewerGooded = 1;
									answer.metaData.numberOfGoods = answer.metaData.numberOfGoods + 1;
								}
							}

							AnswerCard.this.socialContextWidget
									.updateWidgetText(answer.createdAt,
											answer.metaData);
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

	public String toString() {
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = this.answerId;
		return String.format("Answer card for answer id %s", arrayOfObject);
	}

	@Override
	public void setSlidingAlpha(float paramFloat) {
		// TODO Auto-generated method stub
		this.answerText.setAlpha(paramFloat);
		this.socialContextWidget.setAlpha(paramFloat);
	}

}
