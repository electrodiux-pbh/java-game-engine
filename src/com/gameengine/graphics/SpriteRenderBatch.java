package com.gameengine.graphics;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.gameengine.components.GameObject;
import com.gameengine.components.SpriteRenderer;

public class SpriteRenderBatch {

	private static final int POSITION_SIZE = 3;
	private static final int COLOR_SIZE = 4;
	private static final int TEXTURE_COORDS_SIZE = 2;
	private static final int TEXTURE_ID_SIZE = 1;
	
	private static final int POSITION_OFFSET = 0;
	private static final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
	private static final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	private static final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;
	
	private static final int VERTEX_SIZE = 10;
	private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	
	private SpriteRenderer[] sprites;
	private int numberSprites;
	private boolean hasRoom;
	private float[] vertices;
	private int[] textureSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };
	private byte maxTextureSize;
	
	private List<Texture> textures;
	private int vaoID, vboID;
	private int maxBatchSize;
	
	public SpriteRenderBatch(int maxBatchSize, int maxTextureSize) {
		
		this.maxTextureSize = (byte) maxTextureSize;
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.maxBatchSize = maxBatchSize;
		
		this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
		
		this.numberSprites = 0;
		this.hasRoom = true;
		this.textures = new ArrayList<>();
		
	}
	
	public SpriteRenderBatch(int maxBatchSize) {
		this(maxBatchSize, 8);
	}
	
	public void load() {
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		vboID = GL20.glGenBuffers();
		GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboID);
		GL20.glBufferData(GL20.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL20.GL_DYNAMIC_DRAW);
		
		int eboID = GL30.glGenBuffers();
		int[] indicies = generateIndices();
		GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboID);
		GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indicies, GL20.GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(0, POSITION_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
		GL20.glEnableVertexAttribArray(0);
		
		GL20.glVertexAttribPointer(1, COLOR_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		GL20.glEnableVertexAttribArray(1);
		
		GL20.glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDS_OFFSET);
		GL20.glEnableVertexAttribArray(2);
		
		GL20.glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
		GL20.glEnableVertexAttribArray(3);
		
	}
	
	public void render(@NotNull Shader shader) {
		boolean rebufferData = false;
		for(int i = 0; i < numberSprites; i++) {
			SpriteRenderer spriteRender = sprites[i];
			if(spriteRender.isDirty()) {
				loadVertexProperties(i);
				spriteRender.clean();
				rebufferData = true;
			}
		}
		if(rebufferData) {
			GL15.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboID);
			GL15.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, vertices);
		}
		
		for(int i = 0; i < textures.size(); i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i + 1);
			System.out.println(textures.get(i).getTextureID());
			textures.get(i).bind();
		}
		shader.setIntArray("uTextures", textureSlots);
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.numberSprites * 6, GL11.GL_UNSIGNED_INT, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		
		for(Texture texture : textures) {
			texture.unbind();
		}
		
	}
	
	private void loadVertexProperties(int index) {
		SpriteRenderer sprite = this.sprites[index];
		
		int offset = 4 * index * VERTEX_SIZE;
		
		Vector4f color = sprite.getColor();
		Vector2f[] textureCoords = sprite.getTextureCoords();
		
		int textureID = 0;
		if(sprite.getTexture() != null) {
			for(int i = 0; i < textures.size(); i++) {
				if(textures.get(i).equals(sprite.getTexture())) {
					textureID = i + 1;
					break;
				}
			}
		}
		
		boolean rotated = sprite.parent().transform.rotation.isRotated();
		
		float xAdd = 0.5F;
		float yAdd = 0.5F;
		for(int i = 0; i < 4; i++) {
			switch(i) {
			case 1:
				yAdd = -0.5F;
				break;
			case 2:
				xAdd = -0.5F;
				break;
			case 3:
				yAdd = 0.5F;
				break;
			}
			
			Vector4f currentPosition;
			
			if(rotated) {
				currentPosition = new Vector4f(xAdd, yAdd, 0, 1).mul(sprite.parent().transform.toMatrix4f());
			} else {
				currentPosition = new Vector4f(
							sprite.parent().transform.position.x + (xAdd * sprite.parent().transform.localScale.x),
							sprite.parent().transform.position.y + (yAdd * sprite.parent().transform.localScale.y),
							sprite.parent().transform.position.z,
							1);
			}
			
			vertices[offset + 0] = currentPosition.x;
			vertices[offset + 1] = currentPosition.y;
			vertices[offset + 2] = currentPosition.z;
			vertices[offset + 3] = color.x;
			vertices[offset + 4] = color.y;
			vertices[offset + 5] = color.z;
			vertices[offset + 6] = color.w;
			vertices[offset + 7] = textureCoords[0].x;
			vertices[offset + 8] = textureCoords[0].y;
			vertices[offset + 9] = textureID;
			offset += VERTEX_SIZE;
		}
		
	}
	
	private int[] generateIndices() {
		int[] elements = new int[6 * maxBatchSize];
		for(int i = 0; i < maxBatchSize; i++) {
			loadElementIndices(elements, i);
		}
		return elements;
	}
	
	private void loadElementIndices(@NotNull int[] elements, int index) {
		int offsetArrayIndex = 6 * index;
		int offset = 4 * index;
		
		elements[offsetArrayIndex + 0] = offset + 3;
		elements[offsetArrayIndex + 1] = offset + 2;
		elements[offsetArrayIndex + 2] = offset + 0;
		
		elements[offsetArrayIndex + 3] = offset + 0;
		elements[offsetArrayIndex + 4] = offset + 2;
		elements[offsetArrayIndex + 5] = offset + 1;
	}
	
	/**
	 * 
	 * @param spriteRend
	 * @throws java.lang.IllegalStateException if no space available
	 */
	public void addSprite(@NotNull SpriteRenderer spriteRend) {
		if(!hasRoom)
			throw new IllegalStateException("No more sprite space");
		
		int index = this.numberSprites;
		this.sprites[index] = spriteRend;
		this.numberSprites++;
		
		if(spriteRend.getTexture() != null) {
			if(!textures.contains(spriteRend.getTexture())) {
				textures.add(spriteRend.getTexture());
			}
		}
		
		loadVertexProperties(index);
		if(this.numberSprites >= this.maxBatchSize) {
			this.hasRoom = false;
		}
	}
	
	public boolean removeGameObject(@NotNull GameObject obj) {
		SpriteRenderer spriteRender = obj.getComponent(SpriteRenderer.class);
		for(int i = 0; i < numberSprites; i++) {
			if(sprites[i] == spriteRender) {
				for(int j = i; j < numberSprites; j++) {
					sprites[j] = sprites[j + 1];
					sprites[j].setDirty();
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRoom() {
		return hasRoom;
	}
	
	public boolean hasTextureRoom() {
		return textures.size() < maxTextureSize;
	}
	
	public boolean hasTexture(@NotNull Texture texture) {
		return textures.contains(texture);
	}
	
}
