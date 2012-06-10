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
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	
	private double totalPreviousAcceleration;
	private final double accelerationThreshold = 2.5f;
	private double totalAcceleration;
	
	private ViewPager viewPager;
	private MyPagerAdapter pagerAdapter;
	
	private static ImageView[] levelScreens = new ImageView[4]; //TODO: we will have 3 images for each level. Locked screen, black silhouette, colored-in king. these correspond to locked levels, unlocked uncompleted levels, and completed levels
	private int[] levelStatus = new int[levelScreens.length];
	
	private SharedPreferences settings;
	
	private int selectedLevel;
	
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
        
        //Level 0 is always unlocked and selected.
        levelScreens[0] = new ImageView(this);
        levelScreens[0].setBackgroundResource(R.drawable.napoleon_level); //Napoleon silhouette
        levelStatus[0] = 1;
        
        //Level lock/unlocked statuses are kept in preferences.
        settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
        
        //Determine which levels are unlocked. Each should have a corresponding locked vs unlocked background that we are setting here.
        for (int i = 1; i < levelScreens.length; i++) {
        	levelScreens[i] = new ImageView(this);
        	levelStatus[i] = settings.getInt("level"+i+1, 0);
        	
        	//Set background.
        	//TODO: look up correct backgrounds. we should probably do this with 3 arrays and just look up the right thing by index.
        	switch (levelStatus[i]) {
        	case 0:
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_56); //lockedBackgrounds[i]
        		break;
        	case 1:
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_32); //unlockedBackgrounds[i]
        		break;
        	case 2:
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_32); //completedBackgrounds[i]
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
        
        //TODO: launch instructional audio thread - something like "swipe left and right to change levels. shake on it to confirm."
    }
	
	@Override
	public void onPause() {
		if (sensorManager != null) sensorManager.unregisterListener(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		//Load up the sounds for the LevelSelection Activity!
        SoundManager.loadSounds(SoundManager.LEVEL);
		
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
        //TODO: this should probably take time into account, but it seems to work well enough as is.
        
        //A shake event should occur at the end of the shake. Last acceleration is over threshold and this one is under it.
        if ((totalAcceleration < accelerationThreshold) && (totalPreviousAcceleration > accelerationThreshold)) {
        	//Log.d("KingFisher", "SHAKE!");
        	
        	//Launch the selected level only if it is unlocked. greater than 0 is unlocked.
        	if (levelStatus[selectedLevel] > 0) {
        		
        		vibrotron.vibrate(300); //TODO: we should create a pattern to vibrate on level selection.
        		//Launch the next Activity - TravelScene
        		try {
                	Intent ourIntent = new Intent(LevelSelection.this, Class.forName("org.MAG.TravelScene"));
                	ourIntent.putExtra("SelectedLevel", selectedLevel);
                	
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
        		//TODO: if not available, vibrate "no" pattern. two buzzes in quick succession? play something like "you're not skilled enough to go here just yet"
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
	}
}
