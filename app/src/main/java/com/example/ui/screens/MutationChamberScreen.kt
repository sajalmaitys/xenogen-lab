package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.LabCard
import com.example.ui.components.MutationLogList
import com.example.ui.components.RadialGauge
import com.example.ui.components.StatusBadge
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
import com.example.ui.theme.LabVioletGlow
import com.example.ui.viewmodel.XenoGenViewModel

@Composable
fun MutationChamberScreen(
    viewModel: XenoGenViewModel,
    modifier: Modifier = Modifier
) {
    val chamberCode by viewModel.chamberSpecimenCode.collectAsState()
    val specimens by viewModel.allSpecimens.collectAsState()
    val logs by viewModel.mutationLogs.collectAsState()

    val aggression by viewModel.neuralAggression.collectAsState()
    val luminescence by viewModel.bioLuminescence.collectAsState()
    val dermal by viewModel.dermalHardening.collectAsState()

    val isExecuting by viewModel.isExecutingMutation.collectAsState()
    val statusBanner by viewModel.mutationStatusBanner.collectAsState()

    var showPurgeConfirmDialog by remember { mutableStateOf(false) }
    var showEnlargedView by remember { mutableStateOf(false) }
    var showSnapshotFeedback by remember { mutableStateOf(false) }

    val currentSpecimen = specimens.find { it.specimenCode == chamberCode }
        ?: specimens.find { it.specimenCode == "ALPHA-007" }
        ?: specimens.firstOrNull()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_radiation")
    val radiationPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rad_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LabSurfaceContainerLowest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Banner Notification
        AnimatedVisibility(visible = statusBanner != null) {
            statusBanner?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF00363A))
                        .border(1.dp, LabCyanAccent, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🧬 $msg",
                        color = LabCyanAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Chamber Header: Specimen selector & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CONTAINMENT FIELD 03",
                    color = LabSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SPECIMEN: ${currentSpecimen?.specimenCode ?: chamberCode}",
                    color = LabOnSurface,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = currentSpecimen?.commonName ?: "Alpha Hive Construct",
                    color = LabCyanAccent,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            StatusBadge(status = currentSpecimen?.status ?: "MUTATING")
        }

        // Energy Stability & Core Diagnostics Row
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RadialGauge(
                    percentage = 78f,
                    label = "STABILITY",
                    valueText = "78%",
                    gaugeColor = LabCyanAccent,
                    size = 90.dp
                )

                Column(
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DiagnosticItem(label = "THERMAL CORE", value = "42.8°C", isWarning = false)
                    DiagnosticItem(label = "ION BUFFER", value = "OPTIMAL", isWarning = false)
                    DiagnosticItem(label = "RAD FLUX", value = "0.04 mSv/h", isWarning = false)
                    DiagnosticItem(label = "CRYO-LOCK", value = "ENGAGED", isWarning = false)
                }
            }
        }

        // Glass Chamber Viewport
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow,
            borderColor = LabSecondary.copy(alpha = 0.35f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Viewport Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE CHAMBER VIEWPORT",
                        color = LabOnSurfaceVariant,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LabSurfaceContainerHigh)
                                .clickable { showSnapshotFeedback = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("snapshot_button")
                        ) {
                            Text(
                                text = "📸 SNAPSHOT",
                                color = LabOnSurface,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LabSurfaceContainerHigh)
                                .clickable { showEnlargedView = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("enlarge_button")
                        ) {
                            Text(
                                text = "🔍 ENLARGE",
                                color = LabCyanAccent,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The Mutation Chamber Tank Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF090B10),
                                    Color(0xFF13101C),
                                    Color(0xFF07090E)
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            LabSecondary.copy(alpha = radiationPulse),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentSpecimen?.imageUrl ?: "https://lh3.googleusercontent.com/aida/ADBb0uiZTlaCg5Wvz_nwytR5G-PBqD-d_3B_rvEF8BApXZ_pS2ksFRV23dh7FAI7A5Kdg3DkkO8vXyMoR6cJqjG4agTSgNfBL05SDljLQ_TSgFFoLZ6nhqQo9tU9vyDb3lhvQaW2EehNTQPK1TgXF6TyuKHdPbc5VemcThVTO9b1Gwto-3A28yMUHHin9e32kMUAT_P1uNe-wR5dh44eHwNNzb7FuKdJkEBWJJ7jFstDd7sW9o6ftwft39K88A")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Specimen in Mutation Chamber",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(210.dp)
                            .shadow(16.dp, CircleShape, spotColor = LabSecondary)
                    )

                    // Overlay HUD Tags
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color(0x99000000), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0x33EBB2FF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SPLICE: ALPHA-PRIME",
                            color = LabSecondary,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color(0x99000000), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0x3300DBE9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CONTAINMENT: SEALED",
                            color = LabCyanAccent,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mutation Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(LabSurfaceContainerLowest)
                        .border(1.dp, Color(0x1AEBB2FF), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Current splice: Alpha-Prime. Resulting in enhanced metabolic rate (+15%) and thermal resistance to extreme subterranean pressures.",
                        color = LabOnSurfaceVariant,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Genetic Modifiers Interactive Sliders
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow,
            borderColor = LabCyanAccent.copy(alpha = 0.35f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "GENETIC MODIFIER MATRIX",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                // Slider 1: Neural Aggression
                ModifierSlider(
                    label = "NEURAL AGGRESSION",
                    value = aggression,
                    onValueChange = { viewModel.setNeuralAggression(it) },
                    activeColor = Color(0xFFFF5252),
                    testTag = "aggression_slider"
                )

                // Slider 2: Bio-Luminescence Intensity
                ModifierSlider(
                    label = "BIO-LUMINESCENCE INTENSITY",
                    value = luminescence,
                    onValueChange = { viewModel.setBioLuminescence(it) },
                    activeColor = LabCyanAccent,
                    testTag = "luminescence_slider"
                )

                // Slider 3: Dermal Hardening
                ModifierSlider(
                    label = "DERMAL HARDENING",
                    value = dermal,
                    onValueChange = { viewModel.setDermalHardening(it) },
                    activeColor = LabSecondary,
                    testTag = "dermal_slider"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Action Buttons: Execute Mutation & Emergency Purge
                ElevatedButton(
                    onClick = { viewModel.executeMutation() },
                    enabled = !isExecuting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("execute_mutation_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = LabCyanAccent,
                        contentColor = Color(0xFF00363A)
                    )
                ) {
                    if (isExecuting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF00363A),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "APPLYING SPLICING SEQUENCE...",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "⚡ EXECUTE MUTATION",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showPurgeConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("emergency_purge_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LabError
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(LabError, LabError))
                    )
                ) {
                    Text(
                        text = "⚠️ EMERGENCY PURGE",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Live Mutation Logs
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            MutationLogList(logs = logs)
        }
    }

    // Emergency Purge Confirmation Dialog
    if (showPurgeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeConfirmDialog = false },
            title = {
                Text(
                    text = "CONFIRM EMERGENCY PURGE",
                    color = LabError,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                Text(
                    text = "Warning: This action will neutralize the active radioactive splice and reset genetic modifiers for Specimen $chamberCode to safe baselines.",
                    color = LabOnSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emergencyPurge()
                        showPurgeConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LabError)
                ) {
                    Text(
                        text = "PURGE",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeConfirmDialog = false }) {
                    Text(
                        text = "CANCEL",
                        color = LabOnSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            containerColor = LabSurfaceContainer
        )
    }

    // Enlarged Specimen Viewport Dialog
    if (showEnlargedView) {
        Dialog(onDismissRequest = { showEnlargedView = false }) {
            LabCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                backgroundColor = LabSurfaceContainerLowest,
                borderColor = LabCyanAccent
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ENLARGED OPTICAL FEED • $chamberCode",
                            color = LabCyanAccent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "✕",
                            color = LabOnSurfaceVariant,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { showEnlargedView = false }
                                .padding(4.dp)
                        )
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentSpecimen?.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Enlarged Specimen",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    )

                    Text(
                        text = "Sub-micron optical scan verified. Bio-luminescence active at 1.4 THz.",
                        color = LabOnSurfaceVariant,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    // Snapshot Feedback Dialog
    if (showSnapshotFeedback) {
        AlertDialog(
            onDismissRequest = { showSnapshotFeedback = false },
            title = {
                Text(
                    text = "SPECIMEN TELEMETRY SNAPSHOT",
                    color = LabCyanAccent,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "High-definition biometric frame recorded and archived to local research log under ID #SNAP-${chamberCode}-${System.currentTimeMillis() % 10000}.",
                    color = LabOnSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showSnapshotFeedback = false }) {
                    Text(
                        text = "ACKNOWLEDGE",
                        color = LabCyanAccent,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            containerColor = LabSurfaceContainer
        )
    }
}

@Composable
private fun DiagnosticItem(label: String, value: String, isWarning: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = LabOnSurfaceVariant,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = if (isWarning) LabError else LabSuccess,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ModifierSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = LabOnSurfaceVariant,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${value.toInt()}%",
                color = LabOnSurface,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = LabSurfaceContainerHigh
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
