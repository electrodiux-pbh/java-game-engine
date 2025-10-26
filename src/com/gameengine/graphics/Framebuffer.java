package com.gameengine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Framebuffer {

	private int fboID = 0;
	private Texture texture;
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @throws java.lang.IllegalStateException
	 */
	public Framebuffer(int width, int height) {
		fboID = GL30.glGenFramebuffers();
		bind();
		
		texture = new Texture(width, height, GL11.GL_RGB);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, texture.getTextureID(), 0);
		
		int rboID = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboID);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rboID);
		
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			unbind();
			destroy();
			throw new IllegalStateException("Frame buffer is not complete");
		}
		unbind();
	}

	public int getFboID() {
		return fboID;
	}
	
	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
	}
	
	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public int getTextureID() {
		return texture.getTextureID();
	}
	
	public void destroy() {
		GL30.glDeleteFramebuffers(fboID);
	}
	
	public int getWidth() {
		return texture.getWidth();
	}
	
	public int getHeight() {
		return texture.getHeight();
	}
	
}
