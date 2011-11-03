package org.MAG;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Reeler extends ImageView implements OnTouchListener {
	
	
	Handler mHandler = new Handler();
	
	private static final DisplayMetrics metrics = new DisplayMetrics();
	private int width;
	private int height;
	private int minusHalfWidth;
	private int minusHalfHeight;
	private int reelability;
	
	//oh god. reeling.
	private float currentTheta;
	private float currentRadius;
	private float previousTheta;
	private float previousRadius;
	private float x, y;
	private float angularDelta;
	private float angularDistance;
	private float angularVelocity;
	private float[] velocities = {1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};
	private int velCount;
	private float vListSum;
	private float averageVelocity;
	private float currentTimeStep;
	private float dispatchTimer;
	private int play = 1;
	private int cap = 1;
	private int fishDistance, lineStrength;
	
	private boolean hooked; //TODO IMPLEMENT ME! you shouldn't just automatically hook something. be patient.
	//we also need something to decide WHAT is hooked. you shouldn't always get the king.
	
	private Vibrator vibrotron;
	private Reeler me;
	
	public int getReel() {
		return reelability;
	}
	
	//sync up wheel movement with other stuff. starts at 44, goes to 56. separate that from cast2 into wheel.xml
	//TODO: make reeling difficulty scale based on level. also make it take more time in general.
	public Reeler(final KingFisherActivity owner) {
		super(owner);
		me = this;
		vibrotron = (Vibrator) owner.getSystemService(Context.VIBRATOR_SERVICE);
		owner.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        minusHalfHeight = -height/2;
        minusHalfWidth = -width/2;
        Log.e("KingFisher", "made the Reeler");
        
        this.clearAnimation();
        this.setBackgroundResource(R.drawable.cast2);
		owner.runOnUiThread(new Runnable() {
			public void run() {
				owner.setImageView(me);
				AnimationDrawable frameAnimation = (AnimationDrawable) getBackground();
				frameAnimation.start();
			}
		});
        
        //we need this. trust me, bro.
        this.setOnTouchListener(this);

        fishDistance = 50;
        lineStrength = 100;
	}
	
	
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		event.offsetLocation(minusHalfWidth, minusHalfHeight); //offset - now relative to center
		x = event.getX();
		y = event.getY();
		
		//let's rock and reel!
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//start tracking.
			previousRadius = (float) Math.sqrt( x * x + y * y );
			previousTheta = (float) Math.acos( x / previousRadius );
			//
		         Log.i("TIMER_SAMPLE", "Starting timer");
		         mHandler.removeCallbacks(mUpdateTimeTask);
		         mHandler.postDelayed(mUpdateTimeTask, 1000);
		         //SoundManager.playSound(1, 1);
	        
			
			break;
		case MotionEvent.ACTION_MOVE:
			//update movement.
			currentRadius = (float) Math.sqrt( x * x + y * y );
			currentTheta = (float) Math.acos( x / currentRadius );
			
			angularDelta = Math.abs(currentTheta - previousTheta); //find the angular change in radians from last update
			
			float change = currentTheta - previousTheta;
			
			currentTimeStep = event.getHistoricalEventTime(0); //time since last update
			
			angularDistance = angularDistance + angularDelta;
			angularVelocity = (float) ((angularDelta / currentTimeStep) * (1000000000 / Math.PI));
			previousTheta = currentTheta; //updating these for next cycle through.
			previousRadius = currentRadius;
			
			
			//this section averages angular velocity over the last 10 reads.
			velCount++;
			if (velCount == 10) velCount = 0;
			velocities[velCount] = angularVelocity;
			for (Float velocity : velocities) {
				vListSum += velocity;
			}
			averageVelocity = vListSum/velocities.length;
			vListSum = 0;
			Log.e("KingFisher", "" + averageVelocity);
			//timeslice. evaluate how the user is doing, vibrate, play sound and update fishDistance and lineStrength
			dispatchTimer += currentTimeStep;
			if (dispatchTimer > 50000000L) {
				//TODO: the RHS of these comparisons should change based on the level difficulty.
				//make noises and set vibration patterns for these!
				if (averageVelocity < 0.08f) { //TOO SLOW!
					lineStrength += .10;
					fishDistance += .05;
					cap = 4;
				}
				else if (averageVelocity > 0.8f) { //TOO FAST!
					lineStrength -= .25;
					fishDistance -= .15;
					cap = 0;
				}
				else { //JUST RIGHT!
					lineStrength += .05;
					fishDistance -= .10;
					cap = 2;
				}
				
				//TODO: if (change > 0), move the reel one way, otherwise move it the other way.
				//maybe the reel animation should just be done with one image being turned to the same angle as from center between 0 and finger location
				
				play--;
				if (play <= 0) {
					SoundManager.playClickSound(4, 0.7f);
					vibrotron.vibrate(50);
					play = cap;
				}
				if (play > cap) play = cap;
				if (fishDistance <= 0) {
					//SUCCESS
					Log.e("REELER", "YOU'RE DOING IT RIGHT!");
					reelability = 2;
					SoundManager.playKingSound(2, 1);
					this.setOnTouchListener(null);
					
				}
				else if (fishDistance > 100) {
					//FAILURE
					Log.e("REELER", "YOU'RE DOING IT WRONG!");
					reelability = 1;
				}
				if (lineStrength > 100) lineStrength = 100;
				else if (lineStrength < 0) {
					//FAILURE
					Log.e("REELER", "YOU'RE DOING IT WRONG!");
					reelability = 1;
					
				}
				dispatchTimer = 0;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			//stop tracking.
			vibrotron.cancel();
			mHandler.removeCallbacks(mUpdateTimeTask);
			angularDistance = 0;
			break;
		}
		
		return true;
	}
	private Runnable mUpdateTimeTask = new Runnable() {
	  	  public void run() {
	  		  SoundManager.playSound(1, 1);
	  		  Log.i("TIMER_SAMPLE", "Timer updated ");
	  		  mHandler.postDelayed(this, 1000); //change 1000 to another variable
	  	  }
		};
}