package com.gxq.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallReceiver extends BroadcastReceiver {

	private static final String TAG = "InstallReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
			Log.d(TAG, "ACTION_PACKAGE_ADDED");
			
		}
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) { 
			Log.d(TAG, "ACTION_PACKAGE_REMOVED");
        }   

	}

}
