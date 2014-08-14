package com.medialab.jelly.ui.view;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.medialab.jelly.R;
import com.medialab.jelly.controller.FullCardView;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class OutOfQuestionsView extends FullCardView {

	private final LinearLayout buttonRow;
	private final FrameLayout container;
	private final RoundedImageView imageView;
	private final Button inviteButton;

	public OutOfQuestionsView(Activity paramActivity,
			StarfishScreenUtils paramStarfishScreenUtils) {
		super(paramActivity, paramStarfishScreenUtils);
		// TODO Auto-generated constructor stub
		this.setClickable(true);
		this.container = new FrameLayout(paramActivity);
		this.container.setBackgroundResource(R.drawable.hollowed_card_overlay);
		this.imageView = new RoundedImageView(paramActivity);
		this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.imageView.setCornerRadius(
				R.dimen.card_top_background_radius);
		
		SharedPreferences sp = UTools.Storage.getSharedPreferences(paramActivity, UConstants.BASE_PREFS_NAME);
		String inviteCardUrl = sp.getString(UConstants.OUT_OF_QUESTION_INVITE_CARD_URL, "");
		
		if (!inviteCardUrl.equals("")) {
			FinalBitmap.create(paramActivity).display(imageView, UDisplayWidth.getPicUrlByWidth(UDisplayWidth.PIC_WIDTH_680, inviteCardUrl));
		} else {
			this.imageView.setImageResource(R.drawable.out_of_question_invite_card);
		}
		
		this.container.addView(this.imageView);
		
		this.buttonRow = ((LinearLayout) LinearLayout.class
				.cast(((LayoutInflater) paramActivity
						.getSystemService("layout_inflater")).inflate(
						R.layout.out_of_question_bottom_row_layout, this, false)));
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				-1, getResources().getDimensionPixelSize(
						R.dimen.question_card_button_row_height));
		localLayoutParams.gravity = 80;
		this.inviteButton = ((Button) Button.class.cast(this.buttonRow
				.findViewById(R.id.invite_button)));
		
		this.inviteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MobclickAgent.onEvent(getContext(), UMengEventID.QUESTION_INVITE_FRIEND_CARD_INVITE);
				
				Intent localIntent = new Intent();
		        localIntent.setAction("android.intent.action.SEND");
		        localIntent.putExtra("android.intent.extra.TEXT", (getContext()).getResources().getString(R.string.out_of_questions_invite_main_text));
		        localIntent.putExtra("android.intent.extra.SUBJECT", (getContext()).getResources().getString(R.string.out_of_questions_invite_subject));
		        localIntent.setType("text/plain");
		        (getContext()).startActivity(Intent.createChooser(localIntent, (getContext()).getResources().getString(R.string.out_of_questions_invite_chooser_title)));
			}
		});
		
		FontManager.setTypeface(this.inviteButton, FontManager.Weight.HUAKANG);
		
		this.container.addView(this.buttonRow, localLayoutParams);
		setCurrentView(this.container);
	}
}
