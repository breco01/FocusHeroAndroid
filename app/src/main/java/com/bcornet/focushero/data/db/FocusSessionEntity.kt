package com.bcornet.focushero.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(

    @PrimaryKey
    val id: String,

    val startTimeEpochSeconds: Long,
    val endTimeEpochSeconds: Long,

    val durationSeconds: Int,
    val pointsEarned: Int,

    val status: String,
)