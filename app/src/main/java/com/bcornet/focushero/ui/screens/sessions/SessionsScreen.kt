package com.bcornet.focushero.ui.screens.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bcornet.focushero.domain.model.FocusSession
import com.bcornet.focushero.domain.model.SessionStatus
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Sessions",
                style = MaterialTheme.typography.headlineLarge,
            )

            when {
                uiState.isLoading -> {
                    Text(
                        text = "Loading your session history...",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                uiState.sessions.isEmpty() -> {
                    EmptySessionsState()
                }

                else -> {
                    SessionsList(
                        sessions = uiState.sessions,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySessionsState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "No sessions yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Start a focus session to see your history here.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SessionsList(
    sessions: List<FocusSession>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        items(
            count = sessions.size,
            key = { index -> sessions[index].id },
        ) { index ->
            val session = sessions[index]
            SessionRow(session = session)
        }
    }
}

@Composable
private fun SessionRow(
    session: FocusSession,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = sessionStatusLabel(session.status),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${session.pointsEarned} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Duration: ${formatDuration(session.durationSeconds)}",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = formatEndTime(session),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

private fun sessionStatusLabel(status: SessionStatus): String {
    return when (status) {
        SessionStatus.COMPLETED -> "Completed"
        SessionStatus.STOPPED -> "Stopped"
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
