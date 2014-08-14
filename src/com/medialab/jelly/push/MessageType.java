package com.medialab.jelly.push;


public enum MessageType {
	
	STARRED_QUESTION_TYPE(2, "别人收藏了我的问题"),
	ANSWER_QUESTION_TYPE(3, "回答问题"),
	LIKE_QUESTION_TYPE(4, "赞了问题"),
	LIKE_ANSWER_TYPE(5, "赞了答案"),
	RECEIVE_THANKCARD_TYPE(6, "收到感谢卡"),
	ANSWER_STARRED_QUESTION_TYPE(7, "回答了收藏的问题"),
	FORWARD_QUESTION_TYPE(8, "转发了问题"),
	FORWARD_ANSWER_TYPE(9, "转发了答案"),
	NORMAL_NOTIFICATION_TYPE(10, "通知"),
	APPLY_NEW_FRIEND_TYPE(11, "收到添加朋友申请"),
	ACCEPT_NEW_FRIEND_TYPE(12, "审核朋友通过"),
	NEW_FRIEND_PUSH_TYPE(13, "通讯录匹配到新朋友");
	
	public static MessageType getMessageTypeByCode(int code) {
		if (code == STARRED_QUESTION_TYPE.getCode()) {
			return STARRED_QUESTION_TYPE;
		} else if (code == ANSWER_QUESTION_TYPE.getCode()) {
			return ANSWER_QUESTION_TYPE;
		} else if (code == LIKE_QUESTION_TYPE.getCode()) {
			return LIKE_QUESTION_TYPE;
		} else if (code == LIKE_ANSWER_TYPE.getCode()) {
			return LIKE_ANSWER_TYPE;
		} else if (code == RECEIVE_THANKCARD_TYPE.getCode()) {
			return RECEIVE_THANKCARD_TYPE;
		} else if (code == ANSWER_STARRED_QUESTION_TYPE.getCode()) {
			return ANSWER_STARRED_QUESTION_TYPE;
		} else if (code == FORWARD_QUESTION_TYPE.getCode()) {
			return FORWARD_QUESTION_TYPE;
		} else if (code == FORWARD_ANSWER_TYPE.getCode()) {
			return FORWARD_ANSWER_TYPE;
		} else if (code == NORMAL_NOTIFICATION_TYPE.getCode()) {
			return NORMAL_NOTIFICATION_TYPE;
		} else if (code == APPLY_NEW_FRIEND_TYPE.getCode()) {
			return APPLY_NEW_FRIEND_TYPE;
		} else if (code == ACCEPT_NEW_FRIEND_TYPE.getCode()) {
			return ACCEPT_NEW_FRIEND_TYPE;
		} else if (code == NEW_FRIEND_PUSH_TYPE.getCode()) {
			return NEW_FRIEND_PUSH_TYPE;
		} else {
			return null;
		}
	}
	
	private int code;
	private String name;

	private MessageType(int code, String name) {
		this.code = code;
		this.name = name;
	}
	
	/**
	 * @return the code
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("MessageType [code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
