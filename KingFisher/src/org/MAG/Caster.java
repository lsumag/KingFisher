package org.MAG;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Caster extends ImageView implements OnTouchListener, SensorEventListener {
	
	private KingFisherActivity owner;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private boolean casting;
	private boolean touched;
	private Vibrator vibrotron;
	private final long[] castPattern = {0, 50, 50, 50, 50, 50, 50, 50, 50};
	private Caster me;
	private boolean casted;
	
	public Caster(final KingFisherActivity owner) {
		super(owner);
		this.owner = owner;
		vibrotron = (Vibrator) owner.getSystemService(Context.VIBRATOR_SERVICE);
		Log.e("Caster", "made the Caster");
		this.setBackgroundResource(R.drawable.cast);
		
		
		me = this;
		
		owner.runOnUiThread(new Runnable() {
			public void run() {
				owner.setImageView(me);
				AnimationDrawable frameAnimation = (AnimationDrawable) getBackground();
				frameAnimation.start();
			}
		});
		
		sensorManager = (SensorManager)owner.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        this.setOnTouchListener(this);
	}
	
	public void exterminate() {
		this.setOnTouchListener(null);
		sensorManager.unregisterListener(this);
		vibrotron = null;
	}
	
	public boolean getCasted() {
		return casted;
	}
	
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

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// get out of my house		
	}

	public void onSensorChanged(SensorEvent event) {
		if (touched) {
			if (!casting) {
				if (event.values[1] < -SensorManager.GRAVITY_EARTH * 1.5 && (Math.abs(event.values[0]) > SensorManager.GRAVITY_EARTH || Math.abs(event.values[2]) > SensorManager.GRAVITY_EARTH)) {
					Log.e("KingFisher", "CAST!!!");
					casting = true;
					vibrotron.vibrate(castPattern, -1);
					casted = true;
					exterminate();
					SoundManager.playSound(1, 1);
					
					
					//Log.e("KingFisher", "X: "+ event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
				}
			}
		}
		else casting = false;
	}
}