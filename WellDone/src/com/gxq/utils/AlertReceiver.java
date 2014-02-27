package com.gxq.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gxq.welldone.AlertActivity;

public class AlertReceiver extends BroadcastReceiver {

	public static final String TAG = "AlertReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("alert", "receive alert");

		String title = intent.getExtras().getString(Strings.TITLE);
		int id = intent.getExtras().getInt(Strings.ID);

		if (id > 0) {
			Log.d(TAG, "not delete");
			Log.d(TAG, "id = " + id);
			Intent myintent = new Intent(context, AlertActivity.class);
			myintent.putExtra(Strings.TITLE, title);
			Log.d(TAG, "title: " + title);
			myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myintent);
		} else {
			Log.d(TAG, "delete");
		}
	}

}
