package com.gameengine.event.components;

import org.jetbrains.annotations.NotNull;

import com.gameengine.event.EventListener;

public abstract class GameObjectEventListener implements EventListener<GameObjectEvent> {

	public void addedComponent(@NotNull GameObjectEvent e) { };
	
	public void removedComponent(@NotNull GameObjectEvent e) { };
	
	public void execute(@NotNull GameObjectEvent e) {
		switch(e.getType()) {
		case GameObjectEvent.ADD_COMPONENT:
			addedComponent(e);
			break;
		case GameObjectEvent.REMOVE_COMPONENT:
			removedComponent(e);
			break;
		}
	}
	
}
