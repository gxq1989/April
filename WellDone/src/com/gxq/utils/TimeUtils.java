package com.gxq.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

/**
 * @author gongxiaqing
 *
 */
public class TimeUtils {

	private final static String TAG = "TimeUtils";

	Date now = new Date();
	

	public TimeUtils() {

	}

	public String getCurrentTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Log.d(TAG, dateFormat.format(now));
		return dateFormat.format(now);
	}

	public String getToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Log.d(TAG, dateFormat.format(now));
		return dateFormat.format(now);
	}
	
	public String getTommorrow() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		//向后推一天
		calendar.add(calendar.DATE, 1);
		//现在now里的值就是明天的哦
		now = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(now);
	}
	
	public String getCatagory(String time, int isComplete) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date myDate = dateFormat.parse(time);
		Date today = dateFormat.parse(getToday());
		Date tommorrow = dateFormat.parse(getTommorrow());
		
		if(myDate.compareTo(today) == 0){
			Log.d(TAG, Strings.TODAY);
			return Strings.TODAY;
		}else if(myDate.compareTo(tommorrow) == 0){
			Log.d(TAG, Strings.TOMMORROW);
			return Strings.TOMMORROW;
		}else if(myDate.after(tommorrow)){
			Log.d(TAG, Strings.LATER);
			return Strings.LATER;
		}else if(isComplete == 0){
			//这里还要考虑处理把时间设置到之前的时间的问题，要跳转到已完成，或未完成的哦
			Log.d(TAG, "UNFINISHED");
			return Strings.UNFINISHED;
		}else{
			Log.d(TAG, "FINISHED");
			return Strings.FINISHED;
		}
		
	}
	
}
