package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author UnderGear
 *
 */
public class KingFisher2Activity extends Activity {
	
	public final String TAG = "KingFisher";
	
	/**
	 * Set the app fullscreen and portrait with no title bar, grab a static instance of SoundManager, load up the intro activity
	 * 
	 * @param savedInstanceState the 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //set up the window orientation, etc.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        
        //grab a static instance and set up the SoundManager.
        SoundManager.getInstance();
        SoundManager.initSounds(getApplicationContext());
        
        //Launch the Intro Activity
        try {
        	Intent ourIntent = new Intent(KingFisher2Activity.this, Class.forName("org.MAG.Intro"));
        	ourIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ourIntent);
			finish();

		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Failed to jump to another activity");
		}
    }
}