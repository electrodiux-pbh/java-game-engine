package com.gameengine.graphics;

public enum AspectRatio {
	ASPECT_1_1(1080, 1080),
	ASPECT_3_1(3240, 1080),
	ASPECT_3_2(1620, 1080),
	ASPECT_4_3(1440, 1080),
	ASPECT_5_4(1350, 1080),
	ASPECT_16_9(1920, 1080);

	private float aspectValue;
	private short width;
	private short height;

	private AspectRatio(int width, int height) {
		this.width = (short) width;
		this.height = (short) height;
		aspectValue = getAspectRatioValue(width, height);
	}

	public float getAspectValue() {
		return aspectValue;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static float getAspectRatioValue(int width, int height) {
		if (width == 0 || height == 0)
			return 0;
		return (float) width / (float) height;
	}

}
