package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class TravelScene extends Activity implements OnTouchListener, MediaPlayer.OnCompletionListener {

	private MyVideoView travel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		setContentView(R.layout.travel);
        
        //TODO: travel audio.
		
		
		//Display display = getWindowManager().getDefaultDisplay(); 
		//int width = display.getWidth();  // deprecated
		//int height = display.getHeight();
		
		
		//TODO: make travel video full screen
        travel = (MyVideoView)findViewById(R.id.travel_background);
        //travel = new MyVideoView(this);
        
        travel.setOnTouchListener(this);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.travel);
        travel.setVideoURI(video);
        
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        //travel.setLayoutParams(params);
        
        travel.setOnCompletionListener(this);
        
        //travel.changeVideoSize(width, height);
        
        
        Display display = getWindowManager().getDefaultDisplay(); 
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(display.getWidth(),
                MeasureSpec.UNSPECIFIED);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(display.getHeight(),
                MeasureSpec.UNSPECIFIED);
        travel.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        
        
        travel.start();
        
    }
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		Log.e("Travel", "touched");
		loadNextLevel();
		return true;
	}
	
	private void loadNextLevel() {
		Log.e("TravelScene", "finished. Loading Caster");
		try {
			travel.stopPlayback();
        	Intent ourIntent = new Intent(TravelScene.this, Class.forName("org.MAG.Caster"));
        	travel.setOnTouchListener(null);
        	travel.setOnCompletionListener(null);
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();
		} catch (ClassNotFoundException ex) {
			Log.e("INTRO", "Failed to jump to another activity");
		}
	}

	public void onCompletion(MediaPlayer arg0) {
		loadNextLevel();
	}
}
