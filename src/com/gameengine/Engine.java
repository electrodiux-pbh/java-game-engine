package com.gameengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import com.gameengine.audio.AudioContext;
import com.gameengine.audio.AudioException;
import com.gameengine.data.SourceManager;
import com.gameengine.enginegui.EditorDebugDraw;
import com.gameengine.enginegui.EngineProperties;
import com.gameengine.enginegui.EngineViewPort;
import com.gameengine.enginegui.GUI;
import com.gameengine.enginegui.GUIFileExplorer;
import com.gameengine.enginegui.GUILayer;
import com.gameengine.enginegui.windows.PopupWindow;
import com.gameengine.event.engine.EngineEvent;
import com.gameengine.event.engine.EngineEventListener;
import com.gameengine.graphics.AspectRatio;
import com.gameengine.graphics.Framebuffer;
import com.gameengine.graphics.Keyboard;
import com.gameengine.graphics.Mouse;
import com.gameengine.graphics.ShaderException;
import com.gameengine.graphics.SpriteSheet;
import com.gameengine.graphics.Window;
import com.gameengine.languages.Language;
import com.gameengine.languages.Languages;
import com.gameengine.program.GameScript;
import com.gameengine.program.GameScriptLoader;
import com.gameengine.program.InvalidCodeException;
import com.gameengine.util.Console;
import com.gameengine.util.IllegalProjectException;
import com.gameengine.util.Timer;

import imgui.ImGui;

public final class Engine {

	public static final String ENGINE_NAME = "Java Game Engine";
	public static final String ENGINE_PROJECT_PROPERTIES_NAME = "engine-properties.dat";
	
	private static boolean loaded;
	private static boolean runningRuntimeGame;
	
	private static Language engineLanguage;
	
	private static Project project;
	
	private static EngineProperties engineProperties;
	
	private static Window window;
	private static GLCapabilities graphicCapabilities;
	private static AudioContext audioContext;
	private static GUILayer guiEngineContext;
	
	private static GameScriptLoader gameScriptLoader;
	
	// Simple configuration instead of complex SecurityManager
	private static SimpleConfig config;
	
	//TEMP
	
	private static List<EngineEventListener> listeners = new ArrayList<>();
	
	public static void addEventListener(@NotNull EngineEventListener listener) {
		listeners.add(listener);
	}
	
	public static void removeEventListener(@NotNull EngineEventListener listener) {
		listeners.remove(listener);
	}
	
	public static void performanceEvent(@NotNull EngineEvent e) {
		for(int i = 0; i < listeners.size(); i++) {
			listeners.get(i).execute(e);
		}
	}
	
	// BASICS
	
	/**
	 * Simple engine loading without authentication system
	 */
	public static void loadSimple() throws Exception {
		if(loaded)
			throw new IllegalStateException("You can not load the Engine two times");
		
		Console.out.println("Starting up the engine");
		
		// Initialize simple configuration
		Engine.config = new SimpleConfig();
		
		// Initialize core systems without HTTP/authentication
		GameEngineManager.load();
		Engine.engineProperties = new EngineProperties();
		Engine.gameScriptLoader = new GameScriptLoader();
		
		Engine.setEngineLanguage(new Language(Languages.getDefaultLanguage()));
		
		loaded = true;
		
		Console.out.println("Engine started up!");
	}
	
