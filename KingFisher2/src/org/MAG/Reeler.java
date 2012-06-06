package org.MAG;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
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
public class Reeler extends Activity implements OnTouchListener {

	private static final String TAG = "Reeler";
	
	private ImageView reelerBackground;
	private SurfaceView foreground;
	private Vibrator vibrotron;
	
	private WaitForHookTask waitTask;
	private StruggleTask struggleTask;
	
	private Random random;
	
	private static final DisplayMetrics metrics = new DisplayMetrics();
	private int minusHalfWidth, minusHalfHeight;
	
	private boolean fingerDown;
	private float currentTheta;
	private float currentRadius;
	private float previousTheta;
	private float previousRadius;
	private float x, y;
	private float angularDelta;
	private float distance, lineStrength = 100; //TODO: set these onCreate based on the line you have and the quality of the cast.
	
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
        }
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.reeler);
        reelerBackground = (ImageView)findViewById(R.id.reeler_background);
        
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
        
        //TODO: determine what will be caught and its stats. this will be based on quality of cast, equipment, and current level
        
        waitTask = new WaitForHookTask();
        waitTask.execute();
    }
	
	/**
	 * We've waited and hooked something! Allow the player to start reeling in and have the fish/king struggle
	 */
	private void hook() {
		Log.e(TAG, "HOOK!");
		//TODO: determine what it is we've hooked based on random numbers and distance.
		reelerBackground.setOnTouchListener(this);
		struggleTask = new StruggleTask();
		struggleTask.execute();
	}
	
	private void success() {
		//TODO: launch the shaker activity.
		if (struggleTask != null) struggleTask.cancel(true);
		Log.e(TAG, "SUCCESS!");
		reelerBackground.setOnTouchListener(null);
	}
	
	private void recast() {
		
		Log.e(TAG, "RECAST!");
		
		reelerBackground.setOnTouchListener(null);
		
		try {
        	Intent ourIntent = new Intent(Reeler.this, Class.forName("org.MAG.Caster"));
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
				Thread.sleep(random.nextInt(10) * 1000 + 1000); // wait for a random amount of time. 1-11 seconds. TODO: make this depend upon the cast quality? less magic numbery. wider range.
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
					Thread.sleep(1000); //TODO: time should depend on what you've hooked.
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
				//TODO: fish line break attempt. damage the line based on level difficulty
				
				if (!fingerDown) distance += 5; //TODO: depends upon what's hooked as well. you should hold down to prevent the fish from unreeling itself.
				
				if (lineStrength <= 0) return true;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean escape) {
			if (escape) recast(); //the fish has broken the line and escaped. cast again.
		}
		
	}
	
	public void onDestroy() {
		if (waitTask != null) waitTask.cancel(true);
		if (struggleTask != null) struggleTask.cancel(true);
		super.onDestroy();
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		event.offsetLocation(minusHalfWidth, minusHalfHeight); //offset our touch location. it is now relative to the center of the screen.
		x = event.getX(0);
		y = event.getY(0);
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			
			previousRadius = (float) Math.sqrt( x * x + y * y ); //Euclidean distance of touch from center of screen
			previousTheta = (float) Math.acos( x / previousRadius ); //angle in radians of the touch relative to center of screen
			
			fingerDown = true;
			
			Log.d(TAG, "finger down.");
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			//TODO: let's just remove the delta from the fish distance as we reel. fishDistance -= (currentTheta - previousTheta)/(Math.PI*100) or something like that.
			
			
			currentRadius = (float) Math.sqrt( x * x + y * y ); //distance of the touch from center of the screen
			currentTheta = (float) Math.acos( x / currentRadius ); //angle in radians.
			
			angularDelta = Math.abs(currentTheta - previousTheta); //find the angular change in radians from last update
			
			Log.e(TAG, "angular delta: " + angularDelta + ", distance: " + distance);
		
			
			
			distance -= angularDelta;
			//if (distance <= 0) success();
			
			//updating these for next cycle through.
			previousTheta = currentTheta; 
			previousRadius = currentRadius;
			break;
		case MotionEvent.ACTION_UP:
			fingerDown = false;
			Log.d(TAG, "finger up.");
			break;
		}
		return true;
	}
}
