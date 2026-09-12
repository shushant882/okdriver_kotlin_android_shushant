package com.example.panicbutton.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestHistoryDao {

    @Insert
    suspend fun insert(record: RequestHistoryEntity)

    @Query("SELECT * FROM request_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<RequestHistoryEntity>>

    @Query("SELECT * FROM request_history WHERE outcome = :outcome ORDER BY timestamp DESC")
    fun getByOutcome(outcome: String): Flow<List<RequestHistoryEntity>>

    @Query("DELETE FROM request_history")
    suspend fun clearAll()
}
