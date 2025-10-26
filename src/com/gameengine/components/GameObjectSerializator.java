package com.gameengine.components;

import java.lang.reflect.Type;

import com.gameengine.util.Transform;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GameObjectSerializator implements JsonDeserializer<GameObject> {

	@Override
	public GameObject deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObj = json.getAsJsonObject();
		String name = jsonObj.get("name").getAsString();
		JsonArray components = jsonObj.getAsJsonArray("components");
		Transform transform = context.deserialize(jsonObj.get("transform"), Transform.class);
		
		GameObject obj = new GameObject(name, transform);
		for(JsonElement element : components) {
			Component component = context.deserialize(element, Component.class);
			obj.addComponent(component);
		}
		
		return obj;
	}

}
