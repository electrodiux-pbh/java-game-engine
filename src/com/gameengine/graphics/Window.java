package com.gameengine.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.system.MemoryUtil;

import com.gameengine.GameEngineManager;
import com.gameengine.data.PropertyStorage;

public class Window {
	
	private int width, height;
	private String title;
	private long glfwWindow;
	private Framebuffer framebuffer;
	
	private boolean fullScreen;
	
	/**
	 * @param properties
	 * @throws java.lang.IllegalStateException
	 */
	public Window(@NotNull PropertyStorage properties) {
		width = 640;
		height = 360;
		fullScreen = true;
		title = "";
		
		try {
			Object w = properties.getProperty("screen-width");
			if(w != null)
				width = Integer.parseInt(w.toString());
		} catch (NumberFormatException e) { }
		try {
			Object h = properties.getProperty("screen-height");
			if(h != null)
				height = Integer.parseInt(h.toString());
		} catch (NumberFormatException e) { }
		
		init();
	}
	
	/**
	 * Initialize all important things for the Window
	 * 
	 * @throws java.lang.IllegalStateException
	 */
	private void init() {
		
		if(!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, fullScreen ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		
		glfwWindow = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		if(glfwWindow == MemoryUtil.NULL)
			throw new IllegalStateException("Failed to create the GLFW window");
		
		GLFW.glfwMakeContextCurrent(glfwWindow);
		GLFW.glfwSwapInterval(1);
		
		GLFW.glfwSetWindowSizeCallback(glfwWindow, (window, newWidth, newHeight) -> {
			this.width = newWidth;
			this.height = newHeight;
		});
		
		setIcon(GameEngineManager.sourceManager().getIcon());
		
		GLFW.glfwShowWindow(glfwWindow);
		
	}
	
	public void destroy() {
		GLFW.glfwDestroyWindow(glfwWindow);
	}
	
	/**
	 * Calls the method {@link org.lwjgl.glfw.GLFW#glfwWindowHint(int, int)}
	 * 
	 * @param hint
	 * @param value
	 * @see org.lwjgl.glfw.GLFW
	 */
	public void setWindowAttrib(int attrib, int value) {
		GLFW.glfwSetWindowAttrib(glfwWindow, attrib, value);
	}
	
	public int getWindowAttrib(int attrib) {
		return GLFW.glfwGetWindowAttrib(glfwWindow, attrib);
	}
	
	public void setResizable(boolean resizable) {
		setWindowAttrib(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
	}

	public void setBounds(int width, int height) {
		GLFW.glfwSetWindowSize(glfwWindow, width, height);
		this.width = width;
		this.height = height;
	}
	
	public void setIcon(@NotNull Icon icon) {
		if(icon != null) {
			Buffer imagebf = GLFWImage.malloc(1);
			imagebf.put(0, icon.getImage());
			GLFW.glfwSetWindowIcon(glfwWindow, imagebf);
		} else {
			GLFW.glfwSetWindowIcon(glfwWindow, null);
		}
	}
	
	public void setUndecorated(boolean value) {
		setWindowAttrib(GLFW.GLFW_DECORATED, value ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
	}
	
	public boolean isDecorated() {
		return getWindowAttrib(GLFW.GLFW_DECORATED) == GLFW.GLFW_TRUE;
	}

	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(glfwWindow, title);
		this.title = title;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;

		if(fullScreen)
			GLFW.glfwMaximizeWindow(glfwWindow);
	}
	
	public void setOpacity(float opacity) {
		if(opacity < 0 || opacity > 1)
			return;
		GLFW.glfwSetWindowOpacity(glfwWindow, opacity);
	}
	
	public long getWindowID() {
		return glfwWindow;
	}
	
	public Framebuffer getFramebuffer() {
		return framebuffer;
	}
	
	public void setFramebuffer(@NotNull Framebuffer buffer) {
		if(this.framebuffer != null)
			this.framebuffer.destroy();
		this.framebuffer = buffer;
	}
	
}
