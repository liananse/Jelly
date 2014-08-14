package com.medialab.jelly.ui.view;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.model.Notification;
import com.medialab.jelly.ui.SocialContextUtils;
import com.medialab.jelly.ui.SocialContextUtils.SocialVariants;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.RoundedImageView;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.view.CustomTextView;

public class NotificationRowView extends ViewGroup {

	private final NotificationClickedListener clickListener;
	private Notification currentNotification;
	private final ImageView mainImageView;
	private final int mainImageViewSize;
	private final CustomTextView mainTextView;
	private final int notifTextHeight;
	private final int padding;
	private final ImageView readIndicator;
	private final int readIndicatorSize;
	private final RoundedImageView social1;
	private final RoundedImageView social2;
	private final ImageView socialConnection;
	private final int socialConnectionHeight;
	private final SocialContextUtils socialContextUtils;
	private final int socialHeight;
	private final int socialImageSpacing;
	private final int spacingToFirstIcon;
	private final int timeTextSize;
	private final CustomTextView timeTextView;

	private FinalBitmap fb;

	public NotificationRowView(Context paramContext,
			SocialContextUtils paramSocialContextUtils,
			NotificationClickedListener paramNotificationClickedListener) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.socialContextUtils = paramSocialContextUtils;
		fb = FinalBitmap.create(paramContext);
		Resources localResources = getResources();
		this.spacingToFirstIcon = localResources
				.getDimensionPixelSize(R.dimen.notification_left_to_picture);
		this.padding = localResources
				.getDimensionPixelSize(R.dimen.notification_padding);
		this.readIndicatorSize = localResources
				.getDimensionPixelSize(R.dimen.notification_read_indicator_size);
		this.timeTextSize = localResources
				.getDimensionPixelSize(R.dimen.notification_time_row_text_size);
		this.notifTextHeight = localResources
				.getDimensionPixelSize(R.dimen.notification_main_text_size);
		this.mainImageViewSize = localResources
				.getDimensionPixelSize(R.dimen.notification_main_image_size);
		this.socialImageSpacing = localResources
				.getDimensionPixelSize(R.dimen.social_context_icon_horizontal_spacing);
		this.socialHeight = localResources
				.getDimensionPixelSize(R.dimen.social_context_image_height);
		this.socialConnectionHeight = localResources
				.getDimensionPixelSize(R.dimen.social_connection_height);

		this.mainImageView = new ImageView(paramContext);
		this.mainImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.mainImageView
				.setBackgroundResource(R.drawable.notification_thumbnail_overlay);

		this.social1 = new RoundedImageView(paramContext);
		this.social1.setOval(true);
		this.social1.setBackgroundResource(R.drawable.circular_photo_mask);
		this.social1.setScaleType(ImageView.ScaleType.CENTER_CROP);
		this.socialConnection = new ImageView(paramContext);
		this.social2 = new RoundedImageView(paramContext);
		this.social2.setOval(true);
		this.social2.setBackgroundResource(R.drawable.circular_photo_mask);
		this.social2.setScaleType(ImageView.ScaleType.CENTER_CROP);

		this.timeTextView = new CustomTextView(paramContext);
		FontManager.setTypeface(this.timeTextView, FontManager.Weight.HUAKANG);
		this.timeTextView.setTextColor(localResources
				.getColor(R.color.notification_context_time_color));
		this.timeTextView.setTextSize(0, this.timeTextSize);
		this.timeTextView.setGravity(19);
		this.mainTextView = new CustomTextView(paramContext);
		this.mainTextView.setTextColor(localResources
				.getColor(R.color.notification_context_connection_line_color));
		FontManager.setTypeface(this.mainTextView, FontManager.Weight.HUAKANG);
		this.mainTextView.setGravity(16);
		this.mainTextView.setTextSize(0, this.notifTextHeight);

		this.readIndicator = new ImageView(paramContext);
		this.readIndicator
				.setImageResource(R.drawable.notification_unread_badge);
		addView(this.mainImageView);
		addView(this.social1);
		addView(this.socialConnection);
		addView(this.social2);
		addView(this.timeTextView);
		addView(this.mainTextView);
		addView(this.readIndicator);

