package com.medialab.jelly.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

	public static void hideKeyboard(Context paramContext, View paramView) {
		
		InputMethodManager inputMethodManager = ((InputMethodManager) paramContext
				.getSystemService("input_method"));
		
		inputMethodManager.hideSoftInputFromWindow(
				paramView.getWindowToken(), 0);
	}

	public static void showKeyboard(Context paramContext, View paramView) {
		InputMethodManager inputMethodManager = ((InputMethodManager) paramContext
				.getSystemService("input_method"));
		
		inputMethodManager.showSoftInput(paramView, 1);
	}
}
