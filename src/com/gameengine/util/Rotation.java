package com.gameengine.util;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Rotation extends Vector3f {
	
	public Rotation() {
		this(0, 0, 0);
	}
	
	public Rotation(float yaw, float pitch, float roll) {
		super(yaw, pitch, roll);
	}
	
	public Rotation(@NotNull Rotation rotation) {
		super(rotation.x, rotation.y, rotation.z);
	}
	
	public float getYaw() {
		return x;
	}

	public void setYaw(float yaw) {
		this.x = yaw;
	}
	
	public float getPitch() {
		return y;
	}
	
	public void setPitch(float pitch) {
		this.y = pitch;
	}

	public float getRoll() {
		return z;
	}

	public void setRoll(float roll) {
		this.z = roll;
	}
	
	public boolean isRotated() {
		return x != 0 || y != 0 || z != 0;
	}
	
	public void set(@NotNull Rotation rotation) {
		this.x = Float.valueOf(rotation.x).floatValue();
		this.y = Float.valueOf(rotation.y).floatValue();
		this.z = Float.valueOf(rotation.z).floatValue();
	}
	
	public String toString() {
		return "[yaw=" + x + ",pitch=" + y + ",roll=" + z + "]";
	}
	
}
