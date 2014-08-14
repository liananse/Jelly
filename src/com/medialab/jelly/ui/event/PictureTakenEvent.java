package com.medialab.jelly.ui.event;

import com.medialab.jelly.data.image.ComposingImage;

public class PictureTakenEvent {
	public final ComposingImage data;

	public PictureTakenEvent(ComposingImage paramComposingImage) {
		this.data = paramComposingImage;
	}
}
