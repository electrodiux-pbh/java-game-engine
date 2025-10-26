package com.gameengine.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4f;

import com.gameengine.enginegui.GUI;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Sprite;
import com.gameengine.graphics.Texture;
import com.gameengine.util.Transform;

public class SpriteRenderer extends Component {

	private Color color;
	private Sprite sprite;
	private transient Transform lastTransform;
	private transient boolean isDirty = false;
	
	public SpriteRenderer() {
		this(new Color(1.0F, 1.0F, 1.0F, 1.0F));
	}
	
	public SpriteRenderer(@NotNull Color color) {
		this.sprite = new Sprite(null);
		this.color = color;
		isDirty = true;
	}
	
	public SpriteRenderer(@NotNull Sprite sprite) {
		this.sprite = sprite;
		this.color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
		isDirty = true;
	}
	
	public SpriteRenderer(@NotNull Texture texture) {
		this(new Sprite(texture));
	}
	
	@Override
	public void load() {
		lastTransform = Transform.clone(parent().transform);
	}
	
	@Override
	public void engineUpdate() {
		if(!lastTransform.equals(parent().transform)) {
			parent().transform.copyTo(lastTransform);
			isDirty = true;
		}
	}
	
	@Override
	public void update() {
		if(!lastTransform.equals(parent().transform)) {
			parent().transform.copyTo(lastTransform);
			isDirty = true;
		}
	}
	
	public Vector4f getColor() {
		return color;
	}

	public Texture getTexture() {
		return sprite.getTexture();
	}
	
	public Vector2f[] getTextureCoords() {
		return sprite.getTextureCoords();
	}
	
	public void setSprite(@NotNull Sprite sprite) {
		this.sprite = sprite;
		isDirty = true;
	}

	public void setColor(@NotNull Vector4f color) {
		if(!this.color.equals(color)) {
			isDirty = true;
			this.color.set(color);
		}
	}
	
	public void clean() {
		isDirty = false;
	}

	public void setDirty() {
		isDirty = true;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	@Override
	public void componentGui() {
		if(GUI.colorPicker(color)) {
			isDirty = true;
		}
	}
	
}
