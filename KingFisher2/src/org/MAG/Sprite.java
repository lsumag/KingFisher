package org.MAG;

import android.graphics.Bitmap;

public class Sprite {

	private String name;
	private Bitmap bmp;
	private int x, y;
	private float rotation;
	
	public Sprite(String name, Bitmap bmp, int x, int y, float rotation) {
		this.name = name;
		this.bmp = bmp;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Bitmap getImage() {
		return bmp;
	}
	
	public String getName() {
		return name;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public void setRotation(float r) {
		rotation = r;
	}
	
	public void setImage(Bitmap bmp) {
		this.bmp = bmp;
	}
}
