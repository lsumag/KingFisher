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
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		loadNextLevel();
		return true;
	}

	public void onCompletion(MediaPlayer mP) {
		loadNextLevel();
	}
	
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
	
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}
}
