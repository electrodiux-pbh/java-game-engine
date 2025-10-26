package com.gameengine.components;

import org.jetbrains.annotations.NotNull;

import com.gameengine.enginegui.GUI;
import com.gameengine.enginegui.GUIable;

public abstract class Component implements GUIable {

	private static int ID_COUNTER = 0;
	
	private int uid = -1;
	
	private transient GameObject parent = null;

	public void load() { }
	
	public void update() { }
	
	public void engineUpdate() { }
	
	protected void destroy() { }
	
	public final void gui() {
		GUI.renderObjectProperties(this);
		componentGui();
	}
	
	protected void componentGui() { }
	
	public void generateUID() {
		if(this.uid == -1) {
			this.uid = ID_COUNTER++;
		}
	}
	
	public int getUID() {
		if(this.uid == -1)
			generateUID();
		return this.uid;
	}
	
	public GameObject parent() {
		return parent;
	}
	
	protected void setParent(@NotNull GameObject parent) {
		this.parent = parent;
	}
	
}
