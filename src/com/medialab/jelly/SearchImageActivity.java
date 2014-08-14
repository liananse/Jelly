package com.medialab.jelly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.jelly.fragment.FLoadingProgressBarFragment;
import com.medialab.jelly.http.HHttpDataLoader;
import com.medialab.jelly.http.HHttpDataLoader.HDataListener;
import com.medialab.jelly.resultmodel.SearchImageResultModel;
import com.medialab.jelly.ui.adapter.ImageWallAdapter;
import com.medialab.jelly.util.KeyboardUtils;
import com.medialab.jelly.util.UConfig;
import com.medialab.jelly.util.UUtils;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class SearchImageActivity extends BaseActivity implements
		OnClickListener {

	private GridView mStockImageWall;
	private TextView mStockImageSearch;
	private EditText mStockImageEditText;
	private String keyword = "";

	/**
	 * GridView的适配器
	 */
	private ImageWallAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.stock_image);

		mStockImageSearch = (TextView) this
				.findViewById(R.id.stock_image_search);

		mStockImageSearch.setOnClickListener(this);

		mStockImageEditText = (EditText) this
				.findViewById(R.id.stock_image_text);

		mStockImageWall = (GridView) this
				.findViewById(R.id.stock_image_gridview);

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private final static int IMAGE_COUNT = 15;

	private void searchImage(String keyword) {

		final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
		FragmentTransaction ft = this.getSupportFragmentManager()
				.beginTransaction();
		mLoadingProgressBarFragment.show(ft, "dialog");
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("keyWord", keyword);
			params.put("count", IMAGE_COUNT + "");
			mDataLoader.getData(UConfig.IMAGE_SEARCH, params, this,
					new HDataListener() {

						@Override
						public void onSocketTimeoutException(String msg) {
							// TODO Auto-generated method stub
							UUtils.showTimeoutToast(SearchImageActivity.this);
							mLoadingProgressBarFragment.dismiss();
						}

						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							loadImage(source);
							mLoadingProgressBarFragment.dismiss();
						}

						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
							mLoadingProgressBarFragment.dismiss();
						}

						@Override
						public void onConnectTimeoutException(String msg) {
							// TODO Auto-generated method stub
							UUtils.showTimeoutToast(SearchImageActivity.this);
							mLoadingProgressBarFragment.dismiss();
						}
					});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLoadingProgressBarFragment.dismiss();
		}

	}

	private void loadImage(String source) {
		if (source != null) {
			try {
				JSONObject json = new JSONObject(source);
				JSONObject data = json.getJSONObject("data");
				if (data != null) {
					Gson gson = new Gson();
					SearchImageResultListModel mModel = gson.fromJson(
							data.toString(),
							new TypeToken<SearchImageResultListModel>() {
							}.getType());

					if (mModel != null) {
						if (mModel.images != null && mModel.images.size() > 0) {

							adapter = new ImageWallAdapter(this, 0,
									mModel.images, mStockImageWall,
									mModel.referer);
							mStockImageWall.setAdapter(adapter);
						}
					}

				} else {
					UUtils.showServerMessageToast(SearchImageActivity.this,
							"数据错误");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.stock_image_search:

			MobclickAgent.onEvent(this, UMengEventID.IMAGE_SEARCH);
			// 隐藏键盘
			KeyboardUtils.hideKeyboard(this, mStockImageEditText);

			keyword = mStockImageEditText.getText().toString();
			if (!keyword.equals("")) {
				// 加载网页
				searchImage(keyword);
			} else {
				UUtils.showServerMessageToast(this, "搜索内容不能为空！");
			}

			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的下载任务
		if (adapter != null)
			adapter.cancelAllTasks();
	}

	class SearchImageResultListModel {

		private String referer;
		private List<SearchImageResultModel> images;

	}

}
