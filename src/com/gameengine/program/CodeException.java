package com.gameengine.program;

public class CodeException extends Exception {

	private static final long serialVersionUID = 5700354615239854513L;

	public CodeException() {}
	
	public CodeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CodeException(String message) {
		super(message);
	}
	
	public CodeException(Throwable cause) {
		super(cause);
	}

}
