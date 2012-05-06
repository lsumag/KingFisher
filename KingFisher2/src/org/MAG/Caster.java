package org.MAG;

import android.app.Activity;
import android.content.Context;
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

public class Caster extends Activity implements OnTouchListener, SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	
	private boolean touched;
	private boolean casting;
	private final long[] castPattern = {0, 50, 50, 50, 50, 50, 50, 50, 50};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.caster);
        
        //TODO: instructional audio
        
        //TODO: background animation. we'll need 2 layers of SurfaceView and an asynctask
        
        sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
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

	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	public void onSensorChanged(SensorEvent event) {
		if (touched) {
			if (!casting) {
				if (event.values[1] < -SensorManager.GRAVITY_EARTH * 1.5 && (Math.abs(event.values[0]) > SensorManager.GRAVITY_EARTH || Math.abs(event.values[2]) > SensorManager.GRAVITY_EARTH)) {
					Log.e("KingFisher", "CAST!!!");
					casting = true;
					vibrotron.vibrate(castPattern, -1);
					
					exterminate();
					SoundManager.playSound(1, 1);
					
					//TODO: launch the cast animation, wait for it to finish. launch next activity. reeler.
					
					//Log.e("KingFisher", "X: "+ event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
				}
			}
		}
		else casting = false;
	}
	
	public void exterminate() {
		sensorManager.unregisterListener(this);
		vibrotron = null;
		sensorManager = null;
	}
	
	//TODO: listen for the cast gesture, launch cast animation if touched.
	
	//TODO: once animation finishes, launch Reeler Activity
}
