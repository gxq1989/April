package com.gxq.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarmManager extends BroadcastReceiver {

	public static final String TAG = "MyAlarmManager";

	private AlarmManager mAlarmManager;

	private ArrayList<AlertInfo> alertInfos;
	private String mTitle;
	private String mRemindTime;
	private int mId;
	private long mRemindTimeLong;
	private boolean mDeleteFlag;

	// private int mIsComplete;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive sendBroadcast");

		// 收到broadcast 表明需要被提醒
		mAlarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		// 先看是不是需要删除提醒时间哦
		mDeleteFlag = intent.getBooleanExtra(Strings.DELETE_FLAG, false);
		// 把时间和内容取出来呀
		alertInfos = intent.getParcelableArrayListExtra(Strings.ALERTINFO);
		int size = alertInfos.size();
		Log.d(TAG, "SIZE: " + size);
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				mTitle = alertInfos.get(i).mTitle;
				mRemindTime = alertInfos.get(i).mRemindTime;
				mId = alertInfos.get(i).mId;
				// mIsComplete = alertInfos.get(i).mIsComplete;

				// 转成long
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date;
				try {
					date = (Date) simpleDateFormat.parse(mRemindTime);
					Log.d(TAG, "date : " + date.toString());
					mRemindTimeLong = date.getTime();
					Log.d(TAG, "date : " + String.valueOf(mRemindTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Intent myIntent = new Intent(context, AlertReceiver.class);
				// myIntent.putExtra(Strings.TITLE, mTitle);
				// myIntent.putExtra(Strings.ID, mId);
				// PendingIntent pendingIntent = PendingIntent.getBroadcast(
				// context, mId, myIntent,
				// PendingIntent.FLAG_UPDATE_CURRENT);
				//
				// Log.d(TAG, "id: " + mId + "deleteflag: " + mDeleteFlag);
				// Log.d(TAG, "pendingIntent1 : " + pendingIntent.toString());
				//
				// if (mRemindTimeLong > System.currentTimeMillis()) {
				// Log.d(TAG, "alerttime : " + mRemindTime + "systime: "
				// + System.currentTimeMillis());
				// mAlarmManager.set(AlarmManager.RTC, mRemindTimeLong,
				// pendingIntent);
				// Log.d(TAG, "pendingIntent2 : " + pendingIntent.toString());
				// if (mDeleteFlag) {
				// Log.d(TAG, "delete");
				// Log.d(TAG, "delete id : " + mId);
				// Log.d(TAG, "delete title : " + mTitle);
				// Log.d(TAG,
				// "pendingIntent3 : " + pendingIntent.toString());
				// mAlarmManager.cancel(pendingIntent);
				// }
				// }

				// 这里标识闹钟被取消，是用的myIntent里的id字段，如果被设成了负值，则不闹；由于每个闹钟对应着一个pendingIntent，这个也是
				// 由id来决定的 ，到AlertReceiver里再通过判断id>0 or <0来决定要不要闹
				Intent myIntent = new Intent(context, AlertReceiver.class);
				myIntent.putExtra(Strings.TITLE, mTitle);
				if (mDeleteFlag) {
					myIntent.putExtra(Strings.ID, -mId);
				} else {
					myIntent.putExtra(Strings.ID, mId);
				}
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, mId, myIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				Log.d(TAG, "id: " + mId + "deleteflag: " + mDeleteFlag);
				Log.d(TAG, "pendingIntent1 : " + pendingIntent.toString());

				if (mRemindTimeLong > System.currentTimeMillis()) {
					Log.d(TAG, "alerttime : " + mRemindTime + "systime: "
							+ System.currentTimeMillis());
					mAlarmManager.set(AlarmManager.RTC, mRemindTimeLong,
							pendingIntent);
					Log.d(TAG, "pendingIntent2 : " + pendingIntent.toString());
				}

			}
		}
	}
}