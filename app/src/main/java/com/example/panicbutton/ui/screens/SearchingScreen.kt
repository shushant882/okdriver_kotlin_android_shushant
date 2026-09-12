package com.example.panicbutton.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.domain.model.PanicState
import com.example.panicbutton.ui.components.*
import com.example.panicbutton.ui.theme.*

@Composable
fun SearchingScreen(
    panicState: PanicState,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientMid, GradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = panicState,
            transitionSpec = {
                fadeIn(tween(400)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(400)
                ) togetherWith fadeOut(tween(300))
            },
            label = "searching_content"
        ) { state ->
            when (state) {
                is PanicState.Confirming -> ConfirmingContent(
                    secondsLeft = state.secondsLeft,
                    onCancel = onCancel
                )
                is PanicState.Searching -> SearchingContent()
                is PanicState.RequestSent -> RequestSentContent(
                    helper = state.helper,
                    distanceKm = state.distanceKm,
                    secondsLeft = state.secondsLeft,
                    onAccept = onAccept,
                    onDecline = onDecline
                )
                is PanicState.AllDeclined -> AllDeclinedContent(onRetry = onRetry)
                else -> {  }
            }
        }
    }
}

@Composable
private fun ConfirmingContent(
    secondsLeft: Int,
    onCancel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "🆘",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sending SOS",
            style = MaterialTheme.typography.headlineMedium,
            color = AlertRed,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sending help request in...",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(32.dp))

        CountdownIndicator(
            secondsLeft = secondsLeft,
            totalSeconds = 5,
            accentColor = AlertRed
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = GlassSurface,
                contentColor = TextPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(52.dp)
        ) {
            Text(
                text = "Cancel",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SearchingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "searching_anim")
    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "dot1"
    )
    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = 200), RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = 400), RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        PanicButtonComposable(
            isActive = true,
            onClick = {},
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Searching for help",
            style = MaterialTheme.typography.headlineMedium,
            color = AmberOrange,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("●", color = AmberOrange.copy(alpha = dotAlpha1), fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("●", color = AmberOrange.copy(alpha = dotAlpha2), fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("●", color = AmberOrange.copy(alpha = dotAlpha3), fontSize = 20.sp)
        }
    }
}

@Composable
private fun RequestSentContent(
    helper: MockUser,
    distanceKm: Double,
    secondsLeft: Int,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Request Sent",
            style = MaterialTheme.typography.titleLarge,
            color = AmberOrange,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(
                    initials = helper.initials,
                    color = helper.displayColor,
                    size = 72.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = helper.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${"%.1f".format(distanceKm)} km away",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                CountdownIndicator(
                    secondsLeft = secondsLeft,
                    totalSeconds = 8,
                    accentColor = AmberOrange
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Waiting for ${helper.name.split(" ").first()}... ${secondsLeft}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = AmberOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Simulate ${helper.name.split(" ").first()}'s response:",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreen.copy(alpha = 0.8f),
                    contentColor = DeepNavy
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("✅ Yes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Button(
                onClick = onDecline,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AlertRed.copy(alpha = 0.8f),
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("❌ No", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun AllDeclinedContent(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "😔",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No One Available",
            style = MaterialTheme.typography.headlineMedium,
            color = MutedGrey,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "All nearby helpers are unavailable right now.\nPlease try again.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberOrange,
                contentColor = DeepNavy
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(52.dp)
        ) {
            Text(
                text = "Retry",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
