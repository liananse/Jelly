package com.medialab.jelly.ui.view;

import android.content.Context;
import android.net.Uri;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.medialab.jelly.R;
import com.medialab.jelly.model.Question;
import com.medialab.jelly.model.RemoteImage;
import com.medialab.jelly.ui.ComposingTextListener;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.KeyboardUtils;

public class AnswerTextCompositionView extends ViewGroup {

	private final int itemSpacing;
	private final LinkAndRemovalIconRow linkRow;
	private boolean needsToBringKeyboardUp = false;
	private final DrawLinkProgressBar progressRow;
	private Question question;
	private ComposingTextListener textListener;
	private final EditText textView;

	public AnswerTextCompositionView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setBackgroundResource(R.drawable.answer_card_background_with_borders);
		this.itemSpacing = getResources().getDimensionPixelOffset(
				R.dimen.text_to_social_context_spacing_vertical);
		setPadding(
				getResources().getDimensionPixelSize(
						R.dimen.question_card_main_text_left_right_padding),
				getResources().getDimensionPixelSize(
						R.dimen.question_card_main_text_top_padding),
				getResources().getDimensionPixelSize(
						R.dimen.question_card_main_text_left_right_padding),
				getResources().getDimensionPixelSize(
						R.dimen.question_card_main_text_bottom_padding));
		this.linkRow = new LinkAndRemovalIconRow(getContext());
		this.linkRow.setLinkRemovalClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				if (AnswerTextCompositionView.this.textListener != null)
					AnswerTextCompositionView.this.textListener.hasLink(null);
			}
		});
		this.progressRow = new DrawLinkProgressBar(getContext());

		this.textView = new EditText(getContext());
		FontManager.setTypeface(this.textView, FontManager.Weight.MEDIUM);
		this.textView.setGravity(48);
		this.textView.setInputType(49153);
		this.textView.setImeOptions(6);
		this.textView.setTextColor(getResources().getColor(
				R.color.question_card_main_text_color));
		this.textView.setBackgroundColor(getResources().getColor(17170445));
		this.textView.setTextSize(
				0,
				getResources().getDimension(
						R.dimen.question_card_main_text_size));
		this.textView.setHorizontallyScrolling(false);
		this.textView.setMaxLines(2147483647);
		this.textView.setPadding(0, 0, 0, 0);
		EditText localEditText = this.textView;
		InputFilter[] arrayOfInputFilter = new InputFilter[1];
		arrayOfInputFilter[0] = new InputFilter.LengthFilter(240);
		localEditText.setFilters(arrayOfInputFilter);
		this.textView.addTextChangedListener(new LinkAwareTextWatcher(
				new LinkAwareTextWatcher.HasLinkProvider() {
					public boolean currentlyHasLink() {
						return AnswerTextCompositionView.this.linkRow
								.currentlyHasLink();
					}
				}, this.textView, new LinkAwareTextWatcher.Listener() {
					public void hasText(boolean paramBoolean) {
						if (AnswerTextCompositionView.this.textListener != null)
							AnswerTextCompositionView.this.textListener
									.hasText(paramBoolean);
					}

					public void setLink(RemoteImage paramRemoteImage) {
						if (AnswerTextCompositionView.this.textListener != null)
							AnswerTextCompositionView.this.textListener
									.hasLink(paramRemoteImage.uri);
					}

					public void setProgress(int paramInt) {
						AnswerTextCompositionView.this.progressRow
								.setProgress((int) (100.0F * (paramInt / 240.0F)));
					}
				}));
		setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
			}
		});

		addView(this.textView);
		addView(this.linkRow);
		addView(this.progressRow);
	}

	public String getLinkText() {
		return this.linkRow.getLinkText().toString();
	}

	public Question getQuestion() {
		return this.question;
	}

	public CharSequence getText() {
		Object localObject = this.textView.getText();
		if (localObject == null)
			localObject = "";
		return (CharSequence) localObject;
	}

	public void hideKeyboard() {
		this.textView.clearFocus();
		KeyboardUtils.hideKeyboard(getContext(), this.textView);
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int m = paramInt4 - paramInt2 - getPaddingBottom();
		int j = m - this.progressRow.getMeasuredHeight();
		int k = getPaddingLeft();
		int i = k + this.progressRow.getMeasuredWidth();
		this.progressRow.layout(k, j, i, m);
		if (this.linkRow.getVisibility() == 0) {
			k = j - this.itemSpacing;
			i = k - this.linkRow.getMeasuredHeight();
			m = getPaddingLeft();
			j = m + this.linkRow.getMeasuredWidth();
			this.linkRow.layout(m, i, j, k);
		}
		j = getPaddingTop();
		k = j + this.textView.getMeasuredHeight();
		i = getPaddingLeft();
		m = i + this.textView.getMeasuredWidth();
		this.textView.layout(i, j, m, k);
		if (this.needsToBringKeyboardUp) {
			this.textView.requestFocus();
			KeyboardUtils.showKeyboard(getContext(), this.textView);
			this.needsToBringKeyboardUp = false;
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int j = View.MeasureSpec.getSize(paramInt1);
		int i = View.MeasureSpec.getSize(paramInt2);
		j -= getPaddingLeft() + getPaddingRight();
		this.progressRow.measure(
				View.MeasureSpec.makeMeasureSpec(j, 1073741824),
				View.MeasureSpec.makeMeasureSpec(i, -2147483648));
		this.linkRow.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824),
				View.MeasureSpec.makeMeasureSpec(i, -2147483648));
		int k = getPaddingBottom() + getPaddingTop() + this.itemSpacing
				+ this.progressRow.getMeasuredHeight() + this.itemSpacing;
		int m;
		if (this.linkRow.getVisibility() != 0)
			m = 0;
		else
			m = this.linkRow.getMeasuredHeight() + this.itemSpacing;
		k += m;
		this.textView.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824),
				View.MeasureSpec.makeMeasureSpec(i - k, -2147483648));
		super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(
				Math.max(getSuggestedMinimumHeight(),
						k + this.textView.getMeasuredHeight()), 1073741824));
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		float f1 = paramMotionEvent.getY();
		boolean bool2;
		if (f1 >= this.textView.getY() + this.textView.getMeasuredHeight()) {
			if (f1 <= -5 + this.progressRow.getTop()) {
				bool2 = super.onTouchEvent(paramMotionEvent);
			} else {
				float f2 = paramMotionEvent.getX() - this.progressRow.getX();
				float f3 = Math.min(-1 + this.progressRow.getMeasuredHeight(),
						this.progressRow.getY() - paramMotionEvent.getY());
				if (f2 >= 0.0F) {
					if (f2 > this.progressRow.getMeasuredWidth())
						paramMotionEvent.setLocation(
								-1 + this.progressRow.getMeasuredWidth(), f3);
				} else
					paramMotionEvent.setLocation(1.0F, f3);
				bool2 = this.progressRow.dispatchTouchEvent(paramMotionEvent);
			}
		} else
			bool2 = super.onTouchEvent(paramMotionEvent);
		return bool2;
	}

	public void reset() {
		if (this.textView.hasFocus())
			this.textView.clearFocus();
	}

	public void setLinkText(Uri paramUri) {
		LinkAndRemovalIconRow localLinkAndRemovalIconRow = this.linkRow;
		SpannableString localSpannableString;
		if (paramUri != null)
			localSpannableString = new SpannableString(paramUri.toString());
		else
			localSpannableString = null;
		localLinkAndRemovalIconRow.setTextLink(localSpannableString);
	}

	public void setQuestion(Question paramQuestion,
			View.OnClickListener linkOnClickListener,
			View.OnClickListener drawOnClickListener,
			ComposingTextListener paramComposingTextListener) {
		this.textView.setText("");
		this.textListener = paramComposingTextListener;
		setLinkText(null);
		this.question = paramQuestion;
		this.progressRow.setDrawAndLinkOnClickListener(drawOnClickListener,
				linkOnClickListener);
	}

	public void setText(CharSequence paramCharSequence) {
		if (paramCharSequence == null)
			paramCharSequence = "";
		this.textView.setText(paramCharSequence);
		if (this.textView.getEditableText() != null)
			Selection.setSelection(this.textView.getEditableText(),
					paramCharSequence.length());
	}

	public void showKeyboardAndFocus() {
		if (getMeasuredWidth() <= 0) {
			this.needsToBringKeyboardUp = true;
		} else {
			this.textView.requestFocus();
			postDelayed(new Runnable() {
				public void run() {
					KeyboardUtils.showKeyboard(getContext(), AnswerTextCompositionView.this.textView);
				}
			}, 100L);
			this.needsToBringKeyboardUp = false;
		}
	}

}
