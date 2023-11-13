package com.umc.insider.model

data class DeleteChatItem(
    val chatId: String,
    val recentMessage: String,
    val isChecked: Boolean = false
)
