package com.medialab.jelly.controller;

import android.app.Activity;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class StarfishScreenUtils {
//	private static final float CARD_RATIO_FAT = 1.043046F;
//	private static final float CARD_RATIO_POINTS_PER_RATIO = 0.298013F;
	private static final float CARD_RATIO_TALL = 1.175497F;
	private static final float FATTEST_RATIO = 1.333333F;
//	private static final float TALLEST_RATIO = 1.777778F;
//	private static final int TOP_NAV_FATTEST_DP = 47;
	private static final float TOP_NAV_POINTS_PER_RATIO_DP = 58.500004F;
//	private static final int TOP_NAV_TALLEST_DP = 73;
	private final float topNavFattestPx;
	private final float topNavPointsPerRatio;
	private final WindowManager windowManager;

	public StarfishScreenUtils(Activity paramActivity,
			WindowManager paramWindowManager) {
		this.windowManager = paramWindowManager;
		this.topNavPointsPerRatio = TypedValue.applyDimension(1,
				TOP_NAV_POINTS_PER_RATIO_DP, paramActivity.getResources()
						.getDisplayMetrics());
		this.topNavFattestPx = TypedValue.applyDimension(1, 47.0F,
				paramActivity.getResources().getDisplayMetrics());
	}

	public int getDisplayCardHeight(int paramInt) {
		Display localDisplay = this.windowManager.getDefaultDisplay();
		Point localPoint = new Point();
		localDisplay.getSize(localPoint);
		return (int) (CARD_RATIO_TALL * paramInt);
	}

	public int getTopNavHeight() {
		Display localDisplay = this.windowManager.getDefaultDisplay();
		Point localPoint = new Point();
		localDisplay.getSize(localPoint);

		return (int) ((localPoint.y / localPoint.x - FATTEST_RATIO)
				* this.topNavPointsPerRatio + this.topNavFattestPx);
	}
}
