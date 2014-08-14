package com.medialab.jelly.model;

import java.io.Serializable;

public class Notification implements Serializable {

	public String content;
	public JellyUser middleUser;
	public long createdAt;
	public Question question;
	public int type;
	public JellyUser friend;
	public long aid;
	public int activityId;
	public int readState;
	public ThankYou thanksCard;

	public ThankYou getThanksCard() {
		return thanksCard;
	}

	public void setThanksCard(ThankYou thanksCard) {
		this.thanksCard = thanksCard;
	}

	public int getReadState() {
		return readState;
	}

	public void setReadState(int readState) {
		this.readState = readState;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public JellyUser getMiddleUser() {
		return middleUser;
	}

	public void setMiddleUser(JellyUser middleUser) {
		this.middleUser = middleUser;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public JellyUser getFriend() {
		return friend;
	}

	public void setFriend(JellyUser friend) {
		this.friend = friend;
	}

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

}
