package com.gameengine.enginegui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.gameengine.GameEngineManager;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Shader;
import com.gameengine.graphics.ShaderException;

public class EditorDebugDraw {

	public static final int MIN_LINE_WIDTH = 1;
	public static final int MAX_LINE_WIDTH = 10;
	public static final int DEFAULT_LINE_WIDTH = 2;
	public static final int MAX_LINES = 3000;
	
	private static List<Line3D> lines = new ArrayList<>();
	private static Shader shader;
	
	private static float[] vertexArray = new float[MAX_LINES * 12];
	private static int vaoID;
	private static int vboID;
	
	public static void load() throws IOException, ShaderException {
		GameEngineManager.sourceManager().loadShader("editor-debugdraw", "com/gameengine/graphics/assets/shaders/editordebugdraw.glsl");
		shader = GameEngineManager.sourceManager().getShader("editor-debugdraw");
		
		vaoID = GL30.glGenVertexArrays();
		vboID = GL15.glGenBuffers();
		
		bind();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
		GL20.glEnableVertexAttribArray(0);
		
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		GL20.glEnableVertexAttribArray(1);
		
		GL20.glLineWidth(DEFAULT_LINE_WIDTH);
		unbind();
	}
	
	private static void bind() {
		GL30.glBindVertexArray(vaoID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
	}
	
	private static void unbind() {
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public static void setLineWidth(float width) {
		bind();
		GL20.glLineWidth(width);
		unbind();
	}
	
	public static void render(EngineCamera camera) {
		if(lines.size() <= 0)
			return;
		int i = 0;
		int linesAmmount = lines.size();
		Iterator<Line3D> iter = lines.iterator();
		while(iter.hasNext()) {
			Line3D line = iter.next();
			Vector3f from = line.getFrom();
			Vector3f to = line.getTo();
			Color color = line.getColor();
			
			vertexArray[i] = from.x;
			vertexArray[i + 1] = from.y;
			vertexArray[i + 2] = from.z;
			vertexArray[i + 3] = color.x;
			vertexArray[i + 4] = color.y;
			vertexArray[i + 5] = color.z;
			vertexArray[i + 6] = to.x;
			vertexArray[i + 7] = to.y;
			vertexArray[i + 8] = to.z;
			vertexArray[i + 9] = color.x;
			vertexArray[i + 10] = color.y;
			vertexArray[i + 11] = color.z;
			i += 12;
			
			iter.remove();
		}
		GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboID);
		GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, linesAmmount * 12));
		
		shader.use();
		shader.setMatix4f("uProjection", camera.getProjectionMatrix());
		shader.setMatix4f("uView", camera.getViewMatrix());
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL30.glDrawArrays(GL11.GL_LINES, 0, linesAmmount * 2);
		
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		
		shader.detach();
	}
	
	public static void addLine3D(@NotNull Vector3f from, @NotNull Vector3f to) {
		addLine3D(from, to, Color.BLACK);
	}
	
	public static void addLine3D(@NotNull Vector3f from, @NotNull Vector3f to, @NotNull Color color) {
		if(lines.size() >= MAX_LINES)
			return;
		lines.add(new Line3D(from, to, color));
	}
	
	private static class Line3D {

		private Vector3f from;
		private Vector3f to;
		private Color color;
		
		public Line3D(Vector3f from, Vector3f to, Color color) {
			this.from = from;
			this.to = to;
			this.color = color;
		}
		
		public Vector3f getFrom() {
			return from;
		}
		
		public Vector3f getTo() {
			return to;
		}
		
		public Color getColor() {
			return color;
		}
		
	}
	
}
