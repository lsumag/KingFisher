package org.MAG;

/**
 * Class used to express anything that can be caught by the player.
 * @author undergear
 *
 */
public class CatchableObject {

	private Sprite sprite; //sprite representation of this
	private boolean king; //whether this is a king or not
	private int struggleDelay; //time to wait between struggle attempts on the line
	private int lowLimit; //lowest distance you can possibly find this
	private int highLimit; //highest distance you can find this
	private int strength; //how much damage to do to the line on break attempts or unreel if not held down
	
	public CatchableObject(Sprite sprite, int strength, int struggleDelay, int lowLimit, int highLimit, boolean king) {
		this.sprite = sprite;
		this.king = king;
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
		this.strength = strength;
		this.struggleDelay = struggleDelay;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public boolean isKing() {
		return king;
	}
	
	public int getStruggleDelay() {
		return struggleDelay;
	}
	
	public int getLowLimit() {
		return lowLimit;
	}
	
	public int getHighLimit() {
		return highLimit;
	}
	
	public int getStrength() {
		return strength;
	}
}
