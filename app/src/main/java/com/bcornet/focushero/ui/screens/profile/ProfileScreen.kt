package com.bcornet.focushero.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bcornet.focushero.ui.theme.accentColorFor

@Composable
fun ProfileScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    uiState: ProfileUiState,

    // Appearance actions
    onThemeSelected: (ThemePreference) -> Unit = {},
    onAccentSelected: (AccentColorOption) -> Unit = {},
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
            )

            if (uiState.isLoading) {
                Text(
                    text = "Loading your profile...",
                    style = MaterialTheme.typography.bodyLarge,
                )
                return@Column
            }

            uiState.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyLarge,
                )
                return@Column
            }

            OverviewCard(uiState)
            ProgressCard(uiState)

            AchievementsHeader(
                unlocked = uiState.achievementsUnlockedCount,
                total = uiState.achievementsTotalCount,
            )

            AchievementsSection(
                title = "Unlocked",
                items = uiState.unlockedAchievements,
                isUnlockedSection = true,
            )

            AchievementsSection(
                title = "Locked",
                items = uiState.lockedAchievements,
                isUnlockedSection = false,
            )

            AppearanceCard(
                themePreference = uiState.themePreference,
                accent = uiState.accentColor,
                onThemeSelected = onThemeSelected,
                onAccentSelected = onAccentSelected,
            )
        }
    }
}

@Composable
private fun OverviewCard(uiState: ProfileUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatBlock(
                    label = "Total points",
                    value = "${uiState.totalPoints}",
                    modifier = Modifier.weight(1f),
                )
                StatBlock(
                    label = "Level",
                    value = "${uiState.currentLevel}",
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatBlock(
                    label = "Completed",
                    value = "${uiState.completedSessionsCount}",
                    modifier = Modifier.weight(1f),
                )
                StatBlock(
                    label = "Stopped",
                    value = "${uiState.stoppedSessionsCount}",
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatBlock(
                    label = "Total sessions",
                    value = "${uiState.totalSessionsCount}",
                    modifier = Modifier.weight(1f),
                )
                StatBlock(
                    label = "Focus time",
                    value = "${uiState.totalCompletedFocusMinutes} min",
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = "Completion rate: ${uiState.completionRatePercent}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProgressCard(uiState: ProfileUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

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
}

@Composable
private fun AchievementsHeader(
    unlocked: Int,
    total: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$unlocked / $total",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    HorizontalDivider()
}

@Composable
private fun AchievementsSection(
    title: String,
    items: List<AchievementUi>,
    isUnlockedSection: Boolean,
) {
    if (items.isEmpty()) return

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { ach ->
                AchievementRow(
                    achievement = ach,
                    unlocked = isUnlockedSection,
                )
            }
        }
    }
}

@Composable
private fun AchievementRow(
    achievement: AchievementUi,
    unlocked: Boolean,
) {
    val accent = if (unlocked) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant
    val endIcon = if (unlocked) Icons.Outlined.CheckCircle else Icons.Outlined.Lock
    val rowAlpha = if (unlocked) 1f else 0.55f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (unlocked) Icons.Outlined.CheckCircle else Icons.Outlined.Lock,
                contentDescription = null,
                tint = accent,
                modifier = Modifier,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = rowAlpha),
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = rowAlpha),
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = endIcon,
                contentDescription = null,
                tint = accent,
            )
        }
    }
}

@Composable
private fun AppearanceCard(
    themePreference: ThemePreference,
    accent: AccentColorOption,
    onThemeSelected: (ThemePreference) -> Unit,
    onAccentSelected: (AccentColorOption) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            // Theme selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Tune, contentDescription = null)
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            SegmentedThemeSelector(
                selected = themePreference,
                onSelected = onThemeSelected,
            )

            // Accent selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
                Text(
                    text = "Accent color",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            AccentSelector(
                selected = accent,
                onSelected = onAccentSelected,
            )
        }
    }
}

@Composable
private fun SegmentedThemeSelector(
    selected: ThemePreference,
    onSelected: (ThemePreference) -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ThemePreference.values().forEach { option ->
            val isSelected = option == selected
            val label = when (option) {
                ThemePreference.SYSTEM -> "System"
                ThemePreference.LIGHT -> "Light"
                ThemePreference.DARK -> "Dark"
            }

            OutlinedButton(
                onClick = { onSelected(option) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun AccentSelector(
    selected: AccentColorOption,
    onSelected: (AccentColorOption) -> Unit,
) {
    val options = remember {
        listOf(
            AccentColorOption.DEFAULT,
            AccentColorOption.BLUE,
            AccentColorOption.GREEN,
            AccentColorOption.ORANGE,
            AccentColorOption.PURPLE,
            AccentColorOption.PINK,
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val color = accentColorFor(option)
            val isSelected = option == selected

            Surface(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                color = color,
                tonalElevation = if (isSelected) 2.dp else 0.dp,
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.onSurface
                    )
                } else null,
                onClick = { onSelected(option) },
            ) {}
        }
    }

    Text(
        text = "Selected: ${selected.name.lowercase().replaceFirstChar { it.uppercase() }}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}