	public static void start() {
		if(!loaded)
			throw new IllegalStateException("You nead to startUp the engine to load it");
		
		GLFWErrorCallback.createPrint(Console.err).set();
		Engine.window = new Window(Engine.config);
		
		try {
			Engine.audioContext = new AudioContext();
			Engine.audioContext.init();
		} catch (AudioException e) {
			Console.err.println("An error ocurred loading the audio context: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		GL.createCapabilities();
		graphicCapabilities = GL.getCapabilities();
		
		Engine.setFrameBufferBounds(1920, 1080); // TODO SAVE IN CONSTANTS
		GL11.glViewport(0, 0, 1920, 1080);
		
		Mouse.configureMouse(Engine.window);
		Keyboard.configureKeyboard(Engine.window);
		
		init();
		
		Timer timer = Timer.getDefaultTimer();
		
		timer.addHandler(() -> {
			if(GLFW.glfwWindowShouldClose(window.getWindowID())) {
				GLFW.glfwSetWindowShouldClose(window.getWindowID(), false);
				GUI.setPopupWindow(new PopupWindow("Do you wana close " + Engine.ENGINE_NAME + "?", window.getWidth() / 5, window.getHeight() / 5) {
					
					@Override
					protected void gui() {
						if(ImGui.button("Confirm")) {
							Engine.stop();
							windowOpened.set(false);
						}
						ImGui.sameLine();
						if(ImGui.button("Cancel")) {
							windowOpened.set(false);
						}
					}
					
				});
			}
			GLFW.glfwPollEvents();
			render();
			Mouse.endFrame();
			GLFW.glfwSwapBuffers(window.getWindowID());
		});
		
		timer.setStopOnException(true);
		timer.start(false);
		
		Engine.stop();
		
	}
	
	// INITIALICE
	
	private static void init() {
		Engine.window.setTitle(getWindowTitle(Engine.getProject()));
		
		Engine.addEventListener(new EngineEventListener() {
		
			@Override
			public void projectChanged(EngineEvent e) {
				Project project = e.getProject();
				if(project == null)
					return;
				Engine.window.setTitle(getWindowTitle(project));
				if(project.getSourcesLocation() != null) {
					try {
						EngineProperties newProperties = EngineProperties.getFromStreamOrDefault(new FileInputStream(new File(project.getProjectLocation(), ENGINE_PROJECT_PROPERTIES_NAME)));
						
						newProperties.getCamera().setFramebuffer(Engine.engineProperties.getCamera().getFramebuffer());
						newProperties.setEngineSourcesExplorer(Engine.engineProperties.getEngineSourcesExplorer());
						
						Engine.engineProperties = newProperties;
						Engine.engineProperties.loadGraphicProperties();
						
					} catch (IOException e1) { }
					Engine.getEngineProperties().setEngineSourcesExplorer(new GUIFileExplorer(project.getSourcesLocation()));
				}
			}
			
		});
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		try {
			EditorDebugDraw.load();
		} catch (IOException | ShaderException e) { e.printStackTrace(); }
		
		Engine.getEngineProperties().loadGraphicProperties();
		
		Engine.guiEngineContext = new GUILayer(Engine.window);
		Engine.guiEngineContext.initImGui();
		
		Engine.loadEngineSources();
		Engine.getEngineProperties().getCamera().setFramebuffer(new Framebuffer(Engine.window.getFramebuffer().getWidth(), Engine.window.getFramebuffer().getHeight()));
	}
	
	private static String getWindowTitle(@NotNull Project project) {
		if(project != null)
			return ENGINE_NAME + " - " + project.getName();
		return ENGINE_NAME;
	}
	
	private static void loadEngineSources() {
		SourceManager sm = GameEngineManager.sourceManager();
		
		try {
			sm.loadTexture("engineTextures", "sources/engineTextures.png");
			sm.loadSpriteSheet("engineSprites", new SpriteSheet(sm.getTexture("engineTextures"), 128, 128, 0, 5));
			
			sm.loadTexture("temDefault", "sources/spritesheet.png");
			sm.loadTexture("icon", "sources/icon.png");
			sm.loadShader("default", "sources/shaders/default.glsl");
		} catch (IOException | ShaderException  e) {
			e.printStackTrace();
			System.exit(0xFFFFFF);
		}
		
		sm.loadSpriteSheet("mySprites", new SpriteSheet(sm.getTexture("temDefault"), 16, 16, 0, 2));
	}
	
	private static void render() {
		Mouse.setMouseOnScreen(EngineViewPort.getWantCaptureMouse());
		Scene scene = GameEngineManager.getCurrentScene();
		
		PopupWindow errorWindow = GUI.getPopupWindow();
		
		if(scene != null) {
			if(errorWindow == null || !errorWindow.isOpened()) {
				if(Engine.isRunningGameRuntime())
					scene.update();
				scene.engineUpdate();
			}
		}
		
		Engine.guiEngineContext.render(() -> {
			if(errorWindow != null) {
				if(errorWindow.isOpened())
					errorWindow.errorWindowGui();
			}
			EngineViewPort.gameViewport(Engine.window.getFramebuffer(), scene != null ? scene.getCurrentCamera() : null, scene);
			EngineViewPort.editorViewport(getEngineProperties().getCamera(), scene);
			GUI.inspectorWindow(Engine.getEngineProperties().getCurrentInspectable());
			GUI.scenesWindow(GameEngineManager.loadedScenes);
			GUI.projectExplorerWindow(getProject());
			GUI.consoleGUI();
			GUI.fileExplorer(Engine.getEngineProperties().getEngineSourcesExplorer());
			GUI.mainMenuBar();
		});
	}
	
	public static void stop() {
		if(loaded) {
			
			Timer.getDefaultTimer().stop();
			
			try {
				if(Engine.engineProperties != null && Engine.getProject() != null) {
					if(Engine.getProject().getProjectLocation() != null)
						Engine.engineProperties.saveInStream(new FileOutputStream(new File(Engine.getProject().getProjectLocation(), ENGINE_PROJECT_PROPERTIES_NAME)));
				}
			} catch (Exception e) {
				Console.warn.println("An error ocurred saving the engine properties.");
				e.printStackTrace();
			}
			
			Engine.guiEngineContext.destroyImGui();
			Engine.audioContext.destroy();
			Callbacks.glfwFreeCallbacks(window.getWindowID());
			GLFW.glfwDestroyWindow(window.getWindowID());
			GLFW.glfwTerminate();
			GLFW.glfwSetErrorCallback(null).free();
			window.destroy();
			
			loaded = false;
			Runtime.getRuntime().exit(0);
		}
	}
	
	// PROJECTS
	
	public static void createNewProject(@NotNull ProjectArguments args) {
		try {
			Engine.project = Project.createNewProject(args);
			Scene scene = new Scene();
			GameEngineManager.loadScene(scene);
			Engine.performanceEvent(new EngineEvent(getProject(), EngineEvent.PROJECT_CREATED));
			Console.out.println("The project '" + getProject().getName() + "' has been created!");
		} catch (IllegalProjectException e) {
			Console.err.print("An error ocurred while trying to create a new project");
			e.printStackTrace();
		}
	}
	
	public static boolean loadProject(@NotNull File location) {
		try {
			Engine.project = Project.loadProject(location);
			GameEngineManager.loadScene(project.getScenes().get(0));
			Engine.performanceEvent(new EngineEvent(getProject(), EngineEvent.PROJECT_LOADED));
			Console.out.println("Project '" + getProject().getName() + "' has been loaded");
			return true;
		} catch (IllegalProjectException e) {
			Console.err.println("An error ocurred while trying to load the project at: '" + location.getAbsolutePath() + "'");
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean saveProject() throws IOException {
		Engine.stopRunningGameRuntime();
		boolean saved = getProject().save();
		if(saved) {
			Engine.performanceEvent(new EngineEvent(getProject(), EngineEvent.PROJECT_SAVED));
			Console.out.println("Project '" + getProject().getName() + "' has been saved");
		}
		return saved;
	}
	
	public static boolean saveProjectAs(@NotNull File path) throws IOException {
		getProject().setProjectLocation(path);
		return saveProject();
	}
	
	@NotNull
	public static ProjectProperties getProjectProperties() {
		Project project = getProject();
		if(project != null)
			return project.getProperties();
		return ProjectProperties.getDefaultProperties();
	}
	
	@Nullable
	public static Project getProject() {
		return project;
	}
	
	// ENGINE RUNTIME
	
	public static void startRunningGameRuntime() {
		if(isRunningGameRuntime())
			return;
		Scene scene = GameEngineManager.getCurrentScene();
		Project project = Engine.getProject();
		if(scene != null && project != null) {
			try {
				if(!project.saveScene(scene))
					return;
				Scene sceneLoad = Scene.reloadScene(new File(project.getProjectLocation(), "scenes/" + scene.getName()), scene);
				GameEngineManager.loadScene(sceneLoad);
				GameEngineManager.setCurrentScene(sceneLoad);
				runningRuntimeGame = true;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			Console.out.println("Starting play '" + Engine.getProject().getName() + "/" + GameEngineManager.getCurrentScene().getName() + "'");
		}
	}
	
	public static void stopRunningGameRuntime() {
		if(!Engine.isRunningGameRuntime())
			return;
		Scene scene = GameEngineManager.getCurrentScene();
		Project project = Engine.getProject();
		if(scene != null && project != null) {
			try {
				Scene sceneLoad;
				sceneLoad = Scene.reloadScene(new File(project.getProjectLocation(), "scenes/" + scene.getName()), scene);
				GameEngineManager.loadScene(sceneLoad);
				GameEngineManager.setCurrentScene(sceneLoad);
				runningRuntimeGame = false;
				Console.out.println("Ending play '" + Engine.getProject().getName() + "/" + GameEngineManager.getCurrentScene().getName() + "'");
			} catch(IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public static boolean isRunningGameRuntime() {
		return runningRuntimeGame;
	}
	
	// GUI
	
	@NotNull
	public static EngineProperties getEngineProperties() {
		return engineProperties;
	}
	
	// VIEWPORT WINDOW
	
	public static void setFrameBufferAspectRatio(@NotNull AspectRatio ratio) {
		Engine.setFrameBufferBounds(ratio.getWidth(), ratio.getHeight());
	}
	
	public static void setFrameBufferBounds(int width, int height) {
		window.setFramebuffer(new Framebuffer(width, height));
	}
	
	@NotNull
	public static AudioContext getAudioContext() {
		return Engine.audioContext;
	}
	
	public static int getWindowWidth() {
		return window.getWidth();
	}
	
	public static int getWindowHeight() {
		return window.getHeight();
	}
	
	public static GLCapabilities getGraphicCapabilities() {
		return Engine.graphicCapabilities;
	}
	
	// SCRIPTS
	
	@NotNull
	public static Class<? extends GameScript> getGameScriptClass(@NotNull File file) throws InvalidCodeException, IOException {
		return gameScriptLoader.loadCodeClass(file);
	}
	
	@NotNull
	public static GameScript getGameScript(@NotNull Class<? extends GameScript> clazz) throws InvalidCodeException, IOException {
		return gameScriptLoader.getCodeInstanceFromClass(clazz);
	}
	
	// LANGUAGE
	
	@NotNull
	public static Language getEngineLanguage() {
		return engineLanguage;
	}
	
	public static void setEngineLanguage(@NotNull Language lang) {
		engineLanguage = lang;
	}
	
	/**
	 * Get the simple configuration
	 */
	public static SimpleConfig getConfig() {
		return config;
	}
	
}
