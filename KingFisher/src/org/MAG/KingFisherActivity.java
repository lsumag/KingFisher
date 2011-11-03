package org.MAG;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**TODO: 
 * general options, save preferences
 * a tutorial mode
 * levels screens, multiple kings to unlock
 * a few null pointer exceptions when closing the app.
 * implement king images during shake
 * work on reeling - haptics, animations. we should also have a time before the king bites.
 *    king difficulties, different catches, maybe some accelerometer use when reeling?
 * achievement screen
 * WIN
 */
public class KingFisherActivity extends Activity {
	
    //hook for the image
    private FrameLayout mainLayout;
    private MainGameView gameView;
    private View view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		gameView = new MainGameView(this);
		
		view = new ImageView(this);
        //imageView.setOnTouchListener(this);
        view.setBackgroundResource(R.drawable.title_screen);
		
		
        mainLayout = new FrameLayout(this);
        setContentViewDefault();
    }
    
    public void setContentViewDefault() {
    	
    	mainLayout.removeAllViews();
    	
    	((ViewGroup) mainLayout).addView(gameView);
		((ViewGroup) mainLayout).addView(view);
		
        setContentView(mainLayout);
    }
    
	
	public void setImageView(View iV) {
		mainLayout.removeView(view);
		//view = iV;
		mainLayout.addView(iV);
	}
	
	public void setView(View v) {
		mainLayout.removeAllViews();
		//mainLayout.addView(gameView);
		mainLayout.addView(v);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Log.e("KingFisher", ""+sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gameView.getThread().onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		gameView.getThread().exterminate();
		//imageView = null;
	}
}