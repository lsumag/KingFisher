package org.MAG;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageView;

public class Rejecterator extends ImageView implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private Rejecterator me;
	private boolean rejectorated;
	
	public Rejecterator(final KingFisherActivity owner) {
		super(owner);
		
		rejectorated = false;
		
		setBackgroundResource(R.drawable.cast_animation_04);
		me = this;
		owner.runOnUiThread(new Runnable() {
			public void run() {
				owner.setImageView(me);
			}
		});
		
		Log.e("KingFisher", "made the Rejecterator");
		
		vibrotron = (Vibrator) owner.getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)owner.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public boolean getRejectorated() {
		return rejectorated;
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Yeah, well, you know, that's just like, your opinion, man.
	}

	public void onSensorChanged(SensorEvent event) {
		if (!rejectorated) {
			if (event.values[1] < -SensorManager.GRAVITY_EARTH * 1.5 && (Math.abs(event.values[0]) > SensorManager.GRAVITY_EARTH * 1.5 || Math.abs(event.values[2]) > SensorManager.GRAVITY_EARTH * 1.5)) {
				Log.e("KingFisher", "Throw him in the river!");
				
				vibrotron.vibrate(1500);
				SoundManager.playKingSound(1, 1);
				rejectorated = true;
				//SoundManager.playJesterSound(1, 1);
				//Log.e("KingFisher", "X: "+ event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
			}
		}
	}
	
}
