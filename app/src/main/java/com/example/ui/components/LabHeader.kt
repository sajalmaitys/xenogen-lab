package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.LabCyanAccent
import com.example.ui.theme.LabCyanGlow
import com.example.ui.theme.LabOnSurface
import com.example.ui.theme.LabOnSurfaceVariant
import com.example.ui.theme.LabSecondary
import com.example.ui.theme.LabSuccess
import com.example.ui.theme.LabSurfaceContainer
import com.example.ui.theme.LabSurfaceContainerHigh
import com.example.ui.theme.LabSurfaceContainerLowest
import com.example.ui.viewmodel.LabNavTab

@Composable
fun LabHeader(
    currentTab: LabNavTab,
    onTabSelected: (LabNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LabSurfaceContainerLowest)
            .border(1.dp, Color(0x1AEBB2FF), RoundedCornerShape(0.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Top row: Brand & Status & Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LabSurfaceContainerHigh)
                        .border(1.dp, LabCyanAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧬",
                        fontSize = 16.sp
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "XENOGEN",
                            color = LabOnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LAB",
                            color = LabCyanAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "EXTRATERRESTRIAL BIOTECH",
                        color = LabOnSurfaceVariant,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Radar Status & Geneticist Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LabSurfaceContainer)
                        .border(1.dp, LabSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LabSuccess.copy(alpha = alphaAnim))
                        )
                        Text(
                            text = "ACTIVE",
                            color = LabSuccess,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Chief Geneticist Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuDCcRy83E5ztbENUhXDrdi6uJIZYTlR-1GeeHFV1DmQWE6D9202DnEUyj0lWYSzAa_V6Cp1xQZUw3rM1AIDTtW9tJpCNAn1YvmdO4Ban4ueJG8GDfgSmLyZiuTw6b5dyWAbxGBMK67TC5pDiRRyN1aJelgkn64bT5NXKEid-Pezau4GgGQjTm8Da-opEiBvxgzofwJS1UL6SExOA31dQsNvOPGRCFN1aTAVCVbd97NnheB7zsf5wXmE1pWGMg9gFyyRopzxki7ARGg")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Chief Geneticist Dr. Elara Vance",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, LabCyanAccent, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LabNavTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val tabBg = if (isSelected) LabCyanAccent else LabSurfaceContainer
                val tabTextColor = if (isSelected) Color(0xFF00363A) else LabOnSurfaceVariant
                val border = if (isSelected) LabCyanAccent else Color(0x22849495)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tabBg)
                        .border(1.dp, border, RoundedCornerShape(6.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Text(
                        text = tab.title.uppercase(),
                        color = tabTextColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
