package org.MAG;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 
 * @author UnderGear
 *
 */
public class Reeler extends Activity implements OnTouchListener {

	private ImageView reelerBackground;
	private SurfaceView foreground;
	private Vibrator vibrotron;
	
	/**
	 * Called on Activity creation. Set the background, touch listener, vibrator, load up sounds.
	 * 
	 * @param savedInstanceState
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reeler); 
        
        reelerBackground = (ImageView)findViewById(R.id.reeler_background);
        reelerBackground.setOnTouchListener(this);
        
        SoundManager.loadSounds(SoundManager.REELABLE);
        
        //TODO: implement foreground images - the fishing rod should be in the front. we need a different background, too.
        //TODO: instructional audio - waiting/patience
        
        //TODO: wait for a bite - launch an asynctask to wait a random amount of time. after it completes, then get rocking.
        
        //TODO: determine what will be caught and its stats. this will be based on quality of cast, equipment, and current level
        
        vibrotron = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }
	
	//TODO: BITE! 
	
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
	//TODO: play success audio
	//TODO: go to catcher activity
}
