package com.gameengine.graphics;

import java.io.PrintStream;

public class ShaderException extends Exception {

	private static final long serialVersionUID = 285254459959460242L;

	private String shaderInfo = "";
	
	public ShaderException() {}
	
	public ShaderException(String message, String shaderInfo, Throwable cause) {
		super(message, cause);
		this.shaderInfo = shaderInfo;
	}
	
	public ShaderException(String message, String shaderInfo) {
		super(message);
		this.shaderInfo = shaderInfo;
	}
	
	public ShaderException(String shaderInfo) {
		this.shaderInfo = shaderInfo;
	}
	
	public ShaderException(Throwable cause) {
		super(cause);
	}

	public String getShaderInfo() {
		return shaderInfo;
	}
	
	public void printStackTrace(PrintStream stream) {
		super.printStackTrace(stream);
		
		String[] info = shaderInfo.split("\n");
		for(String line : info) {
			stream.println(line);
		}
	}
	
}
