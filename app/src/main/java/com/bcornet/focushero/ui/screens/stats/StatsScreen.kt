package com.bcornet.focushero.ui.screens.stats


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import kotlin.math.roundToInt

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
                text = "Stats", style = MaterialTheme.typography.headlineLarge
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

            DailyFocusSection(
                daily = uiState.dailyFocusMinutes,
            )

            DailyPointsSection(
                daily = uiState.dailyPoints,
            )

            DailyOutcomesSection(
                daily = uiState.dailyOutcomeCounts,
            )

            WeeklyFocusSection(
                weekly = uiState.weeklyFocusMinutes,
            )
        }
    }
}

@Composable
private fun RangeSelector(
    selected: StatsRange,
    onSelected: (StatsRange) -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatsRange.values().forEach { range ->
            val isSelected = range == selected

            OutlinedButton(
                onClick = { onSelected(range) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(
                    text = range.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
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

            val total = (summary.completedSessions + summary.stoppedSessions).coerceAtLeast(0)
            val progress =
                if (total == 0) 0f else summary.completedSessions.toFloat() / total.toFloat()

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { progress.coerceIn(0f, 1f) },
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

@Composable
private fun rememberFixedChartInteraction() = Pair(
    rememberVicoScrollState(scrollEnabled = false),
    rememberVicoZoomState(
        zoomEnabled = false,
        initialZoom = Zoom.Content,
        minZoom = Zoom.Content,
        maxZoom = Zoom.Content,
    ),
)

private fun labelSpacingFor(count: Int): Int =
    when {
        count <= 7 -> 1
        count <= 14 -> 2
        else -> 5
    }

@Composable
private fun DailyFocusSection(
    daily: List<DailyValue>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Daily focus time",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        if (daily.isEmpty()) {
            Text(
                text = "No focus data in this range.",
                style = MaterialTheme.typography.bodyLarge,
            )
            return@Column
        }

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
                // Compact day labels
                val labelFormatter = remember {
                    DateTimeFormatter.ofPattern("EE d", Locale.US)
                }
                val labels = remember(daily) {
                    daily.map { it.date.format(labelFormatter) }
                }

                val modelProducer = remember { CartesianChartModelProducer() }
                val (scrollState, zoomState) = rememberFixedChartInteraction()

                LaunchedEffect(daily) {
                    modelProducer.runTransaction {
                        columnSeries {
                            series(daily.map { it.value })
                        }
                    }
                }

                val bottomAxisFormatter = remember(labels) {
                    CartesianValueFormatter { _, x, _ ->
                        val idx = x.roundToInt()
                        labels.getOrNull(idx) ?: "_"
                    }
                }

                CartesianChartHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = bottomAxisFormatter,
                            itemPlacer = remember(daily) {
                                HorizontalAxis.ItemPlacer.aligned(
                                    spacing = { labelSpacingFor(daily.size) }
                                )
                            },
                        ),
                    ),
                    modelProducer = modelProducer,
                    scrollState = scrollState,
                    zoomState = zoomState,
                )
            }
        }
    }
}

@Composable
private fun DailyPointsSection(
    daily: List<DailyValue>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Daily points",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        if (daily.isEmpty()) {
            Text(
                text = "No points data in this range.",
                style = MaterialTheme.typography.bodyLarge,
            )
            return@Column
        }

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
                val labelFormatter = remember {
                    DateTimeFormatter.ofPattern("EE d", Locale.US)
                }
                val labels = remember(daily) {
                    daily.map { it.date.format(labelFormatter) }
                }

                val modelProducer = remember { CartesianChartModelProducer() }
                val (scrollState, zoomState) = rememberFixedChartInteraction()

                LaunchedEffect(daily) {
                    modelProducer.runTransaction {
                        // x = index
                        // y = points
                        lineSeries {
                            series(daily.map { it.value })
                        }
                    }
                }

                val bottomAxisFormatter = remember(labels) {
                    CartesianValueFormatter { _, x, _ ->
                        val idx = x.roundToInt()
                        labels.getOrNull(idx) ?: "_"
                    }
                }

                CartesianChartHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = bottomAxisFormatter,
                            itemPlacer = remember(daily) {
                                HorizontalAxis.ItemPlacer.aligned(
                                    spacing = { labelSpacingFor(daily.size) }
                                )
                            },
                        ),
                    ),
                    modelProducer = modelProducer,
                    scrollState = scrollState,
                    zoomState = zoomState,
                )
            }
        }
    }
}

