package com.medialab.jelly.resultmodel;

public class SearchImageResultModel{
	
	private String thumbURL;
	
	private String largeImageUrl;
	
	private int hasLarge;
	
	public String getThumbURL() {
		return thumbURL;
	}
	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}
	public String getLargeImageUrl() {
		return largeImageUrl;
	}
	public void setLargeImageUrl(String largeImageUrl) {
		this.largeImageUrl = largeImageUrl;
	}
	public int getHasLarge() {
		return hasLarge;
	}
	public void setHasLarge(int hasLarge) {
		this.hasLarge = hasLarge;
	}
}