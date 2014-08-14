package com.medialab.jelly.ui.event;

import com.medialab.jelly.data.image.ComposingImage;
import com.medialab.jelly.model.Question;

public class AnswerComposedNotSent {
	public final ComposingImage image;
	public final CharSequence linkText;
	public final Question question;
	public final CharSequence text;

	public AnswerComposedNotSent(Question paramQuestion,
			CharSequence paramCharSequence1, CharSequence paramCharSequence2,
			ComposingImage paramComposingImage) {
		this.question = paramQuestion;
		this.text = paramCharSequence1;
		this.image = paramComposingImage;
		this.linkText = paramCharSequence2;
	}
}
