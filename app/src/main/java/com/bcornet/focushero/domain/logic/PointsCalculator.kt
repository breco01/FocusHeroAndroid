package com.bcornet.focushero.domain.logic

import com.bcornet.focushero.domain.model.SessionStatus
import kotlin.math.floor

object PointsCalculator {
    fun calculatePoints(
        durationSeconds: Int,
        status: SessionStatus,
        secondsPerPoint: Int = DEFAULT_SECONDS_PER_POINT,
    ): Int{
        require(durationSeconds >= 0) {"durationSeconds must be >= 0"}
        require(secondsPerPoint > 0) {"secondsPerPoint must be > 0"}

        if (status != SessionStatus.COMPLETED) return 0

        return floor(durationSeconds.toDouble() / secondsPerPoint.toDouble()).toInt()
    }

    const val DEFAULT_SECONDS_PER_POINT: Int = 25 * 60
}