package org.MAG;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;


//TODO: shake to confirm level selection?
public class Level extends LinearLayout implements SensorEventListener {

	private KingFisherActivity owner;
	private Gallery gallery;
    private ImageView imgView;
    private boolean levelChosen;
    private int selectedLevel;
    private ImageAdapter imgAdapt;
    private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private float m_totalForcePrev;
	private Level me;
    
    public boolean getFinished() {
    	return levelChosen;
    }
    
	public Level(final KingFisherActivity owner) {
		super(owner);
		this.owner = owner;

		vibrotron = (Vibrator) owner.getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)owner.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		LayoutParams lP = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		gallery = new Gallery(owner);
		imgView = new ImageView(owner);
		
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		gallery.setLayoutParams(lP);
		imgView.setLayoutParams(lP);
		
		imgAdapt = new ImageAdapter(owner);
		gallery.setAdapter(imgAdapt);
		
		this.addView(gallery);
		this.addView(imgView);
		this.setBackgroundResource(R.drawable.cast);
		me = this;
		
		owner.runOnUiThread(new Runnable() {
			public void run() {
				//owner.setContentView(linLay);
				owner.setImageView(me);
			}
		});
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
	        public void onItemSelected(AdapterView parent, View v, int position, long id) {
	        	//Log.e("KingFisher", "selected a Level");
	        	selectedLevel = position;
	        	setBackgroundResource(imgAdapt.getImg(position));
	        }

			public void onNothingSelected(AdapterView parent) {
				// TODO Auto-generated method stub
			}
	    });
		
		
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// 
	}

	public void onSensorChanged(SensorEvent event) {
		double forceThreshHold = 2.5f;
        double totalForce = 0.0f;
        totalForce += Math.pow(event.values[SensorManager.DATA_X]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(event.values[SensorManager.DATA_Y]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(event.values[SensorManager.DATA_Z]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce = Math.sqrt(totalForce);
       
        if ((totalForce < forceThreshHold) && (m_totalForcePrev > forceThreshHold)) {
        	Log.e("KingFisher", "SHAKE!");
        	levelChosen = true;
        	vibrotron.vibrate(300);
        }
        
        m_totalForcePrev = (float) totalForce;
	}
	
	public void onDestroy() {
		sensorManager.unregisterListener(this);
	}

}
