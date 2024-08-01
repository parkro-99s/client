package com.parkro.client;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    private static final String PREFS = "prefs";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String USER_NAME = "USER_NAME";
    private static final String NAME = "NAME";
    private static Utils instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor prefsEditor;

    // Private constructor to enforce singleton pattern
    private Utils(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    // Initialize the Utils instance
    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new Utils(context.getApplicationContext());
        }
    }

    // Ensure Utils is initialized before using
    private static Utils getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Utils not initialized. Call Utils.init(context) first.");
        }
        return instance;
    }

    // Set access token
    public static void setAccessToken(String value) {
        getInstance().prefsEditor.putString(ACCESS_TOKEN, value).apply();
    }

    // Set username
    public static void setUsername(String value) {
        getInstance().prefsEditor.putString(USER_NAME, value).apply();
    }

    // Get access token
    public static String getAccessToken(String defValue) {
        return getInstance().prefs.getString(ACCESS_TOKEN, defValue);
    }

    // Get username
    public static String getUsername(String defValue) {
        return getInstance().prefs.getString(USER_NAME, defValue);
    }

    // Clear all preferences
    public static void clear() {
        getInstance().prefsEditor.clear().apply();
    }
}
