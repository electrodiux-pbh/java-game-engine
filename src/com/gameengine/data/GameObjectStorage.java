package com.gameengine.data;

import org.jetbrains.annotations.NotNull;

import com.gameengine.components.GameObject;

public interface GameObjectStorage {

	void addGameObject(@NotNull GameObject object);
	
	void removeGameObject(@NotNull GameObject object);
	
}
