package com.gameengine.util;

public class IllegalProjectException extends Exception {

	private static final long serialVersionUID = -7341374382622665846L;

	
	public IllegalProjectException() {}
	
	public IllegalProjectException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public IllegalProjectException(String message) {
		super(message);
	}
	
	public IllegalProjectException(Throwable cause) {
		super(cause);
	}
	
}
