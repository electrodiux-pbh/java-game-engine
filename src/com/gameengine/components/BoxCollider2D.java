package com.gameengine.components;

import org.joml.Vector2f;

public class BoxCollider2D extends Collider {

	public Vector2f halfSize = new Vector2f(1.0F, 1.0F);
	public Vector2f origin = new Vector2f();

	public Vector2f getHalfSize() {
		return halfSize;
	}

	public void setHalfSize(Vector2f halfSize) {
		this.halfSize = halfSize;
	}
	
	public Vector2f getOrigin() {
		return origin;
	}
	
}
