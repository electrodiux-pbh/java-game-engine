package com.gameengine.languages;

import org.jetbrains.annotations.NotNull;



/**
 * Enum for all most talk languages
 */
public enum Languages {
	EN("English"),
	ES("Español"),
	FR("Français");
	
	private final String name;
	
	private Languages(@NotNull String name) {
		this.name = name;
	}
	
	/**
	 * This returns the complete name of language, in the language of destination
	 * 
	 * @return the name of the language
	 */
	@NotNull
	public String getName() {
		return name;
	}
	
	private static Languages defaultLanguage = Languages.EN; // Default to English
	
	public static Languages getDefaultLanguage() {
		return defaultLanguage;
	}
	
}
