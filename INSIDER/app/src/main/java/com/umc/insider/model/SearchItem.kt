package com.umc.insider.model

data class SearchItemListResponse(
    val searchItems : List<SearchItem>

)

data class SearchItem (
    val id : Int,
    val itemName : String,
    val itemWeight : String,
    val itemPrice : String
)