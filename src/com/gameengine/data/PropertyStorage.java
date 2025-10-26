package com.gameengine.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PropertyStorage {

	/**
	 * This method return a value with the key passed by params,
	 * if the property doesn't found return null
	 * 
	 * @param key
	 * @return the object
	 */
	@Nullable
	Object getProperty(@NotNull String key);
	
}
