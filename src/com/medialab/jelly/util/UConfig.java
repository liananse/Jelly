package com.medialab.jelly.util;

/**
 * @author liananse
 * 
 *         配置
 * 
 */
public class UConfig {

	// 服务器请求地址

	// 正式机
	public static final String SERVER_HOST = "";
	public static final String IMAGE_SERVER = "";

	// 我的电脑
	// public static final String SERVER_HOST =
	// "";

	// 测试机
//	 public static final String SERVER_HOST =
//	 "";
//	 public static final String IMAGE_SERVER =
//	 "";

	// 请求连接
	/**
	 * 注册 POST
	 */
	public static final String REGISTER_URL = SERVER_HOST + "user/register";
	/**
	 * 登录 POST/GET mobile(手机号) digest（客户端MD5加密的密码，密码+时间戳的MD5加密） time（时间戳） x
	 * 位置坐标x y 位置坐标y
	 */
	public static final String LOGIN_URL = SERVER_HOST + "user/login";

	/**
	 * 上传设备信息接口 POST/GET
	 */
	public static final String UPLOAD_DEVICE_INFO_URL = SERVER_HOST
			+ "deviceInfo";

	/**
	 * 举报问题接口 POST/GET
	 */
	public static final String QUESTION_REPORT_URL = SERVER_HOST
			+ "question/report";

	/**
	 * 推送设置接口 POST
	 */
	public static final String PUSH_SETTING_URL = SERVER_HOST
			+ "user/pushSetting";

	/**
	 * 屏蔽用户接口 POST/GET
	 */
	public static final String BLOCK_USER_URL = SERVER_HOST + "user/black/add";

	/**
	 * 发送验证码接口 POST/GET mobile（手机号） type 0-注册 1-登录
	 */
	public static final String SEND_CODE_URL = SERVER_HOST
			+ "user/mobile/sendCode";
	
	/**
	 * 验证手机接口 POST/GET mobile (手机号) code (验证码)
	 */
	public static final String CHECK_CODE_URL = SERVER_HOST
			+ "user/mobile/checkCode";

	/**
	 * 启用通讯录接口 POST mobileList:
	 * 通讯录好友的号码和名称，格式：[{"mobile":"111111","name":"ddddd"},{}] mobileListMd5
	 * 通讯录的MD5值
	 */
	public static final String MOBILE_LIST_URL = SERVER_HOST
			+ "user/mobile/mobileList";

	/**
	 * 用户注销接口 POST/GET
	 */
	public static final String LOGOUT_URL = SERVER_HOST + "user/logout";

	/**
	 * 用户密码重置接口 POST/GET mobile 手机 code 验证码 password 密码
	 */
	public static final String RESET_PASSWORD_URL = SERVER_HOST
			+ "user/password/reset";

	/**
	 * 发布问题接口 POST/GET
	 * title
	 * link
	 * questionPic
	 * x
	 * y
	 */
	public static final String PUT_QUESTION_URL = SERVER_HOST + "question/put";

	/**
	 * 拉取问题列表
	 */
	public static final String QUESTION_LIST_URL = SERVER_HOST
			+ "question/list";

	/**
	 * 问题点赞/取消赞
	 */
	public static final String QUESTION_GOOD_URL = SERVER_HOST
			+ "question/good";

	/**
	 * 问题收藏/取消收藏
	 */
	public static final String QUESTION_STAR_URL = SERVER_HOST
			+ "question/star";

	/**
	 * 答案点赞/取消赞
	 */
	public static final String ANSWER_GOOD_URL = SERVER_HOST + "answer/good";

	/**
	 * 答案举报
	 */
	public static final String ANSWER_REPORT_URL = SERVER_HOST
			+ "answer/report";

	/**
	 * 不喜欢一个答案
	 */
	public static final String ANSWER_DISLIKE_URL = SERVER_HOST
			+ "answer/dislike";

	/**
	 * 不喜欢一个问题
	 */
	public static final String QUESTION_DISLIKE_URL = SERVER_HOST
			+ "question/dislike";

	/**
	 * 动态列表
	 */
	public static final String USER_ACTIVITY_LIST_URL = SERVER_HOST
			+ "user/activity/list";

	/**
	 * 发送感谢卡
	 */
	public static final String ANSWER_THANKYOU_URL = SERVER_HOST
			+ "answer/thankYou";

	/**
	 * 回答问题
	 */
	public static final String ANSWER_PUT_URL = SERVER_HOST + "answer/put";

	/**
	 * 获取感谢卡数量
	 */
	public static final String ANSWER_THANKYOUCOUNT = SERVER_HOST
			+ "answer/thankYouCount";

	/**
	 * 问题解决
	 */
	public static final String QUESTION_CLOSE_URL = SERVER_HOST
			+ "question/close";

	/**
	 * 图片搜索
	 * keyWord 关键字
	 * count 数量
	 */
	public static final String IMAGE_SEARCH = SERVER_HOST + "image/search";

	/**
	 * 根据qid获取问题 qid
	 */
	public static final String QUESTION_GET_URL = SERVER_HOST + "question/get";
	
	/**
	 * 心跳接口
	 */
	public static final String HEART_BEAT_URL = SERVER_HOST + "user/heartbeat";
	
	/**
	 * 新朋友列表
	 */
	public static final String NEW_FRIEND_URL = SERVER_HOST + "user/friend/newFriend";
	
	/**
	 * 一度朋友列表
	 */
	public static final String FRIEND_LIST_URL = SERVER_HOST + "user/friend/list";
	
	/**
	 * 审核好友申请
	 * fid 好友的Id
	 * type: 1--通过 0--拒绝
	 */
	public static final String FRIEND_AUDIT_URL = SERVER_HOST + "user/friend/audit";
	
	/**
	 * 
	 * 添加好友申请
	 * fid 好友的Id
	 */
	public static final String FRIEND_ADD_URL = SERVER_HOST + "user/friend/add";

}
