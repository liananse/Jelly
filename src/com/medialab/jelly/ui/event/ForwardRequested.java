package com.medialab.jelly.ui.event;

import com.medialab.jelly.model.Question;

public class ForwardRequested {
	public final Question question;

	public ForwardRequested(Question paramQuestion) {
		this.question = paramQuestion;
	}
}
