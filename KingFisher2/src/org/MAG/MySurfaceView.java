package org.MAG;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * MySurfaceView is used to draw sprites on top of a background. Attach this to a root node with whatever background.
 * Be sure to set this one's z order on top and set its holder pixel format to TRANSPARENT.
 * @author undergear
 *
 */
public class MySurfaceView extends SurfaceView {

	private static final String TAG = "MySurfaceView";
	
	private ArrayList<Sprite> sprites; //list of the sprites to be drawn on this surface
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); //this paint will be used to draw each sprite
	
	private Matrix matrix;
	
	/**
	 * Constructors.
	 * @param context
	 */
	public MySurfaceView(Context context) {
		super(context);
		sprites = new ArrayList<Sprite>();
		matrix = new Matrix();
	}
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sprites = new ArrayList<Sprite>();
		matrix = new Matrix();
	}
	
	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sprites = new ArrayList<Sprite>();
		matrix = new Matrix();
	}
	
	/**
	 * Overridden for drawing sprites onto the SurfaceView
	 * 
	 * @param canvas to be drawn to
	 */
	@Override
	public void draw(Canvas canvas) {
		
		if (getHolder().getSurface().isValid()) { //Make sure that the surface is actually valid before trying to draw on it!
			
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Clear out what was previously there with alpha.
			
			for (Sprite sprite : sprites) { //Iterate through our sprites list.
				
				float x, y; //Absolute coordinates at which to draw the sprite. Remember, this is the top left of the image.
				x = this.getWidth() * sprite.getX() - sprite.getImage().getWidth() * sprite.getAlignX();
				y = this.getHeight() * sprite.getY() - sprite.getImage().getHeight() * sprite.getAlignY();
				if (sprite.getRotation() == 0.) { //If the sprite is not rotated
					
					//Calculate absolute coordinates to draw each sprite. Based on relative coordinates, its height/width, and its alignment settings.
					
					canvas.drawBitmap(sprite.getImage(), x, y, paint); //Draw the sprite at our coordinates!
				} else { //The sprite is rotated. We will have to account for this.
					
					/**
					 * TODO: solve this huge problem.
					 * 
					 * stage 1: calculate the x and y coordinates of the original sprite - not rotated -> position scale * canvas width + alignment scale * sprite width
					 * 
					 * stage 2: set the rotate matrix correctly. the sprite's rotation (in degrees) and the center.
					 * 
					 * stage 3: calculate the new x and y coordinates for the rotated sprite - fancy trig?
					 * 
					 * stage 4: apply the matrix to the canvas, draw the image at new x and y, remove the matrix
					 * 
					 */
					
					
					
					//Rotation matrix we'll use on the sprite's bitmap
					matrix.reset();
					
					matrix.setRotate(sprite.getRotation(), this.getWidth() * sprite.getX() + sprite.getImage().getWidth() * sprite.getAlignX(), sprite.getY() * this.getHeight() + sprite.getImage().getHeight() * sprite.getAlignY());
					
					
					//Same calculation as before, but with the rotated bitmap instead of the original.
					//x = this.getWidth() * sprite.getX() - rotatedBMP.getWidth() * sprite.getAlignX();
					//y = this.getHeight() * sprite.getY() - rotatedBMP.getHeight() * sprite.getAlignY();
					
					
					
					float rotation = (float) Math.toRadians(sprite.getRotation());
					
					x = (float) (x * Math.cos(rotation) + y * -Math.sin(rotation));
					y = (float) (y * Math.sin(rotation) + y * Math.cos(rotation));
					
					Log.d(TAG, "X: " + x + ", Y: " + y);
					//canvas.drawBitmap(rotatedBMP, x, y, paint); //Draw the sprite at our coordinates!
					canvas.setMatrix(matrix);
					//canvas.drawBitmap(sprite.getImage(), matrix, paint);
					canvas.drawBitmap(sprite.getImage(), x, y, paint);
					canvas.setMatrix(null);
				}
			}
		}
		super.draw(canvas); //Pass it off to super to deal with.
	}
	
	/**
	 * Adding sprite to our list of sprites.
	 * 
	 * @param sprite to add
	 */
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}
	
	/**
	 * Replacing the sprite at index
	 * 
	 * @param index
	 * @param newSprite
	 */
	public void replaceSprite(int index, Sprite newSprite) {
		sprites.set(index, newSprite);
	}
	
	/**
	 * Wipe out our sprites.
	 */
	public void clearSprites() {
		sprites.clear();
	}
}