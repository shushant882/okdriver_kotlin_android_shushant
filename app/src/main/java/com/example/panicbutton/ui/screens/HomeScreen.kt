package com.example.panicbutton.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.domain.model.PanicState
import com.example.panicbutton.ui.components.GlassCard
import com.example.panicbutton.ui.components.PanicButtonComposable
import com.example.panicbutton.ui.components.UserAvatar
import com.example.panicbutton.ui.theme.*

@Composable
fun HomeScreen(
    panicState: PanicState,
    users: List<MockUser>,
    onPanicPressed: () -> Unit,
    onToggleUserOnline: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = panicState !is PanicState.Idle

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientMid, GradientEnd)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 60.dp, bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "Panic Button",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Emergency Help Request",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                StatusChip(panicState)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                PanicButtonComposable(
                    isActive = isActive,
                    onClick = onPanicPressed
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(
                    visible = !isActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Tap to request help",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby People",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    val onlineCount = users.count { it.isOnline && !it.isMe }
                    Text(
                        text = "$onlineCount online",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnlineGreen
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(
                items = users.filter { !it.isMe },
                key = { it.id }
            ) { user ->
                UserListItem(
                    user = user,
                    onToggleOnline = { onToggleUserOnline(user.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatusChip(panicState: PanicState) {
    val (text, color) = when (panicState) {
        is PanicState.Idle -> "Ready" to TextMuted
        is PanicState.Confirming -> "Confirming..." to AmberOrange
        is PanicState.Searching -> "Searching..." to AmberOrange
        is PanicState.RequestSent -> "Request Sent" to AmberOrange
        is PanicState.Accepted -> "Accepted!" to TealMint
        is PanicState.Confirmed -> "Help Confirmed" to EmeraldGreen
        is PanicState.AllDeclined -> "No Response" to MutedGrey
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun UserListItem(
    user: MockUser,
    onToggleOnline: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                initials = user.initials,
                color = user.displayColor,
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (user.isOnline) OnlineGreen else OfflineGrey)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (user.isOnline) "Online" else "Offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (user.isOnline) OnlineGreen else MutedGrey
                    )
                }
            }

            Switch(
                checked = user.isOnline,
                onCheckedChange = { onToggleOnline() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnlineGreen,
                    checkedTrackColor = OnlineGreen.copy(alpha = 0.3f),
                    uncheckedThumbColor = MutedGrey,
                    uncheckedTrackColor = MutedGrey.copy(alpha = 0.2f)
                ),
                modifier = Modifier.size(width = 46.dp, height = 26.dp)
            )
        }
    }
}
