package com.gameengine.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

public class Texture {
	
	private int textureID;
	private int width, height;
	
	public Texture(@NotNull String path) throws IOException {
		
		this.textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		STBImage.nstbi_set_flip_vertically_on_load(GLFW.GLFW_TRUE);
		ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 0);
		
		if(image != null) {
			this.width = width.get(0);
			this.height = height.get(0);
			
			int type = channels.get(0) == 3 ? GL11.GL_RGB : channels.get(0) == 4 ? GL11.GL_RGBA : -1;
			if(type == -1)
				throw new IOException("Unknown number of channels '" + channels.get(0) + "'");
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, type, width.get(), height.get(), 0, type, GL11.GL_UNSIGNED_BYTE, image);
		} else {
			throw new IOException("Could not load the texture image '" + path + "'");
		}
		
		Texture.freeMemory(image);
	}
	
	public Texture() {
		this.textureID = -1;
		this.width = -1;
		this.height = -1;
	}
	
	public Texture(int width, int height, int format) {
		this.width = width;
		this.height = height;
		
		this.textureID = GL11.glGenTextures();
		
		bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, 0);
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public int getTextureID() {
		return textureID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setWidth(int width) {
		this.width = width;
		bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WIDTH, this.width);
		unbind();
	}
	
	public void setHeight(int height) {
		this.height = height;
		bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_HEIGHT, this.height);
		unbind();
	}
	
	public void setBounds(int width, int height) {
		this.width = width;
		this.height = height;
		bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WIDTH, this.width);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_HEIGHT, this.height);
		unbind();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Texture))
			return false;
		
		Texture objTex = (Texture) obj;
		return objTex.width == this.width && objTex.height == this.height && objTex.textureID == this.textureID;
	}
	
	@NotNull
	public static ByteBuffer loadImage(@NotNull String path) throws IOException {
		return loadImage(path, BufferUtils.createIntBuffer(1), BufferUtils.createIntBuffer(1), BufferUtils.createIntBuffer(1));
	}
	
	@NotNull
	public static ByteBuffer loadImage(@NotNull String path, IntBuffer width, IntBuffer height, IntBuffer channels) throws IOException {
		STBImage.nstbi_set_flip_vertically_on_load(GLFW.GLFW_FALSE);
		ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 0);
		
		if(image != null) {
			return image;
		} else {
			throw new IOException("Could not load the texture image '" + path + "'");
		}
	}
	
	public static GLFWImage loadImageToGLFW(@NotNull String path) throws IOException {
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer c = BufferUtils.createIntBuffer(1);
		
		ByteBuffer buff = loadImage(path, w, h, c);
		GLFWImage img = new GLFWImage(buff);
		img.pixels(buff);
		img.width(w.get());
		img.height(h.get());
		Texture.freeMemory(buff);
		
		return img;
	}
	
	public static void freeMemory(@NotNull ByteBuffer buffer) {
		STBImage.stbi_image_free(buffer);
	}
	
}
