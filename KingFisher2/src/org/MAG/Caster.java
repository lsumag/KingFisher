package org.MAG;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Caster extends Activity implements OnTouchListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caster);
        
        //TODO: instructional audio
        
        //TODO: background animation. we'll need 2 layers of SurfaceView and an asynctask
        
        //TODO: register listeners
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO set a touched boolean to true if down, false if up
		return true;
	}
	
	//TODO: listen for the fling gesture, launch cast animation if touched.
	
	//TODO: once animation finishes, launch Reeler Activity
}
