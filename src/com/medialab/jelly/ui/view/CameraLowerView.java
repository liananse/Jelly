package com.medialab.jelly.ui.view;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.medialab.jelly.R;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.NeedyPeople;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.view.CustomTextView;

public class CameraLowerView extends ViewGroup {

	private List<RoundedImageView> addedPeople = Lists.newArrayList();
	private final int desiredHeight;
	private final int leftRightPadding;
	private NeedyPeople needyPeople;
	private final int singleImageHeight;
	private final int singleImageSpacing;
	private final CustomTextView textView;

	private FinalBitmap fb;

	public CameraLowerView(Context paramContext) {
		super(paramContext);
		fb = FinalBitmap.create(paramContext);
		this.desiredHeight = getResources().getDimensionPixelSize(
				R.dimen.camera_lower_view_height);
		this.leftRightPadding = getResources().getDimensionPixelSize(
				R.dimen.camera_lower_view_left_padding);
		this.singleImageHeight = getResources().getDimensionPixelSize(
				R.dimen.camera_lower_view_image_height);
		this.singleImageSpacing = getResources().getDimensionPixelSize(
				R.dimen.camera_lower_view_image_spacing);
		this.textView = new CustomTextView(paramContext);
		FontManager.setTypeface(this.textView, FontManager.Weight.ROMAN);
		this.textView.setGravity(19);
		this.textView.setTextSize(
				0,
				getResources().getDimensionPixelSize(
						R.dimen.camera_lower_view_text_height));
		this.textView.setTextColor(getResources().getColor(
				R.color.camera_lower_view_text_color));
		setBackgroundResource(R.drawable.camera_lower_view_background);
		setAlpha(0.0F);
		addView(this.textView);
	}

	private void addPeoplePhotos(int paramInt) {
		int i = this.needyPeople.topNeedyUsers.size();
		for (int j = 0;; j++) {
			if ((j >= paramInt) || (j >= i))
				return;
			if (((JellyUser) this.needyPeople.topNeedyUsers.get(j))
					.getAvatarName() == null) {
				j++;
			} else {
				RoundedImageView localNetworkImageView = new RoundedImageView(
						getContext());
				localNetworkImageView.setOval(true);
				localNetworkImageView
						.setBackgroundResource(R.drawable.circular_photo_mask);
				fb.display(localNetworkImageView, UDisplayWidth
						.getPicUrlByWidth(UDisplayWidth.PIC_WIDTH_120,
								((JellyUser) this.needyPeople.topNeedyUsers
										.get(j)).getAvatarName()));
				addView(localNetworkImageView);
				this.addedPeople.add(localNetworkImageView);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int k = this.textView.getMeasuredWidth();
		int i = this.addedPeople.size();
		for (int j = 0;; j++) {
			if (j >= i) {
				j = paramInt4 - paramInt2;
				k = (paramInt3 - paramInt1 - k) / 2;
				int m = k + this.textView.getMeasuredWidth();
				this.textView.layout(k, 0, m, j);
				k = (j - this.singleImageHeight) / 2;
				j = k + this.singleImageHeight;
				int n = m;
				for (m = 0;; m++) {
					if (m >= i)
						return;
					int i1 = n + this.singleImageSpacing;
					n = i1
							+ ((RoundedImageView) this.addedPeople.get(m))
									.getMeasuredWidth();
					((RoundedImageView) this.addedPeople.get(m)).layout(i1, k,
							n, j);
				}
			}
			k += this.singleImageSpacing
					+ ((RoundedImageView) this.addedPeople.get(j))
							.getMeasuredWidth();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int j = View.MeasureSpec.getSize(widthMeasureSpec) - 2
				* this.leftRightPadding;
		int i = Math.min(View.MeasureSpec.getSize(heightMeasureSpec),
				this.desiredHeight);
		if (this.needyPeople != null) {
			this.textView.measure(
					View.MeasureSpec.makeMeasureSpec(j, -2147483648),
					View.MeasureSpec.makeMeasureSpec(i, 1073741824));
			if ((this.addedPeople.isEmpty())
					&& (this.needyPeople.topNeedyUsers != null)
					&& (!this.needyPeople.topNeedyUsers.isEmpty())) {
				j -= this.textView.getMeasuredWidth();
				addPeoplePhotos(Math.min(this.needyPeople.topNeedyUsers.size(),
						j / (this.singleImageSpacing + this.singleImageHeight)));
			}
			j = this.addedPeople.size();
		}
		for (int k = 0;; k++) {
			if (k >= j) {
				super.onMeasure(widthMeasureSpec,
						View.MeasureSpec.makeMeasureSpec(i, 1073741824));
				return;
			}
			((RoundedImageView) this.addedPeople.get(k)).measure(
					View.MeasureSpec.makeMeasureSpec(this.singleImageHeight,
							1073741824), View.MeasureSpec.makeMeasureSpec(
							this.singleImageHeight, 1073741824));
		}
	}

	public void setNeedyPeople(NeedyPeople paramNeedyPeople) {
		this.needyPeople = paramNeedyPeople;
		if (this.needyPeople != null) {
			setAlpha(1.0F);
			if (!this.needyPeople.topNeedyUsers.isEmpty()) {
				Object localObject2;
				Object localObject3;
				Object localObject1;
				if (this.needyPeople.topNeedyUsers.size() != 1) {
					localObject2 = this.textView;
					localObject3 = getResources().getString(
							R.string.needy_people_multiple_string);
					Object[] arrayOfObject = new Object[1];
					arrayOfObject[0] = Integer
							.valueOf(this.needyPeople.topNeedyUsers.size());
					((CustomTextView) localObject2).setText(String.format(
							(String) localObject3, arrayOfObject));
				} else {
					if (!((JellyUser) this.needyPeople.topNeedyUsers.get(0))
							.getNickName().isEmpty())
						localObject1 = ((JellyUser) this.needyPeople.topNeedyUsers
								.get(0)).getNickName();
					else
						localObject1 = getResources().getString(
								R.string.top_nav_user_no_name_name);
					localObject3 = this.textView;
					String str = getResources().getString(
							R.string.needy_people_one_help);
					Object[] arrayOfObject = new Object[1];
					arrayOfObject[0] = localObject1;
					((CustomTextView) localObject3).setText(String.format(str,
							arrayOfObject));
				}
			} else {
				this.textView.setText(getResources().getString(
						R.string.needy_people_no_one));
			}
			requestLayout();
		} else {
			setAlpha(0.0F);
		}
	}

}
