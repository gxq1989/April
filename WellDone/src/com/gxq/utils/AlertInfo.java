package com.gxq.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AlertInfo implements Parcelable {
	public static final String TAG = "AlertInfo";
	
	public static Parcel alertInfoParcel = Parcel.obtain();
	
	public String mTitle;
	public String mRemindTime;
	public int mId;

	public AlertInfo(){
		
	}
	
	public AlertInfo(String title, String remindTime, int id){
		mTitle = title;
		mRemindTime = remindTime;
		mId = id;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	// 保存到包裹中
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.d(TAG, "enter writeToParcel");
		Log.d(TAG, "title " + mTitle);
		Log.d(TAG, "remindTime " + mRemindTime);
		Log.d(TAG, "id: " + mId);
		
		
		dest.writeString(mTitle);
		dest.writeString(mRemindTime);
		dest.writeInt(mId);
//		dest.writeInt(mIsComplete);
	}

	// 实现Parcelable接口的类型中，必须有一个实现了Parcelable.Creator接口的静态常量成员字段，
	// 并且它的名字必须为CREATOR的
	public static final Parcelable.Creator<AlertInfo> CREATOR = new Parcelable.Creator<AlertInfo>() {
		// From Parcelable.Creator
		@Override
		public AlertInfo createFromParcel(Parcel in) {
			AlertInfo brief = new AlertInfo();

			// 从包裹中读出数据
			brief.mTitle = in.readString();
			brief.mRemindTime = in.readString();
			brief.mId = in.readInt();
//			brief.mIsComplete = in.readInt();

			return brief;
		}

		// From Parcelable.Creator
		@Override
		public AlertInfo[] newArray(int size) {
			return new AlertInfo[size];
		}
	};
}
