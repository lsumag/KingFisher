package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 
 * @author UnderGear
 *
 */
public class ModeSelection extends Activity implements OnTouchListener {

	private ImageView img;
	
	/**
	 * Called on activity create. set up the touch listener.
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.mode_selecter); //TODO: put a source to the image
        
        img = (ImageView)findViewById(R.id.mode_image);
        
        //TODO: instructional audio plays here at intervals. use asynctask
        
        img.setOnTouchListener(this);
    }
	
	//TODO: launch a practice activity once it exists.
	/**private void swipePractice() {
	 
	}*/
	
	private void swipeLevel() {
		try {
        	Intent ourIntent = new Intent(ModeSelection.this, Class.forName("org.MAG.LevelSelection"));
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();

		} catch (ClassNotFoundException ex) {
			Log.e("MODE_SELECTION", "Failed to jump to another activity");
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		//TODO: detect left and right swipe gestures and then calls swipePractice() or swipeLevel()
		
		//for now, just load up the level selection. we're doing practice stuff later.
		swipeLevel();
		
		return true;
	}
	
}
