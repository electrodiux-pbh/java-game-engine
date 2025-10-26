package com.gameengine.enginegui.windows;

import java.awt.Toolkit;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.gameengine.Engine;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public abstract class PopupWindow {

	public static final String DEFAULT_WINDOW_TITLE = "Popup Window";
	
	protected String windowTitle;
	
	protected ImBoolean windowOpened;
	
	protected Vector2i windowPosition;
	protected Vector2i windowSize;
	
	public PopupWindow(int width, int height) {
		this(DEFAULT_WINDOW_TITLE, width, height);
	}
	
	public PopupWindow(@NotNull String title, int width, int height) {
		this.windowTitle = title;
		
		this.windowOpened = new ImBoolean(true);
		this.windowSize = new Vector2i(width, height);
		this.windowPosition = new Vector2i(Engine.getWindowWidth() / 2, Engine.getWindowHeight() / 2);
	}
	
	public void errorWindowGui() {
		if(windowOpened.get()) {
			if(ImGui.begin(windowTitle + "###window-popup", windowOpened,
					ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse |
					ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize)) {
				if(!ImGui.isWindowFocused()) {
					ImGui.setWindowFocus();
					Toolkit.getDefaultToolkit().beep();
				}
				
				ImGui.setWindowSize(windowSize.x, windowSize.y);
				ImGui.setWindowPos(windowPosition.x - ImGui.getWindowSizeX() / 2, windowPosition.y - ImGui.getWindowSizeY() / 2);
				
				gui();
			}
			if(!windowOpened.get())
				onDisable();
			ImGui.end();
		}
	}
	
	public void onLoad() { }
	
	public void disable() {
		if(windowOpened.get()) {
			onDisable();
		}
		windowOpened.set(false);
	}
	
	protected void onDisable() { }
	
	protected abstract void gui();
	
	public boolean isOpened() {
		return windowOpened.get();
	}
	
	public void setOpened(boolean opened) {
		this.windowOpened.set(opened);
	}
	
}
