package org.MAG;

import android.graphics.Bitmap;

public class Sprite {

	private String name;
	private Bitmap bmp;
	private float x, y; //these are relative coordinates, not absolute.
	private float rotation;
	
	public Sprite(String name, Bitmap bmp, float x, float y, float rotation) {
		this.name = name;
		this.bmp = bmp;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
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
