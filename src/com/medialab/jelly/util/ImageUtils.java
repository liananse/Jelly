package com.medialab.jelly.util;

import android.graphics.Bitmap;
import android.view.View;

import com.baidu.mapapi.map.MapView;

public class ImageUtils {

	// 将view保存成图片
	public static Bitmap convertViewToBitmap(View view) {
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	public static Bitmap getViewBitmap(MapView v) {

		v.clearFocus();
		v.setPressed(false);

		// 能画缓存就返回false
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = null;
		while (cacheBitmap == null) {
			cacheBitmap = v.getDrawingCache();
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

}
