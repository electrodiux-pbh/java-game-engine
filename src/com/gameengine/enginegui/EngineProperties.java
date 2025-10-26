package com.gameengine.enginegui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gameengine.data.SourceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EngineProperties {

	private transient Inspectable inspectable = null;
	private transient GUIFileExplorer engineSourcesExplorer;
	private EngineCamera camera = new EngineCamera();
	private boolean gridLines = true;
	private float linesWidth = EditorDebugDraw.DEFAULT_LINE_WIDTH;
	private byte gridRadius = 30;
	
	public void loadGraphicProperties() {
		EditorDebugDraw.setLineWidth(linesWidth);
	}
	
	public Inspectable getCurrentInspectable() {
		return inspectable;
	}

	public void setCurrentInspectable(@Nullable Inspectable inspectable) {
		this.inspectable = inspectable;
	}

	public GUIFileExplorer getEngineSourcesExplorer() {
		return engineSourcesExplorer;
	}

	public void setEngineSourcesExplorer(GUIFileExplorer engineSourcesExplorer) {
		this.engineSourcesExplorer = engineSourcesExplorer;
	}

	public EngineCamera getCamera() {
		return camera;
	}

	public void setCamera(@NotNull EngineCamera camera) {
		this.camera = camera;
	}

	public boolean isGridLines() {
		return gridLines;
	}

	public void setGridLines(boolean gridLines) {
		this.gridLines = gridLines;
	}

	public float getLinesWidth() {
		return linesWidth;
	}

	public void setLinesWidth(float linesWidth) {
		this.linesWidth = linesWidth;
		EditorDebugDraw.setLineWidth(linesWidth);
	}

	public byte getGridRadius() {
		return gridRadius;
	}

	public void setGridRadius(int gridRadius2) {
		this.gridRadius = (byte) gridRadius2;
	}
	
	public void saveInStream(@NotNull OutputStream stream) throws IOException {
		saveInStream(this, stream);
	}
	
	public static void saveInStream(@NotNull EngineProperties properties, @NotNull OutputStream stream) throws IOException {
		Gson gson = new GsonBuilder().create();
		String data = gson.toJson(properties);
		
		SourceManager.writeStringStream(stream, data);
	}
	
	public static EngineProperties getFromStream(@NotNull InputStream stream) throws IOException {
		String data = SourceManager.readStringStream(stream);
		Gson gson = new GsonBuilder().create();
		
		EngineProperties properties = gson.fromJson(data, EngineProperties.class);
		
		return properties;
	}
	
	public static EngineProperties getFromStreamOrDefault(@NotNull InputStream stream) {
		EngineProperties properties;
		try {
			properties = getFromStream(stream);
			if(properties != null)
				return properties;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		properties = new EngineProperties();
		
		return properties;
	}
	
}
