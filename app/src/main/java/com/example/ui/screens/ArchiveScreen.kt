package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.SpecimenEntity
import com.example.ui.components.LabCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.TraitMeter
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
import com.example.ui.viewmodel.LabNavTab
import com.example.ui.viewmodel.XenoGenViewModel

@Composable
fun ArchiveScreen(
    viewModel: XenoGenViewModel,
    modifier: Modifier = Modifier
) {
    val specimens by viewModel.filteredSpecimens.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedClassFilter.collectAsState()
    val selectedSpecimen by viewModel.selectedSpecimenForDetail.collectAsState()
    val isAddDialogOpen by viewModel.isAddSpecimenDialogOpen.collectAsState()

    var syncProgress by remember { mutableStateOf(84) }
    var isSyncing by remember { mutableStateOf(false) }

    val filterOptions = listOf("ALL", "INSECTOID", "AQUATIC", "REPTILIAN", "AVIAN", "TECTONIC", "VOID")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LabSurfaceContainerLowest)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Archive Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BIOLOGICAL REPOSITORY",
                    color = LabCyanAccent,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "SPECIES ARCHIVE VAULT",
                    color = LabOnSurface,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF00363A))
                    .border(1.dp, LabCyanAccent, RoundedCornerShape(6.dp))
                    .clickable { viewModel.openAddSpecimenDialog(true) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("add_specimen_button")
            ) {
                Text(
                    text = "+ NEW SEQUENCE",
                    color = LabCyanAccent,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Search Query Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_specimens_input"),
            placeholder = {
                Text(
                    text = "Search specimen ID, genus, or origin planet...",
                    color = LabOnSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LabSurfaceContainerLow,
                unfocusedContainerColor = LabSurfaceContainerLow,
                focusedBorderColor = LabCyanAccent,
                unfocusedBorderColor = Color(0x33849495),
                focusedTextColor = LabOnSurface,
                unfocusedTextColor = LabOnSurface
            )
        )

        // Class Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) LabSecondary else LabSurfaceContainerHigh)
                        .border(
                            1.dp,
                            if (isSelected) LabSecondary else Color(0x22849495),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { viewModel.setClassFilter(filter) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color(0xFF520072) else LabOnSurfaceVariant,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Vault Sync Status Bar
        LabCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = LabSurfaceContainerLow
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "VAULT SYNC STATUS:",
                            color = LabOnSurfaceVariant,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "$syncProgress% SYNCHRONIZED",
                            color = LabSuccess,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(LabSurfaceContainerHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(syncProgress / 100f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(LabCyanAccent)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(LabSurfaceContainerHigh)
                        .clickable {
                            syncProgress = 100
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("sync_vault_button")
                ) {
                    Text(
                        text = "SYNC VAULT",
                        color = LabCyanAccent,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Specimen Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(specimens) { specimen ->
                SpecimenCard(
                    specimen = specimen,
                    onClick = { viewModel.selectSpecimenForDetail(specimen) }
                )
            }
        }
    }

    // Detail Modal Dialog for Specimen
    selectedSpecimen?.let { specimen ->
        Dialog(onDismissRequest = { viewModel.selectSpecimenForDetail(null) }) {
            LabCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                backgroundColor = LabSurfaceContainerLowest,
                borderColor = LabCyanAccent
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = specimen.specimenCode,
                                color = LabCyanAccent,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = specimen.commonName,
                                color = LabOnSurface,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusBadge(status = specimen.status)
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(specimen.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = specimen.commonName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x3300DBE9), RoundedCornerShape(8.dp))
                    )

                    // Bio Details
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DetailField(label = "GENUS", value = specimen.genus)
                        DetailField(label = "ORIGIN", value = specimen.originPlanet)
                        DetailField(label = "HABITAT", value = specimen.habitatType)
                        DetailField(label = "DNA SEQUENCE", value = specimen.dnaSequence)
                    }

                    TraitMeter(
                        label = "AGGRESSION",
                        value = specimen.aggression,
                        barColor = Color(0xFFFF5252)
                    )
                    TraitMeter(
                        label = "INTELLIGENCE",
                        value = specimen.intelligence,
                        barColor = LabCyanAccent
                    )

                    Text(
                        text = specimen.notes,
                        color = LabOnSurfaceVariant,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.transferToChamber(specimen) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("transfer_chamber_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = LabCyanAccent),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "MUTATE IN CHAMBER",
                                color = Color(0xFF00363A),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { viewModel.deleteSpecimen(specimen.id, specimen.specimenCode) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFB4AB)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PURGE",
                                color = LabError,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Specimen Dialog
    if (isAddDialogOpen) {
        var code by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var genus by remember { mutableStateOf("Draconic Void") }
        var origin by remember { mutableStateOf("Kepler-16b") }
        var habitat by remember { mutableStateOf("Cryo") }
        var aggression by remember { mutableStateOf(70f) }
        var intel by remember { mutableStateOf(65f) }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.openAddSpecimenDialog(false) }) {
            LabCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                backgroundColor = LabSurfaceContainerLowest,
                borderColor = LabSecondary
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "INITIATE NEW SEQUENCING",
                        color = LabSecondary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        placeholder = { Text("Code (e.g. SPECIMEN-X88)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Name (e.g. Gorgon Basilisk)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = origin,
                        onValueChange = { origin = it },
                        placeholder = { Text("Origin Planet (e.g. Kepler-16b)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "AGGRESSION: ${aggression.toInt()}%", fontSize = 10.sp, color = LabOnSurface)
                        Text(text = "INTEL: ${intel.toInt()}%", fontSize = 10.sp, color = LabOnSurface)
                    }

                    Slider(
                        value = aggression,
                        onValueChange = { aggression = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = LabSecondary, activeTrackColor = LabSecondary)
                    )

                    Button(
                        onClick = {
                            viewModel.createCustomSpecimen(
                                code = code,
                                name = name,
                                genus = genus,
                                origin = origin,
                                habitat = habitat,
                                aggression = aggression.toInt(),
                                intelligence = intel.toInt(),
                                notes = notes
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LabSecondary)
                    ) {
                        Text(
                            text = "SYNTHESIZE SPECIMEN",
                            color = Color(0xFF520072),
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
private fun SpecimenCard(
    specimen: SpecimenEntity,
    onClick: () -> Unit
) {
    LabCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("specimen_card_${specimen.specimenCode.lowercase()}"),
        backgroundColor = LabSurfaceContainerLow,
        borderColor = Color(0x22849495)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(specimen.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = specimen.commonName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0x3300DBE9), RoundedCornerShape(8.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = specimen.specimenCode,
                        color = LabCyanAccent,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    StatusBadge(status = specimen.status)
                }
                Text(
                    text = specimen.commonName,
                    color = LabOnSurface,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${specimen.genus} • ${specimen.originPlanet}",
                    color = LabOnSurfaceVariant,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "AGGR: ${specimen.aggression}%",
                        color = Color(0xFFFF5252),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "INTEL: ${specimen.intelligence}%",
                        color = LabCyanAccent,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "HAB: ${specimen.habitatType.uppercase()}",
                        color = LabSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
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
            color = LabOnSurface,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
