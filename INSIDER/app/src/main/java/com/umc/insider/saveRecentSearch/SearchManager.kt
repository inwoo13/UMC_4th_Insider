package com.umc.insider.saveRecentSearch

import android.content.Context
import com.umc.insider.model.RankingItem
import com.umc.insider.model.RecentSearchItem
import com.umc.insider.saveRecentSearch.SearchSharedPreferences.Companion.PREFS_NAME

class SearchManager(private val context: Context) {

    private val MAX_SEARCH_HISTORY_SIZE = 5
    private val searchSharedPreferences = SearchSharedPreferences(context)

    fun addSearchWord(query: String) {
        var searchHistory = searchSharedPreferences.loadSearchHistory()

        searchHistory = ArrayList(searchHistory.filter { it != query })
        searchHistory.add(0, query)

        while (searchHistory.size > MAX_SEARCH_HISTORY_SIZE) {
            searchHistory.removeAt(searchHistory.size - 1)
        }

        searchSharedPreferences.storeSearchHistory(searchHistory)
    }

    fun removeSearchWord(query: String) {
        val searchHistory = searchSharedPreferences.loadSearchHistory()

        if (searchHistory.contains(query)) {
            searchHistory.remove(query)
            searchSharedPreferences.storeSearchHistory(searchHistory)
        }
    }

    fun getSearchHistory(): ArrayList<RecentSearchItem> {

        val history = searchSharedPreferences.loadSearchHistory()
        val arr = ArrayList<RecentSearchItem>()
        for((index, searches) in history.withIndex()){
            arr.add(RecentSearchItem(index, searches))
        }

        return arr
    }

    fun clearSearchHistory() {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }

}
