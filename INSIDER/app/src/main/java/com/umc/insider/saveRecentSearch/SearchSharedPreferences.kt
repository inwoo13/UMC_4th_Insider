package com.umc.insider.saveRecentSearch

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SearchSharedPreferences(context: Context) {

    companion object {
        const val PREFS_NAME = "recent_search"
        const val SEARCH_HISTORY = "search_history"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun storeSearchHistory(searchHistory: ArrayList<String>) {
        val editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(searchHistory)
        editor.putString(SEARCH_HISTORY, json)
        editor.apply()
    }

    fun loadSearchHistory(): ArrayList<String> {
        val gson = Gson()
        val json = preferences.getString(SEARCH_HISTORY, null)
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}