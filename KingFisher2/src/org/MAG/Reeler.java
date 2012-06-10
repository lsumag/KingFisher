package org.MAG;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 
 * @author UnderGear
 *
 */
public class Reeler extends Activity implements OnTouchListener, SurfaceHolder.Callback {

	private static final String TAG = "Reeler";
	
	private ImageView background;
	private MySurfaceView foreground;
	private SurfaceHolder holder;
	
	private Vibrator vibrotron;
	
	private WaitForHookTask waitTask; //AsyncTask - while waiting for a bite
	private StruggleTask struggleTask; //AsyncTask - working against player while reeling
	
	private Random random; //Random to determine what hooks on the line
	
	//For determining where a touch is on the screen relative to the center. Cartesian -> Polar
	private static final DisplayMetrics metrics = new DisplayMetrics();
	private int minusHalfWidth, minusHalfHeight;
	private float currentTheta;
	private float currentRadius;
	private float previousTheta;
	private float previousRadius;
	private float x, y;
	private float angularDelta; //Change in radians since last event
	
	private Sprite rod, spindle; //to be drawn on the foreground.
	
	private boolean fingerDown;
	
	//Passed in from Caster activity or calculated (at least partially) from that
	private int levelID, catchID; //from Bundle.
	private float distance = 100;
	private float lineStrength = 100; //TODO: set these onCreate based on the line you have and the quality of the cast. we should have something about your hook and rod, too.
	
	/**
	 * Called on Activity creation. Set the background, touch listener, vibrator, load up sounds.
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            distance = extras.getInt("CastDistance");
            levelID = extras.getInt("SelectedSelected");
            Log.d(TAG, "Selected Level ID: " + levelID + ", Cast Distance: " + distance);
        }
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.reeler);
        background = (ImageView)findViewById(R.id.reeler_background);
        foreground = (MySurfaceView)findViewById(R.id.reeler_foreground);
        
        holder = foreground.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        
        holder.addCallback(this);
        
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        minusHalfHeight = -height/2;
        minusHalfWidth = -width/2;
        
        SoundManager.loadSounds(SoundManager.REELABLE);
        
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        
        random = new Random();
        
        //TODO: implement foreground images/background animation - the fishing rod should be in the front. we need a background animation/video
        //TODO: instructional audio - waiting/patience
        foreground.getHolder().setFormat(PixelFormat.TRANSPARENT);
        foreground.setZOrderOnTop(true);
        
        rod = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.reel_back_sprite), 0.5f, 0.5f, 0, Sprite.ALIGNMENT_CENTER);
        spindle = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.reel_handle_sprite), 0.5f, 0.5f, 0, Sprite.ALIGNMENT_CENTER);
        
        foreground.addSprite(rod);
        foreground.addSprite(spindle);
        
        drawSprites();
        
        waitTask = new WaitForHookTask();
        waitTask.execute();
    }
	
	/**
	 * We've waited and hooked something! Allow the player to start reeling in and have the fish/king struggle
	 */
	private void hook() {
		Log.e(TAG, "HOOK!");
		
		vibrotron.vibrate(500);
		SoundManager.playSound(2, 1);
		
		//TODO: determine what it is we've hooked based on random numbers, your bait, the current level ID, and distance.
		background.setOnTouchListener(this);
		struggleTask = new StruggleTask();
		struggleTask.execute();
	}
	
	private void success() {
		
		//TODO: go to the Catcher next. it will decide what to do from there. it will need the level ID and the catch ID
		
		if (struggleTask != null) struggleTask.cancel(true);
		Log.e(TAG, "SUCCESS!");
		background.setOnTouchListener(null);
		holder.removeCallback(this);
		
		//TODO: we're going to launch the next activity based on what was caught. if it was junk, rejecterator. if it was a king, shaker.
		//TODO: bundle up the catch's ID and ship it off to the next intent.
		
		try {
        	Intent ourIntent = new Intent(Reeler.this, Class.forName("org.MAG.Shaker"));
        	ourIntent.putExtra("CatchID", catchID);
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Failed to jump to another activity");
		}
		
	}
	
