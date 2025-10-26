package com.gameengine.graphics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public class Sprite {

	private Texture texture;
	private Vector2f[] textureCoords;
	private float width, height;
	
	public Sprite() {
		this.textureCoords = new Vector2f[] {
				new Vector2f(1, 1),
				new Vector2f(1, 0),
				new Vector2f(0, 0),
				new Vector2f(0, 1)
		};
	}
	
	public Sprite(@Nullable Texture texture) {
		this();
		this.texture = texture;
	}
	
	public Sprite(@Nullable Texture texture, float width, float height) {
		this();
		this.texture = texture;
		this.width = width;
		this.height = height;
	}
	
	public Sprite(@Nullable Texture texture, float width, float height, @NotNull Vector2f[] textureCoords) {
		this.texture = texture;
		this.textureCoords = textureCoords;
		this.width = width;
		this.height = height;
	}

	public void setTexture(@Nullable Texture texture) {
		this.texture = texture;
	}
	
	public void setTextureCoords(@NotNull Vector2f[] textureCoords) {
		this.textureCoords = textureCoords;
	}
	
	@Nullable
	public Texture getTexture() {
		return texture;
	}
	
	public int getTextureID() {
		return texture == null ? -1 : texture.getTextureID();
	}

	@NotNull
	public Vector2f[] getTextureCoords() {
		return textureCoords;
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
