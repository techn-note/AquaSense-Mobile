package com.technote.aquasense.presentation.ui.screens.test

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.data.model.User
import com.technote.aquasense.data.model.WaterPrediction

/**
 * Estados da UI para a tela de testes da API
 * Cada seção tem seu próprio estado para controle independente
 */
data class ApiTestUiState(
    // Auth Section
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val authLoading: Boolean = false,
    val authError: String? = null,

    // Tank Section
    val tanks: List<Tank> = emptyList(),
    val tankLoading: Boolean = false,
    val tankError: String? = null,
    val selectedTank: Tank? = null,

    // Fish Section
    val fishList: List<Fish> = emptyList(),
    val fishLoading: Boolean = false,
    val fishError: String? = null,

    // Sensor Section
    val sensorData: SensorData? = null,
    val sensorHistory: List<SensorData> = emptyList(),
    val sensorLoading: Boolean = false,
    val sensorError: String? = null,

    // Prediction Section
    val prediction: WaterPrediction? = null,
    val predictionLoading: Boolean = false,
    val predictionError: String? = null,

    // Log Section
    val logs: List<String> = emptyList(),

    // Form Data (para inputs temporários)
    val formData: FormData = FormData()
)

/**
 * Dados de formulários temporários para a tela de testes
 */
data class FormData(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val tankName: String = "",
    val tankVolume: String = "",
    val fishName: String = "",
    val fishSpecies: String = "",
    val temperature: String = "",
    val ph: String = "",
    val tds: String = ""
)

/**
 * Eventos que podem ocorrer na tela
 */
sealed class ApiTestEvent {
    // Auth Events
    data class Login(val email: String, val password: String) : ApiTestEvent()
    object Logout : ApiTestEvent()

    // Tank Events
    data class LoadTanks(val userId: String) : ApiTestEvent()
    data class CreateTank(val name: String, val volume: Double, val userId: String) : ApiTestEvent()
    data class UpdateTank(val tankId: String, val name: String, val volume: Double) : ApiTestEvent()
    data class SelectTank(val tank: Tank) : ApiTestEvent()

    // Fish Events
    data class LoadFish(val tankId: String) : ApiTestEvent()
    data class CreateFish(val name: String, val species: String, val tankId: String) : ApiTestEvent()
    data class UpdateFish(val fishId: String, val name: String, val species: String) : ApiTestEvent()

    // Sensor Events
    data class LoadSensorData(val tankId: String) : ApiTestEvent()
    data class LoadSensorHistory(val tankId: String) : ApiTestEvent()
    data class UpdateSensorData(val tankId: String, val temp: Double, val ph: Double, val tds: Double) : ApiTestEvent()

    // Prediction Events
    data class LoadPrediction(val tankId: String) : ApiTestEvent()

    // Form Events
    data class UpdateFormData(val formData: FormData) : ApiTestEvent()

    // Log Events
    object ClearLogs : ApiTestEvent()
}