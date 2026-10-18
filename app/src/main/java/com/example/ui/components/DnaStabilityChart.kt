package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LabCyanAccent
import com.example.ui.theme.LabCyanGlow
import com.example.ui.theme.LabError
import com.example.ui.theme.LabOnSurface
import com.example.ui.theme.LabOnSurfaceVariant
import com.example.ui.theme.LabSecondary
import com.example.ui.theme.LabSuccess
import com.example.ui.theme.LabSurfaceContainer
import com.example.ui.theme.LabSurfaceContainerHigh
import com.example.ui.theme.LabSurfaceContainerLow
import com.example.ui.theme.LabSurfaceContainerLowest
import com.example.ui.theme.LabWarning
import kotlin.math.roundToInt

data class GeneticTrendDataPoint(
    val timeLabel: String,
    val stability: Float, // 0 to 100
    val fidelity: Float,  // 0 to 100
    val mutationEvent: String? = null
)

val sampleTrend1H = listOf(
    GeneticTrendDataPoint("00m", 92.4f, 88.0f),
    GeneticTrendDataPoint("10m", 93.1f, 89.2f),
    GeneticTrendDataPoint("20m", 89.5f, 85.0f, "SPLICE INJECTED"),
    GeneticTrendDataPoint("30m", 91.8f, 88.5f),
    GeneticTrendDataPoint("40m", 95.0f, 92.1f),
    GeneticTrendDataPoint("50m", 94.2f, 91.0f),
    GeneticTrendDataPoint("60m", 96.5f, 93.8f, "STABILIZED")
)

val sampleTrend6H = listOf(
    GeneticTrendDataPoint("T-6h", 84.0f, 80.5f),
    GeneticTrendDataPoint("T-5h", 86.5f, 82.0f),
    GeneticTrendDataPoint("T-4h", 78.2f, 74.0f, "CRYO FLUCTUATION"),
    GeneticTrendDataPoint("T-3h", 88.0f, 85.1f),
    GeneticTrendDataPoint("T-2h", 91.4f, 89.0f),
    GeneticTrendDataPoint("T-1h", 93.8f, 90.5f),
    GeneticTrendDataPoint("NOW", 96.5f, 93.8f, "OPTIMAL")
)

val sampleTrend24H = listOf(
    GeneticTrendDataPoint("00:00", 79.5f, 76.0f),
    GeneticTrendDataPoint("04:00", 82.1f, 79.0f),
    GeneticTrendDataPoint("08:00", 72.4f, 69.5f, "ION SURGE"),
    GeneticTrendDataPoint("12:00", 87.0f, 84.0f),
    GeneticTrendDataPoint("16:00", 92.5f, 89.2f, "CODON RESTORE"),
    GeneticTrendDataPoint("20:00", 94.0f, 91.5f),
    GeneticTrendDataPoint("24:00", 96.5f, 93.8f, "EQUILIBRIUM")
)

val sampleTrendAllGens = listOf(
    GeneticTrendDataPoint("GEN 0", 65.0f, 60.0f, "ANCESTRAL ROOT"),
    GeneticTrendDataPoint("GEN I", 71.5f, 68.0f),
    GeneticTrendDataPoint("GEN II", 79.0f, 75.2f, "AMPHIBIC ADAPT"),
    GeneticTrendDataPoint("GEN III", 86.4f, 83.0f),
    GeneticTrendDataPoint("GEN IV", 94.2f, 91.5f, "CRYONIX CURRENT"),
    GeneticTrendDataPoint("GEN V", 97.8f, 95.0f, "PROJECTED OMEGA")
)

enum class TimeRangeOption(val label: String) {
    ONE_HOUR("1H"),
    SIX_HOURS("6H"),
    TWENTY_FOUR_HOURS("24H"),
    ALL_GENERATIONS("ALL GENS")
}

