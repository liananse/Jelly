package com.medialab.jelly.model;

import java.io.Serializable;

import android.net.Uri;

public class RemoteImage implements Serializable{
	public final Uri uri;

	public RemoteImage(String paramString) {
		this.uri = Uri.parse(paramString);
	}

	public String toString() {
		return this.uri.toString();
	}
}
