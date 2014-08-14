package com.medialab.jelly.ui.event;

public class UserActionFailure extends UserActionFailureNoEvent {
	public final String event;

	public UserActionFailure(String paramString1,
			String paramString2, boolean paramBoolean, Throwable paramThrowable) {
		super(paramString2, paramBoolean, paramThrowable);
		this.event = paramString1;
	}
}