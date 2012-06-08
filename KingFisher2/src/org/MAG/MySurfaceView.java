package org.MAG;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView {

	private ArrayList<Sprite> sprites;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public MySurfaceView(Context context) {
		super(context);
		sprites = new ArrayList<Sprite>();
	}
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sprites = new ArrayList<Sprite>();
	}
	
	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sprites = new ArrayList<Sprite>();
	}
	
	public void draw(Canvas canvas) {
		//TODO: draw sprites here.
		Log.d("surfaceview", "DRAW");
		
		if (getHolder().getSurface().isValid()) {
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			canvas.drawPaint(paint);
			
			//Log.e("Sprite number = ", ""+sprites.size());
			for (Sprite sprite : sprites) {
				//Log.e("Sprite", ""+sprite.getName() + " " + sprite.getRotation());
				if (sprite.getRotation() == 0.) {
					canvas.drawBitmap(sprite.getImage(), sprite.getX(), sprite.getY(), paint);
				} else {
					Matrix mtx = new Matrix();
					//mtx.postRotate(sprite.getRotation(), sprite.getX() + sprite.getImage().getWidth()/2, sprite.getY() + sprite.getImage().getHeight()/2);
					mtx.postRotate(sprite.getRotation());
					// Rotating Bitmap
					Bitmap rotatedBMP = Bitmap.createBitmap(sprite.getImage(), 0, 0, sprite.getImage().getWidth(), sprite.getImage().getHeight(), mtx, true);
					canvas.drawBitmap(rotatedBMP, sprite.getX(), sprite.getY(), paint);
				}
			}
		}
	}
	
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

}
