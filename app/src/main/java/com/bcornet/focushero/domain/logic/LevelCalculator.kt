package com.bcornet.focushero.domain.logic

import kotlin.math.max

object LevelCalculator {

    fun levelForTotalPoints(
        totalPoints: Int,
        pointsPerLevel: Int = DEFAULT_POINTS_PER_LEVEL,
    ): Int {
        require(pointsPerLevel > 0) {"pointsPerLevel must be > 0"}
        val safePoints = max(0, totalPoints)
        return (safePoints / pointsPerLevel) + 1
    }

    fun progressToNextLevel(
        totalPoints: Int,
        pointsPerLevel: Int = DEFAULT_POINTS_PER_LEVEL,
    ): Float {
        require(pointsPerLevel > 0) {"pointsPerLevel mus be > 0"}
        val safePoints = max(0, totalPoints)
        val inLevel = safePoints % pointsPerLevel
        return inLevel.toFloat() / pointsPerLevel.toFloat()
    }

    fun pointsRemainingToNextLevel(
        totalPoints: Int,
        pointsPerLevel: Int = DEFAULT_POINTS_PER_LEVEL,
    ): Int {
        require(pointsPerLevel > 0) {"pointsPerLevel must be > 0"}
        val safePoints = max(0, totalPoints)
        val inLevel = safePoints % pointsPerLevel
        return pointsPerLevel - inLevel
    }

    const val DEFAULT_POINTS_PER_LEVEL: Int = 50
}