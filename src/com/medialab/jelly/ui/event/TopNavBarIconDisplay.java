package com.medialab.jelly.ui.event;

public class TopNavBarIconDisplay extends TopNavBarEvent {
	public final boolean showRightIcon;

	public TopNavBarIconDisplay(String paramString, boolean paramBoolean) {
		super(paramString);
		this.showRightIcon = paramBoolean;
	}
}