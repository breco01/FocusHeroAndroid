package com.bcornet.focushero.data

import android.content.Context
import com.bcornet.focushero.data.repo.FocusSessionRepository
import com.bcornet.focushero.domain.logic.PointsCalculator
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import kotlin.math.max
import kotlin.random.Random

object DemoDataSeeder {

    private const val SEED_VERSION = 1

    private const val PREFS_NAME = "demo_data_seeder_prefs"
    private const val KEY_APPLIED_VERSION = "applied_version"

    private const val DEMO_SEED_ENABLED = true

    enum class SeedResult {
        DISABLED,
        SKIPPED_DB_NOT_EMPTY,
        SKIPPED_ALREADY_APPLIED,
        SEEDED,
    }

    suspend fun seedIfNeeded(
        context: Context,
        repository: FocusSessionRepository,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ) {
        if (!DEMO_SEED_ENABLED) return

        // Never seed if there is already real data.
        val existing = repository.getSessionsMostRecentFirst()
        if (existing.isNotEmpty()) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val appliedVersion = prefs.getInt(KEY_APPLIED_VERSION, 0)
        if (appliedVersion == SEED_VERSION) return

        val sessions = generateSessions(
            daysBack = 45,
            zoneId = zoneId,
            seedVersion = SEED_VERSION,
        )

        // Persist as real sessions
        sessions.forEach { repository.saveSession(it) }

        prefs.edit().putInt(KEY_APPLIED_VERSION, SEED_VERSION).apply()
    }

    private fun generateSessions(
        daysBack: Int,
        zoneId: ZoneId,
        seedVersion: Int,
    ): List<FocusSession> {
        val today = LocalDate.now(zoneId)
        val result = ArrayList<FocusSession>(daysBack * 2)

        for (dayOffset in 0 until daysBack) {
            val date = today.minusDays(dayOffset.toLong())
            val isWeekend = date.dayOfWeek.value >= 6

            // Deterministic per day
            val dayRng = Random(deterministicSeed(seedVersion, dayOffset, salt = 11))

            // Fewer sessions on weekends
            val baseSessions = if (isWeekend) 0 else 1
            val variability = if (isWeekend) dayRng.nextInt(0, 2) else dayRng.nextInt(0, 4)
            val sessionsCount = baseSessions + variability

            // Rest days
            val restDayChance = if (isWeekend) 0.35 else 0.12
            val isRestDay = dayRng.nextDouble() < restDayChance
            if (isRestDay) continue

            val timeSlots = listOf(
                9 to 0,   // Morning
                12 to 30, // Midday
                15 to 0,  // Afternoon
                19 to 0,  // Evening
                22 to 0,  // Late
            )

            for (i in 0 until sessionsCount) {
                val slot = timeSlots[(i + dayRng.nextInt(0, timeSlots.size)) % timeSlots.size]
                val hour = slot.first
                val minute = slot.second + dayRng.nextInt(0, 25)

                val start = localDateTime(date, hour, minute)
                    .atZone(zoneId)
                    .toInstant()

                val latePenalty = if (hour >= 21) 0.18 else 0.0
                val weekendPenalty = if (isWeekend) 0.12 else 0.0

                val baseCompletionProb = if (isWeekend) 0.72 else 0.84
                val noise = (dayRng.nextDouble() - 0.5) * 0.10
                val completionProb = (baseCompletionProb - latePenalty - weekendPenalty + noise)
                    .coerceIn(0.15, 0.95)

                val completed = dayRng.nextDouble() < completionProb

                val durationSeconds = if (completed) {
                    // COMPLETED = Mix of short & deep work blocks
                    val minutes = when (dayRng.nextInt(0, 10)) {
                        in 0..2 -> 25
                        in 3..5 -> 50
                        in 6..7 -> 75
                        else -> 100
                    }
                    minutes * 60
                } else {
                    // STOPPED = Shorter & interrupted
                    val minutes = when (dayRng.nextInt(0, 10)) {
                        in 0..4 -> 10
                        in 5..7 -> 15
                        else -> 20
                    }
                    minutes * 60
                }

                val status = if (completed) SessionStatus.COMPLETED else SessionStatus.STOPPED

                val points = PointsCalculator.calculatePoints(
                    durationSeconds = durationSeconds,
                    status = status,
                )

                val id = deterministicUuid(seedVersion, dayOffset, i)

                val end: Instant = start.plusSeconds(max(1, durationSeconds).toLong())

                result.add(
                    FocusSession(
                        id = id,
                        startTime = start,
                        endTime = end,
                        durationSeconds = durationSeconds,
                        pointsEarned = points,
                        status = status,
                    )
                )
            }
        }

        return result.sortedByDescending { it.endTime }
    }

    private fun localDateTime(
        date: LocalDate,
        hour: Int,
        minute: Int,
    ): LocalDateTime {
        return LocalDateTime.of(date.year, date.month, date.dayOfMonth, hour, minute)
    }

    private fun deterministicSeed(seedVersion: Int, dayOffset: Int, salt: Int): Long {
        // Stable hash -> long
        var x = 1469598103934665603L
        fun mix(v: Int) {
            x = x xor v.toLong()
            x *= 1099511628211L
        }
        mix(seedVersion)
        mix(dayOffset)
        mix(salt)
        return x
    }

    private fun deterministicUuid(seedVersion: Int, dayOffset: Int, index: Int): UUID {
        val bytes = "seed:$seedVersion:$dayOffset:$index".toByteArray(Charsets.UTF_8)
        return UUID.nameUUIDFromBytes(bytes)
    }
}