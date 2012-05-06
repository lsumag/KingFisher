package org.MAG;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Reeler extends Activity implements OnTouchListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selecter); 
        
        //TODO: 2 layers with SurfaceView and background animations on asynctask
        
        //TODO: instructional audio - waiting/patience
        
        //TODO: wait for a bite
        
    }
	
	//TODO: BITE! 
	//TODO: register touch listener now.
	//TODO: instructional audio - reeling advice
	//TODO: another thread works for the fish trying to escape. check for escape here.

	//TODO: ESCAPE!
	//TODO: play failure audio
	//TODO: go back to the caster activity
	
	public boolean onTouch(View v, MotionEvent event) {
		// TODO this is where all positive progress happens. also check for catch here.
		return false;
	}
	
	//TODO: CATCH!
	//TODO: determine what was caught?
	//TODO: play success audio
	//TODO: go to catcher activity
}
