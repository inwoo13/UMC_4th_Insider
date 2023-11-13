package com.umc.insider.model

data class ExchangeItemResponse(
    val exchangeItems : List<ExchangeItem>

)

data class ExchangeItem (
    val id : Int,
    //val imgUrl : String,
    val itemName : String,
    val itemAmount : String,
    val itemExchange :String,
    val itemExchangeAmount : String
)