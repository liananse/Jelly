package com.medialab.jelly.ui.event;

import com.medialab.jelly.data.image.ComposingImage;

public class StartDrawingOnImage {
	public final ComposingImage image;

	public StartDrawingOnImage(ComposingImage paramComposingImage) {
		this.image = paramComposingImage;
	}
}
