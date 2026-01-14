package com.bcornet.focushero.ui.screens.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Divider
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
import com.bcornet.focushero.ui.components.FocusSessionCard

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
                is SessionsListItem.SessionItem -> FocusSessionCard(session = item.session)
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