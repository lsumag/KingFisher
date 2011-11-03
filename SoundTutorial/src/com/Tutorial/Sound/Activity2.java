package com.Tutorial.Sound;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Activity2 extends Activity {
    private Button mChangeActivityButton;
	private Button mPlaySound1Button;
	private Button mPlaySound2Button;
	
	Handler mHandler = new Handler();
    long mStartTime;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);
        
        
        mChangeActivityButton = (Button) this.findViewById(R.id.ChangeActivity);
        mChangeActivityButton.setOnClickListener(new OnClickListener() {
        	  public void onClick(View v) {
        		  Log.i("TIMER_SAMPLE", "Stopping timer");
        		  mHandler.removeCallbacks(mUpdateTimeTask);
        		  Intent StartGameIntent = new Intent(Activity2.this,SoundTutorial.class);
                  startActivity(StartGameIntent);
        	  }
        	});
        
        mPlaySound1Button = (Button) this.findViewById(R.id.PlaySound1);
        mPlaySound1Button.setOnClickListener(new OnClickListener() {
        	  public void onClick(View v) {
        		             		
        		  SoundManager.playSound(1, 1);
        	  }
        	});
        
        mPlaySound2Button = (Button) this.findViewById(R.id.PlaySound2);
        mPlaySound2Button.setOnClickListener(new OnClickListener() {
        	  public void onClick(View v) {
        		             		
        		  SoundManager.playKingSound(1, 1);
        	  }
        	});
        
        
        Log.i("TIMER_SAMPLE", "Initialising timer");
        if (mStartTime == 0L) {
         Log.i("TIMER_SAMPLE", "Starting timer");
         mStartTime = System.currentTimeMillis();
         mHandler.removeCallbacks(mUpdateTimeTask);
         mHandler.postDelayed(mUpdateTimeTask, 1000);
        }
        Log.i("TIMER_SAMPLE", "Timer started");

    }
    
    private Runnable mUpdateTimeTask = new Runnable() {
    	  public void run() {
    		  SoundManager.playClickSound(4, 1);
    		  Log.i("TIMER_SAMPLE", "Timer updated ");
    		  mHandler.postDelayed(this, 1000);
    	  }
    };

}