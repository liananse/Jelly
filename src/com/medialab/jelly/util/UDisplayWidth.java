package com.medialab.jelly.util;

import android.content.Context;

public class UDisplayWidth {

	public static int PIC_WIDTH_120 = 120;
	public static int PIC_WIDTH_160 = 160;
	public static int PIC_WIDTH_220 = 220;
	public static int PIC_WIDTH_320 = 320;
	public static int PIC_WIDTH_480 = 480;
	public static int PIC_WIDTH_680 = 680;

	/**
	 * 
	 * 获取请求小头像的尺寸
	 * 
	 * @author liananse
	 * @param context
	 * @return 2013-8-6
	 */
	public static int getSmallHeadPicWidth(Context context) {
		return 120;
	}

	/**
	 * 获取大头像的尺寸
	 * 
	 * @author liananse
	 * @param context
	 * @return 2013-8-6
	 */
	public static int getLargeHeadPicWidth(Context context) {
		if (context.getResources().getDisplayMetrics().widthPixels <= 480) {
			return 120;
		} else if (context.getResources().getDisplayMetrics().widthPixels <= 720) {
			return 160;
		} else {
			return 220;
		}
	}

	/**
	 * 返回活动封面的图片请求尺寸
	 * 
	 * @author liananse
	 * @param context
	 * @return 2013-8-6
	 */
	public static String getPosterPicWidth(Context context, String picName) {
		if (context.getResources().getDisplayMetrics().widthPixels <= 320) {
			return UConfig.IMAGE_SERVER + 320 + "/" + picName;
		} else if (context.getResources().getDisplayMetrics().widthPixels <= 480) {
			return UConfig.IMAGE_SERVER + 480 + "/" + picName;
		} else if (context.getResources().getDisplayMetrics().widthPixels <= 720) {
			return UConfig.IMAGE_SERVER + 680 + "/" + picName;
		} else {
			return UConfig.IMAGE_SERVER + 680 + "/" + picName;
		}
	}

	public static String getPicUrlByWidth(int width, String picName) {
		return UConfig.IMAGE_SERVER + width + "/" + picName;
	}

	public static String getBaiDuMapAPIUrl(double longitude, double latitude,
			int width, int height) {
		return "http://api.map.baidu.com/staticimage?center=" + longitude + ","
				+ latitude + "&width=" + width + "&height=" + height
				+ "&zoom=16&scale=2&markers=" + longitude + ","
						+ latitude;
	}

	/**
	 * 以往活动图片的尺寸
	 * 
	 * @author liananse
	 * @param context
	 * @return 2013-8-6
	 */
	public static int getPastEventPicWidth(Context context) {
		if (context.getResources().getDisplayMetrics().widthPixels <= 480) {
			return 120;
		} else if (context.getResources().getDisplayMetrics().widthPixels <= 720) {
			return 160;
		} else {
			return 220;
		}
	}

	public static int getEventDetailPicWidth(Context context) {
		if (context.getResources().getDisplayMetrics().widthPixels <= 720) {
			return 120;
		} else {
			return 160;
		}
	}

	public static int getPhotoPicWidth(Context context) {
		if (context.getResources().getDisplayMetrics().widthPixels <= 480) {
			return 160;
		} else if (context.getResources().getDisplayMetrics().widthPixels <= 720) {
			return 220;
		} else {
			return 320;
		}
	}

}
