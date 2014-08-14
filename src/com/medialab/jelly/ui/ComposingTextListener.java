package com.medialab.jelly.ui;

import android.net.Uri;

public abstract interface ComposingTextListener {
	public abstract void hasLink(Uri paramUri);

	public abstract void hasText(boolean paramBoolean);
}
