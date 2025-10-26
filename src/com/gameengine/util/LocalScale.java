package com.gameengine.util;

import org.joml.Vector3f;

public class LocalScale extends Vector3f {

	public LocalScale() {
		this(1, 1, 1);
	}

	public LocalScale(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public LocalScale(LocalScale localScale) {
		this(localScale.x, localScale.y, localScale.z);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
	
	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "[x=" + x + ",y=" + y + ",z=" + z + "]";
	}
	
}
