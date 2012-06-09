package org.MAG;

import android.graphics.Bitmap;

public class Sprite {

	public static final int ALIGNMENT_CENTER = 0;
	public static final int ALIGNMENT_TOP = 1;
	public static final int ALIGNMENT_BOTTOM = 2;
	public static final int ALIGNMENT_LEFT = 3;
	public static final int ALIGNMENT_RIGHT = 4;
	public static final int ALIGNMENT_TOPLEFT = 5;
	public static final int ALIGNMENT_TOPRIGHT = 6;
	public static final int ALIGNMENT_BOTTOMLEFT = 7;
	public static final int ALIGNMENT_BOTTOMRIGHT = 8;
	
	//TODO: alignment! let's be able to align to the bottom, top, center, etc.
	private String name;
	private Bitmap bmp;
	private float x, y; //these are relative coordinates, not absolute. 0.0f-1.0f
	private float rotation;
	private float alignX, alignY;
	
	public Sprite(String name, Bitmap bmp, float x, float y, float rotation, int alignment) {
		this.name = name;
		this.bmp = bmp;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		switch (alignment) {
		
		case ALIGNMENT_CENTER:
			setAlignX(setAlignY(0.5f));
			break;
		case ALIGNMENT_TOP:
			setAlignX(0.5f);
			setAlignY(0.0f);
			break;
		case ALIGNMENT_BOTTOM:
			setAlignX(0.5f);
			setAlignY(1.0f);
			break;
		case ALIGNMENT_LEFT:
			setAlignX(0.0f);
			setAlignY(0.5f);
			break;
		case ALIGNMENT_RIGHT:	
			setAlignX(1.0f);
			setAlignY(0.5f);
			break;
		case ALIGNMENT_TOPLEFT:
			setAlignX(setAlignY(0.0f));
			break;
		case ALIGNMENT_TOPRIGHT:
			setAlignX(1.0f);
			setAlignY(0.0f);
			break;
		case ALIGNMENT_BOTTOMLEFT:
			setAlignX(0.0f);
			setAlignY(1.0f);
			break;
		case ALIGNMENT_BOTTOMRIGHT:
			setAlignX(setAlignY(1.0f));
			break;
		default:
			setAlignX(setAlignY(0.0f)); //default topleft
			break;
		}
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

	public float getAlignX() {
		return alignX;
	}

	public void setAlignX(float alignX) {
		this.alignX = alignX;
	}

	public float getAlignY() {
		return alignY;
	}

	public float setAlignY(float alignY) {
		this.alignY = alignY;
		return alignY;
	}
}
