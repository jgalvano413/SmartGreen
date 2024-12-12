package com.pachi.smartgreen.objets

import android.content.Context

class NotificationSettings(val context:Context) {

    private val NOTIFICATION_KEY: String = "notifications"
    private val PREF_NAME: String = "MyPrefs"
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    private val editor = preferences.edit()

    fun setNotificationPermiass(data:Boolean){
        editor.putBoolean(NOTIFICATION_KEY, data)
        editor.apply()
    }

    fun getNotificationPermiss(): Boolean {
        return preferences.getBoolean(NOTIFICATION_KEY, false)
    }

    fun saveThreshold(key: String,value:String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun saveThresholdMachine(key: String,value:Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getMovving(key: String): Boolean {
        return preferences.getBoolean(key, true)
    }


    fun getData(key: String): String? {
        return preferences.getString(key, "")
    }
}