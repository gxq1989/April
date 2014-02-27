package com.example.whereami;

import android.util.Log;

public class Utils {

    private final static boolean DEBUG = true;
    private final static String TAG = "WhereAmI";

    public final static String ACTION_UPDATE_ADDRESS = "com.april.action.updateaddress";
    public final static String KEY_ADDRESS = "address";

    public static void log(String info) {
        if (DEBUG) {
            Log.d(TAG, " " + info);
        }

    }
}
