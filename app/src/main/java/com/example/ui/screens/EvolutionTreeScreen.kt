package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.ui.components.LabCard
import com.example.ui.components.RadialGauge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TraitMeter
import com.example.ui.theme.LabCyanAccent
import com.example.ui.theme.LabCyanGlow
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
fun EvolutionTreeScreen(
    viewModel: XenoGenViewModel,
    modifier: Modifier = Modifier
) {
    val isEcosystemActive by viewModel.isEcosystemSimulationActive.collectAsState()
    val selectedNode by viewModel.selectedEvoNode.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LabSurfaceContainerLowest)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Section: Specimen #842 Cryonix & Ecosystem switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PHYLOGENETIC MAPPING",
                    color = LabCyanAccent,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SPECIMEN #842: CRYONIX",
                    color = LabOnSurface,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "GENERATION: IV / IV_OMEGA • CYCLES: 12,408",
                    color = LabOnSurfaceVariant,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            StatusBadge(status = "EVOLVING")
        }

        // Ecosystem Simulation Toggle Switch Card
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ECOSYSTEM SIMULATION",
                        color = LabOnSurface,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isEcosystemActive) "Active: Triton Subglacial Cryosphere" else "Simulated: Offline (Static Lineage)",
                        color = if (isEcosystemActive) LabCyanAccent else LabOnSurfaceVariant,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Switch(
                    checked = isEcosystemActive,
                    onCheckedChange = { viewModel.toggleEcosystemSimulation() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LabCyanAccent,
                        checkedTrackColor = Color(0xFF00363A),
                        uncheckedThumbColor = LabOnSurfaceVariant,
                        uncheckedTrackColor = LabSurfaceContainerHigh
                    ),
                    modifier = Modifier.testTag("ecosystem_switch")
                )
            }
        }

        // Phylogenetic Tree Nodes Card
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow,
            borderColor = LabCyanAccent.copy(alpha = 0.35f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "EVOLUTIONARY ROADMAP",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                // Node 0: Proto-Cell
                EvolutionNodeCard(
                    title = "GEN 0: PROTO-ORGANISM",
                    subtitle = "Single-cell lipid vesicle with RNA replication",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDElHKHflSBdwEiKjh3M17HDPdbwY2YYYj7_ON-OQ1d9zm3v4gSH6YQvsGMepUkP2i0cbpzoOFTh6Gp5rbtjvDoLNlEXSIFcqAYxXx1678gV6UbOYF9B_ST-T4c2uFAo7DR8VYfTVNrYQpkG0_M2VG1nJJS9s9FtxVKNOKOXemR-Nwkcp51H-O06hKUNhS0Y7vkd3IJ8FnHsunR3T29Z0FYcrHJUUGlvA1Ms10_uoN0Oml8Gt9aszgcSzxk9qy3GbdBdLFE-mgkT2E",
                    badge = "BASE ROOT",
                    badgeColor = LabOnSurfaceVariant,
                    divergence = "0.0%",
                    isSelected = selectedNode == 0,
                    onClick = { viewModel.selectEvoNode(0) }
                )

                // Tree Branch Connector
                Box(
                    modifier = Modifier
                        .padding(start = 28.dp)
                        .width(2.dp)
                        .height(18.dp)
                        .background(LabCyanAccent)
                )

                // Node 1: Arcton-1
                EvolutionNodeCard(
                    title = "GEN II: ARCTON-1",
                    subtitle = "Glacial amphibian with cryo-resistant tissue",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCjIl_vNSW8pB7XzOkSiWEVvu3vtUVjoY_CP-wl6iCJax2VCh_QbX-e_XCLlQqKgj4586aKyvQI1eYvxIdQt_gTxrJV-sk6P8nirgYy_Z0cNgPQatgWDNKhPJQ1_6nErF2dubmkmRlC1CjdJWCELzVSYIoC598laS1_Kfzn3NPHgJVGeZyQqM1Z1VCdbFcj5_h7Xu-vVPjOAHpBQAg1_xA2hS_y_xWTSUeAUx_603Q_oKydaoXddmgn_NSxye0RV9ncmCzU5rNmjCY",
                    badge = "MODERATE MUTATION",
                    badgeColor = LabSecondary,
                    divergence = "45.0%",
                    isSelected = selectedNode == 1,
                    onClick = { viewModel.selectEvoNode(1) }
                )

                // Tree Branch Connector
                Box(
                    modifier = Modifier
                        .padding(start = 28.dp)
                        .width(2.dp)
                        .height(18.dp)
                        .background(LabCyanAccent)
                )

                // Node 2: Cryonix (Current Phase)
                EvolutionNodeCard(
                    title = "GEN IV: CRYONIX (ACTIVE)",
                    subtitle = "Apex endothermic predator with glacial stealth",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDAPesBenVlbqnMDkTxp1wasVaeG2_a-hGEmKqjj_s0LgshUGsmjqAscYhZ9dw-5aaHDZBqvt0avCO4FArxgJztA0X1QEjSsaEXxsTApyCSBbaXmkpKoY6PkewjmvHvJAJEZA1YHaUqA5UoEU11JToIlM36qgYOhTZ8AytAR-GwUFj2f4E2qG8OlyHBEvtuBM2lG1fJHshbuTBHd4TQ-1mEYs-ehlXa-yWRSbFA7SoPflLahOP1DZgxBmEqa7mHs5brcRfsj0KD8ls",
                    badge = "CURRENT PHASE",
                    badgeColor = LabCyanAccent,
                    divergence = "89.4%",
                    isSelected = selectedNode == 2,
                    onClick = { viewModel.selectEvoNode(2) }
                )
            }
        }

        // Timeline Milestones
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "EVOLUTIONARY TIMELINE & VELOCITY",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                TimelineMilestoneItem(version = "V.1.0", desc = "Basic Homeostasis & Cold Adaptation", isDone = true)
                TimelineMilestoneItem(version = "V.2.1", desc = "Thermal Regulation Organs", isDone = true)
                TimelineMilestoneItem(version = "V.3.2", desc = "Glacial Stealth & Camouflage Skin", isDone = true)
                TimelineMilestoneItem(version = "V.4.0", desc = "Apex Predation & Neural Coordination", isDone = true, isActive = true)
                TimelineMilestoneItem(version = "V.5.0", desc = "Psionic Hive Link & Void Resilience", isDone = false)
            }
        }

        // Environmental Adaptations & Stability Gauge
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
                    percentage = 82f,
                    label = "STABILITY",
                    valueText = "82%",
                    gaugeColor = LabSecondary,
                    size = 92.dp
                )

                Column(
                    modifier = Modifier.weight(1f).padding(start = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ENVIRONMENT ADAPTATIONS",
                        color = LabCyanAccent,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    TraitMeter(label = "COLD RESISTANCE", value = 94, barColor = LabCyanAccent)
                    TraitMeter(label = "GLACIAL STEALTH", value = 88, barColor = LabSecondary)
                    TraitMeter(label = "PRESSURE TOLERANCE", value = 75, barColor = LabCyanAccent)
                }
            }
        }

        // Future Adaptation Forecast
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "FUTURE ADAPTATION FORECAST",
                    color = LabOnSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ForecastPill(
                        modifier = Modifier.weight(1f),
                        title = "VOID ADAPT",
                        prob = "64%",
                        color = LabSecondary
                    )
                    ForecastPill(
                        modifier = Modifier.weight(1f),
                        title = "PSIONIC LINK",
                        prob = "12%",
                        color = LabCyanAccent
                    )
                    ForecastPill(
                        modifier = Modifier.weight(1f),
                        title = "THERMAL ARMOR",
                        prob = "89%",
                        color = Color(0xFFFF5252)
                    )
                }
            }
        }
    }
}

@Composable
private fun EvolutionNodeCard(
    title: String,
    subtitle: String,
    imageUrl: String,
    badge: String,
    badgeColor: Color,
    divergence: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) LabCyanAccent else Color(0x22849495)
    val bgColor = if (isSelected) LabSurfaceContainerHigh else LabSurfaceContainerLowest

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = LabOnSurface,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = subtitle,
                color = LabOnSurfaceVariant,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "DIVERGENCE: $divergence",
                color = LabCyanAccent,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TimelineMilestoneItem(
    version: String,
    desc: String,
    isDone: Boolean,
    isActive: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val dotColor = when {
            isActive -> LabCyanAccent
            isDone -> LabSuccess
            else -> LabOnSurfaceVariant
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = version,
            color = dotColor,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = desc,
            color = if (isActive) LabOnSurface else LabOnSurfaceVariant,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ForecastPill(
    title: String,
    prob: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LabSurfaceContainerLowest)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = LabOnSurfaceVariant,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = prob,
                color = color,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            )
        }
    }
}
