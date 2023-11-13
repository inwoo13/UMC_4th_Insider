package com.umc.insider.auth

import android.content.Context
import android.content.SharedPreferences

class TokenManager private constructor() {

    companion object{
        private const val PREF_NAME = "TokenPref"
        private const val TOKEN_KEY = "jwt"

        private fun getPreferences(context : Context) : SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun saveToken(context: Context, token: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(TOKEN_KEY, token)
            editor.apply()
        }

        fun getToken(context: Context): String? {
            return getPreferences(context).getString(TOKEN_KEY, null)
        }

        fun clearToken(context: Context) {
            val editor = getPreferences(context).edit()
            editor.remove(TOKEN_KEY)
            editor.apply()
        }
    }
}