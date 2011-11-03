package com.Tutorial.Sound;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundManager {
	
	static private SoundManager _instance;
	private static SoundPool mSoundPool; 
	private static HashMap<Integer, Integer> mSoundPoolMap; 
	private static HashMap<Integer, Integer> mSoundPoolClick; 
	private static HashMap<Integer, Integer> mSoundPoolCoins; 
	private static HashMap<Integer, Integer> mKingSayings; 
	// private static HashMap<Integer, Integer> mJesterSayings; 
	private static HashMap<Integer, Integer> mSoundSplash;
	private static AudioManager  mAudioManager;
	private static Context mContext;
	
	private SoundManager()
	{   
	}
	
	/**
	 * Requests the instance of the Sound Manager and creates it
	 * if it does not exist.
	 * 
	 * @return Returns the single instance of the SoundManager
	 */
	static synchronized public SoundManager getInstance() 
	{
	    if (_instance == null) 
	      _instance = new SoundManager();
	    return _instance;
	 }
	
	/**
	 * Initialises the storage for the sounds
	 * 
	 * @param theContext The Application context
	 */
	public static  void initSounds(Context theContext) 
	{ 
		 mContext = theContext;
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	     mSoundPoolMap = new HashMap<Integer, Integer>(); 
	     mSoundPoolClick = new HashMap<Integer, Integer>();
	     mSoundPoolCoins = new HashMap<Integer, Integer>(); 
	     mKingSayings = new HashMap<Integer, Integer>();
	     // mJesterSayings = new HashMap<Integer, Integer>();
	     mSoundSplash = new HashMap<Integer, Integer>();
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	} 
	
	/**
	 * Add a new Sound to the SoundPool
	 * 
	 * @param Index - The Sound Index for Retrieval
	 * @param SoundID - The Android ID for the Sound asset.
	 */
	public static void addSound(int Index,int SoundID)
	{
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	/**
	 * Loads the various sound assets
	 * Currently hardcoded but could easily be changed to be flexible.
	 */
	public static void loadSounds()
	{
		mSoundPoolMap.put(1, mSoundPool.load(mContext, R.raw.reelcast, 1));
		// mSoundSplash.put(2, mSoundPool.load(mContext, R.raw.rodcast, 1));
		
		mSoundSplash.put(1, mSoundPool.load(mContext, R.raw.splash, 1));
		// mSoundSplash.put(2, mSoundPool.load(mContext, R.raw.bigsplash, 1));
		
		// mSoundSplash.put(1, mSoundPool.load(mContext, R.raw.littlecheer, 1));
		// mSoundSplash.put(2, mSoundPool.load(mContext, R.raw.bigcheer, 1));
		// mSoundSplash.put(3, mSoundPool.load(mContext, R.raw.moan, 1));
		
		//mSoundPoolClick.put(1, mSoundPool.load(mContext, R.raw.clicksonefive, 1));
		//mSoundPoolClick.put(2, mSoundPool.load(mContext, R.raw.clicksingle, 1));
		//mSoundPoolClick.put(3, mSoundPool.load(mContext, R.raw.clicksfour, 1));
		mSoundPoolClick.put(4, mSoundPool.load(mContext, R.raw.clickdouble, 1));
		
		mSoundPoolCoins.put(1, mSoundPool.load(mContext, R.raw.coinsmall, 1));
		mSoundPoolCoins.put(2, mSoundPool.load(mContext, R.raw.coinmedium, 1));
		mSoundPoolCoins.put(3, mSoundPool.load(mContext, R.raw.coinsjackpot, 1));
		
		mKingSayings.put(1, mSoundPool.load(mContext, R.raw.kingsnorting, 1));
		mKingSayings.put(2, mSoundPool.load(mContext, R.raw.kingstruggleblurp, 1));
		mKingSayings.put(3, mSoundPool.load(mContext, R.raw.kingangryhooked, 1));
		
		// mJesterSayings.put(1, mSoundPool.load(mContext, R.raw.arthurites, 1));
		// mJesterSayings.put(2, mSoundPool.load(mContext, R.raw.dynasty, 1));
		
	}
	
	/**
	 * Plays a Sound
	 * 
	 * @param index - The Index of the Sound to be played
	 * @param speed - The Speed to play not, not currently used but included for compatibility
	 */
	public static void playSound(int index,float speed) 
	{ 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}
	
	public static void playClickSound(int index,float speed) 
	{ 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundPoolClick.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}
	
	public static void playKingSound(int index,float speed) 
	{ 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mKingSayings.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}
	public static void playCoinSound(int index,float speed) 
	{ 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundPoolCoins.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}
//	public static void playJesterSound(int index,float speed) 
//	{ 		
//		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
//		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//		     mSoundPool.play(mJesterSayings.get(index), streamVolume, streamVolume, 1, 0, speed); 
//	}
	public static void playSplashSound(int index,float speed) 
	{ 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundSplash.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}
	
	
	/**
	 * Stop a Sound
	 * @param index - index of the sound to be stopped
	 */
	public static void stopSound(int index)
	{
		mSoundPool.stop(mSoundPoolMap.get(index));
	}
	
	public static void cleanup()
	{
		mSoundPool.release();
		mSoundPool = null;
	    mSoundPoolMap.clear();
	    mAudioManager.unloadSoundEffects();
	    _instance = null;
	    
	}

	
}