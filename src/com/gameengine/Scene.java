package com.gameengine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.gameengine.components.Component;
import com.gameengine.components.ComponentSerializator;
import com.gameengine.components.GameObject;
import com.gameengine.components.GameObjectSerializator;
import com.gameengine.data.GameObjectStorage;
import com.gameengine.enginegui.EditorDebugDraw;
import com.gameengine.graphics.AspectRatio;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Renderer;
import com.gameengine.phys.Physics2D;
import com.gameengine.util.Camera;
import com.gameengine.util.Position;
import com.gameengine.util.Rotation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Scene implements GameObjectStorage {
	
	private static int ID_COUNTER = 0;
	
	private transient Renderer renderer;
	
	private Camera currentCamera;
	private List<Camera> cameras;
	
	private List<GameObject> gameObjects;
	private SceneProperties properties;
	private transient boolean loaded;
	private String name;
	private transient int uid;
	private transient boolean isSaved;
	private transient Physics2D physics;
	
	public Scene() {
		this(ID_COUNTER, "Scene-" + ID_COUNTER);
		ID_COUNTER++;
	}
	
	public Scene(@NotNull String name) {
		this(ID_COUNTER, name);
		ID_COUNTER++;
	}
	
	private Scene(int uid, String name) {
		this.uid = uid;
		this.name = name;
		
		loaded = false;
		isSaved = false;
		
		physics = new Physics2D();
		gameObjects = new ArrayList<>();
		
		cameras = new ArrayList<>();
		currentCamera = new Camera(AspectRatio.ASPECT_16_9.getAspectValue());
		
		renderer = new Renderer(GameEngineManager.sourceManager().getShader("default"));
		properties = SceneProperties.getDefaultProperties();
	}
	
	public void init() { }
	
	public void stop() { }
	
	public void destroy() {
		for(int i = 0; i < gameObjects.size(); i++) {
			gameObjects.get(i).destroy();
		}
	}
	
	public void engineUpdate() {
		Iterator<GameObject> iterator = gameObjects.iterator();
		while(iterator.hasNext()) {
			GameObject obj = iterator.next();
			obj.engineUpdate();
		}
		
		for(Camera camera : cameras) {
			Position camPos = camera.getPosition();
			Rotation camRot = camera.getRotation();
			
			EditorDebugDraw.addLine3D(camera.getPosition(), new Vector3f(
					(float) (camPos.x + Math.sin(Math.toRadians(camRot.x))),
					(float) (camPos.y - Math.sin(Math.toRadians(camRot.y))),
					(float) (camPos.z - Math.cos(Math.toRadians(camRot.x)))), Color.YELLOW);
		}
	}
	
	public void engineRender(@NotNull Camera camera) {
		renderer.render(camera);
	}
	
	public void update() {
		physics.update();
		for(Camera cam : cameras) {
			cam.update();
		}
		Iterator<GameObject> iterator = gameObjects.iterator();
		while(iterator.hasNext()) {
			GameObject obj = iterator.next();
//			if(obj.isDead()) {
//				renderer.removeGameObject(obj);
//				physics.removeGameObject(obj);
//				iterator.remove();
//				continue;
//			}
			obj.update();
		}
	}
	
	public void render() {
		renderer.render(currentCamera);
	}
	
	public void load() {
		if(loaded)
			throw new IllegalStateException("You can't load two times the same Scene");
		
		init();
		for(GameObject object : gameObjects) {
			loadGameObject(object);
		}
		loaded = true;
	}
	
	private void loadGameObject(@NotNull GameObject object) {
		object.load();
		renderer.addGameObject(object);
		physics.addGameObject(object);
		for(GameObject obj : object.getChilds()) {
			loadGameObject(obj);
		}
	}
	
	@Override
	public void addGameObject(@NotNull GameObject object) {
		if(gameObjects.contains(object))
			return;
		if(loaded)
			loadGameObject(object);
		gameObjects.add(object);
	}

	@Override
	public void removeGameObject(@NotNull GameObject object) {
		gameObjects.remove(object);
	}
	
	public void addCamera(@NotNull Camera camera) {
		if(cameras.contains(camera))
			return;
		cameras.add(camera);
	}
	
	public void removeCamera(@NotNull Camera camera) {
		if(this.currentCamera == camera)
			this.currentCamera = null;
		cameras.remove(camera);
	}

	@NotNull
	public String getName() {
		return name;
	}
	
	public int getUID() {
		return uid;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean isSaved() {
		return isSaved;
	}
	
	@NotNull
	public List<GameObject> getObjects() {
		return gameObjects;
	}
	
	@NotNull
	public Physics2D getPhysics() {
		return physics;
	}
	
	@NotNull
	public SceneProperties getProperties() {
		return properties;
	}
	
	@NotNull
	public List<Camera> getCameras() {
		return cameras;
	}
	
	@Nullable
	public Camera getCurrentCamera() {
		return currentCamera;
	}
	
	public void setCurrentCamera(@NotNull Camera camera) {
		currentCamera = camera;
	}
	
	public void dispose() {
		renderer.dispose();
	}

	static void saveScene(@NotNull File sceneFile, @NotNull Scene scene) throws IOException {
		sceneFile.mkdirs();
		File sceneObjsFile = getSceneObjsFile(sceneFile);
		File scenePropertiesFile = getScenePropertiesFile(sceneFile);
		if(!sceneObjsFile.exists())
			sceneObjsFile.createNewFile();
		if(!scenePropertiesFile.exists())
			scenePropertiesFile.createNewFile();
		saveObjects(new FileOutputStream(sceneObjsFile), scene);
		saveSceneProperties(new FileOutputStream(scenePropertiesFile), scene);
	}
	
	@NotNull
	static Scene loadScene(@NotNull File sceneFile) {
		File sceneObjsFile = getSceneObjsFile(sceneFile);
		File scenePropertiesFile = getScenePropertiesFile(sceneFile);
		Scene scene = new Scene();
		
		try {
			loadObjects(new FileInputStream(sceneObjsFile), scene);
		} catch (Exception e) { }
		try {
			loadSceneProperties(new FileInputStream(scenePropertiesFile), scene);
		} catch (Exception e) { }
		
		return scene;
	}
	
	@NotNull
	static Scene reloadScene(@NotNull File sceneFile, @NotNull Scene origScene) throws IOException {
		File sceneObjsFile = getSceneObjsFile(sceneFile);
		File scenePropertiesFile = getScenePropertiesFile(sceneFile);
		
		origScene.renderer.dispose();
		
		loadObjects(new FileInputStream(sceneObjsFile), origScene);
		loadSceneProperties(new FileInputStream(scenePropertiesFile), origScene);
		
		return origScene;
	}
	
	private static void saveSceneProperties(@NotNull OutputStream stream, @NotNull Scene scene) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream));
		Gson gson = new GsonBuilder().create();
		
		JsonObject json = new JsonObject();
		json.add("properties", gson.toJsonTree(scene.properties));
		
		out.write(json.toString());
		out.flush();
		out.close();
	}
	
	private static void saveObjects(@NotNull OutputStream stream, @NotNull Scene scene) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream));
		Gson gson = getGson();
		
		JsonObject json = new JsonObject();
		
		JsonElement objects = gson.toJsonTree(scene.gameObjects);
		JsonElement cameras = gson.toJsonTree(scene.cameras);
		
		json.add("objects", objects);
		json.add("cameras", cameras);
		json.add("current-camera", new JsonPrimitive(scene.currentCamera.getName()));
		
		out.write(gson.toJson(json));
		out.flush();
		out.close();
	}
	
	private static void loadSceneProperties(@NotNull InputStream stream, @NotNull Scene scene) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		Gson gson = getGson();
		
		String line = "";
		String data = "";
		
		while((line = in.readLine()) != null) {
			data += line.concat("\n");
		}
		
		in.close();
		
		JsonObject json = (JsonObject) JsonParser.parseString(data);
		
		JsonElement jsonProperties = json.get("properties");
		
		SceneProperties properties = gson.fromJson(jsonProperties, SceneProperties.class);
		scene.properties = properties;
	}
	
	private static void loadObjects(@NotNull InputStream stream, @NotNull Scene scene) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		Gson gson = getGson();
		
		String line = "";
		String data = "";
		
		while((line = in.readLine()) != null) {
			data += line.concat("\n");
		}
		
		in.close();
		
		JsonObject json = gson.fromJson(data, JsonObject.class);
		
		GameObject[] gameObjs = gson.fromJson(json.get("objects"), GameObject[].class);
		Camera[] cameras = gson.fromJson(json.get("cameras"), Camera[].class);
		JsonPrimitive currentCameraName = json.getAsJsonPrimitive("current-camera");
		
		scene.gameObjects.clear();
		scene.cameras.clear();
		
		for(GameObject obj : gameObjs) {
			scene.addGameObject(obj);
		}
		for(Camera camera : cameras) {
			if(camera.getName().equals(currentCameraName.getAsString()))
				scene.currentCamera = camera;
			camera.makeProjection();
			scene.addCamera(camera);
		}
	}
	
	@NotNull
	private static File getSceneObjsFile(@NotNull File root) {
		return new File(root, "objects.json");
	}
	
	@NotNull
	private static File getScenePropertiesFile(@NotNull File root) {
		return new File(root, "scene.json");
	}
	
	@NotNull
	private static Gson getGson() {
		return new GsonBuilder()
				.registerTypeAdapter(Component.class, new ComponentSerializator())
				.registerTypeAdapter(GameObject.class, new GameObjectSerializator())
				.create();
	}
	
}
