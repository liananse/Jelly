package com.medialab.jelly.ui.event;

public class UserActionFailureNoEvent {
	public final String displayMessage;
	public final boolean probablyNetworkRelated;
	public final Throwable throwable;

	public UserActionFailureNoEvent(String paramString, boolean paramBoolean,
			Throwable paramThrowable) {
		this.displayMessage = paramString;
		this.probablyNetworkRelated = paramBoolean;
		this.throwable = paramThrowable;
	}
}
