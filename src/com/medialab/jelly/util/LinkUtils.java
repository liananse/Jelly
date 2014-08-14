package com.medialab.jelly.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Patterns;

public class LinkUtils {
	public static boolean isUrl(String paramString) {
		return Patterns.WEB_URL.matcher(paramString).matches();
	}

	public static Spannable noUnderlineLinksFromString(
			CharSequence paramCharSequence) {
		SpannableString localSpannableString;
		if ((paramCharSequence == null) || (paramCharSequence.length() <= 0)) {
			localSpannableString = new SpannableString("");
		} else {
			localSpannableString = new SpannableString(paramCharSequence);
			Linkify.addLinks(localSpannableString, 1);
			stripUnderlines(localSpannableString);
		}
		return localSpannableString;
	}

	private static void stripUnderlines(Spannable paramSpannable) {
		URLSpan[] arrayOfURLSpan = null;
		int i = 0;
		if (paramSpannable != null) {
			arrayOfURLSpan = (URLSpan[]) paramSpannable.getSpans(0,
					paramSpannable.length(), URLSpan.class);
			i = arrayOfURLSpan.length;
		}
		for (int k = 0;; k++) {
			if (k >= i)
				return;
			URLSpan localURLSpan = arrayOfURLSpan[k];
			int j = paramSpannable.getSpanStart(localURLSpan);
			int m = paramSpannable.getSpanEnd(localURLSpan);
			paramSpannable.removeSpan(localURLSpan);
			paramSpannable.setSpan(
					new URLSpanNoUnderline(localURLSpan.getURL()), j, m, 0);
		}
	}

	public static class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String paramString) {
			super(paramString);
		}

		public void updateDrawState(TextPaint paramTextPaint) {
			super.updateDrawState(paramTextPaint);
			paramTextPaint.setUnderlineText(false);
		}
	}
}
