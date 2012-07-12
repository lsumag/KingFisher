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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * This activity is responsible for letting the user select a level to play and then passing that on to the Travel activity.
 * @author UnderGear
 *
 */
public class LevelSelection extends Activity implements SensorEventListener, OnPageChangeListener {

	private static final String TAG = "LevelSelection";
	
	public static final int LEVEL_LOCKED = 0;
	public static final int LEVEL_UNLOCKED = 1;
	public static final int LEVEL_COMPLETE = 2;
	
	private SoundManager soundManager;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private final long[] negativePattern = {0, 50, 50, 50};
	
	private double totalPreviousAcceleration;
	private final double accelerationThreshold = 2.5f;
	private double totalAcceleration;
	
	private ViewPager viewPager;
	private MyPagerAdapter pagerAdapter;
	
	private static ImageView[] levelScreens = new ImageView[4]; 
	//TODO: we will have 3 images for each level. Locked screen, black silhouette, colored-in king. these correspond to locked levels, unlocked uncompleted levels, and completed levels
	private int[] levelStatus = new int[levelScreens.length];
	
	private SharedPreferences settings;
	
	private static int selectedLevel;
	
	private AudioTask audioTask;
	
	/**
	 * Called at activity start. Grab our preferences for selected level, load up sounds from the manager, populate a horizontal pager with level selections
	 * Set up vibrator, sensor manager, accelerometer, and register the listener
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //set up the window
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		soundManager = SoundManager.getInstance();
		
		audioTask = new AudioTask();
		
        //Level lock/unlocked statuses are kept in preferences.
        settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
        
        levelScreens[0] = new ImageView(this);
        
        levelStatus[0] = settings.getInt("level0", LEVEL_UNLOCKED);
        if (levelStatus[0] == LEVEL_UNLOCKED) {
        	levelScreens[0].setBackgroundResource(R.drawable.napoleon_level);
        }
        else
        	levelScreens[0].setBackgroundResource(R.drawable.napoleon_sprite4);
        
        //Determine which levels are unlocked. Each should have a corresponding locked vs unlocked background that we are setting here.
        for (int i = 1; i < levelScreens.length; i++) {
        	
        	levelScreens[i] = new ImageView(this);
        	levelStatus[i] = settings.getInt("level"+i, LEVEL_LOCKED);
        	
        	Log.d(TAG, "level " + i + ": " + levelStatus[i]);
        	
        	//Set background.
        	//TODO: look up correct backgrounds. we should probably do this with 3 arrays and just look up the right thing by index.
        	switch (levelStatus[i]) {
        	case LEVEL_LOCKED:
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_56); //lockedBackgrounds[i]
        		break;
        	case LEVEL_UNLOCKED:
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_32); //unlockedBackgrounds[i]
        		break;
        	case LEVEL_COMPLETE:
        		levelScreens[i].setBackgroundResource(R.drawable.napoleon_sprite4); //completedBackgrounds[i]
        		break;
        	}
        }
        
        //Setting up horizontal paging here.
        setContentView(R.layout.level_selecter);
        pagerAdapter = new MyPagerAdapter();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        
        viewPager.setOnPageChangeListener(this);
        
        //Setting up sensors, vibrator, listeners.
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        //set the viewpager to the selected level - mostly used if we're returning to level selection from a catch
        viewPager.setCurrentItem(selectedLevel);
    }
	
	public static int getLevel() {
		return selectedLevel;
	}
	
	public static void setLevel(int level) {
		selectedLevel = level;
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
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		//Load up the sounds for the LevelSelection Activity!
        soundManager.loadSounds(SoundManager.LEVEL);
        
        
        audioTask.execute();
	}

	/**
	 * Sensor accuracy has changed. This is accelerometer
	 * 
	 * @param sensor the sensor that has changed
	 * @param value the new accuracy value
	 */
	public void onAccuracyChanged(Sensor sensor, int value) { }

	/**
	 * Received a new sensor reading. Accelerometer. We are looking for a shake event.
	 * 
	 * @param event the reading from the accelerometer. Use event.values to pull out x, y, and z values of the read.
	 */
	public void onSensorChanged(SensorEvent event) {
        
        //finding the length of the acceleration vector on this reading.
		totalAcceleration = 0;
        totalAcceleration += Math.pow(event.values[SensorManager.DATA_X]/SensorManager.GRAVITY_EARTH, 2.0);
        totalAcceleration += Math.pow(event.values[SensorManager.DATA_Y]/SensorManager.GRAVITY_EARTH, 2.0);
        totalAcceleration += Math.pow(event.values[SensorManager.DATA_Z]/SensorManager.GRAVITY_EARTH, 2.0);
        totalAcceleration = Math.sqrt(totalAcceleration);
        //this should probably take time into account, but it seems to work well enough as is.
        
        //A shake event should occur at the end of the shake. Last acceleration is over threshold and this one is under it.
        if ((totalAcceleration < accelerationThreshold) && (totalPreviousAcceleration > accelerationThreshold)) {
        	//Log.d("KingFisher", "SHAKE!");
        	
        	//Launch the selected level only if it is unlocked. greater than 0 is unlocked.
        	if (levelStatus[selectedLevel] > LEVEL_LOCKED) {
        		
        		vibrotron.vibrate(300);
        		audioTask.cancel(true);
        		
        		//Launch the next Activity - TravelScene
        		try {
                	Intent ourIntent = new Intent(LevelSelection.this, Class.forName("org.MAG.TravelScene"));
                	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(ourIntent);
        			sensorManager.unregisterListener(this);
        			finish();
        		} catch (ClassNotFoundException ex) {
        			Log.e(TAG, "Failed to jump to another activity");
        		}
        	}
        	//The selected level is locked.
        	else {
        		audioTask.levelLocked();
        		vibrotron.vibrate(negativePattern, -1); //two quick buzzes
        	}
        }
        //set last acceleration to current read.
        totalPreviousAcceleration = totalAcceleration;
	}
	
	/**
	 * PagerAdapter for our Horizontal Pager.
	 * @author UnderGear
	 *
	 */
	private static class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return levelScreens.length;
		}
		
		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }
		
		@Override
		public Object instantiateItem(View collection, int position) {
			((ViewPager) collection).addView(levelScreens[position], 0);
            return levelScreens[position];
        }

		@Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
	}

	public void onPageScrollStateChanged(int state) { }

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

	public void onPageSelected(int position) {
		selectedLevel = position;
		vibrotron.vibrate(50);
		audioTask.levelSelected(position);
	}
	
	/**
	 * Task to handle the narrator giving instructions to the player
	 * @author undergear
	 *
	 */
	private class AudioTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				Log.e(TAG, e1.getMessage());
			}
			//SoundManager.playSound(2, 1); //TODO: instructions go here.
			
			while (true) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
				//TODO: play audio instructions here.
			}
		}
		
		public void levelLocked() {
			//TODO: play something like "you're not skilled enough to go here just yet"
		}
		
		public void levelSelected(int levelID) {
			//TODO: wait a bit and then play something based on the level.
		}
	}
}
