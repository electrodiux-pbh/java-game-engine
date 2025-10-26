package com.gameengine.event;

import org.jetbrains.annotations.NotNull;

public interface EventListener<E extends Event> {

	void execute(@NotNull E event);
	
}
