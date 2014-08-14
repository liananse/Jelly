package com.medialab.jelly.ui.event;

import com.medialab.jelly.model.Answer;
import com.medialab.jelly.model.Question;

public class ViewAnswerSelected {
	public final Answer answer;
	public final Question question;

	public ViewAnswerSelected(Question paramQuestion, Answer paramAnswer) {
		this.answer = paramAnswer;
		this.question = paramQuestion;
	}
}
