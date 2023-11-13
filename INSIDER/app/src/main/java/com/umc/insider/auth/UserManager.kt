package com.umc.insider.auth

import android.content.Context
import android.content.SharedPreferences

class UserManager private constructor() {

    companion object{
        private const val PREF_NAME = "UserPref"
        private const val USER_KEY = "userIdx"
        private const val USER_STATUS_KEY = "userStatus"

        private fun getPreferences(context : Context) : SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun saveUserIdx(context: Context, userIdx: Long?) {
            val editor = getPreferences(context).edit()
            editor.putString(USER_KEY, userIdx.toString())
            editor.apply()
        }

        fun getUserIdx(context: Context): String? {
            return getPreferences(context).getString(USER_KEY, null)
        }

        fun clearUserIdx(context: Context) {
            val editor = getPreferences(context).edit()
            editor.remove(USER_KEY)
            editor.apply()
        }

        fun setUserSellerOrBuyer(context: Context, sellerOrBuyer : Int?) {
            val editor = getPreferences(context).edit()
            editor.putString(USER_STATUS_KEY, sellerOrBuyer.toString())
            editor.apply()
        }

        fun getUserSellerOrBuyer(context: Context): String? {
            return getPreferences(context).getString(USER_STATUS_KEY, null)
        }

        fun clearUserSellerOrBuyer(context: Context) {
            val editor = getPreferences(context).edit()
            editor.remove(USER_STATUS_KEY)
            editor.apply()
        }
    }
}