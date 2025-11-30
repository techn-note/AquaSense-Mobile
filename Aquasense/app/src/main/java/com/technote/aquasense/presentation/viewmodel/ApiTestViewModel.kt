package com.technote.aquasense.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.usecase.auth.LoginUseCase
import com.technote.aquasense.domain.usecase.auth.RegisterUseCase
import com.technote.aquasense.domain.usecase.auth.LogoutUseCase
import com.technote.aquasense.domain.usecase.tank.GetTanksByUserUseCase
import com.technote.aquasense.domain.usecase.tank.CreateTankUseCase
import com.technote.aquasense.domain.usecase.tank.UpdateTankUseCase
import com.technote.aquasense.domain.usecase.fish.GetFishByTankUseCase
import com.technote.aquasense.domain.usecase.fish.CreateFishUseCase
import com.technote.aquasense.domain.usecase.fish.UpdateFishUseCase
import com.technote.aquasense.domain.usecase.sensor.GetSensorDataUseCase
import com.technote.aquasense.domain.usecase.sensor.GetSensorHistoryUseCase
import com.technote.aquasense.domain.usecase.sensor.UpdateSensorDataUseCase
import com.technote.aquasense.domain.usecase.prediction.GetPredictionUseCase
import com.technote.aquasense.presentation.ui.screens.test.ApiTestUiState
import com.technote.aquasense.presentation.ui.screens.test.ApiTestEvent
import com.technote.aquasense.presentation.ui.screens.test.FormData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel para a tela de testes da API
 * Orquestra todos os Use Cases para testar endpoints
 */
