package com.gxq.welldone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.gxq.utils.Strings;

public class AlertActivity extends Activity {

	private static final String TAG = "AlertActivity";

	private Context mContext;
	private MediaPlayer mMediaPlayer;
	private boolean mIsNormalMode;

	private AudioManager audioManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "enter onCreate");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = this;
		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);

		mIsNormalMode = (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);

		// 如果静音的话就是震动，否则是铃声
		if (mIsNormalMode) {
			Log.d(TAG, "RINGER_MODE_NORMAL");
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer = MediaPlayer.create(this, R.raw.ring);
			mMediaPlayer.start();
		} else {
			Log.d(TAG, "silentmode");
			Vibrator vib = (Vibrator) this
					.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(500);
		}

		String title = getIntent().getExtras().getString(Strings.TITLE);
		Log.d(TAG, "title : " + title);
		new AlertDialog.Builder(this).setTitle("待办事项").setMessage(title)
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "确定");

						if (mIsNormalMode) {
							mMediaPlayer.release();
						}
						
						Intent myintent = new Intent(mContext,
								MainActivity.class);
						myintent.putExtra(Strings.CATAGORY, Strings.BACKKEY);
						setResult(MainActivity.EDITPAGE, myintent);
						finish();
					}
				}).show();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "enter onPause");
		super.onPause();
		//可以防止用户没有按确定的情况
		if (mIsNormalMode) {
			mMediaPlayer.release();
		}
		
//		Intent myintent = new Intent(mContext,
//				MainActivity.class);
//		myintent.putExtra(Strings.CATAGORY, Strings.BACKKEY);
//		setResult(MainActivity.EDITPAGE, myintent);
//		finish();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "enter onStop");
		super.onStop();
		Intent myintent = new Intent(mContext, MainActivity.class);
		myintent.putExtra(Strings.CATAGORY, Strings.BACKKEY);
		setResult(MainActivity.EDITPAGE, myintent);
		finish();
	}

}