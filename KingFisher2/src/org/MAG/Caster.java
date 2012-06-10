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

	private static final String TAG = "Caster";
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	
	private boolean touched;
	private boolean casting;
	private final long[] castPattern = {0, 50, 50, 50, 50, 50, 50, 50, 50};
	
	private ImageView casterBackground;
	
	private int levelID;
	private int castDistance = 100; //TODO: change from 100 by default.
	
	//TODO: we need a way to keep track of the quality of the cast.
	
	/**
	 * Initiate the Caster Activity, load the sounds up, set up touch listener, vibrator, sensor manager, and accelerometer sensor
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            levelID = extras.getInt("SelectedLevel");
            Log.d(TAG, "Selected Level ID: " + levelID);
        }
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.caster);
        
        casterBackground = (ImageView)findViewById(R.id.caster_background);
        casterBackground.setOnTouchListener(this);
        
        //TODO: instructional audio - tell the user how to cast!
        
        //TODO: background animation. we'll need 2 layers of SurfaceView and an asynctask
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
	
	@Override
	public void onPause() {
		if (sensorManager != null) sensorManager.unregisterListener(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		
		
        SoundManager.loadSounds(SoundManager.CASTABLE);
		
        //TODO: wait for a few seconds, then play the sound.
		//SoundManager.playSound(2, 1);
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
	    	touched = true;
	    	break;
	    case MotionEvent.ACTION_UP:
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

	
	/** TODO: wait for the cast gesture to finish before deciding on a castDistance value to send.
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
					casting = true;
					vibrotron.vibrate(castPattern, -1);
					
					//play the casting audio.
					SoundManager.playSound(1, 1);
					
					//TODO: launch the cast animation, wait for it to finish before doing the following try block
					
					//Free up listeners, hardware, etc. and launch the Reeler Activity.
					try {
			        	Intent ourIntent = new Intent(Caster.this, Class.forName("org.MAG.Reeler"));
			        	ourIntent.putExtra("SelectedLevel", levelID);
			        	ourIntent.putExtra("CastDistance", castDistance);
			        	sensorManager.unregisterListener(this);
			        	vibrotron = null;
			        	sensorManager = null;
			        	casterBackground.setOnTouchListener(null);
			        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(ourIntent);
						finish();
					} catch (ClassNotFoundException ex) {
						Log.e(TAG, "Failed to jump to another activity");
					}
				}
			}
		}
		else casting = false;
	}
}