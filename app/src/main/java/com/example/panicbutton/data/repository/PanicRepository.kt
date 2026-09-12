package com.example.panicbutton.data.repository

import com.example.panicbutton.data.local.AppDatabase
import com.example.panicbutton.data.local.RequestHistoryEntity
import com.example.panicbutton.data.mock.MockUserDataSource
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.domain.util.DistanceCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class PanicRepository(
    private val mockUserDataSource: MockUserDataSource,
    private val database: AppDatabase
) {
    private val historyDao = database.requestHistoryDao()

    val users: StateFlow<List<MockUser>> = mockUserDataSource.users

    fun getMe(): MockUser = mockUserDataSource.getMe()

    fun toggleUserOnlineStatus(userId: String) {
        mockUserDataSource.toggleOnlineStatus(userId)
    }

    fun setUserOnlineStatus(userId: String, online: Boolean) {
        mockUserDataSource.setOnlineStatus(userId, online)
    }

    fun getOnlineHelpersByDistance(): List<Pair<MockUser, Double>> {
        val me = mockUserDataSource.getMe()
        return mockUserDataSource.getCurrentUsers()
            .filter { !it.isMe && it.isOnline }
            .map { helper ->
                val distance = DistanceCalculator.calculateDistanceKm(
                    me.latitude, me.longitude,
                    helper.latitude, helper.longitude
                )
                helper to distance
            }
            .sortedBy { it.second }
    }

    fun getAllHistory(): Flow<List<RequestHistoryEntity>> = historyDao.getAll()

    fun getHistoryByOutcome(outcome: String): Flow<List<RequestHistoryEntity>> =
        historyDao.getByOutcome(outcome)

    suspend fun logRequest(helperName: String, distanceKm: Double, outcome: String) {
        historyDao.insert(
            RequestHistoryEntity(
                timestamp = System.currentTimeMillis(),
                helperName = helperName,
                distanceKm = distanceKm,
                outcome = outcome
            )
        )
    }

    suspend fun clearHistory() {
        historyDao.clearAll()
    }
}
