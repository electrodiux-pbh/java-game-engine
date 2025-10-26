package com.gameengine;

import org.jetbrains.annotations.NotNull;

import com.gameengine.graphics.AspectRatio;

public class ProjectProperties {

	private static ProjectProperties defaultProperties;
	
	private AspectRatio aspectRatio;

	public ProjectProperties() {
		aspectRatio = AspectRatio.ASPECT_16_9;
	}
	
	public void load() {
		setAspectRatio(aspectRatio);
	}
	
	public AspectRatio getAspectRatio() {
		return aspectRatio;
	}
	
	public float getAspectRatioValue() {
		return aspectRatio.getAspectValue();
	}

	public void setAspectRatio(AspectRatio aspectRatio) {
		Engine.setFrameBufferAspectRatio(aspectRatio);
		this.aspectRatio = aspectRatio;
	}
	
	@NotNull
	public static ProjectProperties getDefaultProperties() {
		if(defaultProperties == null) {
			defaultProperties = new ProjectProperties();
		}
		return defaultProperties;
	}
	
}
