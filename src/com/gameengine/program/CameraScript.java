package com.gameengine.program;

import org.jetbrains.annotations.NotNull;

import com.gameengine.util.Camera;

public abstract class CameraScript extends Script {

	public abstract void onUpdate(@NotNull Camera camera);

	public final void onUpdate(Object obj) {
		if(obj == null)
			return;
		if(!(obj instanceof Camera))
			return;
		onUpdate((Camera) obj);
	}
	
}