	private void recast() {
		
		Log.e(TAG, "RECAST!");
		
		background.setOnTouchListener(null);
		holder.removeCallback(this);
		
		try {
        	Intent ourIntent = new Intent(Reeler.this, Class.forName("org.MAG.Caster"));
        	ourIntent.putExtra("SelectedLevel", levelID);
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Failed to jump to another activity");
		}
	}
	
	/**
	 * AsyncTask to wait a random amount of time for a fish bite.
	 * @author undergear
	 *
	 */
	private class WaitForHookTask extends AsyncTask<Void, Void, Boolean> {

		/**
		 * wait for a random amount of time and determine if there was a bite or not.
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				Thread.sleep(random.nextInt(10) * 1000 + 1000); // wait for a random amount of time. 1-11 seconds. TODO: make this depend upon the cast quality and bait? less magic numbery. wider range.
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
			
			return true;
		}

		/**
		 * after waiting, launch hook code.
		 */
		@Override
		protected void onPostExecute(Boolean bite) {
			hook();
		}
	}
	
	/**
	 * AsyncTask for the fish's escape attempts after getting hooked on the line
	 * @author undergear
	 *
	 */
	private class StruggleTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			while (true) {
				try {
					Thread.sleep(1000); //TODO: time should depend on what you've hooked. This is how often whatever is hooked will struggle.
					vibrotron.vibrate(300);
					
					//TODO: fish line break attempt. damage the line based on level difficulty
					if (!fingerDown) distance += 5; //TODO: depends upon what's hooked as well. you should hold down to prevent the fish from unreeling itself.
					
					if (lineStrength <= 0) return true;
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
		
		@Override
		protected void onPostExecute(Boolean escape) {
			if (escape) recast(); //The line was broken. Cast again.
		}
	}
	
	public void onPause() {
		if (waitTask != null) waitTask.cancel(true);
		if (struggleTask != null) struggleTask.cancel(true);
		super.onPause();
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		
		//offset our touch location. it is now relative to the center of the screen.
		event.offsetLocation(minusHalfWidth, minusHalfHeight);
		x = event.getX(0);
		y = event.getY(0);
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			
			previousRadius = (float) Math.sqrt( x * x + y * y ); //Euclidean distance of touch from center of screen
			previousTheta = (float) Math.acos( x / previousRadius ); //angle in radians of the touch relative to center of screen
			
			//NOTE: -40.0f is because our image isn't quite aligned on the x-axis
			spindle.setRotation((float) (previousTheta * 180 / Math.PI) - 40.0f); //TODO: do we want to just swap background images instead of actually rotating this?
			
			drawSprites();
			
			fingerDown = true;
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			currentRadius = (float) Math.sqrt( x * x + y * y ); //distance of the touch from center of the screen
			
			if (y > 0)
				currentTheta = (float) -Math.acos( x / currentRadius ); //angle in radians.
			else
				currentTheta = (float) Math.acos( x / currentRadius ); //angle in radians.
			
			angularDelta = Math.abs(currentTheta - previousTheta); //find the angular change in radians from last update
			if(angularDelta > 3.1416f) angularDelta = (float) ((2.0 * Math.PI) - angularDelta);
			
			
			//Log.d(TAG, "angular delta: " + angularDelta + ", distance: " + distance);
			
			spindle.setRotation((float) -(currentTheta * 180 / Math.PI) - 40.0f);
			
			drawSprites();
			
			distance -= angularDelta; //TODO: scale this with testing? possibly take the rod into account.
			if (distance <= 0) success();
			
			//updating these for next cycle through.
			previousTheta = currentTheta; 
			previousRadius = currentRadius;
			break;
		case MotionEvent.ACTION_UP:
			fingerDown = false;
			break;
		}
		return true;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) { }
	

	public void surfaceCreated(SurfaceHolder holder) {
		drawSprites();
	}

	public void surfaceDestroyed(SurfaceHolder holder) { }

	private void drawSprites() {
		if (holder.getSurface().isValid()) {
	        Canvas canvas = holder.lockCanvas();
	        foreground.draw(canvas);
	        holder.unlockCanvasAndPost(canvas);
        }
	}
}