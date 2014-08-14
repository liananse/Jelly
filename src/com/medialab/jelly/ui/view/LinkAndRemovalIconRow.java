package com.medialab.jelly.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.LinkUtils;
import com.medialab.jelly.util.view.CustomTextView;

public class LinkAndRemovalIconRow extends ViewGroup {

	private final ImageView linkRemoval;
	private final int linkRemovalIconSize;
	private final int linkRemovalToLinkSpacing;
	private final CustomTextView linkText;

	public LinkAndRemovalIconRow(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		Resources localResources = paramContext.getResources();

		this.linkRemovalIconSize = localResources
				.getDimensionPixelSize(R.dimen.link_removal_icon_size);
		this.linkRemovalToLinkSpacing = localResources
				.getDimensionPixelSize(R.dimen.link_removal_to_link);
		this.linkText = new CustomTextView(paramContext);

		FontManager.setTypeface(this.linkText, FontManager.Weight.HEAVY);
		this.linkText.setTextColor(localResources
				.getColor(R.color.link_text_color));
		this.linkText.setLinkTextColor(localResources
				.getColor(R.color.link_text_color));
		this.linkText.setMovementMethod(LinkMovementMethod.getInstance());
		this.linkText.setTextSize(0, localResources
				.getDimension(R.dimen.question_card_main_text_size));
		this.linkText.setGravity(16);
		this.linkText.setEllipsize(TextUtils.TruncateAt.END);
		this.linkText.setSingleLine();
		addView(this.linkText);
		this.linkRemoval = new ImageView(paramContext);
		this.linkRemoval.setImageResource(R.drawable.delete_link_icon);
		addView(this.linkRemoval);
	}

	public boolean currentlyHasLink() {
		boolean result;
		if ((this.linkText.getText() == null)
				|| (this.linkText.getText().length() <= 0))
			result = false;
		else
			result = true;
		return result;
	}

	public CharSequence getLinkText() {
		Object localObject;
		if (this.linkText.getText() != null)
			localObject = this.linkText.getText();
		else
			localObject = "";
		return (CharSequence) localObject;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int k = 0 + this.linkText.getMeasuredHeight();
		int j;
		if (this.linkRemoval.getVisibility() != 0)
			j = 0;
		else
			j = this.linkRemovalIconSize + this.linkRemovalToLinkSpacing;
		int i = j + this.linkText.getMeasuredWidth();
		this.linkText.layout(j, 0, i, k);
		if (this.linkRemoval.getVisibility() == 0) {
			j = 0 + (int) ((this.linkText.getMeasuredHeight() - this.linkRemovalIconSize) / 2.0F);
			i = this.linkRemovalIconSize;
			k = j + this.linkRemoval.getMeasuredHeight();
			this.linkRemoval.layout(0, j, i, k);
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		if (currentlyHasLink()) {
			int k = View.MeasureSpec.getSize(paramInt1);
			int j = View.MeasureSpec.getSize(paramInt2);
			if (this.linkRemoval.getVisibility() != 0) {
				this.linkText.measure(View.MeasureSpec.makeMeasureSpec(k,
						MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
						j, MeasureSpec.AT_MOST));
			} else {
				int i = this.linkRemovalIconSize
						+ this.linkRemovalToLinkSpacing;
				this.linkText.measure(View.MeasureSpec.makeMeasureSpec(k - i,
						MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
						j, MeasureSpec.AT_MOST));
				this.linkRemoval.measure(View.MeasureSpec.makeMeasureSpec(
						this.linkRemovalIconSize, MeasureSpec.EXACTLY),
						View.MeasureSpec.makeMeasureSpec(
								this.linkRemovalIconSize, MeasureSpec.EXACTLY));
			}
			super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(Math
					.max(this.linkRemoval.getMeasuredHeight(),
							this.linkText.getMeasuredHeight()),
					MeasureSpec.EXACTLY));
		} else {
			super.onMeasure(
					View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
		}
	}

	public void setLinkRemovalClickListener(
			View.OnClickListener paramOnClickListener) {
		if (paramOnClickListener == null)
			this.linkRemoval.setVisibility(View.GONE);
		else
			this.linkRemoval.setOnClickListener(paramOnClickListener);
	}

	public void setTextLink(Spannable paramSpannable) {
		if ((paramSpannable == null) || (paramSpannable.length() <= 0)) {
			this.linkText.setText("");
			setVisibility(View.GONE);
			this.linkText.setClickable(false);
		} else {
			setVisibility(View.VISIBLE);
			this.linkText.setText(LinkUtils
					.noUnderlineLinksFromString(paramSpannable));
			this.linkText.setClickable(true);
			requestLayout();
		}
	}

}
