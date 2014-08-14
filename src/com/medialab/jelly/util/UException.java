package com.medialab.jelly.util;

/**
 * @author zenghui
 * 2013-06-28
 */
public class UException extends Exception {

	private static final long serialVersionUID = 1L;
	private int statusCode = -1;

	public UException(String msg) {
		super(msg);
	}

	public UException(Exception cause) {
		super(cause);
	}

	public UException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;

	}

	public UException(String msg, Exception cause) {
		super(msg, cause);
	}

	public UException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;

	}

	public int getStatusCode() {
		return this.statusCode;
	}
}