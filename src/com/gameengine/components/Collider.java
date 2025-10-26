package com.gameengine.components;

import org.joml.Vector2f;

public abstract class Collider extends Component {

	public Vector2f offset = new Vector2f();

	public Vector2f getOffset() {
		return offset;
	}
	
}
