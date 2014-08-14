package com.medialab.jelly.ui.event;

import com.medialab.jelly.data.image.ComposingImage;

public class QuestionComposed {
	public final ComposingImage image;
	public final CharSequence linkText;
	public final CharSequence text;

	public QuestionComposed(ComposingImage paramComposingImage,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2) {
		this.image = paramComposingImage;
		this.text = paramCharSequence1;
		this.linkText = paramCharSequence2;
	}
}
