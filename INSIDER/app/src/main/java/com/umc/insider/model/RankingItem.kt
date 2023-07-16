package com.umc.insider.model

data class RankingItemListResponse(
    val rankingSearchTerms : List<RankingItem>
)

data class RankingItem (
    val ranking : Int,
    val searchTerm : String
)