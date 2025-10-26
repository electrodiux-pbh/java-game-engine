package com.gameengine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SceneProperties {

private static SceneProperties defaultProperties;
	
	private float gravityScale;
	private transient Scene scene;

	public SceneProperties() {
		setGravityScale(9.8F); // real life earth gravity scale 9.8 m/s^2
	}
	
	void setScene(@Nullable Scene scene) {
		this.scene = scene;
	}
	
	@Nullable
	public Scene getScene() {
		return scene;
	}
	
	public float getGravityScale() {
		return gravityScale;
	}

	public void setGravityScale(float gravityScale) {
		this.gravityScale = gravityScale;
		Scene scene = GameEngineManager.getCurrentScene();
		if(scene != null)
			scene.getPhysics().setGravityScale(gravityScale);
	}
	
	@NotNull
	public static SceneProperties getDefaultProperties() {
		if(defaultProperties == null) {
			defaultProperties = new SceneProperties();
		}
		return defaultProperties;
	}
	
}
