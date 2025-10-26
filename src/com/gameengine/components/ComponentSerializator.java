package com.gameengine.components;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ComponentSerializator implements JsonSerializer<Component>, JsonDeserializer<Component> {

	@Override
	public Component deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObj = json.getAsJsonObject();
		String clazz = jsonObj.get("class").getAsString();
		JsonElement element = jsonObj.get("properties");
		
		try {
			return context.deserialize(element, Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Unkown component '" + clazz + "'", e);
		}
	}

	@Override
	public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("class", new JsonPrimitive(src.getClass().getName()));
		result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}

}
