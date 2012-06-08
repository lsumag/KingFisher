package org.MAG;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * @deprecated
 * @author undergear
 *
 */
public class MyVideoView extends VideoView {
	
	public MyVideoView(Context context) {
		super(context);
	}
	
	public MyVideoView(Context ctx, AttributeSet attr) {
		super(ctx, attr);
	}

    @Override 
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
    }
    
	/**public void changeVideoSize(int width, int height) {

        // not sure whether it is useful or not but safe to do so
        //getHolder().setFixedSize(width, height); 
        
        requestLayout();
        invalidate();     // very important, so that onMeasure will be triggered
    }*/

}
