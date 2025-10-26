package com.gameengine;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gameengine.data.SourceManager;
import com.gameengine.event.Event;
import com.gameengine.event.EventListener;
import com.gameengine.event.EventThread;
import com.gameengine.graphics.Icon;

public final class GameEngineManager {

	private static boolean loaded = false;
	
	private static EventThread eventSystem;
	
	private static SourceManager sources;
	
	public static List<Scene> loadedScenes;
	
	//-----------------------------------------------
	
	private static Scene currentScene;

	public static Scene getCurrentScene() {
		return currentScene;
	}
	
	public static void destroyScene(@Nullable Scene scene) {
		GameEngineManager.loadedScenes.remove(scene);
	}

	public static void setCurrentScene(@NotNull Scene scene) {
		if(scene == null)
			return;
		if(!scene.isLoaded())
			scene.load();
		GameEngineManager.currentScene = scene;
	}
	
	public static void loadScene(@NotNull Scene scene) {
		if(!scene.isLoaded())
			scene.load();
		if(currentScene == null)
			setCurrentScene(scene);
		if(!loadedScenes.contains(scene))
			loadedScenes.add(scene);
	}
	
	public static SceneProperties getSceneProperties() {
		Scene scene = getCurrentScene();
		if(scene != null)
			return scene.getProperties();
		return SceneProperties.getDefaultProperties();
	}
	
	//DEFINITIVE
	
	// BASICS
	
	public static void load() throws Exception {
		if(loaded)
			throw new IllegalStateException("You can not load Game Engine Manager two times");
		
		GameEngineManager.eventSystem = new EventThread();
		GameEngineManager.eventSystem.start();
		
		GameEngineManager.loadedScenes = new ArrayList<>();
		
		GameEngineManager.sources = new SourceManager();
		GameEngineManager.sources.setIcon(new Icon("sources/icon.png"));
		
		loaded = true;
	}
	
	// EVENTS
	
	public static <E extends Event> void addEventListener(@NotNull EventListener<E> listener, Class<E> eventClass) {
		GameEngineManager.eventSystem.addEventListener(listener, eventClass);
	}
	
	public static <E extends Event> void removeEventListener(@NotNull EventListener<E> listener, Class<E> eventClass) {
		GameEngineManager.eventSystem.removeEventListener(listener, eventClass);
	}
	
	public static void performanceEvent(@NotNull Event e) {
		GameEngineManager.eventSystem.performanceEvent(e);
	}

	// GETTERS AND SETTERS

	public static boolean isLoaded() {
		return loaded;
	}
	
	public static SourceManager sourceManager() {
		return sources;
	}
	
}