@Composable
private fun completedColor() = androidx.compose.ui.graphics.Color(0xFF22C55E)

@Composable
private fun stoppedColor() = androidx.compose.ui.graphics.Color(0xFFF97316)

@Composable
private fun DailyOutcomesSection(
    daily: List<DailyOutcomeCounts>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Completed vs stopped",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        if (daily.isEmpty()) {
            Text(
                text = "No outcome data in this range.",
                style = MaterialTheme.typography.bodyLarge,
            )
            return@Column
        }

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
                val labelFormatter = remember {
                    DateTimeFormatter.ofPattern("EE d", Locale.US)
                }
                val labels = remember(daily) {
                    daily.map { it.date.format(labelFormatter) }
                }

                val modelProducer = remember { CartesianChartModelProducer() }
                val (scrollState, zoomState) = rememberFixedChartInteraction()

                LaunchedEffect(daily) {
                    modelProducer.runTransaction {
                        columnSeries {
                            // Serie 0 = completed
                            series(daily.map { it.completedCount })
                            // Serie 1 = stopped
                            series(daily.map { it.stoppedCount })
                        }
                    }
                }

                val bottomAxisFormatter = remember(labels) {
                    CartesianValueFormatter { _, x, _ ->
                        val idx = x.roundToInt()
                        labels.getOrNull(idx) ?: "—"
                    }
                }

                OutcomesLegend()

                val completed = completedColor()
                val stopped = stoppedColor()

                val vicoTheme = rememberM3VicoTheme(
                    columnCartesianLayerColors = listOf(completed, stopped),
                )

                ProvideVicoTheme(vicoTheme) {
                    CartesianChartHost(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        chart = rememberCartesianChart(
                            rememberColumnCartesianLayer(
                                mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                            ),
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                valueFormatter = bottomAxisFormatter,
                                itemPlacer = remember(daily) {
                                    HorizontalAxis.ItemPlacer.aligned(
                                        spacing = { labelSpacingFor(daily.size) }
                                    )
                                },
                            ),
                        ),
                        modelProducer = modelProducer,
                        scrollState = scrollState,
                        zoomState = zoomState,
                    )
                }
            }
        }
    }
}


@Composable
private fun WeeklyFocusSection(
    weekly: List<WeeklyValue>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Weekly focus overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        if (weekly.isEmpty()) {
            Text(
                text = "No weekly data available.",
                style = MaterialTheme.typography.bodyLarge,
            )
            return@Column
        }

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
                val labelFormatter = remember {
                    DateTimeFormatter.ofPattern("MMM d", Locale.US) // e.g., "Jan 8"
                }

                val labels = remember(weekly) {
                    weekly.map { w ->
                        // Label as "Jan 8" (week start)
                        w.weekStart.format(labelFormatter)
                    }
                }

                val modelProducer = remember { CartesianChartModelProducer() }
                val (scrollState, zoomState) = rememberFixedChartInteraction()

                LaunchedEffect(weekly) {
                    modelProducer.runTransaction {
                        columnSeries {
                            series(weekly.map { it.focusMinutes })
                        }
                    }
                }

                val bottomAxisFormatter = remember(labels) {
                    CartesianValueFormatter { _, x, _ ->
                        val idx = x.roundToInt()
                        labels.getOrNull(idx) ?: "_"
                    }
                }

                CartesianChartHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = bottomAxisFormatter,
                            itemPlacer = remember {
                                HorizontalAxis.ItemPlacer.aligned(spacing = { 1 })
                            },
                        ),
                    ),
                    modelProducer = modelProducer,
                    scrollState = scrollState,
                    zoomState = zoomState,
                )
            }
        }
    }
}

@Composable
private fun OutcomesLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendItem(label = "Completed", color = completedColor())
        LegendItem(label = "Stopped", color = stoppedColor())
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}