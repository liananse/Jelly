package com.medialab.jelly.model;

import java.io.Serializable;

public class ThankYou implements Serializable {
	public JellyUser toUser;
	public JellyUser fromUser;
	public ThankCardTempletModel cardTemplet;
	public long createAt;
	public long qid;
	public String shareUrl;
	public long aid;
	public String thankYouCardId;
	public String shareTextWithUrl;
	public Question question;
}
