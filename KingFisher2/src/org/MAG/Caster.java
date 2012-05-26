package org.MAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Caster extends Activity implements OnTouchListener, SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	
	private boolean touched;
	private boolean casting;
	private final long[] castPattern = {0, 50, 50, 50, 50, 50, 50, 50, 50};
	
	private ImageView casterBackground;
	
	/**
	 * Initiate the Caster Activity, load the sounds up, set up touch listener, vibrator, sensor manager, and accelerometer sensor
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.caster);
        
        casterBackground = (ImageView)findViewById(R.id.caster_background);
        casterBackground.setOnTouchListener(this);
        
        
        SoundManager.loadSounds(SoundManager.CASTABLE);
        
        //TODO: instructional audio
        
        //TODO: background animation. we'll need 2 layers of SurfaceView and an asynctask
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

	/**
	 * Listener called when the screen is touched. Only the casterBackground should be using this listener.
	 * Down events allow the user to cast, Up events prevent it.
	 * 
	 * @param v the View that has been touched. this should only be casterBackground!
	 * @param event the MotionEvent that has occurred. 
	 */
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	    	Log.e("KingFisher", "CASTABLE!!!");
	    	
	    	touched = true;
	    	break;
	    case MotionEvent.ACTION_UP:
	    	Log.e("KingFisher", "NOT CASTABLE!!!");
	    	touched = false;
	    	break;
	    }
		return true;
	}

	/**
	 * Sensor accuracy has changed. We don't need to worry about this.
	 * 
	 * @param sensor the sensor that has been affected. Should be accelerometer
	 * @param accuracy the new accuracy setting for that sensor
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	
	/** TODO: determine how good the cast is and save it in prefs.
	 * Called when a sensor gets a read. This will happen a LOT. Determine if the user has casted the rod here.
	 * 
	 * @param event the event that we received from hardware. use event.values to find x, y, and z readings. note: this is also just the accelerometer
	 */
	public void onSensorChanged(SensorEvent event) {
		//If the user's finger is on the screen and we're not already casting...
		if (touched) {
			if (!casting) {
				//if the readings are strong enough over the right axes (x and y)...
				if (event.values[1] < -SensorManager.GRAVITY_EARTH * 1.5 && (Math.abs(event.values[0]) > SensorManager.GRAVITY_EARTH || Math.abs(event.values[2]) > SensorManager.GRAVITY_EARTH)) {
					//CAST!!!
					Log.e("KingFisher", "CAST!!!");
					casting = true;
					vibrotron.vibrate(castPattern, -1);
					
					//play the casting audio.
					SoundManager.playSound(1, 1);
					
					//TODO: launch the cast animation, wait for it to finish. launch next activity. reeler.
					
					//Free up listeners, hardware, etc. and launch the Reeler Activity.
					try {
			        	Intent ourIntent = new Intent(Caster.this, Class.forName("org.MAG.Reeler"));
			        	sensorManager.unregisterListener(this);
			        	vibrotron = null;
			        	sensorManager = null;
			        	casterBackground.setOnTouchListener(null);
			        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(ourIntent);
						finish();
					} catch (ClassNotFoundException ex) {
						Log.e("INTRO", "Failed to jump to another activity");
					}
				}
			}
		}
		else casting = false;
	}
}