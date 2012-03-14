package org.MAG;

import android.app.Activity;
import android.os.Bundle;

public class LevelSelection extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selecter); //TODO: main content is 
        
        //TODO: launch instructional audio thread
        
        //TODO: horizontal pager goes here. flip through the available levels, maybe play audio when a level is focused for 2 seconds
        //maybe vibrate as each one comes into focus.
        
        //TODO: listen for shakes to launch selected level. launches the travel scene
    }
	
}
