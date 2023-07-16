package com.umc.insider.utils

import android.view.View

interface SearchesItemClickListener {
    fun onClickSearch(searchTerm : String)
    fun onClickDelete(searchTerm : String)
}