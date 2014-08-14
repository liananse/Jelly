package com.medialab.jelly.ui.event;

import com.medialab.jelly.model.Notification;

public class ThankYouNotificationSelected {
	private final Notification notification;

	public ThankYouNotificationSelected(Notification paramNotification) {
		this.notification = paramNotification;
	}

	public Notification getNotification() {
		return this.notification;
	}
}
