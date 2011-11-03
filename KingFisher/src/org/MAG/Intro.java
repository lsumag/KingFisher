package org.MAG;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Intro extends ImageView implements OnTouchListener {
	
	private Intro me;
	private boolean complete;
	
	public Intro(final KingFisherActivity owner) {
		super(owner);
		this.setOnTouchListener(this);
		
		this.setBackgroundResource(R.drawable.title_screen);
		
		me = this;
		
		owner.runOnUiThread(new Runnable() {
			public void run() {
				owner.setImageView(me);
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		complete = true;
		return true;
	}
	
	public boolean getFinished() {
		return complete;
	}
	
	public void onDestroy() {
		this.setOnTouchListener(null);
	}
}