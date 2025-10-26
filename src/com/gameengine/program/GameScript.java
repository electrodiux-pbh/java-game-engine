package com.gameengine.program;

import org.jetbrains.annotations.NotNull;

import com.gameengine.components.GameObject;

public abstract class GameScript extends Script {
	
	public abstract void onUpdate(@NotNull GameObject obj);

	@Override
	public final void onUpdate(Object obj) {
		if(obj == null)
			return;
		if(!(obj instanceof GameObject))
			return;
		onUpdate((GameObject) obj);
	}
	
}