@Composable
fun DnaStabilityTrendVisualizer(
    modifier: Modifier = Modifier,
    specimenCode: String = "SPECIMEN-X9"
) {
    var selectedRange by remember { mutableStateOf(TimeRangeOption.ONE_HOUR) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showFidelityLine by remember { mutableStateOf(true) }

    val dataPoints = when (selectedRange) {
        TimeRangeOption.ONE_HOUR -> sampleTrend1H
        TimeRangeOption.SIX_HOURS -> sampleTrend6H
        TimeRangeOption.TWENTY_FOUR_HOURS -> sampleTrend24H
        TimeRangeOption.ALL_GENERATIONS -> sampleTrendAllGens
    }

    // Default to latest point if none tapped
    val activePoint = selectedIndex?.let { dataPoints.getOrNull(it) } ?: dataPoints.last()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LabCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dna_stability_trend_visualizer"),
        backgroundColor = LabSurfaceContainerLow,
        borderColor = LabCyanAccent.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LabCyanAccent.copy(alpha = pulseGlow))
                        )
                        Text(
                            text = "GENETIC STABILITY TREND",
                            color = LabOnSurface,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "RECHARTS-STYLE TELEMETRY • $specimenCode",
                        color = LabOnSurfaceVariant,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Range Selector Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TimeRangeOption.values().forEach { option ->
                        val isSelected = option == selectedRange
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) LabCyanAccent else LabSurfaceContainerHigh)
                                .border(
                                    1.dp,
                                    if (isSelected) LabCyanAccent else Color(0x22849495),
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    selectedRange = option
                                    selectedIndex = null
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("trend_tab_${option.name.lowercase()}")
                        ) {
                            Text(
                                text = option.label,
                                color = if (isSelected) Color(0xFF00363A) else LabOnSurfaceVariant,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Key Metric Summary Readout Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(LabSurfaceContainerLowest)
                    .border(1.dp, Color(0x1AEBB2FF), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE POINT: ${activePoint.timeLabel}",
                        color = LabOnSurfaceVariant,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${activePoint.stability}%",
                            color = LabCyanAccent,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black
                        )
                        activePoint.mutationEvent?.let { event ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(LabSecondary.copy(alpha = 0.2f))
                                    .border(1.dp, LabSecondary.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = event,
                                    color = LabSecondary,
                                    fontSize = 7.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "FIDELITY",
                            color = LabOnSurfaceVariant,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${activePoint.fidelity}%",
                            color = LabSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "DRIFT",
                            color = LabOnSurfaceVariant,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "±0.3%",
                            color = LabSuccess,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Legend Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LabCyanAccent)
                        )
                        Text(
                            text = "Stability (%)",
                            color = LabCyanAccent,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clickable { showFidelityLine = !showFidelityLine }
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (showFidelityLine) LabSecondary else LabOnSurfaceVariant)
                        )
                        Text(
                            text = "Fidelity (%)",
                            color = if (showFidelityLine) LabSecondary else LabOnSurfaceVariant,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "Tap chart to inspect node",
                    color = LabOnSurfaceVariant,
                    fontSize = 8.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // The Canvas Line & Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LabSurfaceContainerLowest)
                    .border(1.dp, Color(0x1F849495), RoundedCornerShape(8.dp))
                    .pointerInput(dataPoints) {
                        detectTapGestures { offset ->
                            val widthPerSegment = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                            val index = (offset.x / widthPerSegment)
                                .roundToInt()
                                .coerceIn(0, dataPoints.size - 1)
                            selectedIndex = index
                        }
                    }
                    .pointerInput(dataPoints) {
                        detectDragGestures { change, _ ->
                            val widthPerSegment = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                            val index = (change.position.x / widthPerSegment)
                                .roundToInt()
                                .coerceIn(0, dataPoints.size - 1)
                            selectedIndex = index
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 12.dp)) {
                    val w = size.width
                    val h = size.height

                    val minVal = 50f
                    val maxVal = 100f
                    val range = maxVal - minVal

                    fun getY(value: Float): Float {
                        val normalized = (value - minVal) / range
                        return h - (normalized * h)
                    }

                    fun getX(index: Int): Float {
                        return index * (w / (dataPoints.size - 1).coerceAtLeast(1))
                    }

                    // 1. Draw horizontal grid lines & axis labels
                    val gridSteps = listOf(60f, 75f, 90f, 100f)
                    gridSteps.forEach { stepVal ->
                        val y = getY(stepVal)
                        drawLine(
                            color = Color(0x14849495),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    // 2. Draw critical threshold line at 70%
                    val critY = getY(70f)
                    drawLine(
                        color = LabError.copy(alpha = 0.35f),
                        start = Offset(0f, critY),
                        end = Offset(w, critY),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    )

                    // 3. Build smooth path for Stability
                    val stabilityPoints = dataPoints.mapIndexed { idx, pt ->
                        Offset(getX(idx), getY(pt.stability))
                    }

                    if (stabilityPoints.size >= 2) {
                        val path = Path()
                        val areaPath = Path()

                        path.moveTo(stabilityPoints.first().x, stabilityPoints.first().y)
                        areaPath.moveTo(stabilityPoints.first().x, h)
                        areaPath.lineTo(stabilityPoints.first().x, stabilityPoints.first().y)

                        for (i in 0 until stabilityPoints.size - 1) {
                            val p0 = stabilityPoints[i]
                            val p1 = stabilityPoints[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            areaPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }

                        areaPath.lineTo(stabilityPoints.last().x, h)
                        areaPath.close()

                        // Draw Area Gradient (Recharts style)
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    LabCyanAccent.copy(alpha = 0.38f),
                                    LabCyanAccent.copy(alpha = 0.01f)
                                )
                            ),
                            style = Fill
                        )

                        // Draw Main Line
                        drawPath(
                            path = path,
                            color = LabCyanAccent,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // 4. Build secondary line for Fidelity
                    if (showFidelityLine) {
                        val fidelityPoints = dataPoints.mapIndexed { idx, pt ->
                            Offset(getX(idx), getY(pt.fidelity))
                        }
                        if (fidelityPoints.size >= 2) {
                            val fedPath = Path()
                            fedPath.moveTo(fidelityPoints.first().x, fidelityPoints.first().y)
                            for (i in 0 until fidelityPoints.size - 1) {
                                val p0 = fidelityPoints[i]
                                val p1 = fidelityPoints[i + 1]
                                val cx = (p0.x + p1.x) / 2f
                                fedPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            }

                            drawPath(
                                path = fedPath,
                                color = LabSecondary.copy(alpha = 0.85f),
                                style = Stroke(
                                    width = 1.8.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
                                )
                            )
                        }
                    }

                    // 5. Draw data point dots & selection highlight
                    dataPoints.forEachIndexed { index, pt ->
                        val center = Offset(getX(index), getY(pt.stability))
                        val isSelected = (selectedIndex == index) || (selectedIndex == null && index == dataPoints.size - 1)

                        if (isSelected) {
                            // Vertical crosshair indicator line
                            drawLine(
                                color = LabCyanAccent.copy(alpha = 0.5f),
                                start = Offset(center.x, 0f),
                                end = Offset(center.x, h),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                            )

                            // Outer halo
                            drawCircle(
                                color = LabCyanAccent.copy(alpha = 0.25f),
                                radius = 9.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = LabCyanAccent,
                                radius = 5.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = Color(0xFF00363A),
                                radius = 2.5.dp.toPx(),
                                center = center
                            )
                        } else {
                            // Standard node dot
                            drawCircle(
                                color = Color(0xFF00363A),
                                radius = 3.5.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = LabCyanAccent,
                                radius = 2.5.dp.toPx(),
                                center = center
                            )
                        }

                        // Event annotation marker on chart
                        if (pt.mutationEvent != null) {
                            drawCircle(
                                color = LabSecondary,
                                radius = 2.dp.toPx(),
                                center = Offset(center.x, center.y - 8.dp.toPx())
                            )
                        }
                    }
                }
            }

            // X-Axis Time Labels Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dataPoints.forEachIndexed { index, pt ->
                    val isSelected = (selectedIndex == index) || (selectedIndex == null && index == dataPoints.size - 1)
                    Text(
                        text = pt.timeLabel,
                        color = if (isSelected) LabCyanAccent else LabOnSurfaceVariant,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
