package com.gameengine.event.engine;

import com.gameengine.Project;
import com.gameengine.event.Event;

public class EngineEvent extends Event {

	public static final int PROJECT_CREATED = 0;
	public static final int PROJECT_LOADED = 1;
	public static final int PROJECT_SAVED = 2;
	
	private static final long serialVersionUID = -8373807505216589485L;

	private int type;
	
	public EngineEvent(Project project, int type) {
		super(project);
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public Project getProject() {
		return (Project) super.getSource();
	}

}
