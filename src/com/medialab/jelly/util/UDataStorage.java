package com.medialab.jelly.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class UDataStorage 
{
	public final static String Dir_Music = "Music/";// - Media scanner classifies all media found here as user music.
	public final static String Dir_Podcasts = "Podcasts/";// - Media scanner classifies all media found here as a podcast.
	public final static String Dir_Ringtones = "Ringtones/";// - Media scanner classifies all media found here as a ringtone.
	public final static String Dir_Alarms = "Alarms/";// - Media scanner classifies all media found here as an alarm sound.
	public final static String Dir_Notifications = "Notifications/";// - Media scanner classifies all media found here as a notification sound.
	public final static String Dir_Pictures = "Pictures/";// - All photos (excluding those taken with the camera).
	public final static String Dir_Movies = "Movies/";// - All movies (excluding those taken with the camcorder).
	public final static String Dir_Download = "Download/";// - Miscellaneous downloads.

	public static boolean ensureDir(String dir)
	{
		boolean result = false;
		File file = new File(dir);
		if (!file.exists())
			result = file.mkdirs();
		return result;
	}

	/**
	 * 调用之前应判断externalStorageAvailable，externalStorageWriteable
	 * 
	 * @author liananse
	 * @create 2013-7-22 上午10:57:31
	 * @param type
	 *            DataStorage.Dir_Music/Dir_Podcasts...
	 * @return File
	 */
	public static File getExternalStoragePublicDirectory(String type)
	{
		File file = null;
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + type;
		file = new File(path);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	/**
	 * 调用之前应判断externalStorageAvailable，externalStorageWriteable
	 * 
	 * @author liananse
	 * @create 2013-7-22 上午11:24:00
	 * @param context
	 * @return File
	 */
	public static File getExternalCacheDir(Context context)
	{
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + context.getPackageName() + "/cache/";
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	public static boolean externalStorageAvailable()
	{
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			// We can read and write the media
			mExternalStorageAvailable = true;
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			// We can only read the media
			mExternalStorageAvailable = true;
		}
		else
		{
			// Something else is wrong. It may be one of many other states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = false;
		}
		return mExternalStorageAvailable;
	}

	public static boolean externalStorageWriteable()
	{
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			// We can read and write the media
			mExternalStorageWriteable = true;
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			// We can only read the media
			mExternalStorageWriteable = false;
		}
		else
		{
			// Something else is wrong. It may be one of many other states, but all we need
			// to know is we can neither read nor write
			mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}
}
