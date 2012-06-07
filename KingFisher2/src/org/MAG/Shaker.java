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
import android.view.Window;
import android.view.WindowManager;

public class Shaker extends Activity implements SensorEventListener {

	private static final String TAG = "Shaker";
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private float m_totalForcePrev;
	private int shiverTimbers;
	
	private int catchID;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.shaker);
		
		Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            catchID = extras.getInt("CatchID");
            Log.d(TAG, "Catch ID: " + catchID);
        }
		
		shiverTimbers = 0;
		Log.e("KingFisher", "made the Shaker");
		
		SoundManager.loadSounds(SoundManager.SHAKABLE);
		
		vibrotron = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	@Override
	public void onPause() {
		sensorManager.unregisterListener(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
	
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
        	
        	vibrotron.vibrate(300);
        	SoundManager.playCoinSound(1, 1);
        	
        	if (shiverTimbers > 15) {
        		sensorManager.unregisterListener(this);
        		//TODO: jump to the next activity now. bundle up which king was caught and send it along!
        		
        		try {
                	Intent ourIntent = new Intent(Shaker.this, Class.forName("org.MAG.Rejecterator"));
                	ourIntent.putExtra("CatchID", catchID);
                	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(ourIntent);
        			finish();
        		} catch (ClassNotFoundException ex) {
        			Log.e(TAG, "Failed to jump to another activity");
        		}
        		
        		
    		}
        }
       
        m_totalForcePrev = (float) totalForce;
	}

}
