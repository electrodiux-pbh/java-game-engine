package com.gameengine.enginegui.windows;

import imgui.ImGui;

public class ErrorWindow extends PopupWindow {

	protected String errorMessage;
	
	public ErrorWindow(String title, String message, int width, int height) {
		super(title, width, height);
		this.errorMessage = message;
	}

	@Override
	protected void gui() {
		ImGui.text(errorMessage);
	}

}
