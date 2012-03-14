package org.MAG;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ModeSelection extends Activity implements OnTouchListener {

	private ImageView img;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selecter); //TODO: put a source to the image
        
        img = (ImageView)findViewById(R.id.mode_image);
        
        //TODO: instructional audio plays here at intervals. use asynctask
        
        img.setOnTouchListener(this);
    }
	
	private void swipePractice() {
		//TODO: launch a practice activity
	}
	
	private void swipeLevel() {
		//TODO: launch level selection
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//TODO: detect left and right swipe gestures and then calls swipePractice() or swipeLevel()
		
		//for now
		swipeLevel();
		
		return true;
	}
	
}
