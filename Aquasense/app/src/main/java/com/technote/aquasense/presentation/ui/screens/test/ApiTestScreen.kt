package com.technote.aquasense.presentation.ui.screens.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.technote.aquasense.presentation.viewmodel.ApiTestViewModel

/**
 * Tela principal para testes da API
 * Interface simples para testar todos os endpoints
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestScreen(
    viewModel: ApiTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "🧪 API Test Center",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // AUTH SECTION
        item {
            AuthSection(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        }

        // TANK SECTION (só aparece se logado)
        if (uiState.isLoggedIn && uiState.currentUser != null) {
            item {
                TankSection(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }

            // FISH SECTION (só aparece se tem tank selecionado)
            if (uiState.selectedTank != null) {
                item {
                    FishSection(
                        uiState = uiState,
                        onEvent = viewModel::onEvent
                    )
                }

                item {
                    SensorSection(
                        uiState = uiState,
                        onEvent = viewModel::onEvent
                    )
                }

                item {
                    PredictionSection(
                        uiState = uiState,
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }

        // LOG SECTION
        item {
            TestSection(
                title = "📋 Sistema de Logs"
            ) {
                LogDisplay(
                    logs = uiState.logs,
                    onClearLogs = { viewModel.onEvent(ApiTestEvent.ClearLogs) }
                )
            }
        }

        // API INFO SECTION
        item {
            TestSection(
                title = "ℹ️ Informações da API"
            ) {
                ApiInfoDisplay()
            }
        }
    }
}

@Composable
fun AuthSection(
    uiState: ApiTestUiState,
    onEvent: (ApiTestEvent) -> Unit
) {
    TestSection(
        title = "🔐 Autenticação",
        isLoading = uiState.authLoading,
        error = uiState.authError
    ) {
        AuthStatusDisplay(
            isLoggedIn = uiState.isLoggedIn,
            currentUser = uiState.currentUser
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!uiState.isLoggedIn) {
            // Formulário de Login
            Column {
                TestTextField(
                    value = uiState.formData.email,
                    onValueChange = { email ->
                        onEvent(
                            ApiTestEvent.UpdateFormData(
                                uiState.formData.copy(email = email)
                            )
                        )
                    },
                    label = "Email"
                )

                TestTextField(
                    value = uiState.formData.password,
                    onValueChange = { password ->
                        onEvent(
                            ApiTestEvent.UpdateFormData(
                                uiState.formData.copy(password = password)
                            )
                        )
                    },
                    label = "Senha"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    TestButton(
                        text = "Login",
                        onClick = {
                            onEvent(
                                ApiTestEvent.Login(
                                    uiState.formData.email,
                                    uiState.formData.password
                                )
                            )
                        },
                        enabled = uiState.formData.email.isNotBlank() &&
                                uiState.formData.password.isNotBlank()
                    )

                    TestButton(
                        text = "Quick Login",
                        onClick = {
                            onEvent(ApiTestEvent.Login("test@test.com", "123456"))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Formulário de Register
            Column {
                Text("📝 Ou criar nova conta:")

                TestTextField(
                    value = uiState.formData.name,
                    onValueChange = { name ->
                        onEvent(
                            ApiTestEvent.UpdateFormData(
                                uiState.formData.copy(name = name)
                            )
                        )
                    },
                    label = "Nome"
                )

                TestButton(
                    text = "Register",
                    onClick = {

                    },
                    enabled = uiState.formData.name.isNotBlank() &&
                            uiState.formData.email.isNotBlank() &&
                            uiState.formData.password.isNotBlank()
                )
            }
        } else {
            // Botão de Logout
            TestButton(
                text = "Logout",
                onClick = { onEvent(ApiTestEvent.Logout) }
            )
        }
    }
}

@Composable
fun TankSection(
    uiState: ApiTestUiState,
    onEvent: (ApiTestEvent) -> Unit
) {
    TestSection(
        title = "🐠 Aquários",
        isLoading = uiState.tankLoading,
        error = uiState.tankError
    ) {
        // Botões de ação
        Row {
            TestButton(
                text = "Load Tanks",
                onClick = {
                    uiState.currentUser?.let { user ->
                        onEvent(ApiTestEvent.LoadTanks(user.id.toString()))
                    }
                }
            )

            TestButton(
                text = "Create Tank",
                onClick = {
                    uiState.currentUser?.let { user ->
                        val volume = uiState.formData.tankVolume.toDoubleOrNull() ?: 100.0
                        onEvent(
                            ApiTestEvent.CreateTank(
                                name = uiState.formData.tankName.ifBlank { "Tank Test" },
                                volume = volume,
                                userId = user.id.toString()
                            )
                        )
                    }
                },
                enabled = uiState.currentUser != null
            )
        }

        // Formulário para criar tank
        Row {
            TestTextField(
                value = uiState.formData.tankName,
                onValueChange = { name ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(tankName = name)
                        )
                    )
                },
                label = "Nome do Tank",
                modifier = Modifier.weight(1f)
            )

            TestTextField(
                value = uiState.formData.tankVolume,
                onValueChange = { volume ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(tankVolume = volume)
                        )
                    )
                },
                label = "Volume (L)",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de tanks
        TankList(
            tanks = uiState.tanks,
            selectedTank = uiState.selectedTank,
            onTankSelect = { tank ->
                onEvent(ApiTestEvent.SelectTank(tank))
            }
        )
    }
}

@Composable
fun FishSection(
    uiState: ApiTestUiState,
    onEvent: (ApiTestEvent) -> Unit
) {
    val selectedTank = uiState.selectedTank
    if (selectedTank?.id == null) return

    TestSection(
        title = "🐟 Peixes (Tank: ${selectedTank.name})",
        isLoading = uiState.fishLoading,
        error = uiState.fishError
    ) {
        // Botões de ação
        Row {
            TestButton(
                text = "Load Fish",
                onClick = {
                    onEvent(ApiTestEvent.LoadFish(selectedTank.id))
                }
            )

            TestButton(
                text = "Create Fish",
                onClick = {
                    onEvent(
                        ApiTestEvent.CreateFish(
                            name = uiState.formData.fishName.ifBlank { "Fish Test" },
                            species = uiState.formData.fishSpecies.ifBlank { "Goldfish" },
                            tankId = selectedTank.id
                        )
                    )
                }
            )
        }

        // Formulário para criar fish
        Row {
            TestTextField(
                value = uiState.formData.fishName,
                onValueChange = { name ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(fishName = name)
                        )
                    )
                },
                label = "Nome do Peixe",
                modifier = Modifier.weight(1f)
            )

            TestTextField(
                value = uiState.formData.fishSpecies,
                onValueChange = { species ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(fishSpecies = species)
                        )
                    )
                },
                label = "Espécie",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de peixes
        FishList(fishList = uiState.fishList)
    }
}

@Composable
fun SensorSection(
    uiState: ApiTestUiState,
    onEvent: (ApiTestEvent) -> Unit
) {
    val selectedTank = uiState.selectedTank
    if (selectedTank?.id == null) return

    TestSection(
        title = "📊 Sensores (Tank: ${selectedTank.name})",
        isLoading = uiState.sensorLoading,
        error = uiState.sensorError
    ) {
        // Botões de ação
        Row {
            TestButton(
                text = "Get Data",
                onClick = {
                    onEvent(ApiTestEvent.LoadSensorData(selectedTank.id))
                }
            )

            TestButton(
                text = "Get History",
                onClick = {
                    onEvent(ApiTestEvent.LoadSensorHistory(selectedTank.id))
                }
            )

            TestButton(
                text = "Update Data",
                onClick = {
                    val temp = uiState.formData.temperature.toDoubleOrNull() ?: 25.0
                    val ph = uiState.formData.ph.toDoubleOrNull() ?: 7.0
                    val tds = uiState.formData.tds.toDoubleOrNull() ?: 200.0

                    onEvent(
                        ApiTestEvent.UpdateSensorData(
                            tankId = selectedTank.id,
                            temp = temp,
                            ph = ph,
                            tds = tds
                        )
                    )
                }
            )
        }

        // Formulário para update de dados
        Row {
            TestTextField(
                value = uiState.formData.temperature,
                onValueChange = { temp ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(temperature = temp)
                        )
                    )
                },
                label = "Temp (°C)",
                modifier = Modifier.weight(1f)
            )

            TestTextField(
                value = uiState.formData.ph,
                onValueChange = { ph ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(ph = ph)
                        )
                    )
                },
                label = "pH",
                modifier = Modifier.weight(1f)
            )

            TestTextField(
                value = uiState.formData.tds,
                onValueChange = { tds ->
                    onEvent(
                        ApiTestEvent.UpdateFormData(
                            uiState.formData.copy(tds = tds)
                        )
                    )
                },
                label = "TDS (ppm)",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display dos dados
        SensorDataDisplay(sensorData = uiState.sensorData)

        if (uiState.sensorHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("📈 Histórico (${uiState.sensorHistory.size} registros)")
        }
    }
}

@Composable
fun PredictionSection(
    uiState: ApiTestUiState,
    onEvent: (ApiTestEvent) -> Unit
) {
    val selectedTank = uiState.selectedTank
    if (selectedTank?.id == null) return

    TestSection(
        title = "🤖 Predições IA (Tank: ${selectedTank.name})",
        isLoading = uiState.predictionLoading,
        error = uiState.predictionError
    ) {
        TestButton(
            text = "Get Predictions",
            onClick = {
                onEvent(ApiTestEvent.LoadPrediction(selectedTank.id))
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PredictionDisplay(prediction = uiState.prediction)
    }
}