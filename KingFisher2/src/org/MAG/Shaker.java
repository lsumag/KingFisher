package org.MAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

public class Shaker extends Activity implements SensorEventListener, SurfaceHolder.Callback {

	private static final String TAG = "Shaker";
	
	private MySurfaceView foreground;
	private SurfaceHolder holder;
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Vibrator vibrotron;
	private float m_totalForcePrev;
	private int timbersShivered; //a counter for how many times the user has shaken the king
	
	private Sprite king, coinPile, fallingLoot;
	
	private int catchID; //TODO: used to determine which king we will draw. add logic for more kings later.
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.shaker);
		
		Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            catchID = extras.getInt("CatchID");
            Log.d(TAG, "Catch ID: " + catchID);
        }
        
        foreground = (MySurfaceView)findViewById(R.id.shaker_foreground);
        
        //TODO: these are hardcoded right now. we could easily create a list up at the top and look up which drawables and names to use based on that.
        king = new Sprite("Napoleon", BitmapFactory.decodeResource(getResources(), R.drawable.napoleon_sprite1), 0, 0, 0);
        coinPile = new Sprite("Coin Pile", BitmapFactory.decodeResource(getResources(), R.drawable.coin_pile1), 0, 0.5f, 0);
        
        foreground.addSprite(king);
        
        holder = foreground.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        
        holder.addCallback(this);
        
		Log.e("KingFisher", "made the Shaker");
		
		SoundManager.loadSounds(SoundManager.SHAKABLE);
		
		vibrotron = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		sensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        foreground.getHolder().setFormat(PixelFormat.TRANSPARENT);
        foreground.setZOrderOnTop(true);
	}
	
	@Override
	public void onPause() {
		sensorManager.unregisterListener(this);
		holder.removeCallback(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		
		//TODO: wait, then "shake him down for the plunder!"
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
	
	public void onSensorChanged(SensorEvent event) {
		double forceThreshHold = 2.5f;
        double totalForce = 0.0f;
        totalForce += Math.pow(event.values[SensorManager.DATA_X]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(event.values[SensorManager.DATA_Y]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce += Math.pow(event.values[SensorManager.DATA_Z]/SensorManager.GRAVITY_EARTH, 2.0);
        totalForce = Math.sqrt(totalForce);
       
        if ((totalForce < forceThreshHold) && (m_totalForcePrev > forceThreshHold)) {
        	Log.e("KingFisher", "SHAKE!");
        	timbersShivered++;
        	
        	vibrotron.vibrate(300);
        	SoundManager.playSound(1, 1);
        	
        	switch (timbersShivered) { //TODO: set cases to add coins to the surfaceview.
        	case 5:
        		king.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.napoleon_sprite2));
        		break;
        	case 7:
        		foreground.addSprite(coinPile);
        		break;
        	case 10:
        		king.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.napoleon_sprite3));
        		break;
        	case 12:
        		coinPile.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.coin_pile2));
        	case 15:
        		king.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.napoleon_sprite4));
        		break;
        	case 17:
        		coinPile.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.coin_pile3));
        		break;
        	case 20:
        		sensorManager.unregisterListener(this);
        		//TODO: jump to the next activity now. bundle up which king was caught and send it along! we also need the levelID still.
        		
        		try {
                	Intent ourIntent = new Intent(Shaker.this, Class.forName("org.MAG.Rejecterator"));
                	ourIntent.putExtra("CatchID", catchID);
                	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(ourIntent);
        			finish();
        		} catch (ClassNotFoundException ex) {
        			Log.e(TAG, "Failed to jump to another activity");
        		}
        		break;
        	default:
        		m_totalForcePrev = (float) totalForce;
        		return;
        	}
        	foreground.replaceSprite(0, king);
        	drawSprites();
        }
        m_totalForcePrev = (float) totalForce;
        
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) { }
	

	public void surfaceCreated(SurfaceHolder holder) {
		drawSprites();
	}

	public void surfaceDestroyed(SurfaceHolder holder) { }

	private void drawSprites() {
		if (holder.getSurface().isValid()) {
	        Canvas canvas = holder.lockCanvas();
	        foreground.draw(canvas);
	        holder.unlockCanvasAndPost(canvas);
        }
	}
	
}
