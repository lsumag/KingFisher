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
import android.view.SurfaceView;

/**
 * MySurfaceView is used to draw sprites on top of a background. Attach this to a root node with whatever background.
 * Be sure to set this one's z order on top and set its holder pixel format to TRANSPARENT.
 * @author undergear
 *
 */
public class MySurfaceView extends SurfaceView {

	private ArrayList<Sprite> sprites;
	private Paint clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //this is used to fill in the foreground alpha before we draw sprites there
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); //this paint will be used to draw each sprite
	
	public MySurfaceView(Context context) {
		super(context);
		sprites = new ArrayList<Sprite>();
		clearPaint.setColor(Color.TRANSPARENT);
	}
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sprites = new ArrayList<Sprite>();
		clearPaint.setColor(Color.TRANSPARENT);
	}
	
	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sprites = new ArrayList<Sprite>();
		clearPaint.setColor(Color.TRANSPARENT);
	}
	
	/**
	 * Overridden for drawing sprites onto the SurfaceView
	 * 
	 * @param canvas to be drawn to
	 */
	@Override
	public void draw(Canvas canvas) {
		
		if (getHolder().getSurface().isValid()) {
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			canvas.drawPaint(clearPaint);
			
			//TODO: set screen stuff up here.
			
			
			//Log.e("Sprite number = ", ""+sprites.size());
			for (Sprite sprite : sprites) {
				
				//TODO: convert sprite's relative coords to absolute here.
				float x = this.getWidth() * sprite.getX();
				float y = this.getHeight() * sprite.getY();
				
				//Log.e("Sprite", ""+sprite.getName() + " " + sprite.getRotation());
				if (sprite.getRotation() == 0.) {
					canvas.drawBitmap(sprite.getImage(), x, y, paint); //TODO: coords here.
				} else {
					Matrix mtx = new Matrix();
					//mtx.postRotate(sprite.getRotation(), sprite.getX() + sprite.getImage().getWidth()/2, sprite.getY() + sprite.getImage().getHeight()/2);
					mtx.postRotate(sprite.getRotation());
					// Rotating Bitmap
					Bitmap rotatedBMP = Bitmap.createBitmap(sprite.getImage(), 0, 0, sprite.getImage().getWidth(), sprite.getImage().getHeight(), mtx, true);
					canvas.drawBitmap(rotatedBMP, x, y, paint); //TODO: coords here.
				}
			}
		}
		super.draw(canvas);
	}
	
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}
	
	public void replaceSprite(int index, Sprite newSprite) {
		sprites.set(index, newSprite);
	}
	
	public void clearSprites() {
		sprites.clear();
	}

}
