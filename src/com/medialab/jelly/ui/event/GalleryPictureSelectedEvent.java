package com.medialab.jelly.ui.event;

import android.net.Uri;

public class GalleryPictureSelectedEvent {
	public final Uri photoUri;

	public GalleryPictureSelectedEvent(Uri paramUri) {
		this.photoUri = paramUri;
	}
}
