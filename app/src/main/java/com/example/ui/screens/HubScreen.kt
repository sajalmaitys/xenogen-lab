package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.MutationLogEntity
import com.example.data.SpecimenEntity
import com.example.ui.components.AnimatedDnaHelix
import com.example.ui.components.DnaStabilityTrendVisualizer
import com.example.ui.components.LabCard
import com.example.ui.components.MutationLogList
import com.example.ui.components.StatusBadge
import com.example.ui.components.TraitMeter
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
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.XenoGenViewModel

@Composable
fun HubScreen(
    viewModel: XenoGenViewModel,
    modifier: Modifier = Modifier
) {
    val dnaSeq by viewModel.dnaSequence.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val logs by viewModel.mutationLogs.collectAsState()
    val specimens by viewModel.allSpecimens.collectAsState()

    val currentSpecimen = specimens.find { it.specimenCode == "SPECIMEN-X9" }
        ?: specimens.firstOrNull()

    HubContent(
        currentSpecimen = currentSpecimen,
        dnaSeq = dnaSeq,
        isGenerating = isGenerating,
        logs = logs,
        onGenerateCreature = { viewModel.generateNewCreature() },
        modifier = modifier
    )
}

@Composable
fun HubContent(
    currentSpecimen: SpecimenEntity?,
    dnaSeq: String,
    isGenerating: Boolean,
    logs: List<MutationLogEntity>,
    onGenerateCreature: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Floating tank animation
    val infiniteTransition = rememberInfiniteTransition(label = "creature_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y_offset"
    )

    val tankGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tank_glow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LabSurfaceContainerLowest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top status headline
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CONTAINMENT SECTOR 4",
                    color = LabCyanAccent,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SPECIMEN-X9 SYNTHESIS",
                    color = LabOnSurface,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }
            StatusBadge(status = currentSpecimen?.status ?: "MUTATING")
        }

        // Central Incubation Tank Viewport
        LabCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("incubation_tank_card"),
            backgroundColor = LabSurfaceContainerLow,
            borderColor = LabCyanAccent.copy(alpha = 0.35f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tank Header HUD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INCUBATION CHAMBER • TANK #09",
                        color = LabOnSurfaceVariant,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "TEMP: -18.4°C [CRYO]",
                        color = LabCyanAccent,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // The Specimen Tank Viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF070B10),
                                    Color(0xFF0F1722),
                                    Color(0xFF05080C)
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            LabCyanAccent.copy(alpha = tankGlow),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Glass reflection / bubbles effect
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                // Draw subtle laboratory grid and scanlines
                                val step = 20.dp.toPx()
                                var y = 0f
                                while (y < size.height) {
                                    drawLine(
                                        color = Color(0x0A00DBE9),
                                        start = androidx.compose.ui.geometry.Offset(0f, y),
                                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                                        strokeWidth = 1f
                                    )
                                    y += step
                                }
                            }
                    )

                    // Creature Image with breathing float animation
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentSpecimen?.imageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAWtsnMLGWdH1qt3ktGxnSW_7mC3UIHxwjnEFA_jbDSsgfxnCHtlBHRaBtpYCia1BVynE0hRGKuSLgGuY_GCR0JmLfjUjh730Hdr3eWNuIMd0yGJUNFak8jnYN_HXacsnXPehGbptcEtPrmS9UEq0uKOwExtsR8Rx-jwNtllUY2NKmrhzqxa4dJSby19Gax5rDs1RlL-qh4tUEQcOktPhUAPpoz6caOpw1Z2WorqK7k00A4UQQ2pWLuSa-D5rU24gLhXzsaeV_DSKQ")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Specimen-X9 in Stasis Tank",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(230.dp)
                            .offset(y = floatOffset.dp)
                            .shadow(16.dp, CircleShape, spotColor = LabCyanAccent)
                    )

                    // Overlay HUD Markers
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .background(Color(0x99000000), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0x3300DBE9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STASIS FIELD: LOCKED",
                            color = LabSuccess,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .background(Color(0x99000000), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0x33EBB2FF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DIV: 89.4% [OMEGA]",
                            color = LabSecondary,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AWAITING SEQUENCE FINALIZATION",
                    color = LabCyanAccent,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Button: Generate Creature
                ElevatedButton(
                    onClick = onGenerateCreature,
                    enabled = !isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_creature_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = LabCyanAccent,
                        contentColor = Color(0xFF00363A),
                        disabledContainerColor = LabSurfaceContainerHigh,
                        disabledContentColor = LabOnSurfaceVariant
                    )
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF00363A),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYNTHESIZING GENETIC SPLICE...",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "⚡ GENERATE CREATURE",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // DNA Sequencing & Nucleotide Readout
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DNA SEQUENCING MODULE",
                        color = LabOnSurface,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CODON MATRIX",
                        color = LabSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Visual Animated DNA Helix
                AnimatedDnaHelix(
                    modifier = Modifier.fillMaxWidth(),
                    height = 90.dp,
                    pairCount = 10
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sequence Code Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(LabSurfaceContainerLowest)
                        .border(1.dp, Color(0x3300DBE9), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEQUENCE:",
                            color = LabOnSurfaceVariant,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dnaSeq,
                            color = LabCyanAccent,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Nucleotide Distribution Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NucleotideItem(label = "ADENINE (A)", value = "28%")
                    NucleotideItem(label = "THYMINE (T)", value = "22%")
                    NucleotideItem(label = "GUANINE (G)", value = "34%")
                    NucleotideItem(label = "CYTOSINE (C)", value = "16%")
                }
            }
        }

        // Genetic Stability Trend Visualizer (Recharts style area/line telemetry)
        DnaStabilityTrendVisualizer(
            modifier = Modifier.fillMaxWidth(),
            specimenCode = currentSpecimen?.specimenCode ?: "SPECIMEN-X9"
        )

        // Trait Analysis Card
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "TRAIT ANALYSIS & PHENOTYPE",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                TraitMeter(
                    label = "AGGRESSION INDEX",
                    value = currentSpecimen?.aggression ?: 90,
                    barColor = Color(0xFFFF5252)
                )

                TraitMeter(
                    label = "INTELLIGENCE RATING",
                    value = currentSpecimen?.intelligence ?: 60,
                    barColor = LabCyanAccent
                )

                TraitMeter(
                    label = "DERMAL HARDENING",
                    value = currentSpecimen?.dermalHardening ?: 78,
                    barColor = LabSecondary
                )

                TraitMeter(
                    label = "BIO-LUMINESCENCE INTENSITY",
                    value = currentSpecimen?.bioLuminescence ?: 85,
                    barColor = LabCyanAccent
                )

                // Venom Indicator Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(LabSurfaceContainerLowest)
                        .border(1.dp, Color(0x33FFB4AB), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⚠️", fontSize = 12.sp)
                        Text(
                            text = "VENOMOUS GLAND SYNTHESIS",
                            color = LabOnSurface,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFB4AB))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (currentSpecimen?.venomGlands == true) "VENOM_TRUE" else "INACTIVE",
                            color = if (currentSpecimen?.venomGlands == true) LabError else LabOnSurfaceVariant,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Mutation Logs Card
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            MutationLogList(logs = logs)
        }
    }
}

