package com.medialab.jelly.data.image;

import android.graphics.Bitmap;

public class ComposingImage {
	public final Bitmap bitmap;
	public final int rotation;
	public final Source source;

	public ComposingImage(Bitmap paramBitmap, int paramInt, Source paramSource) {
		this.bitmap = paramBitmap;
		this.rotation = paramInt;
		this.source = paramSource;
	}

	public static enum Source {
		CAMERA, DRAW
	}
}
