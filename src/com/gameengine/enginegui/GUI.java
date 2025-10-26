package com.gameengine.enginegui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gameengine.Engine;
import com.gameengine.Project;
import com.gameengine.ProjectProperties;
import com.gameengine.Scene;
import com.gameengine.components.GameObject;
import com.gameengine.enginegui.windows.CreateProjectWindow;
import com.gameengine.enginegui.windows.PopupWindow;
import com.gameengine.graphics.AspectRatio;
import com.gameengine.graphics.Color;
import com.gameengine.graphics.Sprite;
import com.gameengine.languages.Language;
import com.gameengine.languages.Languages;
import com.gameengine.program.Script;
import com.gameengine.util.Camera;
import com.gameengine.util.Console;
import com.gameengine.util.Transform;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

public class GUI {

	public static final String OBJECT_PAYLOAD_DRAGDROP_TYPE = "object-dragdrop";
	public static final String CAMERA_PAYLOAD_DRAGDROP_TYPE = "camera-dragdrop";
	public static final String SCRIPT_PAYLOAD_DRAGDROP_TYPE = "script-dragdrop";
	
	private static int defaultColumWidth = 100;
	private static float dragVectorFloat = 0.05F;
	
	private static PopupWindow popupWindow;
	
	public static void showTransform(@Nullable Transform transform) {
		Language lang = Engine.getEngineLanguage();
		if(transform == null) {
			ImGui.text(lang.get("general.properties.transform.no-tranform"));
			return;
		}
		GUI.renderVector3f(lang.get("general.properties.position"), transform.position, 0.01F);
		GUI.renderVector3f(lang.get("general.properties.local-scale"), transform.localScale, 0.01F);
		GUI.renderVector3f(lang.get("general.properties.rotation"), transform.rotation, 0.01F);
	}
	
