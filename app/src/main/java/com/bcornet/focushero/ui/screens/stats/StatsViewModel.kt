package com.bcornet.focushero.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class StatsViewModel(
    private val repository: FocusSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val selectedRange = MutableStateFlow(StatsRange.DAYS_14)

    init {
        observeStats()
    }

    fun onRangeSelected(range: StatsRange) {
        selectedRange.value = range
        _uiState.update { it.copy(selectedRange = range) }
    }

    private fun observeStats() {
        viewModelScope.launch {
            repository.observeSessionsMostRecentFirst()
                .combine(selectedRange) { sessions, range ->
                    sessions to range
                }
                .collectLatest { (sessions, range) ->
                    val hasAny = sessions.isNotEmpty()

                    val computed = computeStats(
                        allSessions = sessions,
                        range = range,
                        zoneId = ZoneId.systemDefault(),
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedRange = range,
                            hasAnySessions = hasAny,

                            summary = computed.summary,
                            dailyFocusMinutes = computed.dailyFocusMinutes,
                            dailyPoints = computed.dailyPoints,
                            dailyOutcomeCounts = computed.dailyOutcomeCounts,
                            weeklyFocusMinutes = computed.weeklyFocusMinutes,

                            errorMessage = null,
                        )
                    }
                }
        }
    }

    private data class ComputedStats(
        val summary: StatsSummary,
        val dailyFocusMinutes: List<DailyValue>,
        val dailyPoints: List<DailyValue>,
        val dailyOutcomeCounts: List<DailyOutcomeCounts>,
        val weeklyFocusMinutes: List<WeeklyValue>,
    )

    private fun computeStats(
        allSessions: List<FocusSession>,
        range: StatsRange,
        zoneId: ZoneId,
    ): ComputedStats {
        val today = LocalDate.now(zoneId)
        val startDate = today.minusDays((range.days - 1).toLong())
        val datesInRange = buildDateRange(startDate, today)

        val sessionsInRange = allSessions.filter { session ->
            val endDate = session.endTime.atZone(zoneId).toLocalDate()
            !endDate.isBefore(startDate) && !endDate.isAfter(today)
        }

        val dailyFocus = datesInRange.map { date ->
            val minutes = sessionsInRange
                .asSequence()
                .filter { it.endTime.atZone(zoneId).toLocalDate() == date }
                .sumOf { it.durationSeconds.coerceAtLeast(0) } / 60

            DailyValue(date = date, value = minutes)
        }

        val dailyPts = datesInRange.map { date ->
            val points = sessionsInRange
                .asSequence()
                .filter { it.endTime.atZone(zoneId).toLocalDate() == date }
                .sumOf { it.pointsEarned.coerceAtLeast(0) }

            DailyValue(date = date, value = points)
        }

        val dailyOutcomes = datesInRange.map { date ->
            val sessionsForDay = sessionsInRange.filter {
                it.endTime.atZone(zoneId).toLocalDate() == date
            }

            DailyOutcomeCounts(
                date = date,
                completedCount = sessionsForDay.count { it.status == SessionStatus.COMPLETED },
                stoppedCount = sessionsForDay.count { it.status == SessionStatus.STOPPED },
            )
        }

        val totalFocusMinutes = dailyFocus.sumOf { it.value }
        val totalPoints = dailyPts.sumOf { it.value }

        val totalSessions = sessionsInRange.size
        val completedSessions = sessionsInRange.count { it.status == SessionStatus.COMPLETED }
        val stoppedSessions = sessionsInRange.count { it.status == SessionStatus.STOPPED }

        val avgMinutesPerDay = if (range.days > 0) totalFocusMinutes / range.days else 0

        val best = dailyFocus.maxByOrNull { it.value }
        val bestDay = best?.date?.takeIf { best.value > 0 }
        val bestDayMinutes = best?.value ?: 0

        val completionRate = if (totalSessions == 0) {
            0
        } else {
            ((completedSessions * 100.0) / totalSessions).toInt().coerceIn(0, 100)
        }

        val summary = StatsSummary(
            totalFocusMinutes = totalFocusMinutes,
            totalPoints = totalPoints,
            totalSessions = totalSessions,
            completedSessions = completedSessions,
            stoppedSessions = stoppedSessions,
            averageFocusMinutesPerDay = avgMinutesPerDay,
            bestDay = bestDay,
            bestDayFocusMinutes = bestDayMinutes,
            completionRatePercent = completionRate,
        )

        val weekly = computeLastWeeksFocus(
            allSessions = allSessions,
            zoneId = zoneId,
            weeks = 6,
        )

        return ComputedStats(
            summary = summary,
            dailyFocusMinutes = dailyFocus,
            dailyPoints = dailyPts,
            dailyOutcomeCounts = dailyOutcomes,
            weeklyFocusMinutes = weekly,
        )
    }

    private fun buildDateRange(start: LocalDate, endInclusive: LocalDate): List<LocalDate> {
        val result = ArrayList<LocalDate>()
        var cursor = start
        while (!cursor.isAfter(endInclusive)) {
            result.add(cursor)
            cursor = cursor.plusDays(1)
        }
        return result
    }

    private fun computeLastWeeksFocus(
        allSessions: List<FocusSession>,
        zoneId: ZoneId,
        weeks: Int,
    ): List<WeeklyValue> {
        val today = LocalDate.now(zoneId)

        val currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekStarts = (weeks - 1 downTo 0).map { offset ->
            currentWeekStart.minusWeeks(offset.toLong())
        }

        return weekStarts.map { weekStart ->
            val weekEnd = weekStart.plusDays(6)

            val minutes = allSessions
                .asSequence()
                .filter { session ->
                    val endDate = session.endTime.atZone(zoneId).toLocalDate()
                    !endDate.isBefore(weekStart) && !endDate.isAfter(weekEnd)
                }
                .sumOf { it.durationSeconds.coerceAtLeast(0) } / 60

            WeeklyValue(
                weekStart = weekStart,
                focusMinutes = minutes,
            )
        }
    }

    class Factory(
        private val repository: FocusSessionRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                return StatsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create(modelClass, CreationExtras.Empty)
        }
    }
}
