package com.gxq.welldone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.gxq.db.TasksInfo.Task;
import com.gxq.utils.ToastShow;

public class TimePickerActivity extends Activity implements
		OnDateChangedListener, OnTimeChangedListener {

	private DatePicker datepicker;
	private TimePicker timepicker;

	private Button mButtonSave;
	private Button mButtonCancel;
	private String mAlertTime;

	private ToastShow mToastShow;

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.save:
				if (isValidAlertTime(mAlertTime)) {
					Intent intent = new Intent();
					intent.putExtra("edit", "edit");
					intent.putExtra(Task.RemindTime, mAlertTime);
					setResult(EidtPageActivity.DIALOG_TIMEPICKER, intent);
					finish();
				} else {
					mToastShow.toastShow("所设置的提醒时间已过期，请重新设置！");
					// Toast.makeText(TimePickerActivity.this,
					// "所设置的提醒时间已过期，请重新设置！", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cancel:
				Intent intent1 = new Intent();
				intent1.putExtra("edit", "no");
				setResult(EidtPageActivity.DIALOG_TIMEPICKER, intent1);
				finish();
				break;
			}

		}

		private boolean isValidAlertTime(String alertTime) {
			long alertLong = 0;
			long currentLong = 0;

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");

			Date currentDate = new Date();
			String currentString = dateFormat.format(currentDate);

			try {
				alertLong = dateFormat.parse(alertTime).getTime();
				currentLong = dateFormat.parse(currentString).getTime();
				Log.d("aaa", "alertLong: " + alertLong);
				Log.d("aaa", "currentLong: " + currentLong);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (alertLong < currentLong) {
				Log.d("aaa", "isNotValidAlertTime");
				return false;
			} else {
				Log.d("aaa", "isValidAlertTime");
				return true;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mToastShow = new ToastShow(this);

		datepicker = (DatePicker) findViewById(R.id.datapicker);
		timepicker = (TimePicker) findViewById(R.id.timepicker);

		Date now = new Date();
		int currentHour = now.getHours();
		int currentMinute = now.getMinutes();

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
		// datepicker.init(2001, 1, 25, this);
		datepicker.init(2013, now.getMonth(), now.getDate(), this);

		timepicker.setIs24HourView(true);
		timepicker.setCurrentHour(currentHour);
		timepicker.setCurrentMinute(currentMinute);
		timepicker.setOnTimeChangedListener(this);
		onDateChanged(null, 0, 0, 0);

		mButtonSave = (Button) findViewById(R.id.save);
		mButtonSave.setOnClickListener(mOnClickListener);
		mButtonCancel = (Button) findViewById(R.id.cancel);
		mButtonCancel.setOnClickListener(mOnClickListener);

	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(datepicker.getYear(), datepicker.getMonth(),
				datepicker.getDayOfMonth(), timepicker.getCurrentHour(),
				timepicker.getCurrentMinute());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		mAlertTime = dateFormat.format(calendar.getTime());
	}

	@Override
	public void onBackPressed() {

		Intent intent1 = new Intent();
		intent1.putExtra("edit", "no");
		setResult(EidtPageActivity.DIALOG_TIMEPICKER, intent1);
		super.onBackPressed();
	}

}