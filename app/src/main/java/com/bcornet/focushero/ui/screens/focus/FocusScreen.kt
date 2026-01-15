package com.bcornet.focushero.ui.screens.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcornet.focushero.domain.model.SessionStatus
import com.bcornet.focushero.ui.components.FocusCompanion
import com.bcornet.focushero.ui.components.FocusCompanionMood
import com.bcornet.focushero.ui.components.FocusCompanionResult
import com.bcornet.focushero.ui.components.FocusSessionCard


@Composable
fun FocusScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),

    // UI State
    uiState: FocusUiState = FocusUiState(),

    // Actions
    onIncreaseMinutes: () -> Unit = {},
    onDecreaseMinutes: () -> Unit = {},
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onStop: () -> Unit = {},
    onDismissResult: () -> Unit = {},

    ) {
    val canEditDuration = uiState.sessionStatus == FocusSessionRunState.IDLE

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // HEADER
            Text(
                text = "Focus",
                style = MaterialTheme.typography.headlineLarge,
            )

            // RESULT BANNER
            uiState.lastSessionResult?.let { result ->
                ResultBanner(
                    status = result.status,
                    pointsEarned = result.pointsEarned,
                    onDismiss = onDismissResult,
                )
            }

            // COMPANION (Lottie)
            val companionBaseMood = when (uiState.sessionStatus) {
                FocusSessionRunState.IDLE -> FocusCompanionMood.Idle
                FocusSessionRunState.RUNNING -> FocusCompanionMood.Focusing
                FocusSessionRunState.PAUSED -> FocusCompanionMood.Paused
            }

            val companionResult = when (uiState.lastSessionResult?.status) {
                SessionStatus.COMPLETED -> FocusCompanionResult.Completed
                SessionStatus.STOPPED -> FocusCompanionResult.Stopped
                null -> null
            }

            FocusCompanion(
                currentLevel = uiState.currentLevel,
                totalPoints = uiState.totalPoints,
                progressToNextLevel = uiState.progressToNextLevel,
                pointsRemainingToNextLevel = uiState.pointsRemainingToNextLevel,
                baseMood = companionBaseMood,
                result = companionResult,
                modifier = Modifier.fillMaxWidth(),
            )


            // Timer
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Current session", style = MaterialTheme.typography.titleMedium)

                    Text(
                        text = formatAsMinutesSeconds(uiState.remainingSeconds),
                        style = MaterialTheme.typography.displayMedium,
                    )

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = {
                            sessionProgress(
                                remainingSeconds = uiState.remainingSeconds,
                                totalSeconds = uiState.totalSeconds,
                            )
                        },
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        when (uiState.sessionStatus) {
                            FocusSessionRunState.IDLE -> {
                                Button(
                                    onClick = onStart,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Start")
                                }
                            }

                            FocusSessionRunState.RUNNING -> {
                                OutlinedButton(
                                    onClick = onPause,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Pause")
                                }
                                Button(
                                    onClick = onStop,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Stop")
                                }
                            }

                            FocusSessionRunState.PAUSED -> {
                                OutlinedButton(
                                    onClick = onResume,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Resume")
                                }
                                Button(
                                    onClick = onStop,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Stop")
                                }
                            }
                        }
                    }
                }
            }

            // DURATION SELECTOR
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Session duration", style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDecreaseMinutes,
                                enabled = canEditDuration,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease minutes",
                                )
                            }

                            Text(
                                text = "${uiState.selectedDurationMinutes} min",
                                style = MaterialTheme.typography.titleLarge,
                            )

                            IconButton(
                                onClick = onIncreaseMinutes,
                                enabled = canEditDuration,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase minutes",
                                )
                            }
                        }

                        Text(
                            text = if (canEditDuration) "Editable" else "Locked",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    Text(
                        text = if (canEditDuration) {
                            "You can adjust the duration while no session is running."
                        } else {
                            "Duration is locked during an active session."
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // LEVEL + POINTS
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Progress", style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Level ${uiState.currentLevel}",
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "${uiState.totalPoints} pts",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { uiState.progressToNextLevel.coerceIn(0f, 1f) },
                    )

                    Text(
                        text = "${uiState.pointsRemainingToNextLevel} points to next level",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // RECENT SESSIONS
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Recent sessions", style = MaterialTheme.typography.titleMedium)

                    if (uiState.recentSessions.isEmpty()) {
                        Text(
                            text = "No sessions yet. Complete a focus session to see it here.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.recentSessions.forEach { session ->
                                FocusSessionCard(session = session)
                            }
                        }
                    }
                }
            }

            HorizontalDivider()
        }
    }
}

@Composable
private fun ResultBanner(
    status: SessionStatus,
    pointsEarned: Int,
    onDismiss: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val title = when (status) {
                SessionStatus.COMPLETED -> "Session completed"
                SessionStatus.STOPPED -> "Session stopped early"
            }

            val subtitle = when (status) {
                SessionStatus.COMPLETED -> "You earned $pointsEarned point(s)."
                SessionStatus.STOPPED -> "No points awarded."
            }

            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)

            OutlinedButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    }
}

private fun formatAsMinutesSeconds(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun sessionProgress(remainingSeconds: Int, totalSeconds: Int): Float {
    if (totalSeconds <= 0) return 0f
    val clampedRemaining = remainingSeconds.coerceIn(0, totalSeconds)
    val done = totalSeconds - clampedRemaining
    return done.toFloat() / totalSeconds.toFloat()
}
