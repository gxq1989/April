package com.gxq.utils;

import android.content.SharedPreferences;
import android.util.Log;

public class UserPreference {

	private static final String TAG = "UserPreference";
	private static final String PREF = "user_pref";

	private static SharedPreferences mSharedPreferences = WellDoneApp
			.getInstance().getSharedPreferences(UserPreference.PREF, 0);

	public static boolean hasUserLoginBefore(String mUsername, String mPassword) {
		String name = mSharedPreferences.getString("UserName", null);
		String password = mSharedPreferences.getString("Password", null);

		if (name == null || password == null) {
			return false;
		} else if (name.equals(mUsername) && password.equals(mPassword)) {
			Log.d(TAG, "hasUserLoginBefore");
			return true;
		} 
		else {
			Log.d(TAG, "has not UserLoginBefore");
			return false;
		}
	}

	public static void setUserInfo(String name, String password, int id) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString("UserName", name);
		editor.putString("Password", password);
		editor.putInt("UserId", id);

		editor.commit();
		Log.d(TAG, "UserName: " + name);
		Log.d(TAG, "Password: " + password);
		Log.d(TAG, "UserId: " + id);
	}

	public static String getName() {
		String name = mSharedPreferences.getString("UserName", null);
		return name;
	}

	public static String getPassword() {
		String password = mSharedPreferences.getString("Password", null);
		return password;
	}

	public static int getUserId() {
		int userid = mSharedPreferences.getInt("UserId", 0);
		return userid;
	}
}
