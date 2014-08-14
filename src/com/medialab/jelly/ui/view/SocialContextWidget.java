package com.medialab.jelly.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.model.AnswerMetaData;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.ui.SocialContextUtils;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.JellyTextUtils;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.view.CustomTextView;

public class SocialContextWidget extends ViewGroup {

	private final int iconToTextSpacing;
	private final int junkDrawerExpansion;
	private final int junkDrawerHeight;
	private final int junkDrawerWidth;
	private final int pixelsBetweenSocialIcons;
	private final int socialConnectionHeight;
	private final int socialHeight;
	private final ImageView junkDrawerIcon;
	private final RoundedImageView social1;
	private final ImageView social2;
	private final RoundedImageView social3;
	private final CustomTextView socialText1;
	private final CustomTextView socialText2;

	private final CustomTextView thankedText;
	private final SocialContextUtils socialContextUtils;
	private boolean hasThanked;

	public SocialContextWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Context localContext = getContext();
		Resources localResources = localContext.getResources();
		this.socialContextUtils = new SocialContextUtils(getContext());
		this.iconToTextSpacing = getResources().getDimensionPixelSize(
				R.dimen.social_context_spacing_between_icon_and_text);
		this.junkDrawerExpansion = getResources().getDimensionPixelSize(
				R.dimen.junk_icon_touch_expansion);
		this.junkDrawerHeight = getResources().getDimensionPixelSize(
				R.dimen.social_context_junk_drawer_height);
		this.junkDrawerWidth = getResources().getDimensionPixelSize(
				R.dimen.social_context_junk_drawer_width);
		this.pixelsBetweenSocialIcons = getResources().getDimensionPixelSize(
				R.dimen.social_context_icon_horizontal_spacing);
		this.socialHeight = getResources().getDimensionPixelSize(
				R.dimen.social_context_image_height);
		this.socialConnectionHeight = getResources().getDimensionPixelSize(
				R.dimen.social_connection_height);

		this.junkDrawerIcon = new ImageView(localContext);
		this.junkDrawerIcon.setImageResource(R.drawable.junk_drawer_icon);
		this.junkDrawerIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

		this.social1 = new RoundedImageView(localContext);
		this.social1.setOval(true);
		this.social1.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.social1.setBackgroundResource(R.drawable.circular_photo_mask);
		this.social1.setVisibility(View.GONE);

		this.social2 = new ImageView(localContext);
		this.social2.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.social2.setVisibility(View.GONE);

		this.social3 = new RoundedImageView(localContext);
		this.social3.setOval(true);
		this.social3.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.social3.setBackgroundResource(R.drawable.circular_photo_mask);
		this.social3.setVisibility(View.GONE);

		this.socialText1 = new CustomTextView(localContext);
		this.socialText1.setGravity(Gravity.CENTER_VERTICAL);
		this.socialText1.setTextColor(localResources
				.getColor(R.color.social_context_time_color));
		this.socialText1
				.setTextSize(
						0,
						localResources
								.getDimensionPixelSize(R.dimen.social_context_line_1_text_height));
		this.socialText1.setPadding(0, 4, 0, 0);
		FontManager.setTypeface(this.socialText1, FontManager.Weight.HUAKANG);
		this.socialText2 = new CustomTextView(localContext);
		this.socialText2.setGravity(Gravity.CENTER_VERTICAL);
		this.socialText2
				.setTextSize(
						0,
						localResources
								.getDimensionPixelSize(R.dimen.social_context_line_2_text_height));
		this.socialText2.setTextColor(localResources
				.getColor(R.color.social_context_connection_line_color));
		this.socialText2.setPadding(0, 8, 0, 0);
		FontManager.setTypeface(this.socialText2, FontManager.Weight.HUAKANG);

		// 是否已经被感谢了
		this.thankedText = new CustomTextView(localContext);
		this.thankedText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		this.thankedText.setBackgroundResource(R.drawable.mark_thanks);
		this.thankedText.setTextColor(localResources
				.getColor(R.color.thanked_text_color));
		this.thankedText
				.setTextSize(
						0,
						localResources
								.getDimensionPixelSize(R.dimen.social_context_line_1_text_height));
		this.thankedText.setText(localResources
				.getString(R.string.answer_thanked_text_hasThanked));
		this.thankedText.setPadding(0, 0, 8, 0);
		FontManager.setTypeface(thankedText, FontManager.Weight.HUAKANG);

