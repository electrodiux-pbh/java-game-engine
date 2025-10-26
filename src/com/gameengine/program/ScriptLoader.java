package com.gameengine.program;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ScriptLoader<C extends Script> {

	/**
	 * Load a code inside a jarfile with a file.
	 * 
	 * @param file
	 * @return the code ID
	 * @throws java.io.IOException
	 * @throws com.gameengine.program.InvalidCodeException
	 */
	public abstract Class<? extends C> loadCodeClass(File file) throws IOException, InvalidCodeException;

	/**
	 * Load a code with the given class instance
	 * 
	 * @param jarClass
	 * @throws InvalidCodeException
	 */
	public C getCodeInstanceFromClass(Class<? extends C> scriptClass) throws InvalidCodeException {
		try {
			return scriptClass.getDeclaredConstructor().newInstance();
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
				| InstantiationException e) {
			throw new InvalidCodeException("Abnormal code type", e);
		} catch (IllegalAccessException e) {
			throw new InvalidCodeException("No public constructor");
		}
	}

}
