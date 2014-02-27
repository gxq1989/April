package com.gxq.utils;

import android.content.Context;
import android.view.WindowManager;

public class DisplayUtils {

	private float mWidth;
	private float mHeight;
	
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public DisplayUtils(Context context){
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);     
		mHeight = windowManager.getDefaultDisplay().getHeight();     
		mWidth = windowManager.getDefaultDisplay().getWidth();  
		
		//for API Level 13 or above
		//Point object to receive the size information.
//		Point point = new Point();
//		windowManager.getDefaultDisplay().getSize(point);
//		mWidth = point.x;
//		mHeight = point.y;
	}
	
	public float getWidth(){
		return mWidth;
	}
   
	public float getHeight(){
		return mHeight;
	}
}
