package com.gameengine.event;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

public abstract class Event implements Serializable {

	private static final long serialVersionUID = -7347617922257927677L;

	private Object source;
	
	public Event(@NotNull Object source) {
		this.source = source;
	}

	public Object getSource() {
		return source;
	}

}
