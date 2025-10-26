package com.gameengine.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;

public class Icon {
	
	private GLFWImage image;
	
	public Icon(@NotNull String path) throws IOException {
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		STBImage.nstbi_set_flip_vertically_on_load(GLFW.GLFW_FALSE);
		ByteBuffer data = STBImage.stbi_load(path, width, height, channels, 0);
		
		if(data == null)
			throw new IOException("An error ocurred while trying to load the icon");
		
		image = GLFWImage.malloc();
		image.set(width.get(), height.get(), data);
		
	}
	
	public GLFWImage getImage() {
		return image;
	}

	public int getWidth() {
		return image.width();
	}

	public int getHeight() {
		return image.height();
	}
	
}
