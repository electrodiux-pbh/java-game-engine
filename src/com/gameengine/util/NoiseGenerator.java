package com.gameengine.util;

import java.util.Random;

import org.jetbrains.annotations.NotNull;

public class NoiseGenerator {

	private Random rand;
	
	private int perm[] = new int[512];
	
	private float maxClapm;
	private float minClapm;
	
	/**
	 * Creates a new NoiseGenerator and set's the seed value in to {@link java.lang.System#nanoTime()}
	 * @see java.lang.System
	 */
	public NoiseGenerator() {
		this(System.nanoTime());
	}
	
	/**
	 * Creates a new NoiseGenerator with the introduced seed
	 * @param seed
	 */
	public NoiseGenerator(long seed) {
		rand = new Random(seed);
		for (int i=0; i < 256 ; i++) {
    		perm[256+i] = perm[i] = rand.nextInt(200);
    	}
	}
	
	/**
	 * Set's the maximun, and minimun values for the generated noises, if the value is smaller than minimun the value will be the minimun,
	 * and if the value is greater than maximun the value will be the maximun
	 * 
	 * @param min value
	 * @param max value
	 */
	public void setClampValues(float min, float max) {
		minClapm = min;
		maxClapm = max;
	}
    
	/**
	 * Returns the noise 2D of the selected zone, with a frequency and weight generation values.
	 * 
	 * @param noise map width
	 * @param noise map height
	 * @param posX for the start point
	 * @param posY for the start point
	 * @param frequency of the noise
	 * @param weight of the noise
	 * @return a float array of the noise values
	 */
	@NotNull
    public float[][] getNoiseOf2D(int width, int height, int posX, int posY, float frequency, float weight) {
    	float[][] noise2D = new float[width][height];
    	
    	float frec = 0.003f;
    	float weig = 1;
    	
    	for(int i = 0; i < 3; i++) {
	    	for(int y = 0; y < height; y++){
	    		for(int x = 0; x < width; x++){
	    			double dx = (double) (x + posX) / width;
	    			double dy = (double) (y + posY) / height;
	    			
	    			noise2D[x][y] += noise(dx * frec, dy * frec) * weig;
	    			noise2D[x][y] = clamp(noise2D[x][y], minClapm, maxClapm);
	        	}
	    	}
	    	frec *= frequency;
	    	weig *= weight;
    	}
    	return noise2D;
    }
    
    private double noise(double x, double y) {
    	int xi = (int) Math.floor(x) & 255;
    	int yi = (int) Math.floor(y) & 255;
    	
    	int g1 = perm[perm[xi] + yi];
    	int g2 = perm[perm[xi + 1] + yi];
    	int g3 = perm[perm[xi] + yi + 1];
    	int g4 = perm[perm[xi + 1] + yi + 1];
    	
    	double xf = x - Math.floor(x);
    	double yf = y - Math.floor(y);
    	
    	double d1 = grad(g1, xf, yf);
    	double d2 = grad(g2, xf - 1, yf);
    	double d3 = grad(g3, xf, yf - 1);
    	double d4 = grad(g4, xf - 1, yf - 1);
    	
    	double u = fade(xf);
    	double v = fade(yf);
    	
    	double x1Inter = lerp(u, d1, d2);
    	double x2Inter = lerp(u, d3, d4);
    	double yInter = lerp(v, x1Inter, x2Inter);
    	
    	return yInter;
    }
    
    private double lerp(double amount, double left, double right) {
    	return ((1-amount) * left + amount * right);
    }
    
    private double fade(double t) {
    	return t*t*t*(t*(t*6-15)+10);
    }
    
    private double grad(int hash, double x, double y) {
    	switch(hash & 3) {
    	case 0: return x + y;
    	case 1: return -x + y;
    	case 2: return x - y;
    	case 3: return -x - y;
    	default: return 0;
    	}
    }
    
    private float clamp(float x, float min, float max) {
        if (x < min) x = min;
        else if (x > max) x = max;
        return x;
    }
}
