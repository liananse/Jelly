package com.medialab.jelly.ui.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.AddFriendModel;
import com.medialab.jelly.resultmodel.AddFriendResultModel;
import com.medialab.jelly.ui.drawable.NotificationDivderDrawable;
import com.medialab.jelly.ui.event.NewFriendAndFriendNumEvent;
import com.medialab.jelly.ui.event.NewUnReadTipsEvent;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.view.XListView;
import com.medialab.jelly.view.XListView.IXListViewListener;
import com.squareup.otto.Bus;

public class FriendsAddListFragment extends Fragment implements
		IXListViewListener {

	public FriendsAddListFragment() {

	}

	private Bus bus;

	private XListView mXListView;
	private FriendsAddListAdapter mAdapter;

	public FriendsAddListFragment(Bus paramBus) {
		this.bus = paramBus;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.friends_add_list_fragment,
				container, false);

		mAdapter = new FriendsAddListAdapter(getActivity());

		mXListView = (XListView) view.findViewById(R.id.friends_list_view);

		mXListView.setFadingEdgeLength(0);
		mXListView.setOverScrollMode(2);
		mXListView.setDivider(new NotificationDivderDrawable(getResources()
				.getColor(R.color.notification_divider_color), getResources()
				.getDimensionPixelSize(R.dimen.notification_left_to_picture)));
		mXListView.setDividerHeight(getResources().getDimensionPixelSize(
				R.dimen.notification_divider_height));

		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);

		mXListView.setAdapter(mAdapter);

		isRefresh = false;
		mXListView.setRefreshState();
		getAddFriendListData();
		return view;
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private boolean isLoading = false;
	private boolean isRefresh = false;

	private void getAddFriendListData() {
		isLoading = true;
		Map<String, String> params = new HashMap<String, String>();

		mDataLoader.postData(UConfig.NEW_FRIEND_URL, params, getActivity(),
				new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						stopLoadMoreAndRefresh();
					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							AddFriendResultModel mModel = gson.fromJson(source,
									new TypeToken<AddFriendResultModel>() {
									}.getType());

							if (mModel != null && mModel.data != null) {
								mAdapter.refreshData(mModel.data);
							}
							
							if (mModel != null) {
								updateNumOfNewFriend(mModel.data);
							}

						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (isRefresh) {
							refreshTips();
						}
						stopLoadMoreAndRefresh();
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						stopLoadMoreAndRefresh();
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						stopLoadMoreAndRefresh();
					}
				});
	}

	private void updateNumOfNewFriend(List<AddFriendModel> mDataList) {
		if (mDataList != null) {
			SharedPreferences.Editor mEditor = UTools.Storage
					.getSharedPreEditor(getActivity(),
							UConstants.BASE_PREFS_NAME);
			mEditor.putInt(UConstants.LOCAL_NUM_OF_NEW_FRIEND, mDataList.size());
			mEditor.commit();

			this.bus.post(new NewFriendAndFriendNumEvent());
		}
	}

	public void refreshTips() {
		// 有新的保存到文件
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				getActivity(), UConstants.BASE_PREFS_NAME);
		// 有新的个人动态保存到文件
		mEditor.putBoolean(UConstants.HAS_NEW_FRIEND_ACTIVITY, false);
		mEditor.commit();
		this.bus.post(new NewUnReadTipsEvent(true));
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!isLoading) {
			isRefresh = true;
			getAddFriendListData();
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	private void stopLoadMoreAndRefresh() {
		isLoading = false;
		mXListView.stopRefresh();
		mXListView.stopLoadMore();
	}
	
	public void refreshMethod() {
		if (!isLoading) {
			isRefresh = true;
//			mXListView.setRefreshState();
			getAddFriendListData();
		}
	}

}
