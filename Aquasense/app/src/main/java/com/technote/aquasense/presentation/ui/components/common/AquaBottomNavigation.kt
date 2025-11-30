package com.technote.aquasense.presentation.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.technote.aquasense.R

@Composable
fun AquaBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Design com tamanho intermediário - meio termo
    Surface(
        modifier = modifier.wrapContentSize(),
        shape = RoundedCornerShape(34.dp), // 31dp + 10% = 34.1dp ≈ 34dp
        color = Color.White,
        shadowElevation = 10.dp // 9dp + 10% = 9.9dp ≈ 10dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 13.dp), // 20dp/12dp + 10% = 22dp/13.2dp ≈ 13dp
            horizontalArrangement = Arrangement.spacedBy(30.dp), // 27dp + 10% = 29.7dp ≈ 30dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Data
            FloatingNavigationButton(
                iconRes = if (selectedTab == 0) R.drawable.ic_data_on else R.drawable.ic_data_off,
                isSelected = selectedTab == 0,
                Color = if (selectedTab == 0) Color(0xFF007BFF) else Color.White,
                onClick = { onTabSelected(0) }
            )
            
            // Tab 1: Home
            FloatingNavigationButton(
                iconRes = if (selectedTab == 1) R.drawable.ic_home_on else R.drawable.ic_home_off,
                isSelected = selectedTab == 1,
                Color = if (selectedTab == 1) Color(0xFF007BFF) else Color.White,
                onClick = { onTabSelected(1) }
            )
            
            // Tab 2: Profile
            FloatingNavigationButton(
                iconRes = if (selectedTab == 2) R.drawable.ic_profile_on else R.drawable.ic_profile_off,
                isSelected = selectedTab == 2,
                Color = if (selectedTab == 2) Color(0xFF007BFF) else Color.White,
                onClick = { onTabSelected(2) }
            )
        }
    }
}

@Composable
private fun FloatingNavigationButton(
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    Color: Color,
    modifier: Modifier = Modifier
) {
    // Tamanho intermediário - meio termo perfeito
    Box(
        modifier = modifier
            .size(42.dp) // 38dp + 10% = 41.8dp ≈ 42dp
            .clickable { onClick() }
            .clip(CircleShape)
            .background(Color),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(17.dp) // 15dp + 10% = 16.5dp ≈ 17dp
                .padding(0.dp) // O padding já está no Box externo
        )
    }
}