package com.medialab.jelly.http;

public class HFileModel {
	
	String key;

	public HFileModel(String source)
	{
		int index = source.indexOf(":");
		key = source.substring(0, index);
		value = source.substring(index + 1);
	}
	
	public HFileModel(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	String value;

}
