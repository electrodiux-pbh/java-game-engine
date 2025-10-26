package com.gameengine.graphics;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public class SpriteSheet {

	private Texture texture;
	private List<Sprite> sprites;
	
	public SpriteSheet(@NotNull Texture texture, int spriteWidth, int spriteHeight, int spacing, int spriteAmmount) {
		this.sprites = new ArrayList<>();
		this.texture = texture;
		
		load(spriteWidth, spriteHeight, spacing, spriteAmmount);
	}
	
	private void load(int spriteWidth, int spriteHeight, int spacing, int spriteAmmount) {
		int currentX = 0;
		int currentY = texture.getHeight() - spriteHeight;
		
		for(int i = 0; i < spriteAmmount; i++) {
			float topY = (currentY + spriteHeight) / (float) texture.getHeight();
			float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
			float leftX = currentX / (float) texture.getWidth();
			float bottomY = currentY / (float) texture.getHeight();
			
			Vector2f[] textureCoords = {
				new Vector2f(leftX, topY),
				new Vector2f(leftX, bottomY),
				new Vector2f(rightX, bottomY),
				new Vector2f(rightX, topY)
			};
			
			Sprite sprite = new Sprite(this.texture, spriteWidth, spriteHeight, textureCoords);
			this.sprites.add(sprite);
			
			currentX += spriteWidth + spacing;
			if(currentX >= texture.getWidth()) {
				currentX = 0;
				currentY -= spriteHeight + spacing;
			}
		}
	}
	
	public Sprite getSprite(int index) {
		return sprites.get(index);
	}
	
	public int ammount() {
		return sprites.size();
	}
	
}
