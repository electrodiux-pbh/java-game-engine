package com.gameengine.enginegui.windows;

import java.io.File;

import com.gameengine.Engine;
import com.gameengine.ProjectArguments;
import com.gameengine.enginegui.GUI;

import imgui.ImGui;

public class CreateProjectWindow extends PopupWindow {

	public static final String WINDOW_PROJECT_TITLE = "Create new project";
	
	private String projectTitle = new String();
	
	public CreateProjectWindow(int width, int height) {
		super(WINDOW_PROJECT_TITLE, width, height);
	}

	@Override
	protected void gui() {
		projectTitle = GUI.inputText("Title", projectTitle);
		if(ImGui.button("Create")) {
			if(canCreate()) {
				Engine.createNewProject(new ProjectArguments() {
					
					@Override
					public File projectLocation() {
						return null;
					}
					
					@Override
					public String getName() {
						return projectTitle;
					}
					
				});
				windowOpened.set(false);
			}
		}
		ImGui.sameLine();
		if(ImGui.button("Cancel")) {
			windowOpened.set(false);
		}
	}

	private boolean canCreate() {
		return !projectTitle.isBlank() && projectTitle.length() >= 3;
	}
	
	public String getProjectTitle() {
		return projectTitle;
	}

}
