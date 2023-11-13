package com.umc.insider.auth

import android.content.Context
import android.content.SharedPreferences

class AutoLoginManager(context: Context) {
    companion object{
        const val PREF_NAME = "loginPrefs"
        const val KEY_AUTO_LOGIN = "autoLogin"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setAutoLogin(isAutoLogin: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_AUTO_LOGIN, isAutoLogin)
        editor.apply()
    }

    fun isAutoLogin(): Boolean {
        return prefs.getBoolean(KEY_AUTO_LOGIN, false)
    }
}