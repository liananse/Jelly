package com.medialab.jelly.model;

public class Contact {
	
	private String name;
	private String mobile;

	@Override
	public String toString() {
		return "Contact [name=" + name + ", mobile=" + mobile + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
