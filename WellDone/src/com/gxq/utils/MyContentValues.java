package com.gxq.utils;

import android.content.ContentValues;

import com.gxq.db.TasksInfo.Task;

public class MyContentValues {

	private ContentValues mContentValues;

	public MyContentValues(int Id, int UserId, String Title, String RemindTime,
			int Priority, int RemindPattern, int IsComplete, String EditTime,
			int IsRemind, int IsCommit, int deleteflag) {
		mContentValues = new ContentValues();
		mContentValues.put(Task.Server_Id, Id);
		mContentValues.put(Task.UserId, UserId);
		mContentValues.put(Task.Title, Title);
		mContentValues.put(Task.RemindTime, RemindTime);
		mContentValues.put(Task.Priority, Priority);
		mContentValues.put(Task.RemindPattern, RemindPattern);
		mContentValues.put(Task.IsComplete, IsComplete);
		mContentValues.put(Task.EditTime, EditTime);
		mContentValues.put(Task.IsRemind, IsRemind);
		mContentValues.put(Task.IsCommit, IsCommit);
		mContentValues.put(Task.DeleteFlag, deleteflag);
	}

	public ContentValues getContentValues() {
		return mContentValues;
	}

}
