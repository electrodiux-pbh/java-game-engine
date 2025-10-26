package com.gameengine.event.components;

import org.jetbrains.annotations.NotNull;

import com.gameengine.components.Component;
import com.gameengine.components.GameObject;
import com.gameengine.event.Event;

public class GameObjectEvent extends Event {

	public static final byte ADD_COMPONENT = 0;
	public static final byte REMOVE_COMPONENT = 1;
	
	private static final long serialVersionUID = 857960775589597831L;

	private Component component;
	private byte type;
	
	public GameObjectEvent(@NotNull GameObject source, byte type) {
		super(source);
		this.type = type;
	}
	
	public GameObjectEvent(@NotNull GameObject source, byte type, @NotNull Component component) {
		this(source, type);
		this.component = component;
	}
	
	public GameObject getGameObject() {
		return (GameObject) super.getSource();
	}
	
	public Component getComponet() {
		return component;
	}

	public byte getType() {
		return type;
	}

}
