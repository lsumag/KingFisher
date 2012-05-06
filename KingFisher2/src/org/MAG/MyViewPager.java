package org.MAG;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

class MyViewPager extends ViewPager {

	private LevelSelection owner;
		
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void init(LevelSelection owner) {
		this.owner = owner;
	}

	@Override
	public void onPageScrolled(int position, float offset, int offsetPixels) {
		super.onPageScrolled(position, offset, offsetPixels);
		owner.updateContentStatus(position);
	}
}
