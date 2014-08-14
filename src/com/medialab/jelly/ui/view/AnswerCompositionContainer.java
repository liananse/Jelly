package com.medialab.jelly.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class AnswerCompositionContainer extends FrameLayout {
	public AnswerCompositionContainer(Context paramContext) {
		super(paramContext);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		super.onMeasure(paramInt1, paramInt2);
	}

	public void setMinimumHeight(int paramInt) {
		super.setMinimumHeight(paramInt);
		int i = getChildCount();
		for (int j = 0;; j++) {
			if (j >= i)
				return;
			View localView = getChildAt(j);
			if (localView == null)
				continue;
			localView.setMinimumHeight(paramInt);
		}
	}
}