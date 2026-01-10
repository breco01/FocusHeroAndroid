package com.bcornet.focushero.domain.model

import java.time.Instant
import java.util.UUID

data class FocusSession(
    val id: UUID = UUID.randomUUID(),
    val startTime: Instant,
    val endTime: Instant,
    val durationSeconds: Int,
    val pointsEarned: Int,
    val status: SessionStatus,
)
enum class SessionStatus{
    COMPLETED,
    STOPPED
}