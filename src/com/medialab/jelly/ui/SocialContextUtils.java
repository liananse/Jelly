package com.medialab.jelly.ui;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.util.JellyTextUtils;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UDisplayWidth;

public class SocialContextUtils {

	private final Context context;

	public SocialContextUtils(Context paramContext) {
		this.context = paramContext;
	}

	public String getTimeTextFromTimestamp(long paramLong) {
		return JellyTextUtils.computeHowLongAgo(context, paramLong);
	}

	public void loadContextIntoImages(SocialVariants paramSocialVariants,
			JellyUser paramJellyUser, JellyUser paramMiddleJellyUser,
			RoundedImageView paramNetworkImageView1, ImageView paramImageView,
			RoundedImageView paramNetworkImageView2, boolean paramBoolean) {

		if (paramSocialVariants == SocialVariants.DARK) {
			paramImageView.setImageDrawable(this.context.getResources()
					.getDrawable(R.drawable.forward_icon_dark));
		} else {
			paramImageView.setImageDrawable(this.context.getResources()
					.getDrawable(R.drawable.forward_icon_light));
		}
		paramNetworkImageView1.setVisibility(0);

		FinalBitmap fb = FinalBitmap.create(this.context);

		if (paramJellyUser != null && paramJellyUser.getAvatarName() != null) {
			fb.display(paramNetworkImageView1,
					UDisplayWidth.getPicUrlByWidth(UDisplayWidth.PIC_WIDTH_120,
							paramJellyUser.getAvatarName()));
		} else {
			paramNetworkImageView1.setImageResource(R.drawable.no_user_photo);
		}

		// if go-between User is not null
		if (paramMiddleJellyUser != null) {
			paramImageView.setVisibility(View.VISIBLE);
			paramNetworkImageView2.setVisibility(View.VISIBLE);

			if (paramMiddleJellyUser.getAvatarName() != null) {
				fb.display(paramNetworkImageView2, UDisplayWidth
						.getPicUrlByWidth(UDisplayWidth.PIC_WIDTH_120,
								paramMiddleJellyUser.getAvatarName()));
			} else {
				paramNetworkImageView2
						.setImageResource(R.drawable.no_user_photo);
			}
		} else {
			paramImageView.setVisibility(View.GONE);
			paramNetworkImageView2.setVisibility(View.GONE);
		}

	}

	public static enum SocialVariants {
		LIGHT, DARK, SocialVariants
	}
}
