package com.example.panicbutton.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.panicbutton.domain.model.MockUser
import com.example.panicbutton.ui.theme.*

@Composable
fun MiniMap(
    me: MockUser,
    helper: MockUser,
    distanceKm: Double,
    modifier: Modifier = Modifier
) {
    val meLabelColor = AvatarIndigo.copy(alpha = 0.9f).toArgb()
    val helperLabelColor = TealMint.copy(alpha = 0.9f).toArgb()
    val locationLabelColor = TextSecondary.toArgb()

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📍 Distance: ${"%.1f".format(distanceKm)} km",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val latRange = maxOf(
                    kotlin.math.abs(me.latitude - helper.latitude),
                    0.001
                )
                val lngRange = maxOf(
                    kotlin.math.abs(me.longitude - helper.longitude),
                    0.001
                )

                val padding = 60f
                val usableWidth = canvasWidth - padding * 2
                val usableHeight = canvasHeight - padding * 2

                val minLat = minOf(me.latitude, helper.latitude)
                val minLng = minOf(me.longitude, helper.longitude)

                val meX = padding + ((me.longitude - minLng) / lngRange * usableWidth).toFloat()
                val meY = padding + usableHeight - ((me.latitude - minLat) / latRange * usableHeight).toFloat()
                val helperX = padding + ((helper.longitude - minLng) / lngRange * usableWidth).toFloat()
                val helperY = padding + usableHeight - ((helper.latitude - minLat) / latRange * usableHeight).toFloat()

                drawLine(
                    color = TextMuted,
                    start = Offset(meX, meY),
                    end = Offset(helperX, helperY),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                )

                drawCircle(
                    color = AvatarIndigo,
                    radius = 14f,
                    center = Offset(meX, meY)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 22f,
                    center = Offset(meX, meY),
                    style = Stroke(width = 2f)
                )

                drawCircle(
                    color = TealMint,
                    radius = 14f,
                    center = Offset(helperX, helperY)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 22f,
                    center = Offset(helperX, helperY),
                    style = Stroke(width = 2f)
                )

                val nativeCanvas = drawContext.canvas.nativeCanvas

                val namePaint = Paint().apply {
                    isAntiAlias = true
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                val locationPaint = Paint().apply {
                    isAntiAlias = true
                    textSize = 22f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    color = locationLabelColor
                }

                val meLabelOffsetY = if (meY > helperY) 50f else -35f
                val helperLabelOffsetY = if (helperY > meY) 50f else -35f

                namePaint.color = meLabelColor
                namePaint.textAlign = Paint.Align.CENTER
                locationPaint.textAlign = Paint.Align.CENTER

                nativeCanvas.drawText(
                    "You",
                    meX,
                    meY + meLabelOffsetY,
                    namePaint
                )
                if (me.locationName.isNotEmpty()) {
                    nativeCanvas.drawText(
                        me.locationName,
                        meX,
                        meY + meLabelOffsetY + 24f,
                        locationPaint
                    )
                }

                namePaint.color = helperLabelColor
                nativeCanvas.drawText(
                    helper.name.split(" ").first(),
                    helperX,
                    helperY + helperLabelOffsetY,
                    namePaint
                )
                if (helper.locationName.isNotEmpty()) {
                    nativeCanvas.drawText(
                        helper.locationName,
                        helperX,
                        helperY + helperLabelOffsetY + 24f,
                        locationPaint
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawCircle(color = AvatarIndigo)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("You", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        if (me.locationName.isNotEmpty()) {
                            Text(
                                me.locationName,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawCircle(color = TealMint)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(helper.name, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        if (helper.locationName.isNotEmpty()) {
                            Text(
                                helper.locationName,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
