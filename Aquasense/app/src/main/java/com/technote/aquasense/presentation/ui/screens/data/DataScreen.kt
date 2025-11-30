package com.technote.aquasense.presentation.ui.screens.data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.technote.aquasense.R
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaHeader

@Composable
fun DataScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) } // Data tab
    val context = LocalContext.current
    var selectedSegment by remember { mutableStateOf(context.getString(R.string.tank_option)) }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            AquaHeader(isExpanded = false)
        },
        bottomBar = {
            // Container fixo para o FAB
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AquaBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { 
                        selectedTab = it
                        when (it) {
                            0 -> navController.navigate("data")
                            1 -> navController.navigate("home")
                            2 -> navController.navigate("profile")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Segmented Button
            SegmentedButton(
                selectedOption = selectedSegment,
                onOptionSelected = { selectedSegment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )

            // Lista de botões baseada na seleção
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                contentPadding = PaddingValues(bottom = 40.dp), // Espaço para o FAB
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedSegment == context.getString(R.string.tank_option)) {
                    // Botões para Tanque
                    item {
                        DataButton(
                            title = stringResource(R.string.graphs_title),
                            onClick = { navController.navigate("graph_screen") }
                        )
                    }
                    item {
                        DataButton(
                            title = stringResource(R.string.update_tank_title),
                            onClick = { navController.navigate("update_tank") }
                        )
                    }
                    item {
                        DataButton(
                            title = stringResource(R.string.delete_tank_title),
                            onClick = { navController.navigate("delete_tank") }
                        )
                    }
                    item {
                        DataButton(
                            title = stringResource(R.string.add_tank_title),
                            onClick = { navController.navigate("add_tank") }
                        )
                    }
                    item {
                        DataButton(
                            title = stringResource(R.string.tank_info_title),
                            onClick = { navController.navigate("graph") }
                        )
                    }
                } else {
                    // Botões para Peixe
                    item {
                        DataButton(
                            title = stringResource(R.string.update_fish_title),
                            onClick = { navController.navigate("update_fish") }
                        )
                    }
                    item {
                        DataButton(
                            title = stringResource(R.string.delete_fish_title),
                            onClick = { navController.navigate("delete_fish") }
                        )
                    }
                    item {
                        DataButton(
                            title = stringResource(R.string.add_fish_title),
                            onClick = { navController.navigate("add_fish") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentedButton(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val options = listOf(stringResource(R.string.tank_option), stringResource(R.string.fish_option))

    Surface(
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(25.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF007BFF))
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(25.dp),
                    color = if (isSelected) Color(0xFF007BFF) else Color.Transparent,
                    onClick = { onOptionSelected(option) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else Color(0xFF007BFF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DataButton(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(30.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color(0xFF23272A)
            )
        }
    }
}
