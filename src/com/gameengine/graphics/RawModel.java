package com.gameengine.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class RawModel {

	private int vaoId;
	private int vboId;
	private int indicesVboId;
	private int vertexCount;
	
	public RawModel(float[] vertex, int[] indices) {
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		
		bindIndicesBuffer(indices);
		
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		FloatBuffer buff = storeDataInFloatBuffer(vertex);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buff, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		this.vertexCount = indices.length;
	}
	
	private void bindIndicesBuffer(int[] indices) {
		indicesVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
		IntBuffer buff = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buff, GL15.GL_STATIC_DRAW);
		
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public int getVaoID() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public void destroy() {
		GL30.glDeleteVertexArrays(vaoId);
		GL15.glDeleteBuffers(vboId);
		GL15.glDeleteBuffers(indicesVboId);
	}
	
}