@HiltViewModel
class ApiTestViewModel @Inject constructor(
    // Auth Use Cases
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,

    // Tank Use Cases
    private val getTanksByUserUseCase: GetTanksByUserUseCase,
    private val createTankUseCase: CreateTankUseCase,
    private val updateTankUseCase: UpdateTankUseCase,

    // Fish Use Cases
    private val getFishByTankUseCase: GetFishByTankUseCase,
    private val createFishUseCase: CreateFishUseCase,
    private val updateFishUseCase: UpdateFishUseCase,

    // Sensor Use Cases
    private val getSensorDataUseCase: GetSensorDataUseCase,
    private val getSensorHistoryUseCase: GetSensorHistoryUseCase,
    private val updateSensorDataUseCase: UpdateSensorDataUseCase,

    // Prediction Use Cases
    private val getPredictionUseCase: GetPredictionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiTestUiState())
    val uiState: StateFlow<ApiTestUiState> = _uiState.asStateFlow()

    /**
     * Processa eventos da UI
     */
    fun onEvent(event: ApiTestEvent) {
        when (event) {
            // Auth Events
            is ApiTestEvent.Login -> login(event.email, event.password)
            is ApiTestEvent.Logout -> logout()

            // Tank Events
            is ApiTestEvent.LoadTanks -> loadTanks(event.userId)
            is ApiTestEvent.CreateTank -> createTank(event.name, event.volume, event.userId)
            is ApiTestEvent.UpdateTank -> updateTank(event.tankId, event.name, event.volume)
            is ApiTestEvent.SelectTank -> selectTank(event.tank)

            // Fish Events
            is ApiTestEvent.LoadFish -> loadFish(event.tankId)
            is ApiTestEvent.CreateFish -> createFish(event.name, event.species, event.tankId)
            is ApiTestEvent.UpdateFish -> updateFish(event.fishId, event.name, event.species)

            // Sensor Events
            is ApiTestEvent.LoadSensorData -> loadSensorData(event.tankId)
            is ApiTestEvent.LoadSensorHistory -> loadSensorHistory(event.tankId)
            is ApiTestEvent.UpdateSensorData -> updateSensorData(event.tankId, event.temp, event.ph, event.tds)

            // Prediction Events
            is ApiTestEvent.LoadPrediction -> loadPrediction(event.tankId)

            // Form Events
            is ApiTestEvent.UpdateFormData -> updateFormData(event.formData)

            // Log Events
            is ApiTestEvent.ClearLogs -> clearLogs()
        }
    }

    // ===========================================
    // AUTH FUNCTIONS
    // ===========================================

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true, authError = null)

            loginUseCase(email, password).fold(
                onSuccess = { (token, user) ->
                    _uiState.value = _uiState.value.copy(
                        authLoading = false,
                        isLoggedIn = true,
                        currentUser = user
                    )
                    addLog("LOGIN SUCCESS - User: ${user.name}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        authLoading = false,
                        authError = error.message
                    )
                    addLog("LOGIN ERROR - ${error.message}")
                }
            )
        }
    }


    private fun logout() {
        viewModelScope.launch {
            logoutUseCase().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = false,
                        currentUser = null,
                        tanks = emptyList(),
                        fishList = emptyList(),
                        sensorData = null,
                        prediction = null
                    )
                    addLog("LOGOUT SUCCESS")
                },
                onFailure = { error ->
                    addLog("LOGOUT ERROR - ${error.message}")
                }
            )
        }
    }

    // ===========================================
    // TANK FUNCTIONS
    // ===========================================

    private fun loadTanks(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tankLoading = true, tankError = null)

            getTanksByUserUseCase(userId).fold(
                onSuccess = { tanks ->
                    _uiState.value = _uiState.value.copy(
                        tankLoading = false,
                        tanks = tanks
                    )
                    addLog("LOAD TANKS SUCCESS - ${tanks.size} tanks found")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        tankLoading = false,
                        tankError = error.message
                    )
                    addLog("LOAD TANKS ERROR - ${error.message}")
                }
            )
        }
    }

    private fun createTank(name: String, volume: Double, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tankLoading = true, tankError = null)

            createTankUseCase(name, volume, 1.0, userId).fold(
                onSuccess = { tank ->
                    _uiState.value = _uiState.value.copy(
                        tankLoading = false,
                        tanks = _uiState.value.tanks + tank
                    )
                    addLog("CREATE TANK SUCCESS - ${tank.name}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        tankLoading = false,
                        tankError = error.message
                    )
                    addLog("CREATE TANK ERROR - ${error.message}")
                }
            )
        }
    }

    private fun updateTank(tankId: String, name: String, volume: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tankLoading = true, tankError = null)

            updateTankUseCase(tankId, name, volume, 1.0).fold(
                onSuccess = { updatedTank ->
                    val updatedTanks = _uiState.value.tanks.map { tank ->
                        if (tank.id == tankId) updatedTank else tank
                    }
                    _uiState.value = _uiState.value.copy(
                        tankLoading = false,
                        tanks = updatedTanks
                    )
                    addLog("UPDATE TANK SUCCESS - ${updatedTank.name}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        tankLoading = false,
                        tankError = error.message
                    )
                    addLog("UPDATE TANK ERROR - ${error.message}")
                }
            )
        }
    }

    private fun selectTank(tank: Tank) {
        _uiState.value = _uiState.value.copy(selectedTank = tank)
        addLog("TANK SELECTED - ${tank.name}")
    }

    // ===========================================
    // FISH FUNCTIONS
    // ===========================================

    private fun loadFish(tankId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(fishLoading = true, fishError = null)

            getFishByTankUseCase(tankId).fold(
                onSuccess = { fishList ->
                    _uiState.value = _uiState.value.copy(
                        fishLoading = false,
                        fishList = fishList
                    )
                    addLog("LOAD FISH SUCCESS - ${fishList.size} fish found")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        fishLoading = false,
                        fishError = error.message
                    )
                    addLog("LOAD FISH ERROR - ${error.message}")
                }
            )
        }
    }

    private fun createFish(name: String, species: String, tankId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(fishLoading = true, fishError = null)

            // Valores padrão para teste
            val idade = 12
            val peso = 2.5f
            val quantidade = 10
            val userId = "test_user_id"

            createFishUseCase(name, idade, species, peso, quantidade, userId).fold(
                onSuccess = { fish ->
                    _uiState.value = _uiState.value.copy(
                        fishLoading = false,
                        fishList = _uiState.value.fishList + fish
                    )
                    addLog("CREATE FISH SUCCESS - ${fish.name}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        fishLoading = false,
                        fishError = error.message
                    )
                    addLog("CREATE FISH ERROR - ${error.message}")
                }
            )
        }
    }

    private fun updateFish(fishId: String, name: String, species: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(fishLoading = true, fishError = null)

            // Valores padrão para teste
            val idade = 18
            val peso = 3.0f
            val quantidade = 15

            updateFishUseCase(fishId, name, idade, species, peso, quantidade).fold(
                onSuccess = { updatedFish ->
                    val updatedFishList = _uiState.value.fishList.map { fish ->
                        if (fish.id == fishId) updatedFish else fish
                    }
                    _uiState.value = _uiState.value.copy(
                        fishLoading = false,
                        fishList = updatedFishList
                    )
                    addLog("UPDATE FISH SUCCESS - ${updatedFish.name}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        fishLoading = false,
                        fishError = error.message
                    )
                    addLog("UPDATE FISH ERROR - ${error.message}")
                }
            )
        }
    }

    // ===========================================
    // SENSOR FUNCTIONS
    // ===========================================

    private fun loadSensorData(tankId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sensorLoading = true, sensorError = null)

            getSensorDataUseCase(tankId).fold(
                onSuccess = { sensorData ->
                    _uiState.value = _uiState.value.copy(
                        sensorLoading = false,
                        sensorData = sensorData
                    )
                    addLog("LOAD SENSOR DATA SUCCESS - T:${sensorData.temperature}°C pH:${sensorData.ph}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        sensorLoading = false,
                        sensorError = error.message
                    )
                    addLog("LOAD SENSOR DATA ERROR - ${error.message}")
                }
            )
        }
    }

    private fun loadSensorHistory(tankId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sensorLoading = true, sensorError = null)

            getSensorHistoryUseCase(tankId).fold(
                onSuccess = { history ->
                    _uiState.value = _uiState.value.copy(
                        sensorLoading = false,
                        sensorHistory = history
                    )
                    addLog("LOAD SENSOR HISTORY SUCCESS - ${history.size} records")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        sensorLoading = false,
                        sensorError = error.message
                    )
                    addLog("LOAD SENSOR HISTORY ERROR - ${error.message}")
                }
            )
        }
    }

    private fun updateSensorData(tankId: String, temp: Double, ph: Double, tds: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sensorLoading = true, sensorError = null)

            updateSensorDataUseCase(tankId, temp, ph, tds).fold(
                onSuccess = { sensorData ->
                    _uiState.value = _uiState.value.copy(
                        sensorLoading = false,
                        sensorData = sensorData
                    )
                    addLog("UPDATE SENSOR DATA SUCCESS - T:${temp}°C pH:${ph}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        sensorLoading = false,
                        sensorError = error.message
                    )
                    addLog("UPDATE SENSOR DATA ERROR - ${error.message}")
                }
            )
        }
    }

    // ===========================================
    // PREDICTION FUNCTIONS
    // ===========================================

    private fun loadPrediction(tankId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(predictionLoading = true, predictionError = null)

            getPredictionUseCase(tankId).fold(
                onSuccess = { prediction ->
                    _uiState.value = _uiState.value.copy(
                        predictionLoading = false,
                        prediction = prediction
                    )
                    addLog("LOAD PREDICTION SUCCESS - Quality: ${prediction.prediction.waterQuality}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        predictionLoading = false,
                        predictionError = error.message
                    )
                    addLog("LOAD PREDICTION ERROR - ${error.message}")
                }
            )
        }
    }

    // ===========================================
    // UTILITY FUNCTIONS
    // ===========================================

    private fun updateFormData(formData: FormData) {
        _uiState.value = _uiState.value.copy(formData = formData)
    }

    private fun clearLogs() {
        _uiState.value = _uiState.value.copy(logs = emptyList())
    }

    private fun addLog(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "$timestamp - $message"

        val currentLogs = _uiState.value.logs
        val newLogs = if (currentLogs.size >= 50) {
            // Manter apenas os últimos 50 logs
            currentLogs.drop(1) + logEntry
        } else {
            currentLogs + logEntry
        }

        _uiState.value = _uiState.value.copy(logs = newLogs)

        // Log no console também para debug
        println("🧪 API Test: $logEntry")
    }
}