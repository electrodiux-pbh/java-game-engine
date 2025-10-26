package com.gameengine.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class Keyboard {
	
	private static Keyboard keyboard = new Keyboard();
	
	private boolean keyPressed[] = new boolean[360];
	
	private boolean clear;
	
	private Keyboard() {
		clear = true;
	}
	
	public static Keyboard get() {
		return keyboard;
	}
	
	public static void configureKeyboard(@NotNull Window window) { }
	
	public static void keyCallBack(long window, int key, int scancode, int action, int mods) {
		if(key >= get().keyPressed.length || key < 0)
			return;
		
		if(action == GLFW.GLFW_PRESS) {
			get().keyPressed[key] = true;
			get().clear = false;
		} else if (action == GLFW.GLFW_RELEASE) {
			get().keyPressed[key] = false;
		}
	}
	
	public static void clear() {
		if(!get().clear) {
			for(int i = 0; i < get().keyPressed.length; i++) {
				get().keyPressed[i] = false;
			}
			get().clear = true;
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return if is pressed
	 * @see org.lwjgl.glfw.GLFW constants
	 */
	public static boolean isKeyPressed(int key) {
		if(key >= get().keyPressed.length || key < 0)
			return false;
		return get().keyPressed[key];
	}
	
}
