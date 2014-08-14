package com.medialab.jelly.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.medialab.jelly.R;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.RemoteImage;
import com.medialab.jelly.ui.ComposingTextListener;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.KeyboardUtils;
import com.medialab.jelly.util.view.CustomTextView;
import com.medialab.jelly.util.view.NoScrollEditText;

public class TopOfCardTextAndIndicators extends ViewGroup {

	private int bottomTextPadding;
	private int itemSpacing;
	private int leftTextPadding;
	private int topTextPadding;

	private final LinkAndRemovalIconRow linkRow;
	private DrawLinkProgressBar progressRow;
	private TextView questionTextView;
	private SocialContextWidget socialContextWidget;
	private ComposingTextListener listener;

	public TopOfCardTextAndIndicators(Context localContext) {
		super(localContext);
		// TODO Auto-generated constructor stub
		Resources localResources = localContext.getResources();
		this.bottomTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_bottom_padding);
		this.itemSpacing = localResources
				.getDimensionPixelSize(R.dimen.text_to_social_context_spacing_vertical);
		this.leftTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_left_right_padding);
		this.linkRow = new LinkAndRemovalIconRow(localContext);
		this.topTextPadding = localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_top_padding);
		addView(this.linkRow);
	}

	public void focusAndShowKeyboard() {
		this.questionTextView.requestFocus();
		KeyboardUtils.showKeyboard(getContext(), this.questionTextView);
	}

	public CharSequence getLinkText() {
		return this.linkRow.getLinkText();
	}

	public CharSequence getQuestionText() {
		Object localObject = this.questionTextView.getText();
		if (localObject == null)
			localObject = "";
		return (CharSequence) localObject;
	}

	public int getTopOfSocialWidget() {
		return this.socialContextWidget.getTop();
	}

	public void init(boolean paramBoolean1, boolean paramBoolean2,
			ComposingTextListener paramComposingTextListener) {
		this.listener = paramComposingTextListener;
		Object localObject = getContext();
		Resources localResources = ((Context) localObject).getResources();
		setBackgroundResource(R.drawable.card_top_background);
		if (paramBoolean1)
			this.progressRow = new DrawLinkProgressBar((Context) localObject);
		if (!paramBoolean2) {
			this.linkRow.setLinkRemovalClickListener(null);
			this.socialContextWidget = new SocialContextWidget(
					(Context) localObject);
		} else {
			this.linkRow
					.setLinkRemovalClickListener(new View.OnClickListener() {
						public void onClick(View paramView) {
							if (TopOfCardTextAndIndicators.this.listener != null)
								TopOfCardTextAndIndicators.this.listener
										.hasLink(null);
						}
					});
		}
		if (!paramBoolean2) {
			this.questionTextView = new CustomTextView((Context) localObject);
			this.questionTextView.setTextIsSelectable(true);
		} else {
			this.questionTextView = new NoScrollEditText((Context) localObject);
			TextView localTextView = this.questionTextView;

			InputFilter[] filterObject = new InputFilter[1];
			filterObject[0] = new InputFilter.LengthFilter(140);
			localTextView.setFilters(filterObject);

			this.questionTextView.setInputType(49153);
			this.questionTextView
					.addTextChangedListener(new LinkAwareTextWatcher(
							new LinkAwareTextWatcher.HasLinkProvider() {
								public boolean currentlyHasLink() {
									return TopOfCardTextAndIndicators.this.linkRow
											.currentlyHasLink();
								}
							}, this.questionTextView,
							new LinkAwareTextWatcher.Listener() {
								public void hasText(boolean paramBoolean) {
									if (TopOfCardTextAndIndicators.this.listener != null)
										TopOfCardTextAndIndicators.this.listener
												.hasText(paramBoolean);
								}

								public void setLink(RemoteImage paramRemoteImage) {
									if (TopOfCardTextAndIndicators.this.listener != null)
										TopOfCardTextAndIndicators.this.listener
												.hasLink(paramRemoteImage.uri);
								}

								public void setProgress(int paramInt) {
									TopOfCardTextAndIndicators.this
											.setProgress((int) (100.0F * (paramInt / 140.0F)));
								}
							}));
		}
		this.questionTextView.setHorizontallyScrolling(false);
		this.questionTextView.setLineSpacing(
				getResources().getDimension(R.dimen.extra_line_height), 1.0F);
		this.questionTextView.setMaxLines(2147483647);
		FontManager.setTypeface(this.questionTextView,
				FontManager.Weight.HUAKANG);
		this.questionTextView.setBackgroundColor(0);
		this.questionTextView.setTextSize(0, localResources
				.getDimensionPixelSize(R.dimen.question_card_main_text_size));
		this.questionTextView.setTextColor(localResources
				.getColor(R.color.question_card_main_text_color));
		this.questionTextView.setGravity(51);
		this.questionTextView.setPadding(0, 0, 0, 0);
		if (this.progressRow != null)
			addView(this.progressRow);
		addView(this.questionTextView);
		if (this.socialContextWidget != null)
			addView(this.socialContextWidget);
	}

	public void loadQuestionSocialContext(String paramJellyContext,
			View.OnClickListener junkDrawerOnClickListener, long paramLong,
			JellyUser paramJellyUser, JellyUser paramMiddleJellyUser) {
		if (this.socialContextWidget != null)
			this.socialContextWidget.loadQuestionSocialContext(
					paramJellyContext, junkDrawerOnClickListener, paramLong,
					paramJellyUser, paramMiddleJellyUser);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int k = this.topTextPadding;
		int i = this.leftTextPadding;
		int m = k + this.questionTextView.getMeasuredHeight();
		int j = i + this.questionTextView.getMeasuredWidth();
		this.questionTextView.layout(i, k, j, m);
		k = m + this.itemSpacing;
		if (this.linkRow.getVisibility() == View.VISIBLE) {
			m = k + this.linkRow.getMeasuredHeight();
			this.linkRow.layout(i, k, i + this.linkRow.getMeasuredWidth(), m);
			k = m + this.itemSpacing;
		}
		if (this.socialContextWidget != null) {
			m = k;
			k = m + this.socialContextWidget.getMeasuredHeight();
			this.socialContextWidget.layout(i, m, j, k);
			k += this.itemSpacing;
		}
		if (this.progressRow != null) {
			m = k;
			k = m + this.progressRow.getMeasuredHeight();
			j = i + this.progressRow.getMeasuredWidth();
			this.progressRow.layout(i, m, j, k);
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int i = 0;
		int k = View.MeasureSpec.getSize(paramInt1) - 2 * this.leftTextPadding;
		int j = View.MeasureSpec.getSize(paramInt2)
				- (this.topTextPadding + this.bottomTextPadding);
		this.questionTextView.measure(
				View.MeasureSpec.makeMeasureSpec(k, MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		this.linkRow.measure(
				View.MeasureSpec.makeMeasureSpec(k, MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		if (this.socialContextWidget != null)
			this.socialContextWidget.measure(
					View.MeasureSpec.makeMeasureSpec(k, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		if (this.progressRow != null)
			this.progressRow.measure(
					View.MeasureSpec.makeMeasureSpec(k, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST));
		k = this.topTextPadding + this.questionTextView.getMeasuredHeight();
		if (this.linkRow.getVisibility() != 0)
			j = 0;
		else
			j = this.linkRow.getMeasuredHeight() + this.itemSpacing;
		j = k + j;
		if (this.socialContextWidget == null)
			k = 0;
		else
			k = this.socialContextWidget.getMeasuredHeight() + this.itemSpacing;
		j = k + j;
		if (this.progressRow != null)
			i = this.progressRow.getMeasuredHeight() + this.itemSpacing;
		super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(j + i
				+ this.bottomTextPadding, MeasureSpec.EXACTLY));
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		float f1 = paramMotionEvent.getY();
		boolean bool2;
		if (f1 >= this.questionTextView.getY()
				+ this.questionTextView.getMeasuredHeight()) {
			if ((this.socialContextWidget == null)
					|| (f1 <= this.socialContextWidget.getY()
							+ this.socialContextWidget.getMeasuredHeight())) {
				if (this.progressRow == null) {
					// boolean bool1 = super.onTouchEvent(paramMotionEvent);
					bool2 = super.onTouchEvent(paramMotionEvent);
				} else {
					float f2 = paramMotionEvent.getX()
							- this.progressRow.getX();
					float f3 = Math.min(
							-1 + this.progressRow.getMeasuredHeight(),
							this.progressRow.getY() - paramMotionEvent.getY());
					if (f2 >= 0.0F) {
						if (f2 > this.progressRow.getMeasuredWidth())
							paramMotionEvent.setLocation(
									-1 + this.progressRow.getMeasuredWidth(),
									f3);
					} else
						paramMotionEvent.setLocation(1.0F, f3);
					bool2 = this.progressRow
							.dispatchTouchEvent(paramMotionEvent);
				}
			} else {
				paramMotionEvent.setLocation(paramMotionEvent.getX(), -1
						+ this.socialContextWidget.getMeasuredHeight());
				this.socialContextWidget.dispatchTouchEvent(paramMotionEvent);
				bool2 = true;
			}
		} else
			bool2 = super.onTouchEvent(paramMotionEvent);
		return bool2;
	}

	public void setClickable(boolean paramBoolean) {
		super.setClickable(paramBoolean);
		if (this.socialContextWidget != null)
			this.socialContextWidget.setIsClickable(paramBoolean);
	}

	public void setDrawAndLinkOnClickListener(
			View.OnClickListener drawOnClickListener,
			View.OnClickListener linkOnClickListener) {
		if (this.progressRow != null)
			this.progressRow.setDrawAndLinkOnClickListener(
					drawOnClickListener, linkOnClickListener);
	}

	public void setProgress(int paramInt) {
		if (this.progressRow != null)
			this.progressRow.setProgress(paramInt);
	}

	public void setQuestionText(Spannable paramSpannable) {
		this.questionTextView.setText(paramSpannable);
		if (((this.questionTextView instanceof EditText))
				&& (this.questionTextView.getText() != null))
			((EditText) this.questionTextView)
					.setSelection(this.questionTextView.getText().length());
	}

	public void setTextLink(Spannable paramSpannable) {
		this.linkRow.setTextLink(paramSpannable);
	}

	public void unfocusAndDismissKeyboard() {
		this.questionTextView.clearFocus();
		KeyboardUtils.hideKeyboard(getContext(), this.questionTextView);
	}

}
