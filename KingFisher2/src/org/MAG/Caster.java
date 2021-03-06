package org.MAG;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * The Caster Activity is where the player actually casts the rod in an attempt to catch a king. 
 * It is responsible for bundling up a cast distance (int) and sending that to the Reeler class.
 * 
 * The user interaction goal is to have this roughly simulate casting a real fishing rod.
 * The user should put a finger down on the touch screen of the device and then swing it forward in their hand, releasing the finger at the end of the cast.
 * 
 * @author undergear
 *
 */
public class Caster extends Activity implements OnTouchListener, SensorEventListener {

	private static final String TAG = "Caster";
	
	private SoundManager soundManager; //this is a reference to a Singleton, used in all Activities in the package that use short audio clips.
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	private Vibrator vibrotron;
	private final long[] castPattern = {0, 50, 50, 50, 50, 50, 50, 50, 50};
	
	private ImageView casterBackground;
	
	//TODO: Seriously, we need some more art!
	//TODO: we may need a MySurfaceView for sprites
	
	private AudioTask audioTask;
	
	private boolean casting;
	private Long lastTimestamp;
	private ArrayList<SensorEvent> readings;
	private int castDistance;
	
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
        
        castDistance = 0;
        
        casterBackground = (ImageView)findViewById(R.id.caster_background);
        casterBackground.setOnTouchListener(this);
        
        audioTask = new AudioTask();
        
        soundManager = SoundManager.getInstance();
        
        //TODO: background animation once we get the assets.
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
	
	@Override
	public void onPause() {
		if (sensorManager != null) sensorManager.unregisterListener(this);
		if (audioTask != null) audioTask.cancel(true);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
        soundManager.loadSounds(SoundManager.CASTABLE);
		
        audioTask.execute();
	}

	/**
	 * Listener called when the screen is touched. Only the casterBackground should be subscribed to this listener!
	 * Down events allow the user to cast, Up events prevent it.
	 * 
	 * @param v the View that has been touched. this should only be casterBackground!
	 * @param event the MotionEvent that has occurred. 
	 */
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	    	casting = true;
	    	lastTimestamp = System.nanoTime();
	    	sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	    	readings = new ArrayList<SensorEvent>();
	    	break;
	    case MotionEvent.ACTION_UP:
	    	sensorManager.unregisterListener(this);
	    	if (casting)
	    		endCast();
	    	break;
	    }
		return true;
	}

	/**
	 * Sensor accuracy has changed. We don't need to worry about this for our purposes.
	 * 
	 * @param sensor the sensor that has been affected. Should be accelerometer
	 * @param accuracy the new accuracy setting for that sensor
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	
	/**
	 * The sensor listener is only active when the user's finger is down. We are adding readings to the list for processing later.
	 * 
	 * @param event from the accelerometer
	 */
	public void onSensorChanged(SensorEvent event) {
		
		readings.add(event);
		
		/**Log.d(TAG, "axis 0: " + event.values[0] + ", axis 1: " + event.values[1]);
		if (SensorManager.GRAVITY_EARTH > Math.abs(event.values[0]) + Math.abs(event.values[1])) {
			endCast();
		}*/
	}
	
	/**
	 * At the end of the cast, analyze the data associated with it and determine a cast distance (or failure if too short)
	 * Launch the Reeler activity if the distance was large enough
	 */
	public void endCast() {
		
		castDistance = 0;
		
		//We're going to do some really inaccurate numerical integration for these readings. Rough results work well enough for this purpose on tested devices.
		//TODO: Test more devices. Currently used EVO4G, EVO3D, Galaxy Nexus, and older Nexus model. 
		for (SensorEvent event : readings) {
			Long timeDelta = (long) ((event.timestamp - lastTimestamp) * 10E-9);
			castDistance += timeDelta * Math.abs(event.values[0]) + timeDelta * Math.abs(event.values[1]);
			lastTimestamp = event.timestamp;
		}
    	
		Log.d(TAG, "Cast Distance: " + castDistance);
		
		//check for a weak cast and try again if it was really bad.
		if (castDistance < 25) {
			//TODO: play an audio track about not casting far enough. We need that asset still.
			casting = false;
			return;
		}
		
		vibrotron.vibrate(castPattern, -1);
		
		//play the casting audio.
		soundManager.playSound(1, 1);
		
		//kill the audio task
		if (audioTask != null) audioTask.cancel(true);
		
		//TODO: launch a cast animation/video when we get it, wait for it to finish before doing the following try block
		
		//Free up listeners, hardware, etc. and launch the Reeler Activity.
		try {
        	Intent ourIntent = new Intent(Caster.this, Class.forName("org.MAG.Reeler"));
        	ourIntent.putExtra("CastDistance", castDistance);
        	sensorManager.unregisterListener(this);
        	vibrotron = null;
        	sensorManager = null;
        	casterBackground.setOnTouchListener(null);
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			casting = false;
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Failed to jump to another activity");
		}
	}
	
	/**
	 * Task to handle the narrator giving instructions to the player. He'll speak every 10 seconds if the player isn't doing anything.
	 * @author undergear
	 *
	 */
	private class AudioTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				Log.e(TAG, e1.getMessage());
			}
			//"cast away!"
			soundManager.playSound(2, 1);
			
			while (true) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
				if (casting == false) {
					//TODO: play audio instructions here. We should have something telling the player exactly how to cast.
					soundManager.playSound(2, 1); //"cast away!" for now.
				}
			}
		}
	}
}