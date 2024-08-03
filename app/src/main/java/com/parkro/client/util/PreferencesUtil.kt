package com.parkro.client.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesUtil private constructor(context: Context) {
    private val prefs: SharedPreferences
    private val prefsEditor: SharedPreferences.Editor

    companion object {
        private const val PREFS = "prefs"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val NAME = "NAME"
        private const val CAR_PROFILE = "car_profile"
        private var instance: PreferencesUtil? = null

        // Initialize the Utils instance
        @Synchronized
        fun init(context: Context) {
            if (instance == null) {
                instance = PreferencesUtil(context.applicationContext)
                // Set default car profile if not set
                if (!instance!!.prefs.contains(CAR_PROFILE)) {
                    instance!!.prefsEditor.putInt(CAR_PROFILE, 1).apply()
                }
            }
        }

        // Ensure Utils is initialized before using
        private fun getInstance(): PreferencesUtil? {
            checkNotNull(instance) { "Utils not initialized. Call Utils.init(context) first." }
            return instance
        }

        // Set access token
        fun setAccessToken(value: String?) {
            getInstance()!!.prefsEditor.putString(ACCESS_TOKEN, value).apply()
        }

        // Set username
        fun setUsername(value: String?) {
            getInstance()!!.prefsEditor.putString(USER_NAME, value).apply()
        }

        // Get access token
        fun getAccessToken(defValue: String?): String? {
            return getInstance()!!.prefs.getString(ACCESS_TOKEN, defValue)
        }

        // Get username
        fun getUsername(defValue: String?): String? {
            return getInstance()!!.prefs.getString(USER_NAME, defValue)
        }

        // Set car profile
        fun setCarProfile(value: Int) {
            getInstance()!!.prefsEditor.putInt(CAR_PROFILE, value).apply()
        }

        // Get car profile
        fun getCarProfile(): Int {
            return getInstance()!!.prefs.getInt(CAR_PROFILE, 1)
        }

        // Clear all preferences
        fun clear() {
            getInstance()!!.prefsEditor.clear().apply()
        }

    }

    // Private constructor to enforce singleton pattern
    init {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefsEditor = prefs.edit()
    }
}