package com.gameengine.graphics;

import org.joml.Vector4f;

public class Color extends Vector4f {

	public static final Color BLACK = new Color(0.0F, 0.0F, 0.0F, 1.0F);
	public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	public static final Color RED = new Color(1.0F, 0.0F, 0.0F, 1.0F);
	public static final Color LIME = new Color(0.0F, 1.0F, 0.0F, 1.0F);
	public static final Color GREEN = new Color(0.0F, 0.470588F, 0.0F, 1.0F);
	public static final Color BLUE = new Color(0.0F, 0.0F, 1.0F, 1.0F);
	public static final Color LIGHT_BLUE = new Color(0.0F, 0.0F, 1.0F, 1.0F);
	public static final Color GRAY = new Color(0.31372F, 0.31372F, 0.31372F, 1.0F);
	public static final Color LIGHT_GRAY = new Color(0.52941F, 0.52941F, 0.52941F, 1.0F);
	public static final Color YELLOW = new Color(1.0F, 1.0F, 0.0F, 1.0F);
	public static final Color PINK = new Color(1.0F, 0.0F, 1.0F, 1.0F);
	
	public Color() {
		this(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public Color(float r, float g, float b) {
		this(r, g, b, 1.0F);
	}
	
	public Color(float r, float g, float b, float a) {
		super.x = r;
		super.y = g;
		super.z = b;
		super.w = a;
	}
	
	public Color(float[] values) {
		if(values == null)
			return;
		for(int i = 0; i < values.length || i < 4; i++) {
			switch(i) {
				case 0: x = values[i]; break;
				case 1: y = values[i]; break;
				case 2: z = values[i]; break;
				case 3: w = values[i]; break;
			}
		}
	}
	
	public float r() {
		return x;
	}
	
	public float g() {
		return y;
	}
	
	public float b() {
		return z;
	}
	
	public float a() {
		return w;
	}
	
}
