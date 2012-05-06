package org.MAG;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class TravelScene extends Activity implements OnTouchListener {

	private ImageView travel;
	private AnimationDrawable frameAnimation;
	
	private TimerTask timerTask;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.travel); 
        
        //TODO: travel audio
        
        travel = (ImageView)findViewById(R.id.travel_background);
        travel.setOnTouchListener(this);
        travel.setBackgroundResource(R.drawable.cast);
        
        frameAnimation = (AnimationDrawable) travel.getBackground();
        frameAnimation.start();
        long totalDuration = 0;
        for (int i = 0; i < frameAnimation.getNumberOfFrames(); i++) {
        	totalDuration += frameAnimation.getDuration(i);
        }
        Timer timer = new Timer();
        timerTask = new TimerTask() {
			@Override
			public void run() {
				frameAnimation.stop();
				loadNextLevel();
			}
        };
        timer.schedule(timerTask, totalDuration);
    }
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		loadNextLevel();
		return true;
	}
	
	private void loadNextLevel() {
		Log.e("TravelScene", "finished. Loading Caster");
		try {
        	Intent ourIntent = new Intent(TravelScene.this, Class.forName("org.MAG.Caster"));
        	travel.setOnTouchListener(null);
        	timerTask.cancel();
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e("INTRO", "Failed to jump to another activity");
		}
	}
}
