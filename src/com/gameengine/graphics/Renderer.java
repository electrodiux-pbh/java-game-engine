package com.gameengine.graphics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.gameengine.GameEngineManager;
import com.gameengine.components.GameObject;
import com.gameengine.components.SpriteRenderer;
import com.gameengine.event.components.GameObjectEvent;
import com.gameengine.event.components.GameObjectEventListener;
import com.gameengine.util.Camera;

public class Renderer {

	private static final int MAX_BATCH_SIZE = 1024;
	private List<SpriteRenderBatch> batches;
	private Shader shader;
	
	public Renderer() throws IOException {
		this(GameEngineManager.sourceManager().getShader("default"));
	}
	
	public Renderer(@NotNull Shader shader) {
		this.batches = new ArrayList<>();
		this.shader = shader;
	}
	
	public void addGameObject(@NotNull GameObject gameObject) {
		SpriteRenderer spriteRend = gameObject.getComponent(SpriteRenderer.class);
		if(spriteRend != null) {
			add(spriteRend);
		} else {
			gameObject.addEventListener(new GameObjectEventListener() {

				@Override
				public void addedComponent(GameObjectEvent e) {
					if(e.getComponet() instanceof SpriteRenderer) {
						SpriteRenderer spriteRend = (SpriteRenderer) e.getComponet();
						if(spriteRend != null) {
							add(spriteRend);
//							gameObject.removeEventListener(this);
						}
					}
				}
				
			});
		}
	}
	
	private void add(@NotNull SpriteRenderer render) {
		boolean added = false;
		for(SpriteRenderBatch batch : batches) {
			if(batch.hasRoom()) {
				Texture texture = render.getTexture();
				if((texture == null || (batch.hasTexture(texture) || batch.hasRoom()))) {
					batch.addSprite(render);
					added = true;
					break;
				}
			}
		}
		
		if(!added) {
			SpriteRenderBatch newBatch = new SpriteRenderBatch(MAX_BATCH_SIZE);
			newBatch.load();
			batches.add(newBatch);
			newBatch.addSprite(render);
		}
	}
	
	public void render(@NotNull Camera camera) {
		
		shader.setMatix4f("uProjection", camera.getProjectionMatrix());
		shader.setMatix4f("uView", camera.getViewMatrix());
		
		for(SpriteRenderBatch batch : batches) {
			batch.render(shader);
		}
		
		shader.detach();
	}
	
	public void removeGameObject(@NotNull GameObject obj) {
		if(obj.getComponent(SpriteRenderer.class) == null)
			return;
		for(SpriteRenderBatch batch : batches) {
			if(batch.removeGameObject(obj))
				return;
		}
	}
	
	public Shader getShader() {
		return shader;
	}
	
	public void setShader(@NotNull Shader shader) {
		this.shader = shader;
	}
	
	public void dispose() {
		batches.clear();
	}
	
}
