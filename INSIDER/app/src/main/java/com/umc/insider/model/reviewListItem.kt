package com.umc.insider.model

import java.util.Date

data class reviewListItem (
    val reviewTitle : String,
    val reviewScore : String,
    val reviewDetail : String,
    val reviewDate : Date
)