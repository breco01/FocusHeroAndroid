package com.bcornet.focushero.ui.screens.stats

import java.time.LocalDate

data class StatsUiState(
    val isLoading: Boolean = true,
    val selectedRange: StatsRange = StatsRange.DAYS_14,
    val hasAnySessions: Boolean = false,

    val summary: StatsSummary = StatsSummary.empty(),
    val dailyFocusMinutes: List<DailyValue> = emptyList(),
    val dailyPoints: List<DailyValue> = emptyList(),
    val dailyOutcomeCounts: List<DailyOutcomeCounts> = emptyList(),

    val weeklyFocusMinutes: List<WeeklyValue> = emptyList(),

    val errorMessage: String? = null,
)

enum class StatsRange(
    val days: Int,
    val label: String,
) {
    DAYS_7(days = 7, label = "1W"),
    DAYS_14(days = 14, label = "2W"),
    DAYS_30(days = 30, label = "1M"),
}

data class StatsSummary(
    val totalFocusMinutes: Int,
    val totalPoints: Int,

    val totalSessions: Int,
    val completedSessions: Int,
    val stoppedSessions: Int,

    val averageFocusMinutesPerDay: Int,

    val bestDay: LocalDate?,
    val bestDayFocusMinutes: Int,

    val completionRatePercent: Int, // 0..100
) {
    companion object {
        fun empty(): StatsSummary = StatsSummary(
            totalFocusMinutes = 0,
            totalPoints = 0,
            totalSessions = 0,
            completedSessions = 0,
            stoppedSessions = 0,
            averageFocusMinutesPerDay = 0,
            bestDay = null,
            bestDayFocusMinutes = 0,
            completionRatePercent = 0,
        )
    }
}

data class DailyValue(
    val date: LocalDate,
    val value: Int,
)

data class DailyOutcomeCounts(
    val date: LocalDate,
    val completedCount: Int,
    val stoppedCount: Int,
)

data class WeeklyValue(
    val weekStart: LocalDate,
    val focusMinutes: Int,
)