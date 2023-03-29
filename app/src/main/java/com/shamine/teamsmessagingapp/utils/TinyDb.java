package com.shamine.teamsmessagingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TinyDb {
    private static final String PREFERENCE_NAME = "PREFERENCE_NAME_Y_P";
    private static final String PREF_LAST_UPDATE_TIMESTAMP = "PREF_LAST_UPDATE_TIMESTAMP";

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setLastUpdateTimestamp(Context context, long timestamp) {
        if (timestamp >= 0L) {
            SharedPreferences.Editor editor = getSharedPreference(context).edit();
            editor.putLong(PREF_LAST_UPDATE_TIMESTAMP, timestamp);
            editor.apply();
        }
    }

    public static long getLastUpdateTimestamp(Context context) {
        return getSharedPreference(context).getLong(PREF_LAST_UPDATE_TIMESTAMP, 0L);
    }
}
