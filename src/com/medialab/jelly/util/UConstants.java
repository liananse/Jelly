package com.medialab.jelly.util;

/**
 * 系统中用到的常量
 * 
 * @author zenghui 2013-07-01
 */
public class UConstants {
	
	public static final boolean isOfficial = true;

	// 应用名称
	public static final String APP_NAME = "";
	// 应用渠道
	public static final String APP_CHANNEL = "";

	// 一些开关 开始
	// 是否显示请求返回的信息
	public static final boolean isDataLoaderDebug = false;
	
	public static final boolean useFont = false;

	// 一些开关 结束
	// 请求返回正确
	public static int SUCCESS = 0;
	// 请求返回错误
	public static int FAILURE = 1;
	// 已失效，已过期
	public static int INVALID = 2;
	
	/**
	 * SharedPreferences name
	 */
	// base SharedPreference name if no others, use this
	public static final String BASE_PREFS_NAME = "base_prefs";

	/**
	 * SharedPreferences item
	 */
	/**
	 * 用户ID
	 */
	public static final String SELF_USER_ID = "self_user_id";
	/**
	 * accessToken
	 */
	public static final String SELF_ACCESS_TOKEN = "self_access_token";
	/**
	 * 首次登录
	 */
	public static final String FIRST_LOGIN = "first_login";
	
	/**
	 * location 城市
	 */
	public static final String LOCATION_CITY = "location_city";
	/**
	 * Latitude 纬度
	 */
	public static final String LOCATION_LATITUDE = "location_latitude";
	/**
	 * Longitude 经度
	 */
	public static final String LOCATION_LONGITUDE = "location_longitude";
	
	/**
	 * 问题的纬度值
	 */
	public static final String QUESTION_LOCATION_LATITUDE = "question_location_latitude";
	
	/**
	 * 问题的经度值
	 */
	public static final String QUESTION_LOCATION_LONGITUDE = "question_location_longitude";
	
	public static final String FRIENDS_NUM = "friends_num";
	
	public static final String UNLOCKED_FRIENDS_NUM = "unlocked_friends_num";
	
	public static final String INVITE_CARD_URL = "invite_card_url";
	
	public static final String OUT_OF_QUESTION_INVITE_CARD_URL = "out_of_question_invite_card_url";
	/**
	 * 本地最新的动态ID
	 */
	public static final String LAST_ACTIVITY_ID = "last_activity_id";
	
	public static final String BOTTOM_ACTIVITY_ID = "botton_activity_id";
	
	public static final String XIAOMI_REGID = "xiaomi_regid";
	
	public static final String HAS_NEW_ACTIVITY = "has_new_activity";
	
	public static final String LOCAL_QUESTION_LIST = "local_question_list";
	
	/**
	 * 有新的个人动态
	 */
	public static final String HAS_NEW_SELF_ACTIVITY = "has_new_self_activity";
	
	/**
	 * 新朋友列表增加
	 */
	public static final String HAS_NEW_FRIEND_ACTIVITY = "has_new_friend_activity";
	
	/**
	 * 一度朋友增加
	 */
	public static final String HAS_FRIEND_ACTIVITY = "has_friend_activity";
	
	/**
	 * 缓存的新朋友数
	 */
	public static final String LOCAL_NUM_OF_NEW_FRIEND = "local_num_of_new_friend";
	
	/**
	 * 缓存的朋友数
	 */
	public static final String LOCAL_NUM_OF_FRIEND = "local_num_of_friend";
	
	/**
	 * 是否是第一次收藏问题
	 */
	public static final String FIRST_FOLLOW_QUESTION = "first_follow_question";
	/**
	 * 上次心跳的时间
	 */
	public static final String LAST_VALID_TIME = "last_valid_time";
	
	public static final String FIRST_LOAD_QUESTION = "first_load_question";
	
	public static final int GETIMAGE = 3;
	public static final int CAPUTRE = 4;
	
	public static final String NOTICE_SOUNDS_SWITCH = "notice_sounds_switch";
	public static final String NOTICE_PUSH_SWITCH = "notice_push_switch";
	
	public static final String ON = "1";
	public static final String OFF = "0";
	
	public static final String FROM_WHERE = "from_where";
	public static final String FROM_NOTIFICATION = "from_notification";
}
