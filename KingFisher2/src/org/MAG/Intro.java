package org.MAG;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Intro extends Activity implements OnTouchListener {

	private ImageView titleScreen;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        //TODO: play intro song.
        
        titleScreen = (ImageView)findViewById(R.id.title_image);
        titleScreen.setOnTouchListener(this);
    }
	
	
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		//TODO: load up the next screen! it's going to be Mode Selection.
		
		return true;
	}
}
