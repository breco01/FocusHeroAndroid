package com.bcornet.focushero.data.mappers

import com.bcornet.focushero.data.db.FocusSessionEntity
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus
import java.time.Instant
import java.util.UUID

fun FocusSessionEntity.toDomain(): FocusSession {
    return FocusSession(
        id = UUID.fromString(id),
        startTime = Instant.ofEpochSecond(startTimeEpochSeconds),
        endTime = Instant.ofEpochSecond(endTimeEpochSeconds),
        durationSeconds = durationSeconds,
        pointsEarned = pointsEarned,
        status = SessionStatus.valueOf(status),
    )
}

fun FocusSession.toEntity(): FocusSessionEntity{
    return FocusSessionEntity(
        id = id.toString(),
        startTimeEpochSeconds =  startTime.epochSecond,
        endTimeEpochSeconds = endTime.epochSecond,
        durationSeconds = durationSeconds,
        pointsEarned = pointsEarned,
        status = status.name,
    )
}