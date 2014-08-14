package com.medialab.jelly.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.view.CustomTextView;

public class NoNotificationsView extends ViewGroup {

	private boolean hasLoadedStuff = false;
	private final CustomTextView textView;

	public NoNotificationsView(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.textView = new CustomTextView(paramContext);
		this.textView.setGravity(17);
		FontManager.setTypeface(this.textView, FontManager.Weight.HUAKANG);
		updateText();
		addView(this.textView);
	}

	private void updateText() {
		SpannableString localSpannableString3;
		SpannableString localSpannableString2;
		if (this.hasLoadedStuff) {
			localSpannableString3 = new SpannableString(getResources()
					.getString(R.string.no_notifications_top_line));
			localSpannableString2 = new SpannableString(getResources()
					.getString(R.string.no_notifications_bottom_line));
		} else {
			localSpannableString3 = new SpannableString(getResources()
					.getString(R.string.unloaded_notifications_top_line));
			localSpannableString2 = new SpannableString(getResources()
					.getString(R.string.unloaded_notifications_bottom_line));
		}
		localSpannableString3.setSpan(new TextAppearanceSpan(null,
				android.graphics.Typeface.BOLD, 0, getResources()
						.getColorStateList(R.color.deep_sea_teal), null), 0,
				localSpannableString3.length(), Spanned.SPAN_MARK_MARK);
		SpannableString localSpannableString1 = new SpannableString("\n");
		localSpannableString2.setSpan(new TextAppearanceSpan(null,
				android.graphics.Typeface.BOLD_ITALIC, 0, getResources()
						.getColorStateList(R.color.classy_turquoise), null), 0,
				localSpannableString2.length(), 17);
		CustomTextView localCustomTextView = this.textView;
		CharSequence[] arrayOfCharSequence = new CharSequence[3];
		arrayOfCharSequence[0] = localSpannableString3;
		arrayOfCharSequence[1] = localSpannableString1;
		arrayOfCharSequence[2] = localSpannableString2;
		localCustomTextView.setText(TextUtils.concat(arrayOfCharSequence));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		this.textView.layout(0, 0, this.textView.getMeasuredWidth(),
				this.textView.getMeasuredHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		this.textView.measure(
				View.MeasureSpec.makeMeasureSpec(
						View.MeasureSpec.getSize(widthMeasureSpec),
						MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
						Math.max(getSuggestedMinimumHeight(),
								View.MeasureSpec.getSize(heightMeasureSpec)),
						MeasureSpec.EXACTLY));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setHasLoadedStuff(boolean paramBoolean) {
		this.hasLoadedStuff = paramBoolean;
		updateText();
	}

}
