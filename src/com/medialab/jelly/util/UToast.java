package com.medialab.jelly.util;

import android.content.Context;
import android.widget.Toast;

public class UToast {

	public static void showSocketTimeoutToast(Context mContext)
	{
		Toast.makeText(mContext, "获取数据超时喽:-( 稍后再试吧", Toast.LENGTH_SHORT).show();
	}
	
	public static void showConnectTimeoutToast(Context mContext)
	{
		Toast.makeText(mContext, "请求超时喽:-( 稍后再试吧", Toast.LENGTH_SHORT).show();
	}
	
	public static void showDataParsingError(Context mContext)
	{
		Toast.makeText(mContext, "数据解析出错喽:-(", Toast.LENGTH_SHORT).show();
	}
	
	public static void showShortToast(Context mContext, String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void showLongToast(Context mContext, String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	
	public static void showOnFail(Context mContext) {
		Toast.makeText(mContext, "返回错误", Toast.LENGTH_SHORT).show();
	}
}