		addView(this.socialText1);
		addView(this.socialText2);
		addView(this.thankedText);
		addView(this.social1);
		addView(this.social2);
		addView(this.social3);
		addView(this.junkDrawerIcon);

	}

	private void loadSocialContext(String paramContext,
			View.OnClickListener junkDrawerOnClickListener, long paramLong,
			JellyUser paramJellyUser, JellyUser paramMiddleJellyUser,
			AnswerMetaData paramAnswerMetaData) {
		this.socialText1.setText(paramContext);
		updateWidgetText(paramLong, paramAnswerMetaData);
		this.socialContextUtils.loadContextIntoImages(SocialContextUtils.SocialVariants.DARK, paramJellyUser,
				paramMiddleJellyUser, this.social1, this.social2, this.social3,
				true);

		this.junkDrawerIcon.setOnClickListener(junkDrawerOnClickListener);
	}

	public void loadAnswerSocialContext(String paramJellyContext,
			View.OnClickListener junkDrawerOnClickListener, long paramLong,
			JellyUser paramJellyUser, JellyUser paramMiddleJellyUser,
			AnswerMetaData paramAnswerMetaData) {
		loadSocialContext(paramJellyContext, junkDrawerOnClickListener,
				paramLong, paramJellyUser, paramMiddleJellyUser,
				paramAnswerMetaData);
	}

	public void loadQuestionSocialContext(String paramJellyContext,
			View.OnClickListener junkDrawerOnClickListener, long paramLong,
			JellyUser paramJellyUser, JellyUser paramMiddleJellyUser) {
		loadSocialContext(paramJellyContext, junkDrawerOnClickListener,
				paramLong, paramJellyUser, paramMiddleJellyUser, null);
	}

	public void updateWidgetText(long paramLong,
			AnswerMetaData paramAnswerMetaData) {
		StringBuilder localStringBuilder = new StringBuilder();
		if ((paramAnswerMetaData != null)
				&& (paramAnswerMetaData.numberOfGoods > 0)
				&& (getResources() != null)) {
			Resources localResources = getResources();
			int j = R.plurals.number_of_goods_string;
			int i = paramAnswerMetaData.numberOfGoods;
			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = Integer
					.valueOf(paramAnswerMetaData.numberOfGoods);
			localStringBuilder.append(localResources.getQuantityString(j, i,
					arrayOfObject));
			localStringBuilder.append(" – ");

			this.socialText2
					.setText(Html.fromHtml("<font color=\"#0095ae\">"
							+ localResources.getQuantityString(j, i,
									arrayOfObject)
							+ " - "
							+ "</font>"
							+ JellyTextUtils.computeHowLongAgo(getContext(),
									paramLong)));
		} else {
			localStringBuilder.append(JellyTextUtils.computeHowLongAgo(
					getContext(), paramLong));
			this.socialText2.setText(localStringBuilder.toString());
		}

		// 表示已经被赞过了
		if (paramAnswerMetaData != null
				&& paramAnswerMetaData.viewerThanked == 1) {
			hasThanked = true;
			this.thankedText.setVisibility(View.VISIBLE);
		} else {
			hasThanked = false;
			this.thankedText.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int i = paramInt3 - paramInt1;
		int k = 0;
		int j = 0 + this.social1.getMeasuredHeight();
		if (this.social1.getVisibility() == 0) {
			this.social1.layout(0, 0, 0 + this.social1.getMeasuredWidth(), j);
			k = 0 + this.social1.getMeasuredWidth();
		}
		if (this.social2.getVisibility() == 0) {
			int m = k + this.pixelsBetweenSocialIcons;
			int n = this.social2.getMeasuredHeight();
			k = 0 + (this.social1.getMeasuredHeight() - n) / 2;
			this.social2.layout(m, k, m + this.social2.getMeasuredWidth(), k
					+ n);
			k = m + this.social2.getMeasuredWidth();
		}
		if (this.social3.getVisibility() == 0) {
			k += this.pixelsBetweenSocialIcons;
			this.social3.layout(k, 0, k + this.social3.getMeasuredWidth(), j);
			k += this.social3.getMeasuredWidth();
		}
		k += this.iconToTextSpacing;
		int n = k + this.socialText1.getMeasuredWidth();
		j = 0 + this.socialText1.getMeasuredHeight();
		this.socialText1.layout(k, 0, n, j);
		int m = j + this.socialText2.getMeasuredHeight();
		this.socialText2.layout(k, j, n, m);
		if (hasThanked) {
			this.thankedText.layout(
					k,
					m + this.pixelsBetweenSocialIcons,
					k + this.thankedText.getMeasuredWidth(),
					m + this.pixelsBetweenSocialIcons
							+ this.thankedText.getMeasuredHeight());
		}
		k = i - this.junkDrawerIcon.getMeasuredWidth();
		m = this.socialText2.getTop() + this.socialText2.getBaseline();
		j = m - this.junkDrawerIcon.getMeasuredHeight();
		this.junkDrawerIcon.layout(k, j, i, m);
	}

	@Override
	protected void onMeasure(int paramInt1, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int i = View.MeasureSpec.getSize(paramInt1);
		int j = View.MeasureSpec.makeMeasureSpec(this.socialHeight,
				MeasureSpec.EXACTLY);
		this.social1.measure(j, j);
		int k = View.MeasureSpec.makeMeasureSpec(this.socialConnectionHeight,
				MeasureSpec.EXACTLY);
		this.social2.measure(k, k);
		this.social3.measure(j, j);
		if (this.social1.getVisibility() != View.VISIBLE)
			k = 0;
		else
			k = 1;
		int m;
		if (this.social2.getVisibility() != View.VISIBLE)
			m = 0;
		else
			m = 1;
		if (this.social3.getVisibility() != View.VISIBLE)
			j = 0;
		else
			j = 1;
		if (k == 0)
			k = 0;
		else
			k = this.socialHeight;
		if (m == 0)
			m = 0;
		else
			m = this.socialConnectionHeight + this.pixelsBetweenSocialIcons;
		k += m;
		if (j == 0)
			j = 0;
		else
			j = this.socialHeight + this.pixelsBetweenSocialIcons;
		j = i - (j + k + this.iconToTextSpacing) - this.junkDrawerWidth;
		this.socialText1.measure(View.MeasureSpec.makeMeasureSpec(j,
				MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
				this.socialHeight, MeasureSpec.AT_MOST));
		this.socialText2.measure(View.MeasureSpec.makeMeasureSpec(j,
				MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
				this.socialHeight, MeasureSpec.AT_MOST));
		this.thankedText.measure(View.MeasureSpec.makeMeasureSpec(j,
				MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(
				this.socialHeight, MeasureSpec.AT_MOST));
		this.junkDrawerIcon.measure(View.MeasureSpec.makeMeasureSpec(
				this.junkDrawerWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.junkDrawerHeight, MeasureSpec.EXACTLY));

		if (hasThanked) {
			setMeasuredDimension(
					i,
					Math.max(this.socialHeight,
							this.socialText1.getMeasuredHeight()
									+ this.socialText2.getMeasuredHeight()
									+ this.thankedText.getMeasuredHeight()
									+ this.pixelsBetweenSocialIcons));
		} else {
			setMeasuredDimension(
					i,
					Math.max(this.socialHeight,
							this.socialText1.getMeasuredHeight()
									+ this.socialText2.getMeasuredHeight()));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		boolean bool;
		if (paramMotionEvent.getX() <= this.junkDrawerIcon.getX()
				- this.junkDrawerExpansion) {
			bool = super.onTouchEvent(paramMotionEvent);
		} else {
			paramMotionEvent.setLocation(1.0F, 1.0F);
			bool = this.junkDrawerIcon.dispatchTouchEvent(paramMotionEvent);
		}
		return bool;
	}

	public void setIsClickable(boolean paramBoolean) {
		this.junkDrawerIcon.setClickable(paramBoolean);
		setClickable(paramBoolean);
	}

}
