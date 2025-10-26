package com.gameengine.graphics;

public class Model {

	private RawModel rawModel;
	private Texture texture;

	public Model(RawModel rawModel, Texture texture) {
		this.rawModel = rawModel;
		this.texture = texture;
	}
	
	public RawModel getRawModel() {
		return rawModel;
	}

	public Texture getTexture() {
		return texture;
	}
	
}
