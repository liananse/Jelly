package com.medialab.jelly.ui.event;

import com.medialab.jelly.data.image.ComposingImage;

public class PictureConfirmedEvent {
	public final ComposingImage data;

	public PictureConfirmedEvent(ComposingImage paramComposingImage) {
		this.data = paramComposingImage;
	}
}
