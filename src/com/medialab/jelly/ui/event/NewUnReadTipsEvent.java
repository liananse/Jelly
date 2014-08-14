package com.medialab.jelly.ui.event;

public class NewUnReadTipsEvent {
	public final boolean shouldBadge;

	public NewUnReadTipsEvent(boolean paramBoolean) {
		this.shouldBadge = paramBoolean;
	}
}
