package com.gameengine.audio;

public class AudioException extends Exception {

	private static final long serialVersionUID = 4416980551787130264L;
	
	public AudioException() {}
	
	public AudioException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AudioException(String message) {
		super(message);
	}
	
	public AudioException(Throwable cause) {
		super(cause);
	}

}
