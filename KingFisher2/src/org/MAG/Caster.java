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

	
	//TODO: determine how good the cast is and save it in prefs.
	public void onSensorChanged(SensorEvent event) {
		if (touched) {
			if (!casting) {
				if (event.values[1] < -SensorManager.GRAVITY_EARTH * 1.5 && (Math.abs(event.values[0]) > SensorManager.GRAVITY_EARTH || Math.abs(event.values[2]) > SensorManager.GRAVITY_EARTH)) {
					Log.e("KingFisher", "CAST!!!");
					casting = true;
					vibrotron.vibrate(castPattern, -1);
					
					SoundManager.playSound(1, 1);
					
					//TODO: launch the cast animation, wait for it to finish. launch next activity. reeler.
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
					//Log.e("KingFisher", "X: "+ event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
				}
			}
		}
		else casting = false;
	}
}
