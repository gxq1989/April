package com.gxq.db;

import java.util.ArrayList;
import java.util.List;

public class TaskTableDbListener {
	
	//define Listeners in order to trace the db update
	public static interface OnDBUpdatedListener{
		public void onUpdated();
	}
	
	private List<OnDBUpdatedListener> mListeners;
	
	public void registerListener(OnDBUpdatedListener listener){
		mListeners.add(listener);
	}
	
	public void unregisterListener(OnDBUpdatedListener listener){
		mListeners.remove(listener);
	}
	
	public void notifyUpdated(){
		for (OnDBUpdatedListener listener : mListeners) {
			listener.onUpdated();
		}
	}
	
	private static TaskTableDbListener sInstance;

	public static synchronized TaskTableDbListener getInstance() {
		if (sInstance == null) {
			sInstance = new TaskTableDbListener();
		}
		return sInstance;
	}
	
	private TaskTableDbListener() {
		//initial mListeners in construct functions 
		mListeners = new ArrayList<OnDBUpdatedListener>();
	}
}
