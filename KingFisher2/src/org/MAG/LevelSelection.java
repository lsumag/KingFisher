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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 
 * @author UnderGear
 *
 */
public class LevelSelection extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	
	private double totalPreviousAcceleration;
	private final double accelerationThreshold = 2.5f;
	private double totalAcceleration;
	
	private MyViewPager viewPager;
	private MyPagerAdapter pagerAdapter;
	
	private static ImageView[] levelScreens = new ImageView[4];
	private boolean[] levelsUnlocked = new boolean[4];
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
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
        
		//Load up the sounds for the LevelSelection Activity!
        SoundManager.loadSounds(SoundManager.LEVEL);
        
        //Level 0 is always unlocked and selected.
        levelScreens[0] = new ImageView(this);
        levelScreens[0].setBackgroundResource(R.drawable.cast2_animation_32); //placeholder background.
        levelsUnlocked[0] = true;
        
        //Level lock/unlocked statuses are kept in preferences.
        settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
        editor = settings.edit();
        editor.putInt("SelectedLevel", 0); //select level 0 to begin with.
        editor.commit();
        
        //Determine which levels are unlocked. Each should have a corresponding locked vs unlocked background that we are setting here.
        for (int i = 1; i < levelScreens.length; i++) {
        	levelScreens[i] = new ImageView(this);
        	levelsUnlocked[i] = settings.getBoolean("level"+i+1, false);
        	
        	//Set background.
        	//TODO: look up correct backgrounds. we should probably do this with 2 arrays and just look up the right thing by index.
        	if (levelsUnlocked[i]) 
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_32); //unlockedBackgrounds[i]
        	else
        		levelScreens[i].setBackgroundResource(R.drawable.cast2_animation_56); //lockedBackgrounds[i]
        }
        
        //Setting up horizontal paging here.
        setContentView(R.layout.level_selecter);
        pagerAdapter = new MyPagerAdapter();
        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        viewPager.init(this);
        viewPager.setAdapter(pagerAdapter);
        
        //Setting up sensors, vibrator, listeners.
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        //TODO: launch instructional audio thread
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
        	Log.e("KingFisher", "SHAKE!");
        	
        	//Launch the selected level only if it is unlocked.
        	if (levelsUnlocked[settings.getInt("SelectedLevel", 0)]) {
        		
        		vibrotron.vibrate(300); //TODO: we should create a pattern to vibrate on level selection.
        		//Launch the next Activity - TravelScene
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
        	//The selected level is not unlocked.
        	else {
        		//TODO: if not available, vibrate "no" pattern. two buzzes in quick succession?
        	}
        }
        
        //set last acceleration to current read.
        totalPreviousAcceleration = totalAcceleration;
	}
	
	/**
	 * 
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
	
	/**
	 * This should be called when a page is selected in the horizontal pager.
	 * It sets the selected level to the index of the page selected and puts that into the preferences.
	 * 
	 * @param i
	 */
	void updateContentStatus(int i) {
		//TODO: we can rename this method and do our level description audio here. launch an asynctask to wait and then play the level description.
		//we can do a little buzz when we switch into focus, too.
		if (levelsUnlocked[i]) {
			editor.putInt("SelectedLevel", i);
			editor.commit();
		}
	}
}