		this.clickListener = paramNotificationClickedListener;
		setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				// if (NotificationRowView.this.currentNotification == null)
				// Log.e(NotificationRowView.TAG,
				// "Error - someone clicked a null notification row?");
				NotificationRowView.this.clickListener
						.notificationClicked(NotificationRowView.this.currentNotification);
			}
		});
	}

	public void loadNotification(
			Notification paramNotification) {
		this.currentNotification = paramNotification;
		if (paramNotification.readState != 1)
			this.readIndicator.setVisibility(View.VISIBLE);
		else
			this.readIndicator.setVisibility(View.INVISIBLE);

		fb.display(this.mainImageView, UDisplayWidth.getPicUrlByWidth(
				UDisplayWidth.PIC_WIDTH_220,
				paramNotification.question.questionImage.picName));

		this.socialContextUtils.loadContextIntoImages(SocialVariants.SocialVariants.LIGHT, paramNotification.friend,
				paramNotification.middleUser, this.social1,
				this.socialConnection, this.social2, false);
		this.timeTextView.setText(this.socialContextUtils
				.getTimeTextFromTimestamp(paramNotification.createdAt));
		this.mainTextView.setText(paramNotification.content);
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int j = (this.spacingToFirstIcon - this.readIndicatorSize) / 2;
		int m = j + this.readIndicatorSize;
		int i = paramInt4 - paramInt2;
		int k = this.padding + (this.socialHeight - this.readIndicatorSize) / 2;
		int n = k + this.readIndicatorSize;
		this.readIndicator.layout(j, k, m, n);
		int i1 = this.spacingToFirstIcon;
		n = i1 + this.social1.getMeasuredWidth();
		m = this.social1.getMeasuredHeight();
		k = this.padding;
		j = k + m;
		this.social1.layout(i1, k, n, j);
		n = n;
		if (this.socialConnection.getVisibility() == 0) {
			i1 = n + this.socialImageSpacing;
			n = i1 + this.socialConnection.getMeasuredWidth();
			m = k + (m - this.socialConnection.getMeasuredHeight()) / 2;
			int i2 = m + this.socialConnection.getMeasuredHeight();
			this.socialConnection.layout(i1, m, n, i2);
			n = n;
		}
		if (this.social2.getVisibility() == 0) {
			m = n + this.socialImageSpacing;
			n = m + this.social2.getMeasuredWidth();
			this.social2.layout(m, k, n, j);
			n = n;
		}
		n += this.socialImageSpacing;
		m = n + this.timeTextView.getMeasuredWidth();
		this.timeTextView.layout(n, k, m, j);
		j += this.padding;
		m = j + this.mainTextView.getMeasuredHeight();
		k = this.spacingToFirstIcon;
		n = k + this.mainTextView.getMeasuredWidth();
		this.mainTextView.layout(k, j, n, m);
		k = paramInt3 - paramInt1 - this.padding;
		j = k - this.mainImageView.getMeasuredWidth();
		m = (i - this.mainImageView.getMeasuredHeight()) / 2;
		i = m + this.mainImageView.getMeasuredHeight();
		this.mainImageView.layout(j, m, k, i);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int i = View.MeasureSpec.getSize(widthMeasureSpec);
		int j = View.MeasureSpec.makeMeasureSpec(this.socialHeight, MeasureSpec.EXACTLY);
		this.social1.measure(j, j);
		this.social2.measure(j, j);
		j = View.MeasureSpec.makeMeasureSpec(this.socialConnectionHeight,
				MeasureSpec.EXACTLY);
		this.socialConnection.measure(j, j);
		j = i
				- (this.padding + this.mainImageViewSize + this.padding + 3
						* this.socialImageSpacing + 2 * this.socialHeight
						+ this.socialConnectionHeight + this.spacingToFirstIcon);
		this.timeTextView
				.measure(View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY),
						View.MeasureSpec.makeMeasureSpec(this.socialHeight,
								MeasureSpec.EXACTLY));
		this.mainImageView.measure(View.MeasureSpec.makeMeasureSpec(
				this.mainImageViewSize, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.mainImageViewSize, MeasureSpec.EXACTLY));
		i -= this.spacingToFirstIcon + 2 * this.padding
				+ this.mainImageViewSize;
		this.mainTextView.measure(View.MeasureSpec.makeMeasureSpec(i,
				MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
				2 * this.mainImageViewSize, MeasureSpec.AT_MOST));
		i = View.MeasureSpec
				.makeMeasureSpec(this.readIndicatorSize, MeasureSpec.EXACTLY);
		this.readIndicator.measure(i, i);
		super.onMeasure(
				widthMeasureSpec,
				View.MeasureSpec.makeMeasureSpec(3 * this.padding
						+ this.mainTextView.getMeasuredHeight()
						+ this.socialHeight, MeasureSpec.EXACTLY));
	}

	public static abstract interface NotificationClickedListener {
		public abstract void notificationClicked(Notification paramNotification);
	}

}
