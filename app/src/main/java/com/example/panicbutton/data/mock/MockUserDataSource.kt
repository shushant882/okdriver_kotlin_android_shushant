package com.example.panicbutton.data.mock

import com.example.panicbutton.domain.model.MockUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockUserDataSource {

    private val _users = MutableStateFlow(createMockUsers())
    val users: StateFlow<List<MockUser>> = _users.asStateFlow()

    fun toggleOnlineStatus(userId: String) {
        _users.update { list ->
            list.map { user ->
                if (user.id == userId) user.copy(isOnline = !user.isOnline)
                else user
            }
        }
    }

    fun setOnlineStatus(userId: String, online: Boolean) {
        _users.update { list ->
            list.map { user ->
                if (user.id == userId) user.copy(isOnline = online)
                else user
            }
        }
    }

    fun getCurrentUsers(): List<MockUser> = _users.value

    fun getMe(): MockUser = _users.value.first { it.isMe }

    companion object {
        private const val BASE_LAT = 28.6139
        private const val BASE_LNG = 77.2090

        private fun createMockUsers(): List<MockUser> = listOf(
            MockUser(
                id = "me",
                name = "You",
                initials = "ME",
                avatarColor = 0xFF6C63FF,
                isOnline = true,
                latitude = BASE_LAT,
                longitude = BASE_LNG,
                locationName = "Connaught Place",
                isMe = true
            ),
            MockUser(
                id = "riya",
                name = "Riya Sharma",
                initials = "RS",
                avatarColor = 0xFFFF6B9D,
                isOnline = true,
                latitude = BASE_LAT + 0.005,
                longitude = BASE_LNG + 0.003,
                locationName = "Barakhamba Road"
            ),
            MockUser(
                id = "arjun",
                name = "Arjun Patel",
                initials = "AP",
                avatarColor = 0xFF4ECDC4,
                isOnline = true,
                latitude = BASE_LAT - 0.008,
                longitude = BASE_LNG + 0.012,
                locationName = "Lodhi Garden"
            ),
            MockUser(
                id = "priya",
                name = "Priya Kapoor",
                initials = "PK",
                avatarColor = 0xFFFFBE76,
                isOnline = true,
                latitude = BASE_LAT + 0.015,
                longitude = BASE_LNG - 0.008,
                locationName = "Karol Bagh"
            ),
            MockUser(
                id = "vikram",
                name = "Vikram Singh",
                initials = "VS",
                avatarColor = 0xFF45B7D1,
                isOnline = false,
                latitude = BASE_LAT - 0.020,
                longitude = BASE_LNG + 0.018,
                locationName = "Nizamuddin"
            ),
            MockUser(
                id = "ananya",
                name = "Ananya Desai",
                initials = "AD",
                avatarColor = 0xFFA29BFE,
                isOnline = true,
                latitude = BASE_LAT + 0.025,
                longitude = BASE_LNG + 0.020,
                locationName = "Civil Lines"
            ),
            MockUser(
                id = "rohan",
                name = "Rohan Mehta",
                initials = "RM",
                avatarColor = 0xFF26DE81,
                isOnline = true,
                latitude = BASE_LAT - 0.030,
                longitude = BASE_LNG - 0.015,
                locationName = "Sarojini Nagar"
            ),
            MockUser(
                id = "neha",
                name = "Neha Gupta",
                initials = "NG",
                avatarColor = 0xFFFF7675,
                isOnline = false,
                latitude = BASE_LAT + 0.040,
                longitude = BASE_LNG + 0.010,
                locationName = "Model Town"
            )
        )
    }
}
