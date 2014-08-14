package com.medialab.jelly.ui.event;

import com.medialab.jelly.model.Notification;

public class StarredNotificationSelected {
	private final Notification notification;

	public StarredNotificationSelected(Notification paramNotification) {
		this.notification = paramNotification;
	}

	public Notification getNotification() {
		return this.notification;
	}
}