@Composable
private fun NucleotideItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = LabOnSurface,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = LabOnSurfaceVariant,
            fontSize = 7.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Preview(name = "Hub Screen Preview", showBackground = true, backgroundColor = 0xFF0A0E14)
@Composable
fun HubContentPreview() {
    val sampleSpecimen = SpecimenEntity(
        specimenCode = "SPECIMEN-X9",
        commonName = "Obsidian Stalker",
        genus = "Draconic Void",
        originPlanet = "Kepler-16b Deep Basin",
        status = "MUTATING",
        dnaSequence = "AGT-X12-FIRE-789-WING",
        aggression = 90,
        intelligence = 60,
        venomGlands = true,
        dermalHardening = 78,
        bioLuminescence = 85,
        imageUrl = "",
        habitatType = "Void",
        notes = "Chitinous exoskeleton infused with silicates."
    )
    val sampleLogs = listOf(
        MutationLogEntity(
            specimenCode = "SPECIMEN-X9",
            message = "Bioluminescent channels synthesized [94.2% STABLE]",
            timeLabel = "10:14:02",
            tag = "MUTATION"
        ),
        MutationLogEntity(
            specimenCode = "SPECIMEN-X9",
            message = "Sub-zero cryo incubation stabilized [91.8% STABLE]",
            timeLabel = "10:11:45",
            tag = "INFO"
        )
    )

    MyApplicationTheme {
        HubContent(
            currentSpecimen = sampleSpecimen,
            dnaSeq = "AGT-X12-FIRE-789-WING",
            isGenerating = false,
            logs = sampleLogs,
            onGenerateCreature = {}
        )
    }
}
