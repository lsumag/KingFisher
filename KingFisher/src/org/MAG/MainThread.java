package org.MAG;

import android.media.MediaPlayer;
import android.os.Looper;
import android.util.Log;

public class MainThread extends Thread {
	
	private boolean running;
	private MediaPlayer mediaPlayer;
	private MainGameView gamePanel;
	private KingFisherActivity owner;
	
	private int state; //is currently 0 - INTRO state.
	//TODO: IMPLEMENT THESE TWO:
	private int activeLevel; //user is currently playing or has most recently beaten previous level (unless previous was n)
	private int newestLevel; //latest level unlocked by user. this determines if the user may play a level or not.
	
	final int INTRO = 0; //intro screen
	final int TITLE = 1; //title screen - not currently used. this should be a menu screen instead.
	final int TUTORIAL = 2; //tutorial - not currently implemented.
	final int LEVEL = 3; //level selection screen
	final int TRAVEL = 4; //en route to level
	final int CASTABLE = 5; //casting screen
	final int REELABLE = 6; //reeling screen
	final int FAIL = 7; //king escaped splash
	final int SUCCESS = 8; //caught the king
	final int POUT = 9; //king looks stupid
	final int SHAKABLE = 10; //take his lunch money
	final int FLINGABLE = 11; //throw him in the river!
	final int ACHIEVEMENT = 12; //the loot
	final int VICTORY = 13; //YAY, YOU WIN!
	
	private Caster caster;
	private Shaker shaker;
	private Reeler reeler;
	private Rejecterator rejecterator;
	private Level level;
	private Intro intro;

	public MainThread(MainGameView gamePanel) {
		super();
		this.owner = gamePanel.getOwner();
		this.gamePanel = gamePanel;
		
		SoundManager.getInstance();
        SoundManager.initSounds(owner);
		
		mediaPlayer = MediaPlayer.create(owner, R.raw.kingfishertitle);
	}
	
	public KingFisherActivity getOwner() {
		return owner;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public void onPause() {
		//
	}
	
	//TODO: do we need to look at the current state?
	public void exterminate() {
		SoundManager.cleanup();
		stopMusic();
		mediaPlayer = null;
	}
	
	public void setState(int state) {
		this.state = state;
		SoundManager.loadSounds(state);
	}
	
	public void stopMusic() {
		if (mediaPlayer != null) if (mediaPlayer.isPlaying()) mediaPlayer.stop();
	}

	public void run() {
		Looper.prepare();
		long tickCount = 0L;
		//TODO: clean up when leaving a state.
		SoundManager.loadSounds(state);
		mediaPlayer.start();
		intro = new Intro(owner);
		while (running) {
			//YAY GAME LOOP - DO STUFF HERE!
			tickCount++;
			
			switch (state) {
			case INTRO:
				if (intro.getFinished() == true || !mediaPlayer.isPlaying()) { //either user pressed screen or music is done
					setState(LEVEL);
					intro.onDestroy();
					stopMusic();
				}
				break;
			case TUTORIAL:
				//back off, man. we'll deal with you later
				
				break;
			case LEVEL:
				//TODO: populate the Level's gallery with images, etc. allow users to select a level to play.
				if (level.getFinished() == true) {
					
					/**owner.runOnUiThread(new Runnable() {
						public void run() {
							owner.setContentViewDefault();
						}
					});*/
					level.onDestroy();
					setState(TRAVEL);
					
					
				}
				//level.setFinished(true);
				
				break;
			case TRAVEL:
				//splash screen of travel to the level.
				if (true) {
					caster = new Caster(owner);
					setState(CASTABLE);
				}
				break;
			case CASTABLE:
				//load up casting sounds
				
				stopMusic();
				if (caster.getCasted() == true) {
					setState(REELABLE);
					caster.exterminate();
					caster = null;
					reeler = new Reeler(owner);
				}
				
				
				break;
			case REELABLE:
				
				if (reeler.getReel() == 0) {
					break;
				}
				else if (reeler.getReel() == 2) {
					setState(SUCCESS);
					reeler = null;
				}
				else {
					setState(FAIL);
					reeler = null;
				}
				break;
			case FAIL:
				
				SoundManager.playSplashSound(1, 1);
				setState(CASTABLE);
				break;
			case SUCCESS:
				setState(POUT);
				break;
			case POUT:
				//show pouting king
				if (true) {
					shaker = new Shaker(owner);
					setState(SHAKABLE);
				}
				break;
			case SHAKABLE:
				//shake the king
				
				if (shaker.getShaken() == true) {
					shaker.chillOut();
					shaker = null;
					setState(FLINGABLE);
					rejecterator = new Rejecterator(owner);
				}
				break;
			case FLINGABLE:
				if (rejecterator.getRejectorated() == true) {
					setState(ACHIEVEMENT);
					rejecterator = null;
				}
				break;
			case ACHIEVEMENT:
				//check victory conditions. state = LEVEL; or state = VICTORY; 
				break;
			case VICTORY:
				//go outside, you nerd.
				break;
			default: //oh, shi-
				break;
			}
		}
	}
}