package com.gameengine.program;

import java.io.File;
import java.io.IOException;

public class GameScriptLoader extends ScriptLoader<GameScript> {
	
	@SuppressWarnings("unchecked")
	public Class<? extends GameScript> loadCodeClass(File file) throws IOException, InvalidCodeException {
		if (!file.exists())
			throw new InvalidCodeException("The file doesn't exist");

		ScriptClassLoader classLoader = new ScriptClassLoader(getClass().getClassLoader(), "MyScript", file);

		Class<? extends Script> scriptClass = classLoader.getCodeClass();
		
		if(!(GameScript.class.isAssignableFrom(scriptClass)))
			throw new InvalidCodeException("The code is not assignable from GameScript instance");
		
		return (Class<? extends GameScript>) scriptClass;
	}

}
