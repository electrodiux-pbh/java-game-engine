package com.gameengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.gameengine.data.SourceManager;
import com.gameengine.util.IllegalProjectException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class Project {

	private boolean saved = false;
	private File projectLocation;
	private File sourcesLocation;
	private ProjectProperties properties;
	
	private String name;
	
	private Map<Integer, Scene> scenes = new HashMap<>();
	
	private Project(@NotNull ProjectArguments args) throws IllegalProjectException {
		this.name = args.getName();
		this.properties = ProjectProperties.getDefaultProperties();
		this.properties.load();
	}
	
	private Project(@NotNull File loadDataPath) throws IllegalProjectException {
		try {
			this.projectLocation = loadDataPath;
			this.sourcesLocation = new File(this.projectLocation, "sources");
			
			File projectFile = new File(projectLocation, ".project");
			File propertiesFile = new File(projectLocation, "properties.json");
			
			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(projectFile));
			
			Object name = json.get("name");
			Object sceneList = json.get("scenes");
			
			if(name == null || sceneList == null)
				throw new IllegalProjectException("The .project file no contains all nead data");
			if(!(name instanceof String) || !(sceneList instanceof JSONArray))
				throw new IllegalProjectException("The .project file have corrupted data");
			
			this.name = name.toString();
			
			JSONArray scenes = (JSONArray) sceneList;
			for(Object sceneName : scenes) {
				Scene scene = loadScene(sceneName.toString());
				this.scenes.put(scene.getUID(), scene);
			}
			
			Gson gson = new GsonBuilder().create();
			if(propertiesFile.exists()) {
				try {
					String data = SourceManager.readStringStream(new FileInputStream(propertiesFile));
					properties = gson.fromJson(data, ProjectProperties.class);
				} catch (Exception e) { e.printStackTrace(); }
			}
			if(properties == null)
				properties = ProjectProperties.getDefaultProperties();
			
			properties.load();
		} catch (Exception e) {
			throw new IllegalProjectException("An error ocurred loading the project", e);
		}
	}
	
	public int addScene(@NotNull Scene scene) {
		scenes.put(scene.getUID(), scene);
		return scene.getUID();
	}
	
	boolean save() throws IOException {
		if(getProjectLocation() == null)
			return false;
		projectLocation.mkdirs();
		
		try {
			saveProjectFile();
		} catch (Exception e) {
			return false;
		}
		
		for(Scene scene : scenes.values()) {
			saveScene(scene);
		}
		saved = true;
		return saved;
	}
	
	@SuppressWarnings("unchecked")
	private void saveProjectFile() throws IOException {
		File projectFile = new File(projectLocation, ".project");
		File propertiesFile = new File(projectLocation, "properties.json");
		if(!projectFile.exists())
			projectFile.createNewFile();
		if(!propertiesFile.exists())
			propertiesFile.createNewFile();
		
		JSONObject json = new JSONObject();
		
		json.put("name", name);
		
		JSONArray sceneList = new JSONArray();
		for(Scene scene : scenes.values()) {
			sceneList.add(scene.getName());
		}
		
		json.put("scenes", sceneList);
		
		Gson gson = new GsonBuilder().create();
		SourceManager.writeStringStream(new FileOutputStream(projectFile), json.toJSONString());
		SourceManager.writeStringStream(new FileOutputStream(propertiesFile), gson.toJson(properties));
		
	}
	
	public boolean saveScene(@NotNull Scene scene) throws IOException {
		if(getProjectLocation() == null)
			return false;
		if(scenes.containsValue(scene)) {
			File sceneFile = new File(projectLocation, "scenes/" + scene.getName());
			sceneFile.mkdirs();
			Scene.saveScene(sceneFile, scene);
		}
		return true;
	}
	
	public Scene loadScene(String sceneName) throws JsonSyntaxException, IOException {
		return Scene.loadScene(new File(projectLocation, "scenes/" + sceneName));
	}
	
	public boolean isSaved() {
		return saved;
	}
	
	public void setProjectLocation(@NotNull File location) {
		location.mkdirs();
		projectLocation = location;
	}
	
	public File getProjectLocation() {
		return projectLocation;
	}
	
	public File getSourcesLocation() {
		return sourcesLocation;
	}
	
	public void setSourcesLocation(File sourcesLocation) {
		this.sourcesLocation = sourcesLocation;
	}
	
	Map<Integer, Scene> getScenes() {
		return scenes;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(@Nullable String name) {
		if(name == null)
			return;
		this.name = name;
	}
	
	public ProjectProperties getProperties() {
		return properties;
	}
	
	static Project createNewProject(@NotNull ProjectArguments args) throws IllegalProjectException {
		return new Project(args);
	}
	
	/**
	 * 
	 * @param loadDataPath
	 * @return
	 * @throws IllegalProjectException 
	 */
	static Project loadProject(@NotNull File loadDataPath) throws IllegalProjectException {
		return new Project(loadDataPath);
	}
	
}
