package com.medialab.jelly.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Application;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FontManager {
	private static boolean useAvenir = true;
	private static Map<Weight, FontStuff> weightToValues = new ConcurrentHashMap();

	public static void initializeTypefaces(Application paramApplication) {

		if (UConstants.useFont) {
			try {
				try {
					weightToValues.put(
							Weight.LIGHT,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/avenir-light.ttf"), 0));
					weightToValues.put(
							Weight.BOOK,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/avenir-book.ttf"), 0));
					weightToValues.put(
							Weight.ROMAN,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/avenir-roman.ttf"), 0));
					weightToValues.put(
							Weight.MEDIUM,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/avenir-medium.ttf"), 0));
					weightToValues.put(
							Weight.HEAVY,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/avenir-heavy.ttf"), 0));
					weightToValues.put(
							Weight.BLACK,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/avenir-black.ttf"), 0));
					weightToValues.put(
							Weight.MISSION,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/mission_gothic.otf"), 0));
					weightToValues.put(
							Weight.MILI,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/mi_li_jian_zhi_ti.TTF"), 0));
					weightToValues.put(
							Weight.HUAKANG,
							new FontStuff(Typeface.createFromAsset(
									paramApplication.getAssets(),
									"fonts/hua_kang_shao_nv_ti.TTF"), 0));
					if (!useAvenir) {
						weightToValues
								.put(Weight.LIGHT, new FontStuff(null, 0));
						weightToValues.put(Weight.BOOK, new FontStuff(null, 0));
						weightToValues
								.put(Weight.ROMAN, new FontStuff(null, 0));
						weightToValues.put(Weight.MEDIUM,
								new FontStuff(null, 1));
						weightToValues
								.put(Weight.HEAVY, new FontStuff(null, 1));
						weightToValues
								.put(Weight.BLACK, new FontStuff(null, 1));
						weightToValues.put(Weight.MISSION, new FontStuff(null,
								1));
						weightToValues.put(Weight.MILI, new FontStuff(null, 1));
						weightToValues.put(Weight.HUAKANG, new FontStuff(null,
								1));
					}
					return;
				} catch (Throwable localThrowable) {
					while (true)
						useAvenir = false;
				}
			} finally {
			}
		}
	}

	public static void setTypeface(Button paramButton, Weight paramWeight) {

		if (UConstants.useFont) {
			FontStuff localFontStuff = (FontStuff) weightToValues
					.get(paramWeight);
			if (useAvenir)
				paramButton.setPaintFlags(Paint.SUBPIXEL_TEXT_FLAG
						| paramButton.getPaintFlags());
			paramButton.setTypeface(localFontStuff.typeface,
					localFontStuff.style);
		}
	}

	public static void setTypeface(TextView paramTextView, Weight paramWeight) {

		if (UConstants.useFont) {
			FontStuff localFontStuff = (FontStuff) weightToValues
					.get(paramWeight);

			// 更平滑的效果，抗锯齿
			if (useAvenir)
				paramTextView.setPaintFlags(Paint.SUBPIXEL_TEXT_FLAG
						| paramTextView.getPaintFlags());
			paramTextView.setTypeface(localFontStuff.typeface,
					localFontStuff.style);
		}
	}

	public static void setTypeface(EditText paramEditText, Weight paramWeight) {

		if (UConstants.useFont) {
			FontStuff localFontStuff = (FontStuff) weightToValues
					.get(paramWeight);

			// 更平滑的效果，抗锯齿
			if (useAvenir)
				paramEditText.setPaintFlags(Paint.SUBPIXEL_TEXT_FLAG
						| paramEditText.getPaintFlags());
			paramEditText.setTypeface(localFontStuff.typeface,
					localFontStuff.style);
		}
	}

	private static class FontStuff {
		public final int style;

		public final Typeface typeface;

		public FontStuff(Typeface paramTypeface, int paramInt) {
			this.typeface = paramTypeface;
			this.style = paramInt;
		}
	}

	public static enum Weight {
		LIGHT, BOOK, ROMAN, MEDIUM, HEAVY, BLACK, MISSION, MILI, HUAKANG
	}
}
