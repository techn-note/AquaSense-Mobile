package com.technote.aquasense.presentation.ui.screens.fish

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.technote.aquasense.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaTextField
import com.technote.aquasense.presentation.ui.components.common.AquaHeader
import com.technote.aquasense.presentation.viewmodel.FishViewModel

@Composable
fun AddFishScreen(
    navController: NavController,
    fishViewModel: FishViewModel = hiltViewModel()
) {
    val uiState by fishViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(-1) } // Não destacar nenhum tab específico

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    LaunchedEffect(uiState.fishCreated) {
        if (uiState.fishCreated) {
            Toast.makeText(context, context.getString(R.string.fish_created_success), Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            fishViewModel.resetFishCreated()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            fishViewModel.clearError()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = { AquaHeader(isExpanded = false) },
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
                .padding(horizontal = 45.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(stringResource(R.string.add_fish_form_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF007BFF))

            Spacer(modifier = Modifier.height(20.dp))

            AquaTextField(
                value = name,
                onValueChange = { if (it.length <= 15) name = it },
                label = stringResource(R.string.fish_name_label),
                placeholder = stringResource(R.string.fish_name_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            AquaTextField(
                value = age,
                onValueChange = { age = it },
                label = stringResource(R.string.fish_age_label),
                placeholder = stringResource(R.string.fish_age_placeholder),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            AquaTextField(
                value = species,
                onValueChange = { species = it },
                label = stringResource(R.string.fish_species_label),
                placeholder = stringResource(R.string.fish_species_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            AquaTextField(
                value = weight,
                onValueChange = { weight = it },
                label = stringResource(R.string.fish_weight_label),
                placeholder = stringResource(R.string.fish_weight_placeholder),
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            AquaTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = stringResource(R.string.fish_quantity_label),
                placeholder = stringResource(R.string.fish_quantity_placeholder),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    when {
                        name.isEmpty() || name.length > 15 -> {
                            Toast.makeText(context, context.getString(R.string.name_length_validation), Toast.LENGTH_SHORT).show()
                        }
                        age.isEmpty() || age.toIntOrNull() == null || age.toInt() < 0 -> {
                            Toast.makeText(context, context.getString(R.string.age_validation), Toast.LENGTH_SHORT).show()
                        }
                        species.isEmpty() -> {
                            Toast.makeText(context, context.getString(R.string.species_validation), Toast.LENGTH_SHORT).show()
                        }
                        weight.isEmpty() || weight.toFloatOrNull() == null || weight.toFloat() <= 0 -> {
                            Toast.makeText(context, context.getString(R.string.weight_validation), Toast.LENGTH_SHORT).show()
                        }
                        quantity.isEmpty() || quantity.toIntOrNull() == null || quantity.toInt() < 1 -> {
                            Toast.makeText(context, context.getString(R.string.quantity_validation), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val userId = "user_id_aqui"
                            fishViewModel.createFish(
                                nome = name.trim(),
                                idade = age.toInt(),
                                especie = species.trim(),
                                peso = weight.toFloat(),
                                quantidade = quantity.toInt(),
                                userId = userId
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(stringResource(R.string.register_button_fish), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}
