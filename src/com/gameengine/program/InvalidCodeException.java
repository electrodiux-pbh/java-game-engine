package com.gameengine.program;

public class InvalidCodeException extends CodeException {

	private static final long serialVersionUID = 6756380766332664723L;

	public InvalidCodeException() {}
	
	public InvalidCodeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidCodeException(String message) {
		super(message);
	}
	
	public InvalidCodeException(Throwable cause) {
		super(cause);
	}

}
