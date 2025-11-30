package com.technote.aquasense.presentation.ui.screens.fish

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.technote.aquasense.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaHeader
import com.technote.aquasense.presentation.viewmodel.FishViewModel

@Composable
fun DeleteFishScreen(
    navController: NavController,
    fishViewModel: FishViewModel = hiltViewModel()
) {
    val uiState by fishViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(-1) } // Não destacar nenhum tab específico
    
    var selectedFishName by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val userId = "user_id_aqui"
        fishViewModel.loadFishes(userId)
    }

    LaunchedEffect(uiState.fishDeleted) {
        if (uiState.fishDeleted) {
            Toast.makeText(context, context.getString(R.string.fish_deleted_success), Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            fishViewModel.resetFishDeleted()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = stringResource(R.string.delete_fish_form_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.select_fish_to_delete),
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFDC3545))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 40.dp), // Espaço para o FAB
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.fishes) { fish ->
                        FishDeleteCard(
                            fishName = fish.name,
                            fishSpecies = fish.species,
                            isSelected = selectedFishName == fish.name,
                            onSelect = { 
                                selectedFishName = fish.name
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão Deletar
            Button(
                onClick = { 
                    if (selectedFishName != null) {
                        showConfirmDialog = true
                    } else {
                        Toast.makeText(context, context.getString(R.string.select_fish_to_delete_error), Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = selectedFishName != null && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545),
                    disabledContainerColor = Color(0xFFCCCCCC)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.delete_fish_title),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialog de confirmação
    if (showConfirmDialog) {
        val selectedFish = uiState.fishes.find { it.name == selectedFishName }
        
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White, // Força fundo branco
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Aviso",
                    tint = Color(0xFFDC3545),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.confirm_deletion),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1A1A1A) // Força cor do texto
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.delete_fish_confirmation_msg),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1A1A1A) // Força cor do texto
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${selectedFish?.name ?: ""}\"",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC3545),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "(${selectedFish?.species ?: ""})",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.warning_fish_deletion),
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedFishName?.let { fishName ->
                            // Usar o nome para buscar o ID real e deletar
                            fishViewModel.deleteFish(fishName)
                        }
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC3545)
                    )
                ) {
                    Text(stringResource(R.string.delete_button), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text(stringResource(R.string.cancel), color = Color(0xFF666666))
                }
            }
        )
    }
}

@Composable
private fun FishDeleteCard(
    fishName: String,
    fishSpecies: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFEBEE) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFDC3545)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de seleção
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (isSelected) Color(0xFFDC3545) else Color(0xFFE0E0E0),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Selecionado",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informações do peixe
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fishName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color(0xFFDC3545) else Color(0xFF1A1A1A)
                )
                Text(
                    text = "Espécie: $fishSpecies",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}