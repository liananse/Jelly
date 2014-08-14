package com.medialab.jelly.ui.event;

import com.medialab.jelly.model.Notification;

public class AnswerNotificationSelected {
	private final Notification notification;

	public AnswerNotificationSelected(Notification paramNotification) {
		this.notification = paramNotification;
	}

	public Notification getNotification() {
		return this.notification;
	}
}
