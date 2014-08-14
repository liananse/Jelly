package com.medialab.jelly.ui.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.R;
import com.medialab.jelly.db.DDBOpenHelper;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.Notification;
import com.medialab.jelly.model.NotificationType;
import com.medialab.jelly.resultmodel.NotificationListResultModel;
import com.medialab.jelly.ui.SocialContextUtils;
import com.medialab.jelly.ui.drawable.NotificationDivderDrawable;
import com.medialab.jelly.ui.event.AnswerNotificationSelected;
import com.medialab.jelly.ui.event.NewUnReadTipsEvent;
import com.medialab.jelly.ui.event.StarredNotificationSelected;
import com.medialab.jelly.ui.event.ThankYouNotificationSelected;
import com.medialab.jelly.ui.view.NotificationRowView;
import com.medialab.jelly.ui.view.NotificationRowView.NotificationClickedListener;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.medialab.jelly.view.NoNotificationsView;
import com.medialab.jelly.view.XListView;
import com.medialab.jelly.view.XListView.IXListViewListener;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class NotificationFragment extends Fragment implements
		IXListViewListener {

	public NotificationFragment() {

	}

	private Bus bus;

	public NotificationFragment(Bus paramBus) {
		this.bus = paramBus;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.notification_fragment, container,
				false);

		initView(view);

		mNotificationListView.setRefreshState();
		getNotificationFromNetWork("1", "0");

		return view;
	}

	private NotificationRowView.NotificationClickedListener clickListener;

	private XListView mNotificationListView;
	private NotificationListViewAdapter listViewAdapter;
	private List<Notification> allNotifications = new ArrayList<Notification>();

	private SocialContextUtils socialContextUtils;

	private void initView(View view) {
		listViewAdapter = new NotificationListViewAdapter();

		mNotificationListView = (XListView) view
				.findViewById(R.id.notification_listview);

		mNotificationListView.setFadingEdgeLength(0);
		mNotificationListView.setOverScrollMode(2);
		mNotificationListView.setDivider(new NotificationDivderDrawable(
				getResources().getColor(R.color.notification_divider_color),
				getResources().getDimensionPixelSize(
						R.dimen.notification_left_to_picture)));
		mNotificationListView.setDividerHeight(getResources()
				.getDimensionPixelSize(R.dimen.notification_divider_height));

		mNotificationListView.setPullLoadEnable(false);
		mNotificationListView.setPullRefreshEnable(true);
		mNotificationListView.setXListViewListener(this);

		socialContextUtils = new SocialContextUtils(getActivity());

		this.clickListener = new NotificationClickedListener() {

			@Override
			public void notificationClicked(Notification paramNotification) {
				// TODO Auto-generated method stub

				MobclickAgent.onEvent(getActivity(),
						UMengEventID.NOTIFICATION_LIST_CLICK);
				// 没有读过这条Notification
				if (paramNotification.readState == 0) {
					paramNotification.readState = 1;

					// 更新数据库
					DDBOpenHelper mDdbOpenHelper = DDBOpenHelper
							.getInstance(getActivity());
					//
					mDdbOpenHelper
							.updateReadState(paramNotification.activityId);
				}

				switch (paramNotification.type) {
				case NotificationType.ANSWER:
					// ask someone's question be same with
					// other_answer_the_question_your_starrred
					NotificationFragment.this.bus
							.post(new AnswerNotificationSelected(
									paramNotification));
					break;
				case NotificationType.ASK:
					// the same interface with starred
					NotificationFragment.this.bus
							.post(new StarredNotificationSelected(
									paramNotification));
					break;
				case NotificationType.LIKED_ANSWER:
					// the same interface with starred
					NotificationFragment.this.bus
							.post(new AnswerNotificationSelected(
									paramNotification));
					break;
				case NotificationType.LIKED_QUESTION:
					// the same interface with starred
					NotificationFragment.this.bus
							.post(new StarredNotificationSelected(
									paramNotification));
					break;
				case NotificationType.OTHER_ANSWER_THE_QUESTION_YOUR_STARRED:
					// the same interface with answer
					NotificationFragment.this.bus
							.post(new AnswerNotificationSelected(
									paramNotification));
					break;
				case NotificationType.OTHER_FORWARD_YOU_QUESTION:
					// the same interface with starred
					NotificationFragment.this.bus
							.post(new StarredNotificationSelected(
									paramNotification));
					break;
				case NotificationType.STARRED:
					NotificationFragment.this.bus
							.post(new StarredNotificationSelected(
									paramNotification));
					break;
				case NotificationType.THANKS_CARD:
					NotificationFragment.this.bus
							.post(new ThankYouNotificationSelected(
									paramNotification));
					break;
				default:
					break;
				}

			}
		};

		this.mNotificationListView.setAdapter(listViewAdapter);
	}

	public class NotificationListViewAdapter extends BaseAdapter {

		private View getEmptyNotifView() {
			NoNotificationsView localNoNotificationsView = new NoNotificationsView(
					getActivity());

			localNoNotificationsView
					.setHasLoadedStuff(!NotificationFragment.this.isLoading);
			localNoNotificationsView.setMinimumHeight(mNotificationListView
					.getMeasuredHeight());

			return localNoNotificationsView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (!allNotifications.isEmpty()) {
				return allNotifications.size();
			}
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (!allNotifications.isEmpty()) {
				return allNotifications.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if ((position != 0)
					|| (!NotificationFragment.this.allNotifications.isEmpty())) {
				if (convertView == null
						|| (!(convertView instanceof NotificationRowView))) {
					convertView = new NotificationRowView(getActivity(),
							NotificationFragment.this.socialContextUtils,
							NotificationFragment.this.clickListener);
				}

				((NotificationRowView) convertView)
						.loadNotification(allNotifications.get(position));

				return convertView;
			} else {
				
				View emptyNotifView = getEmptyNotifView();
				
				emptyNotifView.setOnClickListener(null);
				
				return emptyNotifView;
			}
		}
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private final static int activityCount = 15;
	private int bottomActivityId = 0;
	private boolean isLoading = false;

	private void getNotificationFromNetWork(final String forward,
			String activityId) {

		isLoading = true;

		Map<String, String> params = new HashMap<String, String>();

		params.put("forward", forward);
		params.put("count", activityCount + "");
		params.put("activityId", activityId);

		mDataLoader.postData(UConfig.USER_ACTIVITY_LIST_URL, params,
				getActivity(), new HDataListener() {

					@Override
					public void onSocketTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showSocketTimeoutToast(getActivity());
						stopLoadMoreAndRefresh();
					}

					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();

						try {
							NotificationListResultModel mModel = gson
									.fromJson(
											source,
											new TypeToken<NotificationListResultModel>() {
											}.getType());

							if (mModel != null && mModel.data != null
									&& mModel.data.size() > 0) {

								// 存入数据库
								DDBOpenHelper db = DDBOpenHelper
										.getInstance(getActivity());
								db.insertData(mModel.data,
										DDBOpenHelper.NOTIFICATION_TABLE_NAME);

								if (mModel.data.size() < activityCount) {
									UToast.showShortToast(
											getActivity(),
											getActivity().getString(
													R.string.no_more_activitys));
									mNotificationListView
											.setPullLoadEnable(false);
								} else {
									mNotificationListView
											.setPullLoadEnable(true);
								}
								getNotificationFromDb();

							} else {
								UToast.showShortToast(
										getActivity(),
										getActivity().getString(
												R.string.no_more_activitys));
								mNotificationListView.setPullLoadEnable(false);
								listViewAdapter.notifyDataSetChanged();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							UToast.showOnFail(getActivity());
						}

						if (forward.equals("1")) {
							refreshTips();
						}
						stopLoadMoreAndRefresh();
					}

					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						UToast.showOnFail(getActivity());
						stopLoadMoreAndRefresh();
					}

					@Override
					public void onConnectTimeoutException(String msg) {
						// TODO Auto-generated method stub
						UToast.showConnectTimeoutToast(getActivity());
						stopLoadMoreAndRefresh();
					}
				});
	}

	private void getNotificationFromDb() {

		DDBOpenHelper db = DDBOpenHelper.getInstance(getActivity());
		String orderBy = "activityId desc limit " + activityCount;
		Object o;
		if (bottomActivityId != 0) {
			String where = "activityId < " + bottomActivityId;
			o = db.query(DDBOpenHelper.NOTIFICATION_TABLE_NAME,
					Notification.class, where, orderBy, null);
		} else {
			allNotifications.clear();
			o = db.query(DDBOpenHelper.NOTIFICATION_TABLE_NAME,
					Notification.class, null, orderBy, null);
		}

		List<Notification> mList = (ArrayList<Notification>) o;

		if (mList != null && mList.size() > 0) {
			bottomActivityId = mList.get(mList.size() - 1).activityId;

			allNotifications.addAll(mList);
			listViewAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!isLoading) {
			bottomActivityId = 0;
			getNotificationFromNetWork("1", "0");
		}
	}
	
	public void refreshMethod() {
		if (!isLoading) {
			mNotificationListView.setRefreshState();
			bottomActivityId = 0;
			getNotificationFromNetWork("1", "0");
		}
	}

	public void refreshTips() {
		// 有新的保存到文件
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
				getActivity(), UConstants.BASE_PREFS_NAME);
		// 有新的个人动态保存到文件
		mEditor.putBoolean(UConstants.HAS_NEW_SELF_ACTIVITY, false);
		mEditor.commit();
		this.bus.post(new NewUnReadTipsEvent(true));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (!isLoading)
			getNotificationFromNetWork("0", bottomActivityId + "");
	}

	private void stopLoadMoreAndRefresh() {
		isLoading = false;
		mNotificationListView.stopRefresh();
		mNotificationListView.stopLoadMore();
	}

}
