package com.gameengine.data;

import org.jetbrains.annotations.NotNull;

public interface ModificablePropertyStorage extends PropertyStorage {

	/**
	 * Sets a value inside the property strorage with a key, and a value
	 * 
	 * @param key
	 * @param value
	 */
	void setProperty(@NotNull String key, @NotNull Object value);
	
	/**
	 * Remove a property inside the property strorage stored in the params key, and
	 * return the value of the property before remove it
	 * 
	 * @param key
	 */
	Object removeProperty(@NotNull String key);
	
}
