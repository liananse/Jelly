package com.medialab.jelly.model;

import java.io.Serializable;

public class PushSetting implements Serializable {

	private String pushSwitch;
	private String soundSwitch;

	public String toString() {
		Object[] arrayOfObject = new Object[5];
		arrayOfObject[0] = this.pushSwitch;
		arrayOfObject[1] = this.soundSwitch;
		return String
				.format("pushSwitch: %s,soundSwitch: %s",
						arrayOfObject);
	}

	public String getPushSwitch() {
		return pushSwitch;
	}

	public void setPushSwitch(String pushSwitch) {
		this.pushSwitch = pushSwitch;
	}

	public String getSoundSwitch() {
		return soundSwitch;
	}

	public void setSoundSwitch(String soundSwitch) {
		this.soundSwitch = soundSwitch;
	}
}
