package com.gameengine.util;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

	public Position position;
	public Rotation rotation;
	public LocalScale localScale;
	
	public Transform() {
		this(new Position(), new LocalScale(), new Rotation());
	}
	
	public Transform(Position position) {
		this(position, new LocalScale(), new Rotation());
	}
	
	public Transform(Position position, LocalScale scale) {
		this(position, scale, new Rotation());
	}
	
	public Transform(Position position, Rotation rotation) {
		this(position, new LocalScale(), rotation);
	}
	
	public Transform(Position position, LocalScale scale, Rotation rotation) {
		this.position = position;
		this.localScale = scale;
		this.rotation = rotation;
	}
	
	public Transform(float x, float y, float z) {
		this(new Position(x, y, z));
	}
	
	public Transform(float x, float y, float z, float xScale, float yScale, float zScale) {
		this(new Position(x, y, z), new LocalScale(xScale, yScale, zScale));
	}
	
	public Transform(float x, float y, float z, float xScale, float yScale, float zScale, float yaw, float pitch, float roll) {
		this(new Position(x, y, z), new LocalScale(xScale, yScale, zScale), new Rotation(yaw, pitch, roll));
	}
	
	public void copyTo(@NotNull Transform transform) {
		transform.position.set(position);
		transform.localScale.set(localScale);
		transform.rotation.set(rotation);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Transform))
			return false;
		
		Transform t = (Transform) obj;
		return t.position.equals(this.position) && t.localScale.equals(localScale) && t.rotation.equals(rotation);
	}

	public Position getPosition() {
		return position;
	}
	
	public void setPosition(@NotNull Position position) {
		this.position = position;
	}
	
	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(@NotNull Rotation rotation) {
		this.rotation = rotation;
	}

	public LocalScale getLocalScale() {
		return localScale;
	}

	public void setLocalScale(@NotNull LocalScale localScale) {
		this.localScale = localScale;
	}
	
	public String toString() {
		return "{ \"position\": " + position.toString() + ", \"rotation\": " + rotation.toString() + ", \"localScale\": " + localScale.toString() + " }";
	}
	
	//TODO rotate complete the matrix
	public Matrix4f toMatrix4f() {
		Matrix4f matrix = new Matrix4f().identity();
		matrix.translate(position);
		matrix.rotate((float) Math.toRadians(rotation.getYaw()), new Vector3f(0, 0, 1));
		matrix.rotate((float) Math.toRadians(rotation.getRoll()), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rotation.getPitch()), new Vector3f(1, 0, 0));
		matrix.scale(localScale);
		return matrix;
	}
	
	public static Transform clone(@NotNull Transform transform) {
		return new Transform(new Position(transform.position), new LocalScale(transform.localScale), new Rotation(transform.rotation));
	}
	
}
