package com.bcornet.focushero.ui.screens.stats


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    uiState: StatsUiState,
    onRangeSelected: (StatsRange) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "Stats",
                style = MaterialTheme.typography.headlineLarge
            )

            RangeSelector(
                selected = uiState.selectedRange,
                onSelected = onRangeSelected,
            )

            if (!uiState.hasAnySessions && !uiState.isLoading) {
                EmptyStatsState()
                return@Column
            }

            SummaryCard(
                summary = uiState.summary,
            )
        }
    }
}

@Composable
private fun RangeSelector(
    selected: StatsRange,
    onSelected: (StatsRange) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatsRange.values().forEach { range ->
            val isSelected = range == selected

            TextButton(
                onClick = { onSelected(range) },
                modifier = Modifier.wrapContentWidth(),
            ) {
                Text(
                    text = range.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun EmptyStatsState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "No stats yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Complete focus sessions to see your statistics here.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SummaryCard(
    summary: StatsSummary,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryRow("Total focus time", "${summary.totalFocusMinutes} min")
            SummaryRow("Total points", "${summary.totalPoints}")
            SummaryRow("Total sessions", "${summary.totalSessions}")
            SummaryRow("Completed sessions", "${summary.completedSessions}")
            SummaryRow("Stopped sessions", "${summary.stoppedSessions}")
            SummaryRow("Avg focus / day", "${summary.averageFocusMinutesPerDay} min")

            if (summary.bestDay != null) {
                val formatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)
                SummaryRow(
                    "Best day",
                    "${formatter.format(summary.bestDay)} (${summary.bestDayFocusMinutes} min)",
                )
            }
            SummaryRow(
                "Completion rate",
                "${summary.completionRatePercent}%",
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}