package com.parkro.client.util

import android.content.Context
import android.content.SharedPreferences
/**
 * SharedPreferences
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.25   김지수      최초 생성
 * </pre>
 */
class PreferencesUtil private constructor(context: Context) {
    private val prefs: SharedPreferences
    private val prefsEditor: SharedPreferences.Editor

    companion object {
        private const val PREFS = "prefs"
        private const val ACCESS_TOKEN = "access_token"
        private const val USER_NAME = "user_name"
        private const val PASSWORD = "password"
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

        // Set car profile
        fun setCarProfile(value: Int?){
            if (value != null) {
                getInstance()!!.prefsEditor.putInt(CAR_PROFILE, value).apply()
            }
        }

        fun setPassword(value: String?){
            getInstance()!!.prefsEditor.putString(PASSWORD, value).apply()
        }

        // Get access token
        fun getAccessToken(defValue: String?): String? {
            return getInstance()!!.prefs.getString(ACCESS_TOKEN, defValue)
        }

        // Get username
        fun getUsername(defValue: String?): String? {
            return getInstance()!!.prefs.getString(USER_NAME, defValue)
        }

        // Get car profile
        fun getCarProfile(): Int {
            return getInstance()!!.prefs.getInt(CAR_PROFILE, 1)
        }

        fun getPassword(defValue: String?): String? {
            return getInstance()!!.prefs.getString(PASSWORD, defValue)
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