package com.medialab.jelly.util;

import android.content.Context;
import android.widget.Toast;


public class UUtils {

	public static void showNetErrorToast(Context context)
	{
		try {
			Toast.makeText(context, "网络出错！", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void showTimeoutToast(Context context)
	{
		try {
			Toast.makeText(context, "网络连接超时！", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void showServerMessageToast(Context context,String text)
	{
		try {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
