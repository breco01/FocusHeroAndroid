package com.bcornet.focushero.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bcornet.focushero.R
import com.bcornet.focushero.domain.model.SessionStatus
import com.bcornet.focushero.ui.screens.focus.FocusSessionRunState
import com.bcornet.focushero.ui.screens.focus.FocusUiState
import kotlinx.coroutines.delay

private enum class CompanionMood {
    Idle, Focusing, Paused, Victory, Tired
}

private data class MoodSpec(
    val label: String,
    val icon: ImageVector,
    val rawRes: Int,
    val iterations: Int,
)

/**
 * Focus companion hero card:
 * - Large Lottie zone
 * - Overlay badges: level + mood
 * - Progress block to next level
 *
 * Mood priority:
 * - Temporary override (Victory/Tired for ~2s based on lastSessionResult)
 * - Otherwise mapping from sessionStatus
 */
@Composable
fun FocusCompanion(
    uiState: FocusUiState,
    modifier: Modifier = Modifier,
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
        CompanionMood.Idle ->
            MoodSpec("Idle", Icons.Filled.SentimentSatisfied, R.raw.hero_idle, LottieConstants.IterateForever)

        CompanionMood.Focusing ->
            MoodSpec("Focusing", Icons.Filled.Bolt, R.raw.hero_focus, LottieConstants.IterateForever)

        CompanionMood.Paused ->
            MoodSpec("Paused", Icons.Filled.Pause, R.raw.hero_paused, LottieConstants.IterateForever)

        CompanionMood.Victory ->
            MoodSpec("Victory", Icons.Filled.CheckCircle, R.raw.hero_victory, 1)

        CompanionMood.Tired ->
            MoodSpec("Tired", Icons.Filled.Bedtime, R.raw.hero_tired, LottieConstants.IterateForever)
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(moodSpec.rawRes)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = moodSpec.iterations,
        restartOnPlay = true,
    )

    ElevatedCard(
        modifier = modifier.height(460.dp),
        colors = CardDefaults.elevatedCardColors(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // --- Animation zone with overlay ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                // Lottie center
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (composition != null) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(1.45f),
                        )
                    }
                }

                // Overlay row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .offset(y = (-8).dp),
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

            Spacer(Modifier.height(16.dp))

            // --- Progress block ---
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
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
