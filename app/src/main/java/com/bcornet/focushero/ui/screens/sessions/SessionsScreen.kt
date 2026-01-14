package com.bcornet.focushero.ui.screens.sessions

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val LevelUpColor = Color(0xFF3287FF)

@Composable
fun SessionsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    uiState: SessionsUiState,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Sessions",
                style = MaterialTheme.typography.headlineLarge,
            )

            when {
                uiState.isLoading -> {
                    CenterState(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = null,
                            )
                        },
                        title = "Loading sessions",
                        message = "Fetching your session history...",
                    )
                }

                uiState.errorMessage != null -> {
                    CenterState(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                            )
                        },
                        title = "Something went wrong",
                        message = uiState.errorMessage,
                    )
                }

                uiState.items.isEmpty() -> {
                    CenterState(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = null,
                            )
                        },
                        title = "No sessions yet",
                        message = "Start a focus session to see your history here.",
                    )
                }

                else -> {
                    SessionsList(items = uiState.items)
                }
            }
        }
    }
}

@Composable
private fun CenterState(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Box(
                    modifier = Modifier.padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SessionsList(
    items: List<SessionsListItem>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        items(
            items = items,
            key = { item ->
                when (item) {
                    is SessionsListItem.SessionItem -> "session-${item.session.id}"
                    is SessionsListItem.LevelUpItem -> "levelup-${item.level}"
                }
            },
        ) { item ->
            when (item) {
                is SessionsListItem.SessionItem -> SessionCard(session = item.session)
                is SessionsListItem.LevelUpItem -> LevelUpCard(level = item.level)
            }
        }
    }
}

@Composable
private fun LevelUpCard(
    level: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = LevelUpColor.copy(alpha = 0.3f),
        )

        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = LevelUpColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Level $level reached",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = LevelUpColor,
            )
        }

        Divider(
            modifier = Modifier.weight(1f),
            color = LevelUpColor.copy(alpha = 0.3f),
        )
    }
}


@Composable
private fun SessionCard(
    session: FocusSession,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // HEADER - Date + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = formatEndTime(session),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )

                StatusBadge(status = session.status)
            }
            // DETAILS - Duration + Points
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InlineDetail(
                    label = "Duration",
                    value = formatDuration(session.durationSeconds),
                    modifier = Modifier.weight(1f),
                    alignEnd = false,
                )
                InlineDetail(
                    label = "Points earned",
                    value = session.pointsEarned.toString(),
                    modifier = Modifier.weight(1f),
                    alignEnd = false,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: SessionStatus,
) {
    val (label, icon, accent) = when (status) {
        SessionStatus.COMPLETED -> Triple(
            "Completed",
            Icons.Outlined.CheckCircle,
            Color(0xFF22C55E)
        )

        SessionStatus.STOPPED -> Triple("Stopped", Icons.Outlined.PauseCircle, Color(0xFFF97316))
    }

    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .width(18.dp)
                    .height(18.dp),
                tint = accent,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = accent,
            )
        }
    }
}

@Composable
private fun InlineDetail(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean,
) {
    val arrangement = if (alignEnd) Arrangement.End else Arrangement.Start

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    val seconds = safe % 60

    return if (hours > 0) {
        String.format(Locale.US, "%dh %02dm %02ds", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%dm %02ds", minutes, seconds)
    }
}

private fun formatEndTime(session: FocusSession): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm", Locale.US)
    val zoned = session.endTime.atZone(ZoneId.systemDefault())
    return formatter.format(zoned)
}
