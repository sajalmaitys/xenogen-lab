package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MutationLogEntity
import com.example.ui.theme.LabCardBorder
import com.example.ui.theme.LabCyanAccent
import com.example.ui.theme.LabCyanGlow
import com.example.ui.theme.LabError
import com.example.ui.theme.LabGlassSurface
import com.example.ui.theme.LabOnSurface
import com.example.ui.theme.LabOnSurfaceVariant
import com.example.ui.theme.LabSecondary
import com.example.ui.theme.LabSuccess
import com.example.ui.theme.LabSurfaceContainer
import com.example.ui.theme.LabSurfaceContainerHigh
import com.example.ui.theme.LabSurfaceContainerLow
import com.example.ui.theme.LabSurfaceContainerLowest
import com.example.ui.theme.LabVioletGlow
import com.example.ui.theme.LabWarning
import kotlin.math.sin

@Composable
fun LabCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    borderColor: Color = LabCardBorder,
    backgroundColor: Color = LabSurfaceContainerLow,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun RadialGauge(
    percentage: Float,
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    gaugeColor: Color = LabCyanAccent,
    gaugeBackground: Color = LabSurfaceContainerHigh,
    strokeWidth: Dp = 10.dp,
    size: Dp = 110.dp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val strokePx = strokeWidth.toPx()
                val radius = (this.size.minDimension - strokePx) / 2f
                val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)

                // Background track (270 degrees sweep)
                drawArc(
                    color = gaugeBackground,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Active progress arc
                val sweep = 270f * (percentage.coerceIn(0f, 100f) / 100f)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(gaugeColor, LabSecondary, gaugeColor),
                        center = centerOffset
                    ),
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = valueText,
                    color = LabOnSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = label,
                    color = LabOnSurfaceVariant,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun TraitMeter(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    barColor: Color = LabCyanAccent,
    suffix: String = "%"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = LabOnSurfaceVariant,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$value$suffix",
                color = LabOnSurface,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(LabSurfaceContainerHigh)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (value.coerceIn(0, 100) / 100f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "MUTATING" -> LabVioletGlow to LabSecondary
        "STABILIZED", "STABLE" -> Color(0x334ADE80) to LabSuccess
        "EVOLVING" -> LabCyanGlow to LabCyanAccent
        "ALERT" -> Color(0x33FFB4AB) to LabError
        else -> LabSurfaceContainerHigh to LabOnSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun AnimatedDnaHelix(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    pairCount: Int = 12
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dna_rotation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2*PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.height(height)) {
        val w = size.width
        val h = size.height
        val step = h / (pairCount + 1)
        val amplitude = w * 0.35f
        val centerX = w / 2f

        for (i in 0 until pairCount) {
            val y = (i + 1) * step
            val angle = phase + (i * 0.45f)
            val offset = sin(angle) * amplitude
            val leftX = centerX - offset
            val rightX = centerX + offset

            // Cross link strand
            drawLine(
                color = Color(0x4000DBE9),
                start = Offset(leftX, y),
                end = Offset(rightX, y),
                strokeWidth = 2f
            )

            // Left node (Cyan)
            drawCircle(
                color = LabCyanAccent,
                radius = 4f,
                center = Offset(leftX, y)
            )

            // Right node (Violet)
            drawCircle(
                color = LabSecondary,
                radius = 4f,
                center = Offset(rightX, y)
            )
        }
    }
}

@Composable
fun MutationLogList(
    logs: List<MutationLogEntity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MUTATION LOG",
                color = LabOnSurface,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "LIVE FEED",
                color = LabCyanAccent,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LabSurfaceContainerLowest)
                .border(1.dp, Color(0x22849495), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if (logs.isEmpty()) {
                Text(
                    text = "No mutation events recorded.",
                    color = LabOnSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(logs.take(15)) { log ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = log.timeLabel,
                                color = LabCyanAccent,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                            val tagColor = when (log.tag) {
                                "ALERT" -> LabError
                                "SPLICED" -> LabSecondary
                                "INFO" -> LabCyanAccent
                                else -> LabSuccess
                            }
                            Text(
                                text = "[${log.tag}]",
                                color = tagColor,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = log.message,
                                color = LabOnSurface,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
