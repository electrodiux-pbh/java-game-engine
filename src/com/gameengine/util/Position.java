package com.gameengine.util;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Position extends Vector3f {

	/**
	 * Build's a 3D position width coords (0, 0, 0)
	 */
	public Position() {
		this(0, 0, 0);
	}

	/**
	 * Build's a 3D position width the specified coords
	 */
	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Position(@NotNull Position position) {
		this(position.x, position.y, position.z);
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	/**
	 * Returns the z value for the position
	 * 
	 * @return the z value
	 */
	public float getZ() {
		return z;
	}
	
	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Set's the position z value
	 * 
	 * @param z value
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Return the distance into this position and another introduced position at
	 * square using this equation<br>
	 * (|x1-x2|^2) + (|y1-y2|^2) + (|z1-z2|^2)
	 * 
	 * @param compare position
	 * @return the distance in to this two positions at square
	 */
	public float distanceSquared(Position pos) {
		return (float) (Math.pow(Math.abs(pos.x - x), 2) + Math.pow(Math.abs(pos.y - y), 2)
				+ Math.pow(Math.abs(pos.z - z), 2));
	}

	/**
	 * Return the distance into this position and an introduced position<br>
	 * sqrt((|x1-x2|^2) + (|y1-y2|^2) + (|z1-z2|^2))
	 * 
	 * @param compare position
	 * @return the distance in to this two positions
	 */
	public float distance(Position pos) {
		return (float) Math.sqrt(distanceSquared(pos));
	}

	/**
	 * Substract to the position the introduced values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void substract(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}

	@Override
	public String toString() {
		return "[x=" + x + ",y=" + y + ",z=" + z + "]";
	}

}
