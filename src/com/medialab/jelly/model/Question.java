package com.medialab.jelly.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.Spannable;
import android.text.SpannableString;

import com.google.common.primitives.Longs;

public class Question implements Serializable {

	/**
	 * Question Id
	 */
	public long qid;
	
	/**
	 * Question lastModified Time
	 */
	public long lastModified;
	
	/**
	 * Question link
	 */
	public String link = "";

	/**
	 * QuestionImage include picName, picWidth, picHeight
	 */
	public QuestionImage questionImage;

	/**
	 * Unused
	 */
	public boolean resolved = false;

	/**
	 * 0 QusetionStatusType.UNRESOLVED DEFAULT 1 QuestionStatusType.RESOLVED
	 */
	public int status;

	/**
	 * Question content
	 */
	public String text;

	/**
	 * Question Owner
	 */
	public JellyUser user;
	
	/**
	 * Question Came From Description
	 */
	public String context;
	
	/**
	 * Question Create Time
	 */
	public long createdAt;

	/**
	 * Question go-between User
	 */
	public JellyUser middleUser;

	public List<Answer> answers = null;

	public QuestionMetaData metaData;
	
	/**
	 * latitude 纬度
	 */
	public double x;
	
	/**
	 * longitude 经度
	 */
	public double y;

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

	public boolean addAnswers(List<Answer> paramList) {
		Collections.sort(paramList, Collections.reverseOrder());
		return this.answers.addAll(paramList);
	}

	public void clearAnswers() {
		this.answers.clear();
	}

	public int compareTo(Object paramObject) {
		int i;
		if ((paramObject != null) && ((paramObject instanceof Question)))
			i = Longs.compare(this.createdAt,
					((Question) paramObject).createdAt);
		else
			i = -1;
		return i;
	}

	public boolean equals(Object paramObject) {
		boolean result;
		if ((paramObject == null) || (!(paramObject instanceof Question))
				|| (!(this.qid == ((Question) paramObject).qid)))
			result = false;
		else
			result = true;
		return result;
	}

	public Answer getAnswerById(Long paramAnswerId) {
		Answer localAnswer = null;
		if (this.answers != null) {
			for (int i = 0; i < this.answers.size(); i++) {
				if (answers.get(i).aid == paramAnswerId) {
					localAnswer = answers.get(i);
					break;
				}
			}
		}
		return localAnswer;
	}

	public List<Answer> getAnswers() {
		
		if (this.answers == null) {
			return new ArrayList<Answer>();
		}
		return this.answers;
	}

	public void setAnswers(List<Answer> paramList) {
		this.answers = paramList;
	}

	public void sortAnswers() {
		Collections.sort(this.answers, Collections.reverseOrder());
	}

	public String toString() {
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = String.valueOf(this.qid);
		return String.format("Question: %s", arrayOfObject);
	}

}
