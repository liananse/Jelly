package com.medialab.jelly.ui.event;

import android.net.Uri;

public class LinkChoosenEvent {
	public Uri linkPicked;

	public LinkChoosenEvent(Uri paramUri) {
		this.linkPicked = paramUri;
	}
}
