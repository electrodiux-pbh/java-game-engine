package com.gameengine.data;

import com.gameengine.program.Script;

public interface Scriptable {
	
	void updateScript();
	
	Script getScript();
	
	void setScript(Script script);
	
}
