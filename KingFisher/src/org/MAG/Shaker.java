package org.MAG;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageView;

public class Shaker extends ImageView implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private float m_totalForcePrev;
	private Shaker me;
	private int shiverTimbers;
	private boolean shaken;
	
	public Shaker(final KingFisherActivity owner) {
		super(owner);
		this.clearAnimation();
		setBackgroundResource(R.drawable.cast_animation_04);
		me = this;
		shaken = false;
		shiverTimbers = 0;
		owner.runOnUiThread(new Runnable() {
			public void run() {
				owner.setImageView(me);
			}
		});
		Log.e("KingFisher", "made the Shaker");
		
		vibrotron = (Vibrator) owner.getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)owner.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// so what?
		
	}

	public void chillOut() {
		sensorManager.unregisterListener(this);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {		
		double forceThreshHold = 2.5f;
        double totalForce = 0.0f;
        totalForce += Math.pow(event.values[SensorManager.DATA_X]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(event.values[SensorManager.DATA_Y]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(event.values[SensorManager.DATA_Z]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce = Math.sqrt(totalForce);
       
        if ((totalForce < forceThreshHold) && (m_totalForcePrev > forceThreshHold)) {
        	Log.e("KingFisher", "SHAKE!");
        	shiverTimbers++;
        	if (shiverTimbers > 15) { shaken = true; sensorManager.unregisterListener(this); }
        	vibrotron.vibrate(300);
        	SoundManager.playCoinSound(1, 1);
        	Log.e("KingFisher", ""+shaken);
        	//maybe activate a sound here.
        }
       
        m_totalForcePrev = (float) totalForce;
	}
	
	public boolean getShaken() {
		return shaken;
	}
}