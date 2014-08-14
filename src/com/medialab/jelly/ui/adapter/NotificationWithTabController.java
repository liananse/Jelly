package com.medialab.jelly.ui.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.resultmodel.ThankYouResultModel;
import com.medialab.jelly.ui.SocialContextUtils;
import com.medialab.jelly.ui.event.TopNavBarIconDisplay;
import com.medialab.jelly.ui.event.UpdateBadgeEvent;
import com.medialab.jelly.ui.view.NotificationWithTabOverlay;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.squareup.otto.Bus;

public class NotificationWithTabController implements DismissableViewController {

	private final Context context;
	private final Bus bus;
	private final SocialContextUtils socialContextUtils;

	private final NotificationWithTabOverlay overlay;

	public NotificationWithTabController(Context paramContext, Bus paramBus,
			SocialContextUtils paramSocialContextUtils,
			NotificationWithTabOverlay paramOverlay) {
		this.context = paramContext;
		this.bus = paramBus;
		this.socialContextUtils = paramSocialContextUtils;

		this.overlay = paramOverlay;

	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this.overlay;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		// this.bus.post(new TopNavBarIconDisplay(this.context.getResources()
		// .getString(R.string.top_nav_notification_overlay), true));

		this.bus.post(new TopNavBarIconDisplay("@"
				+ JellyApplication.getMineInfo(context).getNickName(), true));
		this.bus.post(new UpdateBadgeEvent(false));

		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				context, UConstants.BASE_PREFS_NAME);
		mEditor.putBoolean(UConstants.HAS_NEW_ACTIVITY, false);
		mEditor.commit();

		this.initialize();
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canBeDismissed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onStartDragging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopDragging() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub

	}

	public void initialize() {
		showUnReadTips();
		getThankCardsCount();
	}

	public void showUnReadTips() {
		SharedPreferences sp = UTools.Storage.getSharedPreferences(context,
				UConstants.BASE_PREFS_NAME);

		boolean hasNewActivity = sp.getBoolean(
				UConstants.HAS_NEW_SELF_ACTIVITY, false);
		boolean hasNewFriend = sp.getBoolean(
				UConstants.HAS_NEW_FRIEND_ACTIVITY, false);
		boolean hasFriend = sp
				.getBoolean(UConstants.HAS_FRIEND_ACTIVITY, false);

		this.overlay.setNewActivtiyFriednTips(hasNewActivity, hasNewFriend,
				hasFriend);
	}

	public void setNumOfNewFriendAndFriend() {
		SharedPreferences sp = UTools.Storage.getSharedPreferences(context,
				UConstants.BASE_PREFS_NAME);

		int numOfNewFriend = sp.getInt(UConstants.LOCAL_NUM_OF_NEW_FRIEND, 0);
		int numOfFriend = sp.getInt(UConstants.LOCAL_NUM_OF_FRIEND, 0);

		this.overlay.setNumberOfFriendAndNewFriend(numOfNewFriend, numOfFriend);
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private void getThankCardsCount() {

		Map<String, String> params = new HashMap<String, String>();

		mDataLoader.postData(UConfig.ANSWER_THANKYOUCOUNT, params, context,
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							ThankYouResultModel mModel = gson.fromJson(source,
									new TypeToken<ThankYouResultModel>() {
									}.getType());

							if (mModel != null && mModel.data != null) {
								NotificationWithTabController.this.overlay
										.setNumberOfThanks(
												mModel.data.thankCardCount,
												mModel.data.newFriendCount,
												mModel.data.friendCount);
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub

					}
				});
	}

}
