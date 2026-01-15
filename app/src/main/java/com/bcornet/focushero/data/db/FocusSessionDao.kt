package com.bcornet.focushero.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY startTimeEpochSeconds DESC")
    fun observeAllMostRecentFirst(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startTimeEpochSeconds DESC")
    suspend fun getAllMostRecentFirst(): List<FocusSessionEntity>

    @Query("SELECT COALESCE(SUM(pointsEarned), 0) FROM focus_sessions")
    fun observeTotalPoints(): Flow<Int>

    @Query("DELETE FROM focus_sessions")
    suspend fun clearAll()
}