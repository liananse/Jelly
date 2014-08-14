package com.medialab.jelly.view;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.ui.event.ComposeInviteOverlaySelectedEvent;
import com.medialab.jelly.ui.event.ComposeOverlaySelectedEvent;
import com.medialab.jelly.ui.event.NotificationOverlaySelectedEvent;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class IconNavBar extends AbstractNavBar {

	private final Bus bus;
	private ImageView composeButton;
	private ImageView meButton;

	public IconNavBar(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.bus = JellyApplication.getBus();
	}

	@Override
	protected View getLeftView() {
		// TODO Auto-generated method stub
		this.meButton = new ImageView(getContext());
		this.meButton.setImageResource(R.drawable.profile_icon);
		this.meButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {

				// open the notification View
				IconNavBar.this.bus
						.post(new NotificationOverlaySelectedEvent());

				// clear all notifications
				NotificationManager mNotificationManager = (NotificationManager) getContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancelAll();
				
				MobclickAgent.onEvent(getContext(), UMengEventID.MAIN_VIEW_ACTIVITY);
			}
		});
		return this.meButton;
	}

	@Override
	protected View getRightView() {
		// TODO Auto-generated method stub
		this.composeButton = new ImageView(getContext());
		this.composeButton.setImageResource(R.drawable.compose_icon_top_nav);
		this.composeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				
				SharedPreferences sp = UTools.Storage.getSharedPreferences(getContext(), UConstants.BASE_PREFS_NAME);
				int friendsNum = sp.getInt(UConstants.FRIENDS_NUM, 0);
				int unLockedFriensNum = sp.getInt(UConstants.UNLOCKED_FRIENDS_NUM, 1);
				
				int numOfFriend = sp.getInt(UConstants.LOCAL_NUM_OF_FRIEND, 0);
				int numOfNewFriend = sp.getInt(UConstants.LOCAL_NUM_OF_NEW_FRIEND, 0);
				
				if (friendsNum >= unLockedFriensNum || numOfFriend + numOfNewFriend >= unLockedFriensNum) {
					IconNavBar.this.bus.post(new ComposeOverlaySelectedEvent(
							false));
					
					MobclickAgent.onEvent(getContext(), UMengEventID.MAIN_VIEW_EDIT_QUESTION);
				} else {
					IconNavBar.this.bus
							.post(new ComposeInviteOverlaySelectedEvent());
					
					MobclickAgent.onEvent(getContext(), UMengEventID.MAIN_VIEW_INVITE_LOCK);
				}
				
			}
		});
		return this.composeButton;
	}

	public void setDisplay(boolean paramBoolean) {
		if (paramBoolean) {
			this.composeButton.setVisibility(0);
			this.composeButton.setClickable(true);
		} else {
			this.composeButton.setVisibility(View.GONE);
			this.composeButton.setClickable(false);
		}
	}

	public void updateProfileIsBadged(boolean paramBoolean) {
		if (!paramBoolean)
			this.meButton.setImageResource(R.drawable.profile_icon);
		else
			this.meButton.setImageResource(R.drawable.profile_icon_badged);
	}

}
