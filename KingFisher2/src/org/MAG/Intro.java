package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Intro extends Activity implements OnTouchListener, MediaPlayer.OnCompletionListener {

	private ImageView titleScreen;
	private MediaPlayer mediaPlayer;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kingfishertitle);
        mediaPlayer.start();
        
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
		mediaPlayer.release();
		mediaPlayer = null;
		try {
        	Intent ourIntent = new Intent(Intro.this, Class.forName("org.MAG.ModeSelection"));
			startActivity(ourIntent);
		} catch (ClassNotFoundException ex) {
			Log.e("INTRO", "Failed to jump to another activity");
		}
	}
}
