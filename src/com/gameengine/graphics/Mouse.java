package com.gameengine.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import imgui.flag.ImGuiMouseCursor;
import imgui.internal.ImGui;

public final class Mouse {

	public static final int CURSOR_ARROW = ImGuiMouseCursor.Arrow;
	public static final int CURSOR_TEXT_INPUT = ImGuiMouseCursor.TextInput;
	public static final int CURSOR_RESIZE_ALL = ImGuiMouseCursor.ResizeAll;
	public static final int CURSOR_RESIZE_NS = ImGuiMouseCursor.ResizeNS;
	public static final int CURSOR_RESIZE_EW = ImGuiMouseCursor.ResizeEW;
	public static final int CURSOR_RESIZE_NESW = ImGuiMouseCursor.ResizeNESW;
	public static final int CURSOR_RESIZE_NWSE = ImGuiMouseCursor.ResizeNWSE;
	public static final int CURSOR_HAND = ImGuiMouseCursor.Hand;
	public static final int CURSOR_NOT_ALLOWED = ImGuiMouseCursor.NotAllowed;
	public static final int CURSOR_GRAB = 9;
	public static final int CURSOR_GRABBING = 10;
	
	public static final int AMMOUNT_OF_CURSORS = 11;
	
	private static Mouse mouse = new Mouse();
	
	private double scrollX, scrollY;
	private double xPos, yPos, lastX, lastY;
	private boolean buttonBuffer[] = new boolean[3];
	private boolean draggin;
	private boolean onScreen;
	private boolean clean;
	
	private Mouse() {
		setValuesAsDefault();
	}
	
	private void setValuesAsDefault() {
		this.scrollX = 0.0f;
		this.scrollY = 0.0f;
		this.xPos = 0.0f;
		this.yPos = 0.0f;
		this.lastX = 0.0f;
		this.lastX = 0.0f;
	}
	
	public static Mouse get() {
		return mouse;
	}
	
	public static void configureMouse(@NotNull Window window) {
		GLFW.glfwSetCursorPosCallback(window.getWindowID(), Mouse::mousePosCallBack);
		GLFW.glfwSetMouseButtonCallback(window.getWindowID(), Mouse::mouseButtonCallBack);
		GLFW.glfwSetScrollCallback(window.getWindowID(), Mouse::mouseScrollCallBack);
	}
	
	public static void mouseButtonCallBack(long window, int button, int action, int mods) {
		get().onScreen = true;
		if(button >= get().buttonBuffer.length || button < 0)
			return;
		if(action == GLFW.GLFW_PRESS) {
			get().buttonBuffer[button] = true;
			get().clean = false;
		} else if(action == GLFW.GLFW_RELEASE) {
			get().buttonBuffer[button] = false;
			get().draggin = false;
		}
	}
	
	public static void clear() {
		if(!get().clean) {
			for(int i = 0; i < get().buttonBuffer.length; i++) {
				get().buttonBuffer[i] = false;
			}
			get().draggin = false;
			get().clean = true;
			get().scrollX = 0;
			get().scrollY = 0;
			get().onScreen = false;
		}
	}
	
	static void mousePosCallBack(long window, double xPos, double yPos) {
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		get().xPos = xPos;
		get().yPos = yPos;
		get().draggin = get().buttonBuffer[0] || get().buttonBuffer[1] || get().buttonBuffer[2];
	}
	
	public static void mouseScrollCallBack(long window, double xOffset, double yOffset) {
		get().scrollX = xOffset;
		get().scrollY = yOffset;
	}
	
	public static void endFrame() {
		get().scrollX = 0;
		get().scrollY = 0;
		get().lastX = get().xPos;
		get().lastY = get().yPos;
	}
	
	public static void setMouseOnScreen(boolean onScreen) {
		get().onScreen = onScreen;
	}
	
	public static float getX() {
		return (float) get().xPos;
	}
	
	public static float getY() {
		return (float) get().yPos;
	}
	
	public static float getDX() {
		return (float) (get().lastX - get().xPos);
	}
	
	public static float getDY() {
		return (float) (get().lastY - get().yPos);
	}
	
	public static float getScrollX() {
		return (float) get().scrollX;
	}
	
	public static float getScrollY() {
		return (float) get().scrollY;
	}
	
	public static boolean isDraggin() {
		return get().draggin;
	}
	
	public static boolean isMouseButtonDown(int button) {
		if(button >= get().buttonBuffer.length || button < 0)
			return false;
		return get().buttonBuffer[button];
	}
	
	public static boolean isMouseOnScreen() {
		return get().onScreen;
	}
	
	public static void setCursor(int cursorID) {
		if(cursorID >= 0 && cursorID < Mouse.AMMOUNT_OF_CURSORS)
			ImGui.setMouseCursor(cursorID);
	}
	
}
