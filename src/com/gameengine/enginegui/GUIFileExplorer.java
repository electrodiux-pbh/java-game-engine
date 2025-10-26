package com.gameengine.enginegui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.gameengine.Engine;
import com.gameengine.GameEngineManager;
import com.gameengine.audio.AudioException;
import com.gameengine.audio.Sound;
import com.gameengine.graphics.SpriteSheet;
import com.gameengine.languages.Language;
import com.gameengine.program.GameScript;
import com.gameengine.program.InvalidCodeException;

import imgui.ImGui;

public class GUIFileExplorer {

	private static final String FILE_PAYLOAD_DRAGDROP_TYPE = "file-dragdrop";
	
	private File rootFile;
	private Node currentNode;
	
	private SpriteSheet sprites;
	
	private Map<String, Class<? extends GameScript>> scripts;
	
	public GUIFileExplorer(@NotNull File rootFile) {
		scripts = new HashMap<>();
		this.setRootFile(rootFile);
		load();
	}
	
	public void update() {
		Language lang = Engine.getEngineLanguage();
		if(ImGui.beginPopupContextWindow("Files Options")) {
			if(ImGui.beginMenu(lang.get("general.new") + "###new")) {
				if(ImGui.menuItem(lang.get("window.file-explorer.folder") + "###folder")) {
					File file = new File(currentNode.file, "new-folder");
					file.mkdirs();
					currentNode.subNodes.add(file);
				}
				if(ImGui.menuItem(lang.get("window.file-explorer.file") + "###file")) {
					try {
						File file = new File(currentNode.file, "new-file.txt");
						file.getParentFile().mkdirs();
						file.createNewFile();
						currentNode.subNodes.add(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				ImGui.endMenu();
			}
			ImGui.endPopup();
		}
		
		if(ImGui.button(lang.get("window.file-explorer.refresh") + "###refresh")) {
			refresh();
		}
		ImGui.sameLine();
		ImGui.text(lang.get("window.file-explorer.currentpath") + ": " + currentNode.file.getAbsolutePath());
		ImGui.separator();
		renderNode(currentNode);
	}
	
	private void refresh() {
		currentNode = new Node(currentNode.file);
	}
	
	private void renderNode(Node node) {
		Language lang = Engine.getEngineLanguage();
		List<File> subNodes = node.subNodes;
		if(subNodes != null) {
			if(node.parent != null) {
				if(GUI.spriteButton(sprites.getSprite(1), 64, 64)) {
					currentNode = new Node(node.parent);
				}
				if(fileDragDropTarget(node.parent)) {
					refresh();
					return;
				}
				ImGui.sameLine();
			}
			for(int i = 0; i < subNodes.size(); i++) {
				File subNode = subNodes.get(i);
				ImGui.pushID(i);
				if(subNode.isDirectory()) {
					if(GUI.spriteButton(sprites.getSprite(0), 64, 64)) {
						currentNode = new Node(subNode);
					}
					if(fileDragDropTarget(subNode)) {
						refresh();
					}
					fileDragDropSource(subNode);
				} else {
					if(subNode.getName().endsWith(".ogg")) {
						if(GUI.spriteButton(sprites.getSprite(3), 64, 64)) {
							try {
								Sound sound = GameEngineManager.sourceManager().getSound(subNode.getAbsolutePath());
								if(sound == null) {
									GameEngineManager.sourceManager().loadSound(subNode.getAbsolutePath(), new Sound(subNode.getAbsolutePath(), false));
									sound = GameEngineManager.sourceManager().getSound(subNode.getAbsolutePath());
								}
								if(sound.isPlaying()) {
									sound.stop();
								} else {
									sound.play(false);
								}
							} catch (AudioException e) {
								e.printStackTrace();
							}
						}
						fileDragDropSource(subNode);
					} else if(subNode.getName().endsWith(".jar")) {
						GUI.spriteButton(sprites.getSprite(2), 64, 64);
						Class<? extends GameScript> scriptClass = scripts.get(subNode.getAbsolutePath());
						if(scriptClass != null) {
							GameScript script = null;
							if(ImGui.beginDragDropSource()) {
								try {
									script = Engine.getGameScript(scriptClass);
								} catch (InvalidCodeException | IOException e) { }
								if(script != null) {
									ImGui.setDragDropPayload(GUI.SCRIPT_PAYLOAD_DRAGDROP_TYPE, script);
									ImGui.text(lang.get("window.file-explorer.file") + ": " + subNode.getName());
									ImGui.text(lang.get("general.class") + ": " + script.getClass().getName() + ".class");
								}
								ImGui.endDragDropSource();
							}
						}
					} else {
						GUI.spriteButton(sprites.getSprite(4), 64, 64);
						fileDragDropSource(subNode);
					}
				}
				ImGui.sameLine();
				if(ImGui.beginPopupContextItem("File Options")) {
					if(ImGui.menuItem(lang.get("general.remove") + "###remove")) {
						if(subNode.delete()) {
							subNodes.remove(i);
							i--;
						}
					}
					ImGui.separator();
					if(ImGui.beginMenu(lang.get("window.file-explorer.show-in") + "###show-in")) {
						if(ImGui.menuItem(lang.get("window.file-explorer.show-in.system") + "###system")) {
							try {
								File file = subNode.isDirectory() ? subNode : subNode.getParentFile();
								if(file != null)
									Desktop.getDesktop().open(file);
							} catch (UnsupportedOperationException | IOException e) {
								e.printStackTrace();
							}
						}
						ImGui.endMenu();
					}
					if(!subNode.isDirectory()) {
						if(ImGui.beginMenu(lang.get("window.file-explorer.open-with") + "###open-with")) {
							if(ImGui.menuItem(lang.get("window.file-explorer.open-with.system") + "###system")) {
								try {
									Desktop.getDesktop().open(subNode);
								} catch (UnsupportedOperationException | IOException e) {
									e.printStackTrace();
								}
							}
							ImGui.endMenu();
						}
					}
					ImGui.endPopup();
				}
				ImGui.popID();
			}
		}
	}
	
	private void fileDragDropSource(File subNode) {
		if(ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload(GUIFileExplorer.FILE_PAYLOAD_DRAGDROP_TYPE, subNode);
			ImGui.text(Engine.getEngineLanguage().get("window.file-explorer.file") + ": " + subNode.getName());
			ImGui.endDragDropSource();
		}
	}
	
	private boolean fileDragDropTarget(File subFile) {
		if(ImGui.beginDragDropTarget()) {
			File file = ImGui.acceptDragDropPayload(FILE_PAYLOAD_DRAGDROP_TYPE, File.class);
			if(file != null) {
				File newFilePath = new File(subFile, file.getName());
				if(!newFilePath.exists()) {
					file.renameTo(newFilePath);
					return true;
				}
			}
			ImGui.endDragDropTarget();
		}
		return false;
	}
	
	private void load() {
		sprites = GameEngineManager.sourceManager().getSpriteSheet("engineSprites");
	}
	
	public void reload() {
		currentNode.file = rootFile;
	}

	public File getRootFile() {
		return rootFile;
	}

	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
		this.currentNode = new Node(rootFile);
	}
	
	private class Node {
		private File file;
		private List<File> subNodes;
		private File parent;
		
		public Node(File file) {
			this.file = file;
			if(!rootFile.getAbsolutePath().equals(file.getAbsolutePath()))
				this.parent = file.getAbsoluteFile().getParentFile();
			File[] subFiles = file.listFiles();
			if(subFiles != null) {
				subNodes = new ArrayList<>();
				for(File subFile : subFiles) {
					if(subFile.getName().endsWith(".jar") && !scripts.containsKey(subFile.getAbsolutePath())) {
						try {
							scripts.put(subFile.getAbsolutePath(), Engine.getGameScriptClass(subFile));
						} catch (InvalidCodeException | IOException e) {
							e.printStackTrace();
						}
					}
					subNodes.add(subFile);
				}
			}
		}
	}
	
}
