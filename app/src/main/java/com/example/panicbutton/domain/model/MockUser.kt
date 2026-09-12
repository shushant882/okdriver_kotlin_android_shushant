package com.example.panicbutton.domain.model

import androidx.compose.ui.graphics.Color

data class MockUser(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Long,
    val isOnline: Boolean,
    val latitude: Double,
    val longitude: Double,
    val locationName: String = "",
    val isMe: Boolean = false
) {
    val displayColor: Color get() = Color(avatarColor)
}
