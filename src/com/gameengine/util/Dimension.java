package com.gameengine.util;

public class Dimension {

	public float width, height;

	public Dimension() {
		this(0, 0);
	}

	public Dimension(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

}
