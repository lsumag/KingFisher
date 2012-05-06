package org.MAG;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback{

	private MainThread thread;
	private KingFisherActivity owner;
	
	public MainGameView(KingFisherActivity owner) {
		super(owner);
		this.owner = owner;
		getHolder().addCallback(this);
		
		thread = new MainThread(this);

		setFocusable(true);
	}

	public KingFisherActivity getOwner() {
		return owner;
	}
	
	public MainThread getThread() {
		return thread;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		if (thread.isAlive() == false)
			thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.exterminate();
	}
	
	public boolean OnTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	protected void onDraw(Canvas canvas) {
		
	}
}