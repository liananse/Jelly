package com.medialab.jelly.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class UImageManager {

	public final static int ORIENTATION_LEFT = 1;
	public final static int ORIENTATION_RIGHT = 3;
	public final static int ORIENTATION_BOTTOM = 6;
	public final static int ORIENTATION_TOP = 8;
	public final static int MAX_WITHD = 640;

	public static void WriteFileEx(String srcImagePath, String destImagePath)
	{

		ensureFileDir(destImagePath);
		try
		{
			// 获取源图片的大小
			Bitmap bm;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;

			bm = BitmapFactory.decodeFile(srcImagePath, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int destWidth = 0;
			int destHeight = 0;

			double ratio = 0.0;

			int maxLength = 640;

			double proportionate;
			if (srcWidth > srcHeight)
			{
				ratio = (double) srcWidth / maxLength;
				proportionate = (double) srcHeight / ratio;
				destWidth = maxLength;
				destHeight = (int) Math.rint(proportionate);
			}
			else
			{
				ratio = srcHeight / maxLength;
				proportionate = (double) srcWidth / ratio;
				destHeight = maxLength;
				destWidth = (int) Math.rint(proportionate);
			}

			if (ratio < 1.2)
			{
				File fileSrc = new File(srcImagePath);
				File fileDest = new File(destImagePath);
				fileSrc.renameTo(fileDest);
				return;
			}
			// 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			newOpts.inPurgeable = true;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			Bitmap destBm = BitmapFactory.decodeFile(srcImagePath, newOpts);

			if (destBm == null)
			{

			}
			else
			{
				File destFile = new File(destImagePath);

				// 创建文件输出流
				OutputStream os = new FileOutputStream(destFile);
				// 存储
				destBm.compress(CompressFormat.JPEG, 100, os);
				// 关闭流
				os.close();
				destBm.recycle();
				UImageManager.delFile(srcImagePath);
			}
		}
		catch (Exception e)
		{

		}
	}
	
	public static void ensureFileDir(String path)
	{
		File destFile = new File(path);
		if (destFile.exists() == false)
		{
			try
			{
				java.io.File fileDir = new java.io.File(destFile.getParent());
				if (!fileDir.exists())
					fileDir.mkdirs();
				destFile.createNewFile();
			}
			catch (Exception e)
			{
			}

		}
	}
	
	public static void delFile(String strFileName)
	{
		try
		{
			File myFile = new File(strFileName);
			if (myFile.exists())
			{
				myFile.delete();
			}
		}
		catch (Exception e)
		{

		}
	}
	
	/**
	 * 保存图片
	 * @author liananse
	 * @param bitmap
	 * @param path
	 * @return
	 * 2013-8-22
	 */
	public static boolean saveBtimapToFile(Bitmap bitmap, String path)
	{
		boolean result = false;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try
		{
			bos = new BufferedOutputStream(new FileOutputStream(path));
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			bis = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
			int b = -1;
			while ((b = bis.read()) != -1)
			{
				bos.write(b);
			}
			result = true;
		}
		catch (Exception e)
		{
			result = false;
			try
			{
				bos.close();
				bis.close();
			}
			catch (Exception e1)
			{
				result = false;
			}
		}
		finally
		{
			try
			{
				bos.close();
				bis.close();
			}
			catch (Exception e)
			{
				result = false;
			}
		}
		return result;
	}
}
