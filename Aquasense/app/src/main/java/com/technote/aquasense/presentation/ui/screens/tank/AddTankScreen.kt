package com.technote.aquasense.presentation.ui.screens.tank

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
import com.technote.aquasense.presentation.viewmodel.TankViewModel

@Composable
fun AddTankScreen(
    navController: NavController,
    tankViewModel: TankViewModel = hiltViewModel()
) {
    val uiState by tankViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(-1) } // Não destacar nenhum tab específico

    var name by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    LaunchedEffect(uiState.tankCreated) {
        if (uiState.tankCreated) {
            Toast.makeText(context, context.getString(R.string.tank_created_success), Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            tankViewModel.resetTankCreated()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            tankViewModel.clearError()
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

            Text(
                text = stringResource(R.string.add_tank_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007BFF)
            )

            Spacer(modifier = Modifier.height(20.dp))

            AquaTextField(
                value = name,
                onValueChange = { if (it.length <= 15) name = it },
                label = stringResource(R.string.tank_name_label),
                placeholder = stringResource(R.string.tank_name_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            AquaTextField(
                value = volume,
                onValueChange = { volume = it },
                label = stringResource(R.string.tank_volume_label),
                placeholder = stringResource(R.string.tank_volume_placeholder),
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            AquaTextField(
                value = number,
                onValueChange = { number = it },
                label = stringResource(R.string.tank_number_label),
                placeholder = stringResource(R.string.tank_number_placeholder),
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
                        volume.isEmpty() || volume.toDoubleOrNull() == null || volume.toDouble() <= 0 -> {
                            Toast.makeText(context, context.getString(R.string.volume_validation), Toast.LENGTH_SHORT).show()
                        }
                        number.isEmpty() || number.toDoubleOrNull() == null || number.toDouble() <= 0 -> {
                            Toast.makeText(context, context.getString(R.string.number_validation), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // TODO: Obter userId do AuthRepository
                            val userId = "user_id_aqui"
                            tankViewModel.createTank(
                                name = name.trim(),
                                volume = volume.toDouble(),
                                number = number.toDouble(),
                                userId = userId
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.register_button_tank),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
