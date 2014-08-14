package com.medialab.jelly.ui.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.resultmodel.FriendsListResultModel;
import com.medialab.jelly.ui.event.NewFriendAndFriendNumEvent;
import com.medialab.jelly.ui.event.NewUnReadTipsEvent;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.SquareRoundedImageView;
import com.medialab.jelly.view.XListView;
import com.medialab.jelly.view.XListView.IXListViewListener;
import com.squareup.otto.Bus;

public class FriendsFragment extends Fragment implements IXListViewListener {

	private Bus bus;
	private XListView mXListView;

	private View mHeaderView;

	private LinearLayout ll1;
	private LinearLayout ll2;
	private LinearLayout ll3;
	private LinearLayout ll4;

	public FriendsFragment() {

	}

	public FriendsFragment(Bus paramBus) {
		this.bus = paramBus;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fb = FinalBitmap.create(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.friends_fragment, container,
				false);

		mXListView = (XListView) view.findViewById(R.id.friends_list_view);

		mHeaderView = LayoutInflater.from(getActivity()).inflate(
				R.layout.friend_list_header_view, null);

		mHeaderView.setOnClickListener(null);

		ll1 = (LinearLayout) mHeaderView.findViewById(R.id.ll1);
		ll2 = (LinearLayout) mHeaderView.findViewById(R.id.ll2);
		ll3 = (LinearLayout) mHeaderView.findViewById(R.id.ll3);
		ll4 = (LinearLayout) mHeaderView.findViewById(R.id.ll4);

		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);

		mXListView.addHeaderView(mHeaderView);

		mXListView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		isRefresh = false;
		mXListView.setRefreshState();
		getFriendData();
		return view;
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private boolean isLoading = false;

	private boolean isRefresh = false;

	private void getFriendData() {

		isLoading = true;
		Map<String, String> params = new HashMap<String, String>();

		mDataLoader.postData(UConfig.FRIEND_LIST_URL, params, getActivity(),
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
							FriendsListResultModel mModel = gson.fromJson(
									source,
									new TypeToken<FriendsListResultModel>() {
									}.getType());

							if (mModel != null && mModel.data != null) {
								initListView(mModel.data);
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

	private void clearAllLLViews() {
		ll1.removeAllViews();
		ll2.removeAllViews();
		ll3.removeAllViews();
		ll4.removeAllViews();
	}

	public void refreshTips() {
		// 有新的保存到文件
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				getActivity(), UConstants.BASE_PREFS_NAME);
		// 有新的个人动态保存到文件
		mEditor.putBoolean(UConstants.HAS_FRIEND_ACTIVITY, false);
		mEditor.commit();
		this.bus.post(new NewUnReadTipsEvent(true));
	}

	private FinalBitmap fb;

	private void initListView(List<JellyUser> mDataList) {
		clearAllLLViews();
		if (mDataList != null && mDataList.size() > 0) {
			for (int i = 0; i < mDataList.size(); i++) {

				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.friend_list_item, null);

				SquareRoundedImageView friendAvatar = (SquareRoundedImageView) view
						.findViewById(R.id.friend_header);

				TextView friendName = (TextView) view
						.findViewById(R.id.friend_name);

				FontManager.setTypeface(friendName, FontManager.Weight.HUAKANG);

				fb.display(friendAvatar, UDisplayWidth.getPicUrlByWidth(
						UDisplayWidth.PIC_WIDTH_160, mDataList.get(i)
								.getAvatarName()));

				friendName.setText(mDataList.get(i).getNickName());

				int index = i % 4;
				if (index == 0) {
					ll1.addView(view);
				} else if (index == 1) {
					ll2.addView(view);
				} else if (index == 2) {
					ll3.addView(view);
				} else if (index == 3) {
					ll4.addView(view);
				}
			}
		}

		if (mDataList != null) {
			SharedPreferences.Editor mEditor = UTools.Storage
					.getSharedPreEditor(getActivity(),
							UConstants.BASE_PREFS_NAME);
			mEditor.putInt(UConstants.LOCAL_NUM_OF_FRIEND, mDataList.size());
			mEditor.commit();

			this.bus.post(new NewFriendAndFriendNumEvent());
		}

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!isLoading) {
			isRefresh = true;
			getFriendData();
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
			// 暂时先去掉
//			mXListView.setRefreshState();
			getFriendData();
		}
	}

}
