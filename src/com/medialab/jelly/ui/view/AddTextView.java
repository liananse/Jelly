package com.medialab.jelly.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.ui.ComposingTextListener;
import com.medialab.jelly.util.KeyboardUtils;
import com.medialab.jelly.util.RoundedImageView;

public class AddTextView extends ViewGroup {

	private ComposingImage composingImage;
	private final RoundedImageView mainImageView;
	private boolean needsToBringKeyboardUp;
	private ComposingTextListener textListener;
	private final TopOfCardTextAndIndicators topSection;

	public AddTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Context localContext = getContext();
		setBackgroundResource(R.drawable.hollowed_card_overlay);

		this.mainImageView = new RoundedImageView(localContext);
		this.mainImageView.setCornerRadius(R.dimen.card_top_background_radius);
		this.mainImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		this.topSection = new TopOfCardTextAndIndicators(context);
		this.topSection.init(true, true, new ComposingTextListener() {
			public void hasLink(Uri paramUri) {
				if (AddTextView.this.textListener != null)
					AddTextView.this.textListener.hasLink(paramUri);
			}

			public void hasText(boolean paramBoolean) {
				if (AddTextView.this.textListener != null)
					AddTextView.this.textListener.hasText(paramBoolean);
			}
		});

		addView(this.mainImageView);
		addView(this.topSection);
	}

	public String getLinkText() {
		Object localObject = this.topSection.getLinkText();
		if (localObject != null)
			localObject = ((CharSequence) localObject).toString();
		else
			localObject = "";
		return (String) localObject;
	}

	public String getText() {
		return this.topSection.getQuestionText().toString();
	}

	public void hideKeyboard() {
		this.topSection.unfocusAndDismissKeyboard();
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (this.composingImage != null) {
			int j = this.mainImageView.getMeasuredHeight();
			int i = this.mainImageView.getMeasuredWidth();
			this.mainImageView.layout(0, 0, i, j);
			j = this.topSection.getMeasuredHeight();
			this.topSection.layout(0, 0, i, j);
			if (this.needsToBringKeyboardUp) {
				this.topSection.focusAndShowKeyboard();
				this.needsToBringKeyboardUp = false;
			}
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		if (this.composingImage != null) {
			int i = View.MeasureSpec.getSize(paramInt1);
			int j = View.MeasureSpec.getSize(paramInt2);
			int k = View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY);
			i = View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY);
			j = View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.AT_MOST);
			this.mainImageView.measure(k, i);
			this.topSection.measure(k, j);
		}
		super.onMeasure(paramInt1, paramInt2);
	}

	public ComposingImage getImage() {
		return this.composingImage;
	}

	public void setImageDrawAndLinkListeners(
			ComposingImage paramComposingImage,
			View.OnClickListener drawOnClickListener,
			View.OnClickListener linkOnClickListener,
			ComposingTextListener paramComposingTextListener) {
		this.composingImage = paramComposingImage;
		this.textListener = paramComposingTextListener;
		RoundedImageView localRoundedImageView = this.mainImageView;
		Bitmap localBitmap;
		if (paramComposingImage != null)
			localBitmap = paramComposingImage.bitmap;
		else
			localBitmap = null;
		localRoundedImageView.setImageBitmap(localBitmap);
		this.topSection.setDrawAndLinkOnClickListener(drawOnClickListener,
				linkOnClickListener);
	}

	public void setLink(Spannable paramSpannable) {
		this.topSection.setTextLink(paramSpannable);
	}

	public void setText(Spannable paramSpannable) {
		this.topSection.setQuestionText(paramSpannable);
	}

	public void showKeyboardAndFocus() {
		if (getMeasuredWidth() <= 0) {
			this.needsToBringKeyboardUp = true;
		} else {
			this.topSection.focusAndShowKeyboard();
			this.needsToBringKeyboardUp = false;
		}
	}

}
