package org.MAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class KingFisher2Activity extends Activity {
	
	public final String TAG = "KingFisher";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
        	Intent ourIntent = new Intent(KingFisher2Activity.this, Class.forName("org.MAG.Intro"));
			startActivity(ourIntent);

		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Failed to jump to another activity");
		}
    }
    
    
}