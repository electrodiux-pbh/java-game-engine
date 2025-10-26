package com.gameengine.languages;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Language {
	
	private Map<String, String> languageValuesMap = new ConcurrentHashMap<>();
	private String name;
	
	/**
	 * Create a language object as a language string data, using json format
	 * 
	 * @param data from the translation, as json format
	 * @throws ParseException if the format isn't correct
	 */
	public Language(@NotNull String data, @NotNull String name) throws ParseException {
		setName(name);
		load(data);
	}
	
	/**
	 * Create a language object with an input stream and the string data inside stream, using json format
	 * 
	 * @param stream
	 * @throws ParseException if the format isn't correct
	 * @throws java.io.IOException if an io error ocurrs
	 */
	public Language(@NotNull InputStream stream, @NotNull String name) throws ParseException, IOException {
		setName(name);
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		
		String data = "";
		String line = "";
		
		while((line = in.readLine()) != null) {
			data += line;
		}
		
		in.close();
		
		load(data);
	}
	
	/**
	 * Create a language object from the lang folder using json format
	 * 
	 * @param lang
	 * @throws ParseException if the format isn't correct
	 * @throws java.io.IOException if an io error ocurrs
	 * @see #Language(InputStream)
	 */
	public Language(@NotNull Languages lang) throws ParseException, IOException {
		this(new FileInputStream("lang/" + lang.name().toLowerCase() + ".json"), lang.name().toLowerCase());
	}
	
	@SuppressWarnings("unchecked")
	private void load(@NotNull String data) throws ParseException {
		JSONObject json = null;
		try {
			JSONParser parser = new JSONParser();
			json = (JSONObject) parser.parse(data);
			
			json.forEach((key, value) -> {
				languageValuesMap.put(key.toString(), value.toString());
			});
			
		} catch (ClassCastException | NullPointerException e) {
			throw new ParseException(0);
		}
	}
	
	/**
	 * This method gets the value of the string, saved in that path. In case that path doesn't exist
	 * the method will return {@code "(NULL)"}
	 * 
	 * @param path for the property
	 * @return
	 */
	@NotNull
	public String get(@NotNull String path) {
		String value = languageValuesMap.get(path);
		return value == null ? path : value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
