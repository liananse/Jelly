package com.medialab.jelly.model;

import java.io.Serializable;

public class AnswerMetaData implements Serializable {

	public int numberOfGoods;
	public int numberOfThanks;
	public String shareTextWithUrl;
	public String shareUrl;
	public int viewerGooded;
	public int viewerThanked;
	public final Long score = Long.valueOf(-1L);

	public boolean equals(Object paramObject) {
		boolean result = false;
		if ((paramObject != null) && ((paramObject instanceof AnswerMetaData))) {
			AnswerMetaData localAnswerMetaData = (AnswerMetaData) paramObject;
			if ((localAnswerMetaData.viewerGooded == this.viewerGooded)
					&& (localAnswerMetaData.viewerThanked == this.viewerThanked)
					&& (localAnswerMetaData.numberOfGoods == this.numberOfGoods)
					&& (localAnswerMetaData.numberOfThanks == this.numberOfThanks))
				result = true;
		}
		return result;
	}

	public void setValues(AnswerMetaData paramAnswerMetaData) {
		this.viewerGooded = paramAnswerMetaData.viewerGooded;
		this.viewerThanked = paramAnswerMetaData.viewerThanked;
		this.numberOfGoods = paramAnswerMetaData.numberOfGoods;
		this.numberOfThanks = paramAnswerMetaData.numberOfThanks;
	}

	public String toString() {
		Object[] arrayOfObject = new Object[4];
		arrayOfObject[0] = Integer.valueOf(this.numberOfGoods);
		arrayOfObject[1] = Integer.valueOf(this.numberOfThanks);
		arrayOfObject[2] = Integer.valueOf(this.viewerThanked);
		arrayOfObject[3] = Integer.valueOf(this.viewerGooded);
		return String
				.format("AnswerMetaData: NumGoods: %d NumThanks: %d ViwerThanked: %b ViewerGooded %b",
						arrayOfObject);
	}
}
