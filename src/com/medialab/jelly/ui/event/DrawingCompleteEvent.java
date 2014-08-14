package com.medialab.jelly.ui.event;

import com.medialab.jelly.data.image.ComposingImage;

public class DrawingCompleteEvent {
	public final ComposingImage image;

	public DrawingCompleteEvent(ComposingImage paramComposingImage) {
		this.image = paramComposingImage;
	}
}
