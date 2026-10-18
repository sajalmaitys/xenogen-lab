package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LabCard
import com.example.ui.components.RadialGauge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TraitMeter
import com.example.ui.theme.LabCyanAccent
import com.example.ui.theme.LabError
import com.example.ui.theme.LabOnSurface
import com.example.ui.theme.LabOnSurfaceVariant
import com.example.ui.theme.LabSecondary
import com.example.ui.theme.LabSuccess
import com.example.ui.theme.LabSurfaceContainerHigh
import com.example.ui.theme.LabSurfaceContainerLow
import com.example.ui.theme.LabSurfaceContainerLowest
import com.example.ui.theme.LabWarning
import com.example.ui.viewmodel.XenoGenViewModel

@Composable
fun SimulationScreen(
    viewModel: XenoGenViewModel,
    modifier: Modifier = Modifier
) {
    val integrity by viewModel.simulationIntegrity.collectAsState()
    val pulse by viewModel.simulationPulse.collectAsState()
    val isStabilizing by viewModel.isSimulationStabilizing.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LabSurfaceContainerLowest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simulation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EXOPLANETARY ENVIRONMENT",
                    color = LabCyanAccent,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "KEPLER-16B SIMULATION",
                    color = LabOnSurface,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "CIRCUMBINARY ORBIT • SECTOR K16-DELTA",
                    color = LabOnSurfaceVariant,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            StatusBadge(status = "ACTIVE")
        }

        // Climate Projection Chart Card
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow,
            borderColor = LabCyanAccent.copy(alpha = 0.35f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DIURNAL CLIMATE PROJECTION",
                        color = LabOnSurface,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LabCyanAccent))
                            Text(text = "TEMP", color = LabCyanAccent, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LabSecondary))
                            Text(text = "PRESSURE", color = LabSecondary, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Canvas Chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(LabSurfaceContainerLowest)
                        .padding(8.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    for (i in 1..3) {
                        val y = h * (i / 4f)
                        drawLine(
                            color = Color(0x15849495),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // Temperature Path (Cyan)
                    val tempPath = Path().apply {
                        moveTo(0f, h * 0.7f)
                        cubicTo(w * 0.25f, h * 0.9f, w * 0.4f, h * 0.2f, w * 0.6f, h * 0.25f)
                        cubicTo(w * 0.8f, h * 0.3f, w * 0.9f, h * 0.8f, w, h * 0.65f)
                    }
                    drawPath(
                        path = tempPath,
                        color = LabCyanAccent,
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )

                    // Pressure Path (Violet)
                    val pressurePath = Path().apply {
                        moveTo(0f, h * 0.4f)
                        cubicTo(w * 0.3f, h * 0.3f, w * 0.5f, h * 0.7f, w * 0.7f, h * 0.6f)
                        cubicTo(w * 0.85f, h * 0.5f, w * 0.95f, h * 0.45f, w, h * 0.4f)
                    }
                    drawPath(
                        path = pressurePath,
                        color = LabSecondary,
                        style = Stroke(width = 2f, cap = StrokeCap.Round)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "00:00", color = LabOnSurfaceVariant, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "06:00", color = LabOnSurfaceVariant, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "12:00 (PERIHELION)", color = LabCyanAccent, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "18:00", color = LabOnSurfaceVariant, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "24:00", color = LabOnSurfaceVariant, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Survival Matrix
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SPECIMEN SURVIVAL VIABILITY MATRIX",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                TraitMeter(label = "SPECIMEN-X9 (OBSIDIAN STALKER)", value = 92, barColor = LabCyanAccent)
                TraitMeter(label = "CRYONIX (#842 ENDOTHERM)", value = 82, barColor = LabSecondary)
                TraitMeter(label = "AEGIS-4B (HYDROGEN SHIELD)", value = 34, barColor = Color(0xFFFF5252))
                TraitMeter(label = "OBSIDION-REX (TECTONIC)", value = 88, barColor = LabCyanAccent)
            }
        }

        // Planetary Hazards
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ACTIVE PLANETARY HAZARDS",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                HazardItem(name = "ACID CONDENSATION STORM", level = "pH 1.2 • CRITICAL", isDanger = true)
                HazardItem(name = "BINARY SOLAR HEAT SPIKE", level = "+240°C • PEAK", isDanger = true)
                HazardItem(name = "CRUSTAL SEISMIC TREMORS", level = "MAG 4.2 • MODERATE", isDanger = false)
            }
        }

        // Atmospheric Scrubbers & Vitals
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RadialGauge(
                        percentage = integrity.toFloat(),
                        label = "INTEGRITY",
                        valueText = "$integrity%",
                        gaugeColor = LabCyanAccent,
                        size = 90.dp
                    )

                    Column(
                        modifier = Modifier.weight(1f).padding(start = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "STASIS VITALS",
                            color = LabCyanAccent,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "SPECIMEN PULSE: $pulse BPM",
                            color = LabOnSurface,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "DNA REPAIR: CATALYTIC ACTIVE",
                            color = LabSuccess,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "THERMAL STRESS: REGULATED",
                            color = LabOnSurfaceVariant,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Button(
                    onClick = { viewModel.stabilizeAtmosphere() },
                    enabled = !isStabilizing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("stabilize_atmosphere_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabCyanAccent,
                        contentColor = Color(0xFF00363A)
                    )
                ) {
                    if (isStabilizing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF00363A),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "ACTIVATE BIO-SCRUBBERS",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HazardItem(name: String, level: String, isDanger: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LabSurfaceContainerLowest)
            .border(1.dp, if (isDanger) Color(0x33FFB4AB) else Color(0x22849495), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = LabOnSurface,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = level,
            color = if (isDanger) LabError else LabWarning,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
