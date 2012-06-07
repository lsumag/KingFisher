package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Intro extends Activity implements OnTouchListener, MediaPlayer.OnCompletionListener {

	private ImageView titleScreen;
	private MediaPlayer mediaPlayer;
	
	/** TODO: this can probably all be tossed into KingFisher2Activity instead of having its own Activity
	 * TODO: activity stack planning.
	 * mode selection will be the root of the back stack.
	 * level selection will stay on as well.
	 * 
	 * 
	 * Called when this activity begins. set the window up, get a media player for an intro video, set a touch listener, listen for media completion
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.intro);
        
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kingfishertitle);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
        
        titleScreen = (ImageView)findViewById(R.id.title_image);
        titleScreen.setOnTouchListener(this);
    }
	
	/**
	 * Called when the titleScreen receives a touch. it should be the only view listening.
	 * 
	 * @param v the view that has been touched. Should be titleScreen.
	 * @param event the event that has occurred. Doesn't matter - load the next level.
	 */
	public boolean onTouch(View v, MotionEvent event) {
		loadNextLevel();
		return true;
	}

	/**
	 * The media player has completed playback. load the next level
	 * 
	 * @param the media player that has finished
	 */
	public void onCompletion(MediaPlayer mP) {
		loadNextLevel();
	}
	
	/**
	 * Release the media player and its assets, load up the next activity
	 */
	private void loadNextLevel() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		try {
        	//Intent ourIntent = new Intent(Intro.this, Class.forName("org.MAG.ModeSelection"));
        	//TODO: implement tutorial mode, THEN we'll go to mode selection. right now, just skip it.
        	Intent ourIntent = new Intent(Intro.this, Class.forName("org.MAG.LevelSelection"));
        	
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	titleScreen.setOnTouchListener(null);
			startActivity(ourIntent);
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e("INTRO", "Failed to jump to another activity");
		}
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
}
