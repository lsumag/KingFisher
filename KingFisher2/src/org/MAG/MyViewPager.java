package org.MAG;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * TODO: rename to LevelSelectionPager?
 * @author UnderGear
 *
 */
class MyViewPager extends ViewPager {

	private LevelSelection owner;
		
	/**
	 * Constructor.
	 * 
	 * @param context the application's context
	 * @param attrs
	 */
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * Set up the owner. This could not be done in the constructor because we are using an XML layout with this class in it.
	 * 
	 * @param owner the LevelSelection Activity that owns this
	 */
	public void init(LevelSelection owner) {
		this.owner = owner;
	}

	/**
	 * Called when the page has been scrolled.
	 * 
	 * @param position the index of the selected page
	 * @param offset
	 * @param offsetPixels
	 */
	@Override
	public void onPageScrolled(int position, float offset, int offsetPixels) {
		super.onPageScrolled(position, offset, offsetPixels);
		owner.updateContentStatus(position);
	}
}
