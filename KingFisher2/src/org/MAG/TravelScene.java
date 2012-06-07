package org.MAG;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

/**
 * 
 * @author UnderGear
 *
 */
public class TravelScene extends Activity implements OnTouchListener, MediaPlayer.OnCompletionListener, SurfaceHolder.Callback, OnPreparedListener, OnVideoSizeChangedListener {

	private String TAG = "TravelScene";
	
	private SurfaceView travel;
	private SurfaceHolder holder;
	private MediaPlayer mediaPlayer;
	
	private boolean knownSize, sourceReady;
	
	private int levelID;
	
	/**
	 * Called on Activity creation. set the background and video, media player and completion listener
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		setContentView(R.layout.travel);
        
		Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            levelID = extras.getInt("SelectedLevel");
            Log.d(TAG, "Selected Level ID: " + levelID);
        }
		
        //TODO: travel audio. we should probably just redo the video and toss in an audio track at the same time.
		
		
		//Display display = getWindowManager().getDefaultDisplay(); 
		//int width = display.getWidth();  // deprecated
		//int height = display.getHeight();
		
		
		//TODO: make travel video full screen. what is with this layout right now? could it be the video itself?
        travel = (SurfaceView)findViewById(R.id.travel_background);
        //travel = new MyVideoView(this);
        
        travel.setOnTouchListener(this);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.travel);
        
        holder = travel.getHolder();
        
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mediaPlayer = new MediaPlayer();
        try {
			mediaPlayer.setDataSource(getApplicationContext(), video);
			
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnVideoSizeChangedListener(this);
			
            mediaPlayer.setOnCompletionListener(this);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
        
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        //travel.setLayoutParams(params);
        
        //travel.setOnCompletionListener(this);
        
        //travel.changeVideoSize(width, height);
        
        
        /**Display display = getWindowManager().getDefaultDisplay(); 
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(display.getWidth(),
                MeasureSpec.UNSPECIFIED);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(display.getHeight(),
                MeasureSpec.UNSPECIFIED);
        //travel.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        
        
        //travel.start();*/
        
    }
	
	/**
	 * We are exiting the app. release the media player and its assets.
	 */
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}
	
	
	/**
	 * Called when the screen is touched. Loads the next level.
	 * 
	 * @param v view that received the touch
	 * @param event the touch event received by v
	 */
	public boolean onTouch(View v, MotionEvent event) {
		loadNextLevel();
		return true;
	}
	
	/**
	 * Release media player, assets, listeners, etc. Load up the Caster activity
	 */
	private void loadNextLevel() {
		Log.e(TAG, "finished. Loading Caster");
		try {
			mediaPlayer.stop();
        	Intent ourIntent = new Intent(TravelScene.this, Class.forName("org.MAG.Caster"));
        	ourIntent.putExtra("SelectedLevel", levelID);
        	travel.setOnTouchListener(null);
        	mediaPlayer.setOnCompletionListener(null);
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Failed to jump to another activity");
		}
	}

	/**
	 * Called when the media player completes. load the next level.
	 * 
	 * @param mp the media player that completed
	 */
	public void onCompletion(MediaPlayer mp) {
		loadNextLevel();
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) { }

	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			mediaPlayer.setDisplay(holder);
			mediaPlayer.prepare();
		} catch (IllegalStateException e) { 
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder sh) { }

	/**
	 * If we're all ready, start the video. onVideoSizeChanged and this should both be called before we can start.
	 * 
	 * @param mp the media player that was prepared
	 */
	public void onPrepared(MediaPlayer mp) {
		sourceReady = true;
		if (sourceReady && knownSize) {
            startVideoPlayback();
        }
		
	}

	/**
	 * If we're all ready, start the video. onPrepared and this should both be called before we can start.
	 * 
	 * @param mp the media player whose video size has changed
	 * @param width
	 * @param height
	 */
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		knownSize = true;
		if (sourceReady && knownSize) {
            startVideoPlayback();
        }
	}
	
	/**
	 * Set the video size and start playing.
	 */
	private void startVideoPlayback() {
		//Log.d("travel video", "setting size, playing");
		//holder.setFixedSize(getWindow().getWindowManager().getDefaultDisplay().getWidth(), getWindow().getWindowManager().getDefaultDisplay().getHeight());
		holder.setSizeFromLayout();
		mediaPlayer.start();
	}
}
