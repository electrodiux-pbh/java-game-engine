package com.gameengine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.gameengine.components.GameObject;
import com.gameengine.components.ModelRenderer;
import com.gameengine.util.Camera;

public class RenderBatch {

	public static void render(Shader shader, Camera camera, ModelRenderer model) {
		shader.setMatix4f("transMatrix", model.parent().transform.toMatrix4f());
		GL30.glBindVertexArray(model.getRawModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public static void render(Shader shader, Camera camera, GameObject obj) {
		ModelRenderer renderer = obj.getComponent(ModelRenderer.class);
		if(renderer == null) {
			System.out.println("No model renderer");
			return;
		}
		render(shader, camera, renderer);
	}
	
}
