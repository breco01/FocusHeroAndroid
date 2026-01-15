package com.bcornet.focushero.ui.screens.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SentimentSatisfied
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bcornet.focushero.R
import com.bcornet.focushero.domain.model.SessionStatus
import com.bcornet.focushero.ui.components.FocusSessionCard
import kotlinx.coroutines.delay


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
            FocusCompanionCard(
                modifier = Modifier.fillMaxWidth(),
                uiState = uiState,
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

private enum class CompanionMood {
    Idle, Focusing, Paused, Victory, Tired
}

@Composable
private fun FocusCompanionCard(
    modifier: Modifier = Modifier,
    uiState: FocusUiState,
) {
    var moodOverride by remember { mutableStateOf<CompanionMood?>(null) }

    LaunchedEffect(uiState.lastSessionResult?.status) {
        when (uiState.lastSessionResult?.status) {
            SessionStatus.COMPLETED -> {
                moodOverride = CompanionMood.Victory
                delay(2_000)
                moodOverride = null
            }
            SessionStatus.STOPPED -> {
                moodOverride = CompanionMood.Tired
                delay(2_000)
                moodOverride = null
            }
            null -> Unit
        }
    }

    val baseMood = when (uiState.sessionStatus) {
        FocusSessionRunState.IDLE -> CompanionMood.Idle
        FocusSessionRunState.RUNNING -> CompanionMood.Focusing
        FocusSessionRunState.PAUSED -> CompanionMood.Paused
    }

    val mood = moodOverride ?: baseMood

    val moodSpec = when (mood) {
        CompanionMood.Idle -> MoodSpec("Idle", Icons.Filled.SentimentSatisfied, R.raw.hero_idle, LottieConstants.IterateForever)
        CompanionMood.Focusing -> MoodSpec("Focusing", Icons.Filled.Bolt, R.raw.hero_focus, LottieConstants.IterateForever)
        CompanionMood.Paused -> MoodSpec("Paused", Icons.Filled.Pause, R.raw.hero_paused, LottieConstants.IterateForever)
        CompanionMood.Victory -> MoodSpec("Victory", Icons.Filled.CheckCircle, R.raw.hero_victory, 1)
        CompanionMood.Tired -> MoodSpec("Tired", Icons.Filled.Bedtime, R.raw.hero_tired, LottieConstants.IterateForever)
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(moodSpec.rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = moodSpec.iterations,
        restartOnPlay = true,
    )

    ElevatedCard(
        modifier = modifier
            .height(460.dp),
        colors = CardDefaults.elevatedCardColors(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (composition != null) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(1.45f)
                                .clip(RectangleShape),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BadgePill(
                        text = "Level ${uiState.currentLevel}",
                        icon = Icons.Filled.ArrowUpward,
                        contentDescription = "Level",
                    )

                    BadgePill(
                        text = moodSpec.label,
                        icon = moodSpec.icon,
                        contentDescription = "Mood",
                    )
                }
            }

            Text(
                text = "Progress to level ${uiState.currentLevel + 1}",
                style = MaterialTheme.typography.titleMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.weight(1f),
                    progress = { uiState.progressToNextLevel.coerceIn(0f, 1f) },
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "${uiState.pointsRemainingToNextLevel} pts",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "Total points: ${uiState.totalPoints}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BadgePill(
    text: String,
    icon: ImageVector,
    contentDescription: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Text(text, style = MaterialTheme.typography.titleSmall)
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

private data class MoodSpec(
    val label: String,
    val icon: ImageVector,
    val rawRes: Int,
    val iterations: Int,
)
