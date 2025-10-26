package com.gameengine.util;

import com.gameengine.program.CameraScript;

public class CameraScriptTest extends CameraScript {
	
	public float velocity = 1;
	public float rightTop = -7;
	public float leftTop = 7;
	
	@Override
	public void onUpdate(Camera camera) {
		if(camera.getPosition().x >= leftTop && velocity > 0)
			velocity = -velocity;
		if(camera.getPosition().x <= rightTop && velocity < 0)
			velocity = -velocity;
		camera.getPosition().x += velocity * Timer.deltaTime();
	}
}
