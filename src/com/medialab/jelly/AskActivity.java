package com.medialab.jelly;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.medialab.jelly.fragment.FLoadingProgressBarFragment;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.umeng.analytics.MobclickAgent;

public class AskActivity extends BaseActivity implements OnClickListener,
		OnGetSuggestionResultListener, OnGetPoiSearchResultListener {

	private int disabledTextColor;
	private int enabledTextColor;

	private LinearLayout mAskMapLinearLayout;
	// 加标记按钮
	private ImageView mLocateTag;
	// 定位我位置按钮
	private ImageView mLocateMe;
	// 使用地图
	private TextView mUseBtn;

	// suggestion place
	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private SuggestionSearch mSuggestionSearch = null;
	private PoiSearch mPoiSearch = null;
	private String mCurrentCity;

	// //////////////////////////////////////////
	MapView mMapView;
	BaiduMap mBaiduMap;

	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	BitmapDescriptor mCurrentMarker;

	boolean isFirstLoc = true;// 是否首次定位

	BitmapDescriptor mapTag = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);
	private Marker mapTagMarker;

	private double mUseLatitude;
	private double mUseLongitude;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.ask);
		initView();
	}

	private void setUseLocation(double paramLatitude, double paramLongitude) {
		this.mUseLatitude = paramLatitude;
		this.mUseLongitude = paramLongitude;
	}

	private void initView() {
		disabledTextColor = getResources().getColor(
				R.color.top_nav_modal_disabled_text_color);
		enabledTextColor = getResources().getColor(
				R.color.top_nav_modal_text_color);
		mAskMapLinearLayout = (LinearLayout) findViewById(R.id.ask_map_linearlayout);

		mLocateTag = (ImageView) findViewById(R.id.locate_tag);
		mLocateMe = (ImageView) findViewById(R.id.locate_me);
		mUseBtn = (TextView) findViewById(R.id.locate_use);

		mLocateTag.setOnClickListener(this);
		mLocateMe.setOnClickListener(this);
		mUseBtn.setOnClickListener(this);

		mUseBtn.setClickable(false);
		mUseBtn.setTextColor(disabledTextColor);
		// /////////////////
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.search_location_et);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		keyWorldsView.setAdapter(sugAdapter);

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		keyWorldsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(mCurrentCity));
			}
		});

		keyWorldsView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_ENTER) {// 修改回车键功能

					MobclickAgent.onEvent(AskActivity.this,
							UMengEventID.LOCATION_SEARCH);

					// 先隐藏键盘
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(AskActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					// mPoiSearch.searchInCity((new PoiCitySearchOption())
					// .city(mCurrentCity)
					// .keyword(keyWorldsView.getText().toString().trim())
					// .pageNum(0));

					Point p = new Point(
							getResources().getDisplayMetrics().widthPixels / 2,
							getResources().getDisplayMetrics().heightPixels / 2);
					LatLng llInfo = mBaiduMap.getProjection()
							.fromScreenLocation(p);

					mPoiSearch.searchNearby((new PoiNearbySearchOption())
							.location(llInfo)
							.keyword(keyWorldsView.getText().toString().trim())
							.pageNum(0).radius(3000));

				}
				return false;
			}
		});

		// 地图初始化

		SharedPreferences sp = UTools.Storage.getSharedPreferences(this,
				UConstants.BASE_PREFS_NAME);

		mCurrentCity = sp.getString(UConstants.LOCATION_CITY, "深圳");
		// LatLng(double latitude, double longitude)
		LatLng p = new LatLng(Double.parseDouble(sp.getString(
				UConstants.LOCATION_LATITUDE, "22.537976")),
				Double.parseDouble(sp.getString(UConstants.LOCATION_LONGITUDE,
						"113.943617")));

		setUseLocation(p.latitude, p.longitude);

		// 设置默认中心点，及缩放级别
		BaiduMapOptions mBaiduMapOptions = new BaiduMapOptions()
				.mapStatus(new MapStatus.Builder().zoom(17).target(p).build());
		mMapView = new MapView(this, mBaiduMapOptions);

		mAskMapLinearLayout.addView(mMapView);

		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMaxAndMinZoomLevel(19, 13);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub

				setUseLocation(arg0.latitude, arg0.longitude);

				mBaiduMap.clear();

				OverlayOptions ooTag = new MarkerOptions().position(arg0)
						.icon(mapTag).zIndex(50);
				mapTagMarker = (Marker) (mBaiduMap.addOverlay(ooTag));
			}
		});

		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				mUseBtn.setClickable(true);
				mUseBtn.setTextColor(enabledTextColor);
			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				mBaiduMap.clear();

				setUseLocation(arg0.getPosition().latitude,
						arg0.getPosition().longitude);

				OverlayOptions ooTag = new MarkerOptions()
						.position(arg0.getPosition()).icon(mapTag).zIndex(50);
				mapTagMarker = (Marker) (mBaiduMap.addOverlay(ooTag));

				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		super.onDestroy();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);

			if (isFirstLoc) {

				SharedPreferences.Editor mEditor = UTools.Storage
						.getSharedPreEditor(AskActivity.this,
								UConstants.BASE_PREFS_NAME);
				mEditor.putString(UConstants.LOCATION_LATITUDE,
						String.valueOf(location.getLatitude()));
				mEditor.putString(UConstants.LOCATION_LONGITUDE,
						String.valueOf(location.getLongitude()));

				if (location.hasAddr() && location.getCity() != null) {
					mEditor.putString(UConstants.LOCATION_CITY,
							location.getCity());
				}
				mEditor.commit();

				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());

				setUseLocation(ll.latitude, ll.longitude);

				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);

				mLocClient.stop();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mUseBtn) {

			MobclickAgent.onEvent(this, UMengEventID.LOCATION_USE);

			final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			mLoadingProgressBarFragment.show(ft, "dialog");

			mMapView.getMap().snapshot(new SnapshotReadyCallback() {

				@Override
				public void onSnapshotReady(Bitmap arg0) {
					// TODO Auto-generated method stub
					mLoadingProgressBarFragment.dismiss();
					Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
							getContentResolver(), arg0, null, null));
					Intent intent = getIntent();
					intent.setData(uri);

					JellyApplication.setMapQuestionLatitude(mUseLatitude);
					JellyApplication.setMapQuesitonLongitude(mUseLongitude);

					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			});
		} else if (v == mLocateMe) {

			MobclickAgent.onEvent(this, UMengEventID.LOCATION_ME);

			mLocClient.start();
			isFirstLoc = true;
		} else if (v == mLocateTag) {

			MobclickAgent.onEvent(this, UMengEventID.LOCATION_SET);

			mBaiduMap.clear();

			Point p = new Point(
					getResources().getDisplayMetrics().widthPixels / 2,
					getResources().getDisplayMetrics().heightPixels / 2);
			LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);

			setUseLocation(llInfo.latitude, llInfo.longitude);

			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llInfo);
			mBaiduMap.animateMapStatus(u);

			OverlayOptions ooTag = new MarkerOptions().position(llInfo)
					.icon(mapTag).zIndex(9);
			mapTagMarker = (Marker) (mBaiduMap.addOverlay(ooTag));
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		// TODO Auto-generated method stub
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// 中心点
			Point p = new Point(
					getResources().getDisplayMetrics().widthPixels / 2,
					getResources().getDisplayMetrics().heightPixels / 2);
			LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);

			setUseLocation(llInfo.latitude, llInfo.longitude);

			mBaiduMap.clear();
			PoiOverlay overlay = new PoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			// String strInfo = "在";
			// for (CityInfo cityInfo : result.getSuggestCityList()) {
			// strInfo += cityInfo.city;
			// strInfo += ",";
			// }
			// strInfo += "找到结果";
			// Toast.makeText(AskActivity.this, strInfo,
			// Toast.LENGTH_LONG).show();
		}
	}
}
