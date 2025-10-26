package com.gameengine.event.engine;

import org.jetbrains.annotations.NotNull;

import com.gameengine.event.EventListener;

public abstract class EngineEventListener implements EventListener<EngineEvent> {

	public void projectCreated(@NotNull EngineEvent e) { };
	
	public void projectLoaded(@NotNull EngineEvent e) { };
	
	public void projectSaved(@NotNull EngineEvent e) { };
	
	public void projectChanged(@NotNull EngineEvent e) { };
	
	public void execute(EngineEvent e) {
		switch(e.getType()) {
		case EngineEvent.PROJECT_CREATED:
			projectCreated(e);
			projectChanged(e);
			break;
		case EngineEvent.PROJECT_LOADED:
			projectLoaded(e);
			projectChanged(e);
			break;
		case EngineEvent.PROJECT_SAVED:
			projectSaved(e);
			break;
		}
	}
	
}
