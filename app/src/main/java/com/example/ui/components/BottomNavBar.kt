package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavTabItem(
    val id: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun LifeOSBottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavTabItem("home", "Home", Icons.Default.Dashboard),
        NavTabItem("study", "Study", Icons.Default.School),
        NavTabItem("matrix", "Matrix", Icons.Default.GridView),
        NavTabItem("future_goals", "Goals", Icons.Default.TrackChanges),
        NavTabItem("focus", "Focus", Icons.Default.Timer),
        NavTabItem("pillars", "Pillars", Icons.Default.Layers)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lifeos_bottom_nav_bar"),
        color = Color(0xFF10121A).copy(alpha = 0.98f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(60.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = selectedTab == item.id
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(item.id) }
                        .padding(vertical = 4.dp)
                        .testTag("nav_tab_${item.id}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) EmeraldPrimary else TextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.title,
                        color = if (isSelected) EmeraldPrimary else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
