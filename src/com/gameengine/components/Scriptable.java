package com.gameengine.components;

import org.jetbrains.annotations.Nullable;

import com.gameengine.enginegui.GUI;
import com.gameengine.program.GameScript;
import com.gameengine.program.Script;

public class Scriptable extends Component implements com.gameengine.data.Scriptable {
	
	private transient GameScript script;
	
	public Scriptable() {
		this(null);
	}
	
	public Scriptable(@Nullable GameScript script) {
		this.setScript(script);
	}
	
	@Override
	public void engineUpdate() {
		updateScript();
	}
	
	@Override
	public void updateScript() {
		if(script != null) {
			try {
				script.onUpdate(parent());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void componentGui() {
		GUI.renderScriptable(this);
	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		if(script == null)
			this.script = null;
		if(!(script instanceof GameScript))
			return;
		this.script = (GameScript) script;
	}
	
}
