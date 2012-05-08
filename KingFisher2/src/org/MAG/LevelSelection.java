package org.MAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class LevelSelection extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private float m_totalForcePrev;
	
	private MyViewPager viewPager;
	private MyPagerAdapter pagerAdapter;
	
	private static ImageView[] levelScreens = new ImageView[4];
	private boolean[] levelsUnlocked = new boolean[4];
	
	private int selectedLevel;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //we need to keep up with what levels the user has unlocked.
        settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
        editor = settings.edit();
        editor.putInt("SelectedLevel", 0);
        editor.commit();
        
        SoundManager.loadSounds(SoundManager.LEVEL);
        
        levelScreens[0] = new ImageView(this);
        levelScreens[0].setBackgroundResource(R.drawable.cast2_animation_32);
        levelsUnlocked[0] = true;
        
        for (int i = 1; i < levelScreens.length; i++) {
        	levelScreens[i] = new ImageView(this);
        	levelsUnlocked[i] = settings.getBoolean("level"+i+1, false);
        	
        	//TODO: set backgrounds appropriately.
        	
        	if (levelsUnlocked[i]) 
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_32);
        	else
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_56);
        }
        
        setContentView(R.layout.level_selecter);
        pagerAdapter = new MyPagerAdapter();
        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        viewPager.init(this);
        viewPager.setAdapter(pagerAdapter);
        
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        //TODO: launch instructional audio thread
        
        //TODO: horizontal pager goes here. flip through the available levels, maybe play audio when a level is focused for 2 seconds
        //maybe vibrate as each one comes into focus.
    }

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
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
        	
        	if (levelsUnlocked[settings.getInt("SelectedLevel", 0)]) {
        		
        		vibrotron.vibrate(300);
        		try {
                	Intent ourIntent = new Intent(LevelSelection.this, Class.forName("org.MAG.TravelScene"));
                	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(ourIntent);
        			sensorManager.unregisterListener(this);
        			finish();
        		} catch (ClassNotFoundException ex) {
        			Log.e("LEVELSELECTION", "Failed to jump to another activity");
        		}
        	}
        	else {
        		//TODO: if not available, vibrate no pattern.
        	}
        }
        
        m_totalForcePrev = (float) totalForce;
		
	}
	
	private static class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return levelScreens.length;
		}
		
		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }
		
		public Object instantiateItem(View collection, int position) {
			((ViewPager) collection).addView(levelScreens[position], 0);
            return levelScreens[position];
        }

		@Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
	}
	
	void updateContentStatus(int i) {
		//TODO: we can rename this one and do our level description audio here. 
		selectedLevel = i;
		if (levelsUnlocked[i]) {
			editor.putInt("SelectedLevel", i);
			editor.commit();
		}
	}
}
