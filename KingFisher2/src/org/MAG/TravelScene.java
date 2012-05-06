package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TravelScene extends Activity implements OnTouchListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selecter); 
        
        //TODO: travel audio
        
        //TODO: maybe touch listener to skip this like we did the intro?
        
        //TODO: travel animation
    }
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		try {
			
			//TODO: swap this out for a caster. it needs to know what level we chose.
			
        	Intent ourIntent = new Intent(TravelScene.this, Class.forName("org.MAG.ModeSelection"));
			startActivity(ourIntent);

		} catch (ClassNotFoundException ex) {
			Log.e("INTRO", "Failed to jump to another activity");
		}
		return true;
	}
	
	//TODO when travel animation completes, load up Caster Activity
}
