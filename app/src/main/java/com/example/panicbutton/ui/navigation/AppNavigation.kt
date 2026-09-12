package com.example.panicbutton.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.domain.model.PanicState
import com.example.panicbutton.data.local.RequestHistoryEntity
import com.example.panicbutton.ui.screens.*
import com.example.panicbutton.ui.theme.*

@Composable
fun AppNavigation(
    panicState: PanicState,
    users: List<MockUser>,
    history: List<RequestHistoryEntity>,
    historyFilter: String,
    onPanicPressed: () -> Unit,
    onCancelPanic: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onRetry: () -> Unit,
    onDone: () -> Unit,
    onToggleUserOnline: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val me = users.firstOrNull { it.isMe } ?: return

    val isPanicFlowActive = panicState !is PanicState.Idle

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(200))
            },
            label = "tab_content"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    panicState = panicState,
                    users = users,
                    onPanicPressed = onPanicPressed,
                    onToggleUserOnline = onToggleUserOnline
                )
                1 -> HistoryScreen(
                    history = history,
                    selectedFilter = historyFilter,
                    onFilterSelected = onFilterSelected
                )
                2 -> SettingsScreen(
                    me = me,
                    onToggleMeOnline = { onToggleUserOnline(me.id) }
                )
            }
        }

        AnimatedVisibility(
            visible = panicState is PanicState.Confirming ||
                    panicState is PanicState.Searching ||
                    panicState is PanicState.RequestSent ||
                    panicState is PanicState.AllDeclined,
            enter = fadeIn(tween(400)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(400)
            ),
            exit = fadeOut(tween(300)) + slideOutVertically(
                targetOffsetY = { it / 3 },
                animationSpec = tween(300)
            )
        ) {
            SearchingScreen(
                panicState = panicState,
                onCancel = onCancelPanic,
                onAccept = onAccept,
                onDecline = onDecline,
                onRetry = onRetry
            )
        }

        AnimatedVisibility(
            visible = panicState is PanicState.Accepted || panicState is PanicState.Confirmed,
            enter = fadeIn(tween(500)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(500)
            ),
            exit = fadeOut(tween(300))
        ) {
            ConfirmedScreen(
                panicState = panicState,
                me = me,
                onDone = onDone
            )
        }

        AnimatedVisibility(
            visible = !isPanicFlowActive,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            GlassBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Composable
private fun GlassBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        NavTab("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavTab("History", Icons.Filled.History, Icons.Outlined.History),
        NavTab("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(NavBarSurface)
            .border(1.dp, NavBarBorder, RoundedCornerShape(24.dp))
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                        contentDescription = tab.label,
                        tint = if (isSelected) TealMint else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) TealMint else TextMuted
                    )
                }
            }
        }
    }
}

private data class NavTab(
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)