	public static String inputText(@Nullable String label, @Nullable String text) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumWidth);
		ImGui.text(label);
		ImGui.nextColumn();
		
		ImString outString = new ImString(text, 128);
		if(ImGui.inputText("##" + label, outString)) {
			ImGui.columns(1);
			ImGui.popID();
			
			return outString.get();
		}
		
		ImGui.columns(1);
		ImGui.popID();
		
		return text;
	}
	
	public static String inputPassword(@Nullable String label, @Nullable String text) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumWidth);
		ImGui.text(label);
		ImGui.nextColumn();
		
		ImString outString = new ImString(text, 128);
		if(ImGui.inputText("##" + label, outString)) {
			ImGui.columns(1);
			ImGui.popID();
			
			return outString.get();
		}
		
		ImGui.columns(1);
		ImGui.popID();
		
		return text;
	}
	
	public static void renderVector2f(@NotNull String name, @NotNull Vector2f vector) {
		renderVector2f(name, vector, dragVectorFloat);
	}
	
	public static void renderVector2f(@NotNull String name, @NotNull Vector2f vector, float dragVectorFloat) {
		ImGui.pushID(name);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumWidth);
		ImGui.text(name);
		ImGui.nextColumn();
		
		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
		
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0F;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0F, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0F) / 2.0F;
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("X", buttonSize.x, buttonSize.y)) {
			vector.x = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueX = { vector.x };
		if(ImGui.dragFloat("##x", vecValueX, dragVectorFloat)) {
			vector.x = vecValueX[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("Y", buttonSize.x, buttonSize.y)) {
			vector.y = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueY = { vector.y };
		if(ImGui.dragFloat("##y", vecValueY, dragVectorFloat)) {
			vector.y = vecValueY[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.nextColumn();
		ImGui.popStyleVar();
		ImGui.columns(1);
		ImGui.popID();
	}
	
	public static void renderVector3f(@NotNull String name, @NotNull Vector3f vector) {
		renderVector3f(name, vector, dragVectorFloat);
	}
	
	public static void renderVector3f(@NotNull String name, @NotNull Vector3f vector, float dragVectorFloat) {
		ImGui.pushID(name);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumWidth);
		ImGui.text(name);
		ImGui.nextColumn();
		
		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
		
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0F;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0F, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0F) / 3.0F;
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("X", buttonSize.x, buttonSize.y)) {
			vector.x = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueX = { vector.x };
		if(ImGui.dragFloat("##x", vecValueX, dragVectorFloat)) {
			vector.x = vecValueX[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("Y", buttonSize.x, buttonSize.y)) {
			vector.y = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueY = { vector.y };
		if(ImGui.dragFloat("##y", vecValueY, dragVectorFloat)) {
			vector.y = vecValueY[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("Z", buttonSize.x, buttonSize.y)) {
			vector.z = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueZ = { vector.z };
		if(ImGui.dragFloat("##z", vecValueZ, dragVectorFloat)) {
			vector.z = vecValueZ[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.nextColumn();
		ImGui.popStyleVar();
		ImGui.columns(1);
		ImGui.popID();
	}
	
	public static void renderVector4f(@NotNull String name, @NotNull Vector4f vector) {
		renderVector4f(name, vector, dragVectorFloat);
	}
	
	public static void renderVector4f(@NotNull String name, @NotNull Vector4f vector, float dragVectorFloat) {
		ImGui.pushID(name);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumWidth);
		ImGui.text(name);
		ImGui.nextColumn();
		
		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
		
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0F;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0F, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 4.0F) / 4.0F;
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("X", buttonSize.x, buttonSize.y)) {
			vector.x = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueX = { vector.x };
		if(ImGui.dragFloat("##x", vecValueX, dragVectorFloat)) {
			vector.x = vecValueX[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("Y", buttonSize.x, buttonSize.y)) {
			vector.y = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueY = { vector.y };
		if(ImGui.dragFloat("##y", vecValueY, dragVectorFloat)) {
			vector.y = vecValueY[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("Z", buttonSize.x, buttonSize.y)) {
			vector.z = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueZ = { vector.z };
		if(ImGui.dragFloat("##z", vecValueZ, dragVectorFloat)) {
			vector.z = vecValueZ[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		if(ImGui.button("W", buttonSize.x, buttonSize.y)) {
			vector.w = 0.0F;
		}
		ImGui.sameLine();
		float[] vecValueW = { vector.w };
		if(ImGui.dragFloat("##w", vecValueW, dragVectorFloat)) {
			vector.w = vecValueW[0];
		};
		ImGui.popItemWidth();
		ImGui.sameLine();
	
		ImGui.nextColumn();
		ImGui.popStyleVar();
		ImGui.columns(1);
		ImGui.popID();
	}
	
	public static boolean spriteButton(@NotNull Sprite sprite) {
		return spriteButton(sprite, sprite.getWidth(), sprite.getHeight());
	}
	
	public static boolean spriteButton(@NotNull Sprite sprite, float width, float height) {
		Vector2f[] textCoords = sprite.getTextureCoords();
		return ImGui.imageButton(sprite.getTextureID(), width, height, textCoords[0].x, textCoords[0].y, textCoords[2].x, textCoords[2].y, 0, 1.0F, 1.0F, 1.0F, 0.0F);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object renderEnum(@NotNull String label, @Nullable Object value) {
		if(value instanceof Enum) {
			Class<?> clazz = value.getClass();
			String[] values = getEnumValues((Class<Enum>) clazz);
			ImInt index = new ImInt(indexOf(((Enum)value).name(), values));
			if(ImGui.combo(label, index, values, values.length)) {
				return clazz.getEnumConstants()[index.get()];
			}
		}
		return null;
	}
	
	private static <E extends Enum<E>> String[] getEnumValues(Class<E> clazz) {
		String[] values = new String[clazz.getEnumConstants().length];
		int i = 0;
		for(E value : clazz.getEnumConstants()) {
			values[i] = value.name();
			i++;
		}
		return values;
	}
	
	private static int indexOf(String str, String[] array) {
		for(int i = 0; i < array.length; i++) {
			if(str.equals(array[i]))
				return i;
		}
		return -1;
	}
	
	public static void renderScriptable(@NotNull com.gameengine.data.Scriptable  scriptable) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.button(lang.get("general.remove") + "###remove")) {
			scriptable.setScript(null);
		}
		ImGui.sameLine();
		ImGui.text(lang.get("general.properties.scriptable.script") + ": ");
		if(ImGui.beginDragDropTarget()) {
			Object script = ImGui.acceptDragDropPayload(SCRIPT_PAYLOAD_DRAGDROP_TYPE);
			if(script != null) {
				if(script instanceof Script)
					scriptable.setScript((Script) script);
				}
			ImGui.endDragDropTarget();
		}
		if(scriptable.getScript() != null) {
			ImGui.sameLine();
			ImGui.text(scriptable.getScript().getClass().getSimpleName());
			ImGui.text(lang.get("general.properties.scriptable.properties") + ":");
			GUI.renderObjectProperties(scriptable.getScript());
		}
	}
	
	public static boolean colorPicker(Color color) {
		return colorPicker(Engine.getEngineLanguage().get("general.color-picker"), color);
	}
	
	public static boolean colorPicker(@NotNull String label, Color color) {
		float[] colorPick = { color.x, color.y, color.z, color.w };
		if(ImGui.colorPicker4(label, colorPick)) {
			color.set(colorPick);
			return true;
		}
		return false;
	}
	
	public static void renderObjectProperties(@NotNull Object obj) {
		try {
			Field[] fields = obj.getClass().getFields();
			for(Field field : fields) {
				int modifiers = field.getModifiers();
				if(Modifier.isTransient(modifiers) || Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers))
					continue;
				Class<?> clazz = field.getType();
				Object value = field.get(obj);
				String name = field.getName();
				
				if(clazz.isAssignableFrom(GameObject.class) || clazz.equals(GameObject.class)) {
					ImGui.text(name + ": ");
					if(ImGui.beginDragDropTarget()) {
						GameObject gameObj = ImGui.acceptDragDropPayload(OBJECT_PAYLOAD_DRAGDROP_TYPE, GameObject.class);
						if(gameObj != null) {
							if(gameObj.getClass().isAssignableFrom(clazz)) {
								field.set(obj, gameObj);
							}
						}
						ImGui.endDragDropTarget();
					}
					if(value != null) {
						ImGui.sameLine();
						ImGui.text(((GameObject) value).getName());
					}
					return;
				}
				
				if(clazz.isEnum()) {
					Object newValue = GUI.renderEnum(name, value);
					if(newValue != null) {
						field.set(obj, newValue);
					}
					return;
				}
				
				switch(clazz.getName()) {
				case "int":
					int intValue = (int) value;
					int[] intModified = { intValue };
					if(ImGui.dragInt(" : " + name, intModified))
						field.set(obj, intModified[0]);
					break;
				case "float":
					float floatValue = (float) value;
					float[] floatModified = { floatValue };
					if(ImGui.dragFloat(" : " + name, floatModified))
						field.set(obj, floatModified[0]);
					break;
				case "boolean":
					boolean booleanValue = (boolean) value;
					if(ImGui.checkbox(" : " + name, booleanValue))
						field.set(obj, !booleanValue);
					break;
				case "org.joml.Vector2f":
					GUI.renderVector2f(name, (Vector2f) value);
					break;
				case "org.joml.Vector3f":
					GUI.renderVector3f(name, (Vector3f) value);
					break;
				case "org.joml.Vector4f":
					GUI.renderVector4f(name, (Vector4f) value);
					break;
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void renderProjectProperties(@NotNull ProjectProperties properties) {
		Object aspect = GUI.renderEnum(" : " + Engine.getEngineLanguage().get("general.properties.aspectratio") + "###aspectratio", properties.getAspectRatio());
		if(aspect != null)
			properties.setAspectRatio((AspectRatio) aspect);
	}
	
	@Nullable
	public static PopupWindow getPopupWindow() {
		return popupWindow;
	}

	public static void setPopupWindow(@Nullable PopupWindow popupWindow) {
		if(GUI.popupWindow != null)
			GUI.popupWindow.disable();
		GUI.popupWindow = popupWindow;
		if(popupWindow != null)
			popupWindow.onLoad();
	}
	
	public static File getFile(@Nullable File startPath, @Nullable FilenameFilter filter) {
		return getFile(startPath, filter, null);
	}
	
	public static File getFile(@Nullable File startPath, @Nullable FilenameFilter filter, @Nullable String name) {
		FileDialog fd = new FileDialog((Frame) null);
		
		if(name != null)
			fd.setTitle(name);
		
		if(startPath != null) {
			if(startPath.isFile()) {
				fd.setDirectory(startPath.getParent());
			} else {
				fd.setDirectory(startPath.getAbsolutePath());
			}
		}
		
		if(filter != null) {
			fd.setFilenameFilter(filter);
		}
		
		fd.setVisible(true);
		
		String dir = fd.getDirectory();
		String file = fd.getFile();
		
		if(dir != null && file != null) {
			return new File(dir + file);
		}
		return null;
	}
	
	public static void createProject() {
		CreateProjectWindow window = new CreateProjectWindow(420, 360);
		GUI.setPopupWindow(window);
	}
	

	
	private static File loadProjectGUI() {
		FileDialog fd = new FileDialog((Frame) null, "Load Project");
		fd.setVisible(true);
		
		String dir = fd.getDirectory();
		String file = fd.getFile();
		
		if(dir != null && file != null) {
			return new File(dir);
		}
		return null;
	}
	
	private static File saveProjectAsGUI() {
		FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
		fd.setVisible(true);
		
		String dir = fd.getDirectory();
		String file = fd.getFile();
		
		if(dir != null && file != null) {
			return new File(dir);
		}
		return null;
	}
	
	public static File getFile(@Nullable File startPath) {
		return getFile(startPath, null);
	}
	
	public static File getFile(@Nullable FilenameFilter filter) {
		return getFile(null, filter);
	}
	
	public static void mainMenuBar() {
		if(ImGui.beginMainMenuBar()) {
			Language lang = Engine.getEngineLanguage();
			if(ImGui.beginMenu(lang.get("menu.bar.file") + "###file")) {
				if(ImGui.beginMenu(lang.get("general.new") + "###new")) {
					if(ImGui.menuItem(lang.get("menu.bar.file.new.project") + "###project")) {
						GUI.createProject();
					}
					ImGui.endMenu();
				}
				ImGui.separator();
				if(ImGui.menuItem(lang.get("menu.bar.file.open-project") + "###open-project")) {
					loadProject();
				}
				ImGui.separator();
				if(ImGui.menuItem(lang.get("menu.bar.file.save") + "###save", "Ctr+S", false, Engine.getProject() != null)) {
					try {
						if(!Engine.saveProject()) {
							saveProjectAs();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(ImGui.menuItem(lang.get("menu.bar.file.saveas") + "###saveas", null, false, Engine.getProject() != null)) {
					saveProjectAs();
				}
				
				ImGui.endMenu();
			}

			if(ImGui.beginMenu(lang.get("menu.bar.windows") + "###window")) {
				if(ImGui.beginMenu(lang.get("menu.bar.windows.lang") + "###languages")) {
					try {
						Languages langValue = Languages.valueOf(Engine.getEngineLanguage().getName().toUpperCase());
						
						Object value = GUI.renderEnum("", langValue);
						if(value != null) {
							try {
								Engine.setEngineLanguage(new Language((Languages) value));
								Console.out.println("The language '" + ((Languages) value).getName() + "' has been loaded.");
							} catch (IOException e) {
								Console.warn.println("You cannot load that language because an IO error occurs: " + e.getMessage());
							} catch (ParseException e) {
								Console.warn.println("You cannot load that language because the file format contains errors: " + e.getMessage());
							}
						}
					} catch (IllegalArgumentException e) { }
					ImGui.endMenu();
				}
				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
	}
	
	public static void consoleGUI() {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.console") + "###console")) {
			if(ImGui.button(lang.get("window.console.button.clear") + "###clear")) {
				Console.clearLinesBuffer();
			}
			
			ImGui.separator();
			ImGui.beginChild("scrolling");
			Console.foreachLinesBuffer((line) -> {
				ImGui.textUnformatted(line);
			});
			ImGui.endChild();
		}
		ImGui.end();
	}
	
	private static void loadProject() {
		File path = GUI.loadProjectGUI();
		if(path != null) {
			Engine.loadProject(path);
		}
	}
	
	private static void saveProjectAs() {
		File path = GUI.saveProjectAsGUI();
		if(path != null) {
			try {
				if(!Engine.saveProjectAs(path)) {
					Console.warn.println("An error ocurred, project not saved!");
				} else {
					Console.out.println("The project saved correctly at: " + path.getAbsolutePath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void projectExplorerWindow(@Nullable Project project) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.project-explorer") + "###project-explorer")) {
			if(project != null) {
				project.setName(GUI.inputText(lang.get("general.name") + ": ", project.getName()));
				if(ImGui.collapsingHeader(lang.get("general.properties") + "###properties", ImGuiTreeNodeFlags.DefaultOpen)) {
					GUI.renderProjectProperties(project.getProperties());
				}
			} else {
				ImGui.text(lang.get("window.project-explorer.no-loaded"));
			}
		}
		ImGui.end();
	}
	
	public static void scenesWindow(@Nullable Collection<Scene> scenes) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.scenes") + "###scenes")) {
			if(scenes != null) {
				for(Scene scene : scenes) {
					if(ImGui.collapsingHeader(scene.getName(), ImGuiTreeNodeFlags.DefaultOpen)) {
						if(ImGui.treeNode(lang.get("general.properties") + "###properties")) {
							float[] gravityScaleModified = { scene.getProperties().getGravityScale() };
							if(ImGui.dragFloat(" : Gravity", gravityScaleModified, 0.1F))
								scene.getProperties().setGravityScale(gravityScaleModified[0]);
							ImGui.text(lang.get("general.objects.camera") + ": ");
							if(ImGui.beginDragDropTarget()) {
								Object cam = ImGui.acceptDragDropPayload(CAMERA_PAYLOAD_DRAGDROP_TYPE);
								if(cam != null) {
									if(cam instanceof Camera)
										scene.setCurrentCamera((Camera) cam);
								}
								ImGui.endDragDropTarget();
							}
							if(scene.getCurrentCamera() != null) {
								ImGui.sameLine();
								ImGui.text(scene.getCurrentCamera().getName());
							}
							ImGui.treePop();
						}
						if(ImGui.treeNode(lang.get("window.scenes.objects") + "###objects")) {
							if(ImGui.beginPopupContextItem("ObjectActions")) {
								if(ImGui.menuItem(lang.get("general.add") + " " + lang.get("general.objects.gameobject") + "###add-object")) {
									scene.addGameObject(new GameObject());
								}
								if(ImGui.menuItem(lang.get("general.add") + " " + lang.get("general.objects.camera") + "###add-camera")) {
									scene.addCamera(new Camera());
								}
								ImGui.endPopup();
							}
							
							int index = 0;
							List<Camera> cameras = scene.getCameras();
							
							while(index < cameras.size()) {
								Camera cam = cameras.get(index);
								
								ImGui.pushID(index);
								if(ImGui.selectable(cam.getName(), Engine.getEngineProperties().getCurrentInspectable() == cam)) {
									Engine.getEngineProperties().setCurrentInspectable(Engine.getEngineProperties().getCurrentInspectable() == cam ? null : cam);
								}
								if(ImGui.beginDragDropSource()) {
									ImGui.setDragDropPayload(CAMERA_PAYLOAD_DRAGDROP_TYPE, cam);
									ImGui.text(cam.getName());
									ImGui.endDragDropSource();
								}
								if(ImGui.beginPopupContextItem("CameraActions")) {
									if(ImGui.menuItem(lang.get("general.remove") + " " + cam.getName())) {
										index--;
									}
									ImGui.endPopup();
								}
								ImGui.popID();
								
								index++;
							}
							
							for(int i = 0; i < scene.getObjects().size(); i++) {
								GameObject obj = scene.getObjects().get(i);
								ImGui.pushID(i + index);
								boolean nodeOpen = gameObjectTreeNode(obj);
								if(ImGui.beginPopupContextItem("ObjectActions")) {
									if(ImGui.menuItem(lang.get("general.remove") + " " + obj.getName())) {
										obj.destroy();
										i--;
									}
									ImGui.endPopup();
								}
								if(nodeOpen) {
									Engine.getEngineProperties().setCurrentInspectable(Engine.getEngineProperties().getCurrentInspectable() == obj ? null : obj);
								}
								ImGui.popID();
							}
							ImGui.treePop();
						}
					}
				}
			} else {
				ImGui.text(lang.get("window.scenes.no-scenes"));
			}
		}
		ImGui.end();
	}
	
	private static boolean gameObjectTreeNode(@NotNull GameObject obj) {
		
		boolean opened = ImGui.selectable(obj.getName(), Engine.getEngineProperties().getCurrentInspectable() == obj);
		
		if(ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload(OBJECT_PAYLOAD_DRAGDROP_TYPE, obj);
			ImGui.text(obj.getName());
			ImGui.endDragDropSource();
		}
		
		if(ImGui.beginDragDropTarget()) {
			GameObject gameObj = ImGui.acceptDragDropPayload(OBJECT_PAYLOAD_DRAGDROP_TYPE, GameObject.class);
			if(gameObj != null) {
				Console.out.println("Payload accepted '" + gameObj.getName() + "'");
			}
			ImGui.endDragDropTarget();
		}
		
		return opened;
	}
	
	public static void inspectorWindow(@Nullable Inspectable obj) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.inspector") + "###inspector")) {
			if(obj != null) {
				obj.gui();
			} else {
				ImGui.text(lang.get("window.inspector.no-object-selected"));
			}
		}
		ImGui.end();
	}
	
	public static void fileExplorer(@Nullable GUIFileExplorer explorer) {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.begin(lang.get("window.file-explorer") + "###file-explorer")) {
			if(explorer != null) {
				explorer.update();
			} else {
				ImGui.text(lang.get("window.file-explorer.no-file-explorer"));
			}
		}
		ImGui.end();
	}
	
	@SuppressWarnings("unchecked")
	public static void configureStyle(@NotNull ImGuiStyle style, @NotNull String data) throws ParseException {
		Object json = new JSONParser().parse(data);
		
		if(json == null)
			throw new IllegalArgumentException("The data for the styles have not a correct format");
		
		JSONObject obj = (JSONObject) json;
		
		try {
			if(JSONObject.class.isAssignableFrom(json.getClass())) {
				JSONObject colors = (JSONObject) obj.get("colors");
				colors.forEach((property, colorData) -> {
					Color color = new Color();
					((JSONObject)colorData).forEach((canal, value) -> {
						try {
							switch(canal.toString()) {
							case "r": color.x = Float.parseFloat(value.toString()); break;
							case "g": color.y = Float.parseFloat(value.toString()); break;
							case "b": color.z = Float.parseFloat(value.toString()); break;
							case "a": color.w = Float.parseFloat(value.toString()); break;
							}
						} catch (NumberFormatException e) { }
					});
					try {
						style.setColor(Integer.parseInt(property.toString()), color.x, color.y, color.z, color.w);
					} catch (NumberFormatException e) { }
				});
			}
		} catch(Exception e) {
			throw new IllegalStateException("An error occured loading the style", e);
		}
		
		Object frameRounding = obj.get("frameRounding");
		Object grabRounding = obj.get("grabRounding");
		Object frameBorderSize = obj.get("frameBorderSize");
		Object windowMenuButtonPosition = obj.get("windowMenuButtonPosition");
		
		if(frameRounding != null)
			try { style.setFrameRounding(Float.parseFloat(frameRounding.toString())); } catch (NumberFormatException e) { }
		if(grabRounding != null)
			try { style.setGrabRounding(Float.parseFloat(grabRounding.toString())); } catch (NumberFormatException e) { }
		if(frameBorderSize != null)
			try { style.setFrameBorderSize(Float.parseFloat(frameBorderSize.toString())); } catch (NumberFormatException e) { }
		if(windowMenuButtonPosition != null)
			try { style.setWindowMenuButtonPosition(Integer.parseInt(windowMenuButtonPosition.toString())); } catch (NumberFormatException e) { }
		
	}
	
	@SuppressWarnings("unchecked")
	public static String styleToJson(@NotNull ImGuiStyle style) {
		JSONObject json = new JSONObject();
		JSONObject colorsJson = new JSONObject();
		
		float[][] colors = style.getColors();
		for(int i = 0; i < colors.length; i++) {
			JSONObject jsonColor = colorToJson(new Color(colors[i]));
			colorsJson.put(i, jsonColor);
		}
		
		json.put("colors", colorsJson);
		json.put("frameRounding", style.getFrameRounding());
		json.put("grabRounding", style.getGrabRounding());
		json.put("frameBorderSize", style.getFrameBorderSize());
		json.put("windowMenuButtonPosition", style.getWindowMenuButtonPosition());
		
		return json.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject colorToJson(@NotNull Color color) {
		JSONObject colorJson = new JSONObject();
		
		colorJson.put("r", color.x);
		colorJson.put("g", color.y);
		colorJson.put("b", color.z);
		colorJson.put("a", color.w);
		
		return colorJson;
	}
	
}
