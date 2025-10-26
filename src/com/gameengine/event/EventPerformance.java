package com.gameengine.event;

import org.jetbrains.annotations.NotNull;

public interface EventPerformance<L extends EventListener<? extends E>, E extends Event> {

	void addEventListener(@NotNull L listener);
	
	void removeEventListener(@NotNull L listener);
	
}
