package com.medialab.jelly.ui.view;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.FullCardView;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.model.ThankYou;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.view.CustomTextView;

public class ThankYouCard extends FullCardView {

	FinalBitmap fb;
	private final ThankYouViewListener listener;

	public ThankYouCard(Activity paramActivity,
			StarfishScreenUtils paramStarfishScreenUtils,
			ThankYou paramThankYou, boolean paramBoolean,
			ThankYouViewListener paramThankYouViewListener) {
		super(paramActivity, paramStarfishScreenUtils);
		// TODO Auto-generated constructor stub
		this.listener = paramThankYouViewListener;
		setCurrentView((RelativeLayout) RelativeLayout.class
				.cast(((LayoutInflater) paramActivity
						.getSystemService("layout_inflater")).inflate(
						R.layout.thank_you_card, this, false)));

		fb = FinalBitmap.create(paramActivity);
		// 感谢卡图片
		ImageView mThankYouImageView = (ImageView) findViewById(R.id.thank_you_image);
		
		mThankYouImageView.setClickable(true);
		// 查看回答btn
		Button mViewAnswerBtn = (Button) findViewById(R.id.view_answer_button);
		FontManager.setTypeface(mViewAnswerBtn, FontManager.Weight.HUAKANG);

		mViewAnswerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ThankYouCard.this.listener.viewAnswerClicked();
			}
		});
		// 分享btn
		Button mShareBtn = (Button) findViewById(R.id.share_button);
		FontManager.setTypeface(mShareBtn, FontManager.Weight.HUAKANG);
		if (paramBoolean) {
			mShareBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ThankYouCard.this.listener.shareClicked();
				}
			});
		} else {
			mShareBtn.setVisibility(View.GONE);
		}
		// 感谢卡来自
		CustomTextView mFromText = (CustomTextView) findViewById(R.id.thank_you_from_text_field);
		FontManager.setTypeface(mFromText, FontManager.Weight.HUAKANG);

		if (paramThankYou.fromUser != null
				&& !paramThankYou.fromUser.getNickName().isEmpty()) {

			String fromStr = paramActivity.getResources().getString(
					R.string.thank_you_from_string);
			mFromText.setText(String.format(fromStr,
					paramThankYou.fromUser.getNickName()));
		}
		// 感谢卡给谁
		CustomTextView mToText = (CustomTextView) findViewById(R.id.thank_you_to_text_field);
		FontManager.setTypeface(mToText, FontManager.Weight.HUAKANG);

		if (paramThankYou.toUser != null
				&& !paramThankYou.toUser.getNickName().isEmpty()) {
			String toStr = paramActivity.getResources().getString(
					R.string.thank_you_to_string);
			mToText.setText(String.format(toStr,
					paramThankYou.toUser.getNickName()));
		}
		
		fb.display(mThankYouImageView,UDisplayWidth.getPicUrlByWidth(UDisplayWidth.PIC_WIDTH_680, paramThankYou.cardTemplet.picName));


	}

	public static abstract interface ThankYouViewListener {
		public abstract void shareClicked();

		public abstract void viewAnswerClicked();
	}
}
