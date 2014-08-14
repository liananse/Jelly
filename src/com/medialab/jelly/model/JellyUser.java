package com.medialab.jelly.model;

import java.io.Serializable;

public class JellyUser implements Serializable {

	private int uid;
	private String avatarName;
	private String accessToken;
	private String uidStr;
	private String nickName;
	private String mobile;
	private PushSetting pushSetting;

	public String getUidStr() {
		return uidStr;
	}

	public void setUidStr(String uidStr) {
		this.uidStr = uidStr;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public PushSetting getPushSetting() {
		return pushSetting;
	}

	public void setPushSetting(PushSetting pushSetting) {
		this.pushSetting = pushSetting;
	}


	public boolean equals(Object paramObject) {
		boolean bool = false;
		if ((paramObject != null) && ((paramObject instanceof JellyUser)))
			bool = ((JellyUser) paramObject).uid == (this.uid);
		return bool;
	}

	public String toString() {
		Object[] arrayOfObject = new Object[5];
		arrayOfObject[0] = this.nickName;
		arrayOfObject[1] = this.uid + "";
		arrayOfObject[2] = this.avatarName;
		arrayOfObject[3] = this.mobile;
		arrayOfObject[4] = this.accessToken;
		return String
				.format("Name: %s, Id: %s, avatarName: %s, mobile: %s, accessToken: %s",
						arrayOfObject);
	}
}
