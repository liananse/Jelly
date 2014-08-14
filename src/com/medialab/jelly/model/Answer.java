package com.medialab.jelly.model;

import java.io.Serializable;

import android.text.Spannable;
import android.text.SpannableString;

import com.google.common.primitives.Longs;

public class Answer implements Serializable {

	public AnswerImage answerImage;
	public String context;
	public long createdAt;
	public long aid;
	public String link;
	public AnswerMetaData metaData;
	public long questionId;
	public String text;
	public JellyUser user;

	// private Spannable pregeneratedLinkText;

	// private Spannable pregeneratedText;

	/**
	 * @return Spannable link
	 */
	public Spannable getPregeneratedLinkText() {
		return new SpannableString(link);
	}

	// public void setPregeneratedLinkText(Spannable pregeneratedLinkText) {
	// this.pregeneratedLinkText = pregeneratedLinkText;
	// }

	/**
	 * @return Spannable text
	 */
	public Spannable getPregeneratedText() {
		return new SpannableString(text);
	}

	// public void setPregeneratedText(Spannable pregeneratedText) {
	// this.pregeneratedText = pregeneratedText;
	// }

	private boolean hasNullScore() {
		boolean result;
		if ((this.metaData != null) && (this.metaData.score.longValue() >= 0L))
			result = false;
		else
			result = true;
		return result;
	}

	public int compareTo(Answer paramAnswer) {
		int i;
		if ((!hasNullScore()) || (paramAnswer.hasNullScore())) {
			if ((!hasNullScore()) || (!paramAnswer.hasNullScore())) {
				if ((hasNullScore()) || (!paramAnswer.hasNullScore()))
					i = Longs.compare(this.metaData.score.longValue(),
							paramAnswer.metaData.score.longValue());
				else
					i = 1;
			} else
				i = 0;
		} else
			i = -1;
		return i;
	}

	public boolean equals(Object paramObject) {
		boolean result;
		if ((paramObject == null) || (!(paramObject instanceof Answer))
				|| (!(this.aid == ((Answer) paramObject).aid)))
			result = false;
		else
			result = true;
		return result;
	}

	public boolean hasAnswerImage() {
		boolean result;
		if (this.answerImage != null && this.answerImage.picName != null && !this.answerImage.picName.equals("")) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public String toString() {
		Object[] arrayOfObject = new Object[3];
		arrayOfObject[0] = this.context;
		arrayOfObject[1] = this.aid + "";
		arrayOfObject[2] = this.text;
		return String.format("Context: %s, Id: %s, text: %s", arrayOfObject);
	}

}
