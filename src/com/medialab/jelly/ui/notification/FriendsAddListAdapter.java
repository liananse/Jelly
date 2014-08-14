package com.medialab.jelly.ui.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.R;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.model.AddFriendModel;
import com.medialab.jelly.resultmodel.BaseResultModel;
import com.medialab.jelly.ui.event.NewFriendAndFriendNumEvent;
import com.medialab.jelly.ui.event.NewUnReadTipsEvent;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UDisplayWidth;
import com.medialab.jelly.util.UToast;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.SquareRoundedImageView;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class FriendsAddListAdapter extends BaseAdapter {

	private List<AddFriendModel> mAddFriendModels;

	private Context mContext;
	private LayoutInflater mInflater;

	private FinalBitmap fb;

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	private Bus bus;
	public FriendsAddListAdapter(Context context) {
		this(context, null);
	}

	public FriendsAddListAdapter(Context context, List<AddFriendModel> infos) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null) {
			this.mAddFriendModels = infos;
		} else {
			this.mAddFriendModels = new ArrayList<AddFriendModel>();
		}

		fb = FinalBitmap.create(mContext);
		bus = JellyApplication.getBus();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mAddFriendModels != null) {
			return mAddFriendModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mAddFriendModels != null) {
			return mAddFriendModels.get(position);
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
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.add_friend_list_item, null);

			holder = new ViewHolder();
			holder.friendAvatar = (SquareRoundedImageView) convertView
					.findViewById(R.id.friend_header);
			holder.friendName = (TextView) convertView
					.findViewById(R.id.friend_name);
			holder.friendCameFrom = (TextView) convertView
					.findViewById(R.id.friend_came_from);
			holder.friendOperate = (TextView) convertView
					.findViewById(R.id.friend_operate);
			holder.friendItem = convertView.findViewById(R.id.friend_item);

			FontManager.setTypeface(holder.friendName,
					FontManager.Weight.HUAKANG);
			FontManager.setTypeface(holder.friendCameFrom,
					FontManager.Weight.HUAKANG);
			FontManager.setTypeface(holder.friendOperate,
					FontManager.Weight.HUAKANG);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final AddFriendModel mModel = mAddFriendModels.get(position);

		if (mModel != null) {

			fb.display(holder.friendAvatar, UDisplayWidth.getPicUrlByWidth(
					UDisplayWidth.PIC_WIDTH_160, mModel.avatarName));

			holder.friendName.setText(mModel.nickName);
			
			holder.friendCameFrom.setText(mModel.from);

			// 待审核--接受
			if (mModel.type == 1) {
				holder.friendOperate.setText(mContext
						.getText(R.string.add_friend_list_item_accept));
				holder.friendOperate.setTextColor(mContext.getResources()
						.getColor(R.color.white));
				holder.friendOperate
						.setBackgroundResource(R.drawable.accept_friend_btn_bg);
				
				holder.friendOperate.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						MobclickAgent.onEvent(mContext, UMengEventID.ADD_FRIEND_AUDIT);
						
						Map<String, String> params = new HashMap<String, String>();
						params.put("fid", mModel.uid + "");
						params.put("type", "1");
						mDataLoader.postData(UConfig.FRIEND_AUDIT_URL, params,
								mContext, new HDataListener() {

									@Override
									public void onSocketTimeoutException(
											String msg) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onFinish(String source) {
										// TODO Auto-generated method stub
										Gson gson = new Gson();
										
										try {
											BaseResultModel mResultModel = gson.fromJson(
													source,
													new TypeToken<BaseResultModel>() {
													}.getType());

											if (mResultModel != null && mResultModel.result == UConstants.SUCCESS) {
												
												mAddFriendModels.remove(mModel);
												
												// 获取朋友数
												SharedPreferences sp = UTools.Storage.getSharedPreferences(mContext, UConstants.BASE_PREFS_NAME);
												int localNumOfFriend = sp.getInt(UConstants.LOCAL_NUM_OF_FRIEND, 0);
												
												localNumOfFriend++;
												
												SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(
														mContext, UConstants.BASE_PREFS_NAME);
												mEditor.putBoolean(UConstants.HAS_FRIEND_ACTIVITY, true);
												mEditor.putInt(UConstants.LOCAL_NUM_OF_NEW_FRIEND, mAddFriendModels.size());
												mEditor.putInt(UConstants.LOCAL_NUM_OF_FRIEND, localNumOfFriend);
												mEditor.commit();

												FriendsAddListAdapter.this.bus.post(new NewUnReadTipsEvent(true));
												FriendsAddListAdapter.this.bus.post(new NewFriendAndFriendNumEvent());
												
												notifyDataSetChanged();
											} else if (mResultModel != null && mResultModel.result == UConstants.FAILURE){
												UToast.showShortToast(mContext, mResultModel.message);
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
									public void onConnectTimeoutException(
											String msg) {
										// TODO Auto-generated method stub

									}
								});
					}
				});
			} else if (mModel.type == 2) {
				// 已申请--已申请
				holder.friendOperate.setText(mContext
						.getText(R.string.add_friend_list_item_apply));
				holder.friendOperate.setTextColor(mContext.getResources()
						.getColor(R.color.apply_friend_text_color));
				holder.friendOperate
						.setBackgroundResource(R.drawable.apply_friend_btn_bg);
				
				holder.friendOperate.setOnClickListener(null);
			} else if (mModel.type == 3) {
				// 待添加--添加
				holder.friendOperate.setText(mContext
						.getText(R.string.add_friend_list_item_add));
				holder.friendOperate.setTextColor(mContext.getResources()
						.getColor(R.color.white));
				holder.friendOperate
						.setBackgroundResource(R.drawable.add_friend_btn_bg);

				holder.friendOperate.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MobclickAgent.onEvent(mContext, UMengEventID.ADD_FRIEND_ADD);
						
						Map<String, String> params = new HashMap<String, String>();
						params.put("fid", mModel.uid + "");

						mDataLoader.postData(UConfig.FRIEND_ADD_URL, params,
								mContext, new HDataListener() {

									@Override
									public void onSocketTimeoutException(
											String msg) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onFinish(String source) {
										// TODO Auto-generated method stub
										Gson gson = new Gson();
										
										try {
											BaseResultModel mResultModel = gson.fromJson(
													source,
													new TypeToken<BaseResultModel>() {
													}.getType());

											if (mResultModel != null && mResultModel.result == UConstants.SUCCESS) {
												mModel.type = 2;
												notifyDataSetChanged();
											} else if (mResultModel != null && mResultModel.result == UConstants.FAILURE){
												UToast.showShortToast(mContext, mResultModel.message);
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
									public void onConnectTimeoutException(
											String msg) {
										// TODO Auto-generated method stub

									}
								});
					}
				});
			} else if (mModel.type == 4) {
				// 已接受--已接受
				holder.friendOperate.setText(mContext
						.getText(R.string.add_friend_list_item_added));
				holder.friendOperate.setTextColor(mContext.getResources()
						.getColor(R.color.apply_friend_text_color));
				holder.friendOperate
						.setBackgroundResource(R.drawable.apply_friend_btn_bg);
				
				holder.friendOperate.setOnClickListener(null);
			}

		}
		holder.friendName.setOnClickListener(null);
		holder.friendItem.setOnClickListener(null);

		return convertView;

	}

	class AddFriendClickListener implements OnClickListener {

		private TextView friendOperate;

		public AddFriendClickListener(TextView friendOperate) {
			this.friendOperate = friendOperate;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}

	public void refreshData(List list) {
		mAddFriendModels.clear();
		mAddFriendModels.addAll(list);
		notifyDataSetChanged();
	}

	public void addData(List list) {
		mAddFriendModels.addAll(list);
		notifyDataSetChanged();
	}

	static class ViewHolder {
		SquareRoundedImageView friendAvatar;
		TextView friendName;
		TextView friendCameFrom;
		TextView friendOperate;
		View friendItem;
	}
}
