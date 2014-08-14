package com.medialab.jelly.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class UTools {
	
	public static final class OS
	{
		/**
		 * check if network is available
		 * 
		 * @author zenghui
		 * 2013-06-28
		 * @param Context
		 * 
		 * @return boolean 
		 *         true available
		 *         false unavailable
		 * 
		 */
		public static boolean isNetworkAvailable(Context context)
		{
			boolean result = true;

			ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

			if (manager != null)
			{
				NetworkInfo networkinfo = manager.getActiveNetworkInfo();

				if (networkinfo == null || !networkinfo.isAvailable())
				{
					result = false;
				}
			}
			return result;
		}

		/**
		 * check if WIFI is available
		 * 
		 * @author zenghui
		 * 2013-2-4
		 * 
		 * @param Context
		 * 
		 * @return boolean
		 * 		   true available
		 * 		   false unavailable
		 */
		public static boolean isWifiAvailable(Context mContext)
		{
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI"))
			{
				return true;
			}
			return false;
		}

		/**
		 * @author zenghui
		 * 
		 * @date 2013-1-25
		 * 
		 * @return String
		 */
		public static String getPhoneCode(Context context)
		{
			String phonecode = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			if (phonecode == null || phonecode.equals("null"))
			{
				phonecode = "";
			}
			return phonecode;
		}

		/**
		 * get default country
		 * 
		 * @author zenghui
		 * @date 2013-6-28
		 * 
		 * @return String
		 * 			such as tw cn hk and sg
		 */
		public static String getCountryCode()
		{
			return Locale.getDefault().getCountry().toLowerCase();
		}

		/**
		 * get default language
		 * 
		 * @author zenghui
		 * @date2013-6-28
		 * 
		 * @return String
		 * 			such as zh
		 */
		public static String getLanguage()
		{
			return Locale.getDefault().getLanguage().toLowerCase();
		}
		
		/**
		 * get android os version
		 * 
		 * @author zenghui
		 * @date2013-1-25
		 * 
		 * @return String
		 * 			such as 4.1.1
		 */
		public static String getOSVersion()
		{
			return Build.VERSION.RELEASE;
		}

		/**
		 * get application version
		 * 
		 * @author zenghui
		 * @date 2013-6-28
		 * 
		 * @return String
		 */
		public static String getAppVersion(Context context)
		{
			String version = "0";
			String packageName = null;
			PackageInfo pf = null;
			try
			{
				packageName = context.getPackageName();
				pf = context.getPackageManager().getPackageInfo(packageName, 0);
				version = String.valueOf(pf.versionName);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return version;
		}
		
		/**
		 * get application name
		 * 
		 * @author zenghui
		 * @date 2013-6-28
		 * 
		 * @return String
		 */
		public static String getAppName()
		{
			return UConstants.APP_NAME;
		}
		
		public static String getAppChannel() {
			return UConstants.APP_CHANNEL;
		}
		
		public static String getNetWorkType(Context context)
		{
	        try {  
	            final ConnectivityManager connectivityManager = (ConnectivityManager) context  
	                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
	            final NetworkInfo mobNetInfoActivity = connectivityManager  
	                    .getActiveNetworkInfo();  
	            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {  
	                // 注意�?��  
	                // NetworkInfo 为空或�?不可以用的时候正常情况应该是当前没有可用网络�? 
	                // 但是有些电信机器，仍可以正常联网�? 
	                // �?��当成net网络处理依然尝试连接网络�? 
	                // （然后在socket中捕捉异常，进行二次判断与用户提示）�? 
	                return "2G";  
	            } else {  
	                // NetworkInfo不为null�?��判断是网络类�? 
	                int netType = mobNetInfoActivity.getType();  
	                if (netType == ConnectivityManager.TYPE_WIFI) {  
	                    // wifi net处理  
	                    return "wifi";  
	                } else if (netType == ConnectivityManager.TYPE_MOBILE) {  
	                    // 注意二：  
	                    // 判断是否电信wap:  
	                    // 不要通过getExtraInfo获取接入点名称来判断类型�? 
	                    // 因为通过目前电信多种机型测试发现接入点名称大都为#777或�?null�? 
	                    // 电信机器wap接入点中要比移动联�?wap接入点多设置�?��用户名和密码,  
	                    // �?��可以通过这个进行判断�? 
	  
	                    boolean is3G = isFastMobileNetwork(context);  
	  
	                    final Cursor c = context.getContentResolver().query(  
	                            PREFERRED_APN_URI, null, null, null, null);  
	                    if (c != null) {  
	                        c.moveToFirst();  
	                        final String user = c.getString(c  
	                                .getColumnIndex("user"));  
	                        if (!TextUtils.isEmpty(user)) {  
	                            if (user.startsWith(CTWAP)) {  
	                                return is3G ? "3G" : "2G";  
	                            } else if (user.startsWith(CTNET)) {  
	                                return is3G ? "3G" : "2G";  
	                            }  
	                        }  
	                    }  
	                    c.close();  
	  
	                    // 注意三：  
	                    // 判断是移动联通wap:  
	                    // 其实还有�?��方法通过getString(c.getColumnIndex("proxy")获取代理ip  
	                    // 来判断接入点�?0.0.0.172就是移动联�?wap�?0.0.0.200就是电信wap，但�? 
	                    // 实际�?��中并不是�?��机器都能获取到接入点代理信息，例如魅族M9 �?.2）等...  
	                    // �?��采用getExtraInfo获取接入点名字进行判�? 
	  
	                    String netMode = mobNetInfoActivity.getExtraInfo();  
	                    Log.i("", "==================netmode:" + netMode);  
	                    if (netMode != null) {  
	                        // 通过apn名称判断是否是联通和移动wap  
	                        netMode = netMode.toLowerCase();  
	  
	                        if (netMode.equals(CMWAP)) {  
	                            return is3G ? "3G" : "2G";  
	                        } else if (netMode.equals(CMNET)) {  
	                            return is3G ? "3G" : "2G";  
	                        } else if (netMode.equals(NET_3G)  
	                                || netMode.equals(UNINET)) {  
	                            return is3G ? "3G" : "2G";  
	                        } else if (netMode.equals(WAP_3G)  
	                                || netMode.equals(UNIWAP)) {  
	                            return is3G ? "3G" : "2G";  
	                        }  
	                    }  
	                }  
	            }  
	  
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	            return "2G";  
	        }  
	  
	        return "2G";  
		}
		
		public static final String CTWAP = "ctwap";  
	    public static final String CTNET = "ctnet";  
	    public static final String CMWAP = "cmwap";  
	    public static final String CMNET = "cmnet";  
	    public static final String NET_3G = "3gnet";  
	    public static final String WAP_3G = "3gwap";  
	    public static final String UNIWAP = "uniwap";  
	    public static final String UNINET = "uninet";  
	    
		public static Uri PREFERRED_APN_URI = Uri  
	            .parse("content://telephony/carriers/preferapn");  
		
		private static boolean isFastMobileNetwork(Context context) {  
	        TelephonyManager telephonyManager = (TelephonyManager) context  
	                .getSystemService(Context.TELEPHONY_SERVICE);  
	  
	        switch (telephonyManager.getNetworkType()) {  
	        case TelephonyManager.NETWORK_TYPE_1xRTT:  
	            return false; // ~ 50-100 kbps  
	        case TelephonyManager.NETWORK_TYPE_CDMA:  
	            return false; // ~ 14-64 kbps  
	        case TelephonyManager.NETWORK_TYPE_EDGE:  
	            return false; // ~ 50-100 kbps  
	        case TelephonyManager.NETWORK_TYPE_EVDO_0:  
	            return true; // ~ 400-1000 kbps  
	        case TelephonyManager.NETWORK_TYPE_EVDO_A:  
	            return true; // ~ 600-1400 kbps  
	        case TelephonyManager.NETWORK_TYPE_GPRS:  
	            return false; // ~ 100 kbps  
	        case TelephonyManager.NETWORK_TYPE_HSDPA:  
	            return true; // ~ 2-14 Mbps  
	        case TelephonyManager.NETWORK_TYPE_HSPA:  
	            return true; // ~ 700-1700 kbps  
	        case TelephonyManager.NETWORK_TYPE_HSUPA:  
	            return true; // ~ 1-23 Mbps  
	        case TelephonyManager.NETWORK_TYPE_UMTS:  
	            return true; // ~ 400-7000 kbps  
	        case TelephonyManager.NETWORK_TYPE_EHRPD:  
	            return true; // ~ 1-2 Mbps  
	        case TelephonyManager.NETWORK_TYPE_EVDO_B:  
	            return true; // ~ 5 Mbps  
	        case TelephonyManager.NETWORK_TYPE_HSPAP:  
	            return true; // ~ 10-20 Mbps  
	        case TelephonyManager.NETWORK_TYPE_IDEN:  
	            return false; // ~25 kbps  
	        case TelephonyManager.NETWORK_TYPE_LTE:  
	            return true; // ~ 10+ Mbps  
	        case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
	            return false;  
	        default:  
	            return false;  
	  
	        }  
	    }
		/**
		 * @author liananse
		 * @param context
		 * @return
		 * 2013-7-7
		 */
		public static String getAccessToken(Context context)
		{
			return UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME).getString(UConstants.SELF_ACCESS_TOKEN, "");
		}
		
		public static String getUserId(Context context)
		{
			return UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME).getString(UConstants.SELF_USER_ID, "");
		}
		
	}
	
	public static final class Storage
	{
		/**
		 * @author liananse
		 * @param context
		 * @param prefsName
		 * @return
		 * 2013-7-1
		 */
		public static Editor getSharedPreEditor(Context context, String prefsName)
		{
			if(TextUtils.isEmpty(prefsName))
			{
				prefsName = UConstants.BASE_PREFS_NAME;
			}
			SharedPreferences settings = context.getSharedPreferences(prefsName, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    
		    return editor;
		}
		
		/**
		 * @author liananse
		 * @param context
		 * @param prefsName
		 * @return
		 * 2013-7-1
		 */
		public static SharedPreferences getSharedPreferences(Context context, String prefsName)
		{
			if (TextUtils.isEmpty(prefsName))
			{
				prefsName = UConstants.BASE_PREFS_NAME;
			}
			return context.getSharedPreferences(prefsName, 0);
		}
		
		public static byte[] readBinary(Context context, String path)
		{
			if (TextUtils.isEmpty(path))
			{
				return null;
			}
			DataInputStream input = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (path.startsWith("/"))
			{
				try
				{
					File file = new File(path);
					if (!file.exists())
					{
						return null;
					}
					byte[] buffer = new byte[256];
					input = new DataInputStream(new FileInputStream(file));
					int iCount = 0;
					while (true)
					{
						iCount = input.read(buffer);
						if (iCount > 0)
						{
							out.write(buffer, 0, iCount);
						}
						else
						{
							break;
						}
					}
					return out.toByteArray();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if (out != null)
						{
							out.close();
							out = null;
						}
						if (input != null)
						{
							input.close();
							input = null;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				return null;
			}
			else
			{
				try
				{
					AssetManager am = context.getAssets();
					if (TextUtils.isEmpty(path))
					{
						return null;
					}
					input = new DataInputStream(am.open(path));
					byte[] buffer = new byte[256];
					int iCount = 0;
					while (true)
					{
						iCount = input.read(buffer);
						if (iCount > 0)
						{
							out.write(buffer, 0, iCount);
						}
						else
						{
							break;
						}
					}
					byte data[] = out.toByteArray();
					return data;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if (out != null)
						{
							out.close();
							out = null;
						}
						if (input != null)
						{
							input.close();
							input = null;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				return null;
			}
		}
		
		public static String getHeadPicImagePath()
		{
			String path = null;
			
			String dir = UDataStorage.getExternalStoragePublicDirectory(UDataStorage.Dir_Pictures) + "/jelly/temp/";
			path = dir + "head_pic.jpg";
			UDataStorage.ensureDir(dir);

			return path;
		}
		
		public static String getHeadPicSmallImagePath()
		{
			String path = null;
			
			String dir = UDataStorage.getExternalStoragePublicDirectory(UDataStorage.Dir_Pictures) + "/jelly/temp/";
			path = dir + "head_pic_1080.jpg";
			UDataStorage.ensureDir(dir);

			return path;
		}
		
		public static String getQuestionPicSaveImagePath()
		{
			String path = null;
			
			String dir = UDataStorage.getExternalStoragePublicDirectory(UDataStorage.Dir_Pictures) + "/jelly/save/";
//			path = dir + System.currentTimeMillis() + ".jpg";
			path = dir + "answer_temp.jpg";
			
			UDataStorage.ensureDir(dir);
			
			return path;
		}
		
		public static String getAlbumImagePath()
		{
			String path = null;
			
			String dir = UDataStorage.getExternalStoragePublicDirectory("DCIM/") + "/Camera/";
			path = dir + System.currentTimeMillis() + ".jpg";
			
			UDataStorage.ensureDir(dir);
			
			return path;
		}
		
	}
	
	public static final class UI
	{
		public static Integer screenwidth = Integer.valueOf(480);
		public static Integer screenHeight = Integer.valueOf(800);

		/**
		 * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
		 */
		public static int dip2px(Context context, float dpValue)
		{
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dpValue * scale + 0.5f);
		}

		/**
		 * 根据手机的分辨率�?px(像素) 的单�?转成�?dp
		 */
		public static int px2dip(Context context, float pxValue)
		{
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}

		/**
		 * 根据手机的分辨率从sp 的单�?转成�?px(像素)
		 */
		public static float sp2px(Context context, float dpValue)
		{
			final float scale = context.getResources().getDisplayMetrics().scaledDensity;
			return (int) (dpValue * scale + 0.5f);
		}

		/**
		 * 根据手机的分辨率�?px(像素) 的单�?转成�?sp
		 */
		public static float px2sp(Context context, float pxValue)
		{
			final float scale = context.getResources().getDisplayMetrics().scaledDensity;
			return (int) (pxValue / scale + 0.5f);
		}

		/**
		 * ui适配，根据高
		 * 
		 * @param context
		 * @param v
		 * @param originalWidth
		 *            切图宽像素数，如100.0
		 * @param originalHeight
		 *            切图高像素数，如100.0
		 * @param uiWidth
		 *            设计图高像素数，�?60.0
		 *            void
		 */
		public static void fitViewByHeight(Context context, View v, double originalWidth, double originalHeight, double uiHeight)
		{
			LayoutParams lp = v.getLayoutParams();
			double bestRateByScreenHeight = originalHeight / uiHeight;
			double screenHeight = context.getResources().getDisplayMetrics().heightPixels;

			double fitHeight = screenHeight * bestRateByScreenHeight;
			double fitWidth = fitHeight * originalWidth / originalHeight;

			lp.height = (int) fitHeight;
			lp.width = (int) fitWidth;
			v.setLayoutParams(lp);
		}

		/**
		 * ui适配，根据宽
		 * 
		 * @param context
		 * @param v
		 * @param originalWidth
		 *            切图宽像素数，如100.0
		 * @param originalHeight
		 *            切图高像素数，如100.0
		 * @param uiWidth
		 *            设计图宽像素数，�?40.0
		 *            void
		 */
		public static void fitViewByWidth(Context context, View v, double originalWidth, double originalHeight, double uiWidth)
		{
			if (originalWidth == 0 || originalHeight == 0 || uiWidth == 0)
				return;

			LayoutParams lp = v.getLayoutParams();
			double screenWidth = context.getResources().getDisplayMetrics().widthPixels;
			double imageRate = screenWidth / uiWidth;

			double fitWidth = originalWidth * imageRate;
			double fitHeight = originalHeight * imageRate;

			lp.height = (int) fitHeight;
			lp.width = (int) fitWidth;
			v.setLayoutParams(lp);
		}

		public static int fitPixels(Context context, int pixels, double uiWidth)
		{
			double screenWidth = context.getResources().getDisplayMetrics().widthPixels;
			double imageRate = screenWidth / uiWidth;
			double fitPixels = pixels * imageRate;

			return (int) (fitPixels);
		}

		/**
		 * 根据ui设计图获取设备最佳高�?
		 * 
		 * @param originalWidth
		 *            元素�?
		 * @param originalHeight
		 *            元素�?
		 * @param uiWidth
		 *            �?��ui设计图宽
		 * @return
		 */
		public static URectangle getPerfectWidth(Context context, Integer originalWidth, Integer originalHeight, Integer uiWidth)
		{
			URectangle rect = new URectangle();
			if (context == null)
			{
				rect.setWidth(Float.valueOf(screenwidth.intValue() * originalWidth.floatValue() / uiWidth.floatValue()).intValue());
				rect.setHeight(Float.valueOf(originalHeight.floatValue() * rect.getWidth().floatValue() / originalWidth.floatValue()).intValue());
			}
			else
			{
				Integer screenwidth = Integer.valueOf(context.getResources().getDisplayMetrics().widthPixels);
				rect.setWidth(Float.valueOf(screenwidth.intValue() * originalWidth.floatValue() / uiWidth.floatValue()).intValue());
				rect.setHeight(Float.valueOf(originalHeight.floatValue() * rect.getWidth().floatValue() / originalWidth.floatValue()).intValue());
			}
			return rect;
		}
		
		
		public static Bitmap getFitBitmap(Context context, int id, double uiWidth)
		{
			Bitmap resBitmap = null;
			Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), id);
			
			double screenWidth = context.getResources().getDisplayMetrics().widthPixels;
			double imageRate = screenWidth / uiWidth;

			if (mBitmap != null)
			{
				Matrix matrix = new Matrix();
				float scaleWidth = (float) imageRate;
				float scaleHeight = (float) imageRate;
				matrix.postScale(scaleWidth, scaleHeight);
				Bitmap newbmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
				resBitmap = newbmp;
			}
			return resBitmap;
		}
	}
	
	public static final class IO
	{

		/**
		 * 
		 * @param context
		 * @param path
		 * @return
		 */
		public static String getStringFromTxt(Context context, String path)
		{
			String res = null;
			InputStream fin = null;
			try
			{
				if (path.startsWith("/"))
				{
					fin = new FileInputStream(path);
				}
				else
				{
					fin = context.getResources().getAssets().open(path);
				}
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				res = EncodingUtils.getString(buffer, "UTF-8");
			}
			catch (IOException e)
			{
				Log.e("file", "read txt fail: ", e);
			}
			finally
			{
				if (fin != null)
				{
					try
					{
						fin.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			return res;
		}
		
		/**
		 * @author liananse
		 * @param context
		 * @param normalName
		 * @param pressName
		 * @return
		 * 2013-7-18
		 */
		public static StateListDrawable newDrawable(Context context, String normalName, String pressName)
		{
			StateListDrawable bg = new StateListDrawable();
			
			Drawable normal = null;
			Drawable press = null;
			
			AssetManager asm = context.getResources().getAssets();
			try {
				InputStream normalStream = asm.open(normalName);
				normal = Drawable.createFromStream(normalStream, null);
				
				InputStream pressStream = asm.open(pressName);
				press = Drawable.createFromStream(pressStream, null);
				
				bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, press);  
		        // View.ENABLED_STATE_SET  
		        bg.addState(new int[] { android.R.attr.state_enabled }, normal);  
		        // View.EMPTY_STATE_SET  
		        bg.addState(new int[] {}, normal);  
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return bg;
		}
		
		/**
		 * 从assets获取drawable
		 * @author liananse
		 * @param context
		 * @param name
		 * @return
		 * 2013-7-30
		 */
		public static Drawable getDrawableFromAssets(Context context, String name)
		{
			Drawable result = null;
			AssetManager asm = context.getResources().getAssets();
			
			try {
				InputStream inputStream = asm.open(name);
				result = Drawable.createFromStream(inputStream, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
			
		}
	}
	
	public static final class activityhelper
	{
		private static List<Activity> activityList = new ArrayList<Activity>();

		public static int getStackSize()
		{
			int size = activityList.size();
			return size;
		}

		public static boolean remove(Activity activity)
		{
			return activityList.remove(activity);
		}

		public static void add(Activity activity)
		{
			try
			{
				activityList.add(activity);
			}
			catch (Exception e)
			{

			}
		}

		public static boolean has(Activity activity)
		{
			return activityList.contains(activity);
		}

		public static void clearAllBut(Activity activity)
		{
			int index = activityList.indexOf(activity);

			if (index + 1 > 0)
			{
				activityList.remove(activity);
			}

			int len = activityList.size();
			for (int i = len - 1; i >= 0; i--)
			{
				activityList.remove(i).finish();
			}

			activityList.add(activity);
		}
		
		/**
		 * 清除className之后的activity,clearItself为true就包括自�?
		 * @author Kin
		 * @create 2013-9-27 下午5:32:31
		 * @param className
		 * @param clearItself void
		 */
		public static void clearAt(String className,boolean clearItself)
		{
			int index = 0;
			int len = activityList.size();
			//查找该activity
			for(int i=len-1;i>=0;i--)
			{
				String name = activityList.get(i).getClass().getSimpleName();
				if(name.equals(className))
				{
					index = i;
					break;
				}
			}
			//清除这个activity之后的activity
			for(int i=len-1;i>index;i--)
			{
				activityList.remove(i).finish();
			}
			//如果包括清除自己的话，也�?��清除
			if(clearItself)
			{
				len = activityList.size();
				activityList.remove(len-1).finish();
			}
		}

		public static void exit()
		{
			int i = 0;
			for (Activity activity : activityList)
			{
				if (i != 0)
					activity.finish();
				i++;
				
			}
			activityList.clear();
		}

		public static void storeThis(Activity activity, int numOfInstance)
		{
			int num = 0;
			int size = activityList.size();
			for (int i = 0; i < size; i++)
			{
				Activity _activity = activityList.get(i);
				if (_activity.getClass() == activity.getClass())
				{
					num++;
				}
			}

			if (num > numOfInstance)
			{
				int toDelete = num - numOfInstance;
				int lastIndex = activityList.lastIndexOf(activity);
				for (int i = lastIndex - 1; i >= 0; i--)
				{
					Activity _activity = activityList.get(i);
					if (_activity.getClass() == activity.getClass())
					{
						try
						{
							if (toDelete > 0)
							{
								activityList.remove(i).finish();
								toDelete--;
							}
						}
						catch (Exception e)
						{
						}
					}
				}
			}
		}
	}

}
