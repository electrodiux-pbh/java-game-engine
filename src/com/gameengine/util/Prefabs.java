package com.gameengine.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gameengine.components.BoxCollider2D;
import com.gameengine.components.GameObject;
import com.gameengine.components.Rigidbody2D;
import com.gameengine.components.SpriteRenderer;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Sprite;
import com.gameengine.graphics.Texture;

public class Prefabs {

	public static GameObject createSpriteObject(@NotNull Sprite sprite) {
		GameObject obj = new GameObject("Sprite Object");
		
		SpriteRenderer renderer = new SpriteRenderer(sprite);
		obj.addComponent(renderer);
		
		return obj;
	}
	
	public static GameObject createTextureObject(@Nullable Texture texture) {
		GameObject obj = createSpriteObject(new Sprite(texture));
		obj.setName("Texture Object");
		return obj;
	}
	
	public static GameObject createPhysicObject() {
		GameObject obj = new GameObject("Physic Object");
		
		SpriteRenderer renderer = new SpriteRenderer(Color.BLACK);
		Rigidbody2D body = new Rigidbody2D();
		BoxCollider2D collider = new BoxCollider2D();
		obj.addComponent(renderer);
		obj.addComponent(body);
		obj.addComponent(collider);
		
		return obj;
	}
	
}
