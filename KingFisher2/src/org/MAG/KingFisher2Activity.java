package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class KingFisher2Activity extends Activity {
	
	public final String TAG = "KingFisher";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        
        SoundManager.getInstance();
        SoundManager.initSounds(getApplicationContext());
        
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