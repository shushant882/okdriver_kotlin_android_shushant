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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.panicbutton.data.mock.MockUserDataSource
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.domain.model.PanicState
import com.example.panicbutton.ui.components.*
import com.example.panicbutton.ui.theme.*

@Composable
fun ConfirmedScreen(
    panicState: PanicState,
    me: MockUser,
    onDone: () -> Unit,
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
                fadeIn(tween(500)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(500, easing = EaseOutBack)
                ) togetherWith fadeOut(tween(300))
            },
            label = "confirmed_content"
        ) { state ->
            when (state) {
                is PanicState.Accepted -> AcceptedContent(helper = state.helper)
                is PanicState.Confirmed -> ConfirmedContent(
                    helper = state.helper,
                    distanceKm = state.distanceKm,
                    etaMinutes = state.etaMinutes,
                    me = me,
                    onDone = onDone
                )
                else -> {  }
            }
        }
    }
}

@Composable
private fun AcceptedContent(helper: MockUser) {
    val infiniteTransition = rememberInfiniteTransition(label = "accepted_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "accepted_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "✅",
            fontSize = 72.sp,
            modifier = Modifier.scale(scale)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Help Accepted!",
            style = MaterialTheme.typography.headlineLarge,
            color = EmeraldGreen,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${helper.name} is on the way",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}

@Composable
private fun ConfirmedContent(
    helper: MockUser,
    distanceKm: Double,
    etaMinutes: Int,
    me: MockUser,
    onDone: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Help is Coming!",
            style = MaterialTheme.typography.headlineMedium,
            color = EmeraldGreen,
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
                    size = 80.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = helper.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip(
                        label = "ETA",
                        value = "$etaMinutes min",
                        color = TealMint
                    )
                    InfoChip(
                        label = "Distance",
                        value = "${"%.1f".format(distanceKm)} km",
                        color = AmberOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        MiniMap(
            me = me,
            helper = helper,
            distanceKm = distanceKm
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(
                containerColor = TealMint,
                contentColor = DeepNavy
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Done",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun InfoChip(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}
