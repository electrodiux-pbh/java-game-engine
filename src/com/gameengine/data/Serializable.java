package com.gameengine.data;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public interface Serializable {
	@NotNull
	Map<String, java.io.Serializable> serialize();
}
