package com.gameengine.program;

import org.jetbrains.annotations.Nullable;

public abstract class Script {

	/**
	 * This method is called when the application is time to start up.
	 */
	public void onUpdate(@Nullable Object obj) { };

	/**
	 * This method is called when the application needs to disable.
	 */
	public void onDisable() { }

	/**
	 * This method is called when the application is loading.
	 */
	public void onLoad() { }
	
}
