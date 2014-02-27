package com.gxq.utils;

import android.app.Application;
import android.content.ContentResolver;
import android.util.Log;

public class WellDoneApp extends Application{
	
	private static WellDoneApp sInstance;
	public static ContentResolver sContentResolver;
	
	public static synchronized WellDoneApp getInstance(){
		if(sInstance == null){
			return new WellDoneApp();
		}
		else{
			sContentResolver = sInstance.getContentResolver();
			 if(sContentResolver == null){
					Log.d("123", "app null");
				}
			return sInstance;
		}
	}
	
	@Override
	 public void onCreate() {
        super.onCreate();
        sInstance = this;
        sContentResolver = sInstance.getContentResolver();
        if(sContentResolver == null){
			Log.d("123", "app create null");
		}
	    }
}
