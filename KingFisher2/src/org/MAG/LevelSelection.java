package org.MAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO: set the levelScreens up here.
        //we need to keep up with what levels the user has unlocked.
        SharedPreferences settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
        for (int i = 0; i < levelScreens.length; i++) {
        	levelsUnlocked[i] = settings.getBoolean("level"+i+1, false);
        }
        
        setContentView(R.layout.level_selecter);
        pagerAdapter = new MyPagerAdapter();
        viewPager = (MyViewPager) findViewById(R.id.viewpager);
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
        	
        	vibrotron.vibrate(300);
        	
        	//TODO: launch next activity (travel) with current level selection.
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
			 
            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            int resId = 0;
            switch (position) {
            case 0:
                //resId = R.layout.continental_content;
                //screens.set(position, new Continent(this.owner));
                break;
            case 1:
                //resId = R.layout.skype_layout;
                //screens.set(position, new SkypeFeed(this.owner));
                break;
            default:
            	//resId = R.layout.stuff;
            	//screens.set(position, new TEIScreen(this.owner));
                break; 
            }
            
            //screen = screens.get(position);
            View view = inflater.inflate(resId, null);
            //screen.addView(view);
            ((ViewPager) collection).addView(view, 0);
            return view;
        }

		@Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
	}
	
	private static class MyViewPager extends ViewPager {
		
		public MyViewPager(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onPageScrolled(int position, float offset, int offsetPixels) {
			super.onPageScrolled(position, offset, offsetPixels);
			updateContentStatus(position);
		}
		
	}
	
	public static void updateContentStatus(int i) {
		//TODO: we can rename this one and do our level description audio here.
		
	}
}
