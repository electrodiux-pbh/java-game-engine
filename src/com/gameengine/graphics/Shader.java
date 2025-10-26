package com.gameengine.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class Shader {
	
	private String vertexSrc, fragmentSrc;
	private String filePath;
	
	private int vertexID, fragmentID, shaderProgram;
	
	private boolean beingUsed = false;
	
	public Shader(@NotNull String filePath) throws IOException {
		this.filePath = filePath;
		if(!this.filePath.startsWith("/"))
			this.filePath = "/" + filePath;
		load(getClass().getResourceAsStream(this.filePath));
	}
	
	public Shader(@NotNull InputStream stream) throws IOException {
		this.filePath = "stream-path/" + Integer.toHexString(stream.hashCode());
		load(stream);
	}
	
	private void load(@NotNull InputStream stream) throws IOException {
		try {
			String src = getSouceOfStream(stream);
			String[] shaderSrc = src.split("(#type)( )+([a-zA-z]+)");
			
			int index = 0;
			int eol = 0;
			for(int i = 1; i < shaderSrc.length; i++) {
				index = src.indexOf("#type", eol) + 6;
				eol = src.indexOf("\n", index);
				String type = src.substring(index, eol).trim();
				
				if(type.equalsIgnoreCase("vertex"))
					vertexSrc = shaderSrc[i];
				else if (type.equalsIgnoreCase("fragment"))
					fragmentSrc = shaderSrc[i];
				else
					throw new IOException("Unexpected token '" + type + "'");
			}
			
			if(vertexSrc == null || fragmentSrc == null)
				throw new IOException("Fragment or Vertex is null");
		} catch (IOException e) {
			throw new IOException("Could not open file for shader: '" + this.filePath + "'", e);
		}
	}
	
	private String getSouceOfStream(@NotNull InputStream stream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		
		String line = "";
		String data = "";
		
		while((line = in.readLine()) != null) {
			data += line + "\n";
		}
		in.close();
		
		return data;
	}
	
	public void compile() throws ShaderException {
		vertexID = compileShader(GL20.GL_VERTEX_SHADER, vertexSrc, "Vertex shader at: '" + filePath + "' compilation failed.");
		fragmentID = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSrc, "Fragment shader at: '" + filePath + "' compilation failed.");
		
		shaderProgram = GL20.glCreateProgram();
		GL20.glAttachShader(shaderProgram, vertexID);
		GL20.glAttachShader(shaderProgram, fragmentID);
		GL20.glLinkProgram(shaderProgram);
		
		int success = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
		if(success == GL20.GL_FALSE) {
			int len = GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH);
			throw new ShaderException("Linking of shaders failed.", GL20.glGetProgramInfoLog(shaderProgram, len));
		}
	}
	
	private int compileShader(int shaderType, @NotNull String src, @NotNull String errorMessage) throws ShaderException {
		int success;
		int id;
		
		id = GL20.glCreateShader(shaderType);
		GL20.glShaderSource(id, src);
		GL20.glCompileShader(id);
		
		success = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);
		if(success == GL20.GL_FALSE) {
			int len = GL20.glGetShaderi(id, GL20.GL_INFO_LOG_LENGTH);
			throw new ShaderException(errorMessage, GL20.glGetShaderInfoLog(id, len));
		}
		
		return id;
	}
	
	public void setMatix4f(@NotNull String varName, @NotNull Matrix4f matrix) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(16); //4 * 4 matrix
		matrix.get(matrixBuff);
		GL20.glUniformMatrix4fv(varLocation, false, matrixBuff);
	}
	
	public void setMatix3f(@NotNull String varName, @NotNull Matrix3f matrix) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(9); //3 * 3 matrix
		matrix.get(matrixBuff);
		GL20.glUniformMatrix3fv(varLocation, false, matrixBuff);
	}
	
	public void setMatix2f(@NotNull String varName, @NotNull Matrix2f matrix) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(4); //2 * 2 matrix
		matrix.get(matrixBuff);
		GL20.glUniformMatrix2fv(varLocation, false, matrixBuff);
	}
	
	public void setVector4f(@NotNull String varName, @NotNull Vector4f vec) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		GL20.glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}
	
	public void setVector3f(@NotNull String varName, @NotNull Vector3f vec) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		GL20.glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}
	
	public void setVector2f(@NotNull String varName, @NotNull Vector2f vec) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		GL20.glUniform2f(varLocation, vec.x, vec.y);
	}
	
	public void setFloat(@NotNull String varName, float value) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		GL20.glUniform1f(varLocation, value);
	}
	
	public void setInt(@NotNull String varName, int value) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		GL20.glUniform1i(varLocation, value);
	}
	
	public void setIntArray(@NotNull String varName, int[] values) {
		int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
		use();
		GL20.glUniform1iv(varLocation, values);
	}
	
	public void setTexture(int textureID) {
		setInt("texture", textureID);
	}
	
	public void setTexture(@NotNull Texture texture) {
		setTexture(texture.getTextureID());
	}
	
	public void use() {
		if(!beingUsed) {
			GL20.glUseProgram(shaderProgram);
			beingUsed = true;
		}
	}
	
	public void detach() {
		GL20.glUseProgram(0);
		beingUsed = false;
	}
	
	public void destroy() {
		detach();
		GL20.glDetachShader(shaderProgram, vertexID);
		GL20.glDeleteShader(vertexID);
		GL20.glDetachShader(shaderProgram, fragmentID);
		GL20.glDeleteShader(fragmentID);
		GL20.glDeleteProgram(shaderProgram);
	}
	
	public int getShaderProgramID() {
		return shaderProgram;
	}
	
	public int getVertexID() {
		return vertexID;
	}
	
	public int getFragmentID() {
		return fragmentID;
	}
	
}
