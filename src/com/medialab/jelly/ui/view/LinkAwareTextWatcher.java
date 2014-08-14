package com.medialab.jelly.ui.view;

import java.util.Iterator;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.common.base.Splitter;
import com.medialab.jelly.model.RemoteImage;
import com.medialab.jelly.util.LinkUtils;

public class LinkAwareTextWatcher implements TextWatcher {
	boolean doUrlSplitting = false;
	private final HasLinkProvider hasLinkProvider;
	private boolean hasText = false;
	private final Listener listener;
	private final TextView mainText;

	public LinkAwareTextWatcher(HasLinkProvider paramHasLinkProvider,
			TextView paramTextView, Listener paramListener) {
		this.hasLinkProvider = paramHasLinkProvider;
		this.mainText = paramTextView;
		this.listener = paramListener;
	}

	public final void afterTextChanged(Editable paramEditable) {

		boolean bool = false;
		Object localObject3;
		Iterator localIterator;
		Object localObject2;

		String url = "";

		int urlIndex = 0;
		if ((!this.hasLinkProvider.currentlyHasLink()) && (this.doUrlSplitting)) {
			this.doUrlSplitting = false;
			localObject3 = Splitter.on(" ").split(paramEditable.toString());
			localIterator = ((Iterable) localObject3).iterator();
			while (localIterator.hasNext()) {
				localObject2 = (String) localIterator.next();

				urlIndex = urlIndex + ((String) localObject2).length();

				if (LinkUtils.isUrl((String) localObject2)) {
					this.listener
							.setLink(new RemoteImage((String) localObject2));
					url = (String) localObject2;
					break;
				}
				urlIndex++;
			}

			if (url.equals("")) {
				url = "no_url";
			}
		}

		int j = this.mainText.getSelectionStart();

		if (!url.equals("") && !url.equals("no_url")) {

			String newStr = paramEditable.toString().replace(url, "");
			this.mainText.setText(newStr);

			if (j < urlIndex) {
				Selection.setSelection(this.mainText.getEditableText(), j);
			} else {
				Selection.setSelection(this.mainText.getEditableText(),
						Math.max(0, j - (url.length())));
			}
		}

		this.listener.setProgress(paramEditable.toString().length());

		int i = 0;
		i = paramEditable.toString().length();
		this.listener.setProgress(i);
		if (((i > 0) && (!this.hasText)) || ((i <= 0) && (this.hasText))) {
			if (i > 0)
				bool = true;
			this.hasText = bool;
			if (this.listener != null) {
				this.listener.hasText(this.hasText);
			}
		}

	}

	public void beforeTextChanged(CharSequence paramCharSequence,
			int paramInt1, int paramInt2, int paramInt3) {
	}

	public void onTextChanged(CharSequence paramCharSequence, int paramInt1,
			int paramInt2, int paramInt3) {
		String str = paramCharSequence.toString();
		if ((str != null)
				&& (str.length() > 0)
				&& (str.charAt(Math.max(0, -1 + (paramInt1 + paramInt3))) == ' '))
			this.doUrlSplitting = true;
	}

	public static abstract interface HasLinkProvider {
		public abstract boolean currentlyHasLink();
	}

	public static abstract interface Listener {
		public abstract void hasText(boolean paramBoolean);

		public abstract void setLink(RemoteImage paramRemoteImage);

		public abstract void setProgress(int paramInt);
	}
}
