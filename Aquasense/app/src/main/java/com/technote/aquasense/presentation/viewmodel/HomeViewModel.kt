package com.technote.aquasense.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.R
import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.data.model.User
import com.technote.aquasense.domain.repository.SensorRepository
import com.technote.aquasense.domain.repository.TankRepository
import com.technote.aquasense.domain.usecase.auth.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tankRepository: TankRepository,
    private val sensorRepository: SensorRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(statusMessage = context.getString(R.string.home_loading)))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * Inicializa os dados da tela Home
     * Busca o usuário atual e carrega seus tanques
     */
    fun initialize() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Verificar se está logado
            val isLoggedIn = getCurrentUserUseCase.isUserLoggedIn()
            println("🔐 Verificando login - isUserLoggedIn: $isLoggedIn")
            
            if (!isLoggedIn) {
                println("❌ Usuário não autenticado - redirecionando para login")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Usuário não autenticado",
                    isAuthenticated = false
                )
                return@launch
            }
            
            // Se chegou até aqui, está autenticado
            _uiState.value = _uiState.value.copy(isAuthenticated = true)
            
            println("✅ Usuário autenticado - buscando perfil...")
            
            // Buscar perfil do usuário
            getCurrentUserUseCase.getUserProfile().fold(
                onSuccess = { user ->
                    println("✅ Perfil do usuário carregado: ${user.name} (ID: ${user.id})")
                    _uiState.value = _uiState.value.copy(
                        currentUser = user,
                        isAuthenticated = true
                    )
                    // Carregar tanques do usuário
                    user.id?.let { userId ->
                        println("🔄 Carregando tanques para usuário: $userId")
                        loadUserTanks(userId)
                    } ?: run {
                        println("❌ ID do usuário não encontrado")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "ID do usuário não encontrado"
                        )
                    }
                },
                onFailure = { error ->
                    println("❌ Erro ao carregar perfil: ${error.message}")
                    // Não desautenticar imediatamente, pode ser erro de rede temporário
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Erro ao carregar perfil: ${error.message}",
                        isAuthenticated = true // Manter autenticado, apenas mostrar erro
                    )
                }
            )
        }
    }

    private fun loadUserTanks(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            println("🔄 Carregando tanques para usuário: $userId")

            tankRepository.getTanksByUser(userId).fold(
                onSuccess = { tanks ->
                    println("✅ Tanques carregados com sucesso: ${tanks.size} tanques")
                    tanks.forEachIndexed { index, tank ->
                        println("   Tank[$index]: ID=${tank.id}, Name=${tank.name}")
                    }
                    
                    // Manter o tanque selecionado atual se existir, senão selecionar o primeiro
                    val currentSelectedId = _uiState.value.selectedTankId
                    val newSelectedId = if (currentSelectedId != null && tanks.any { it.id == currentSelectedId }) {
                        println("🎯 Mantendo tanque atual selecionado: $currentSelectedId")
                        currentSelectedId // Manter seleção atual
                    } else {
                        val firstTankId = tanks.firstOrNull()?.id
                        println("🎯 Selecionando primeiro tanque: $firstTankId")
                        firstTankId // Selecionar primeiro tanque
                    }
                    
                    // IMPORTANTE: Só atualizar selectedTankId se for diferente do atual
                    // para evitar que a UI seja redefinida indevidamente
                    val shouldUpdateSelectedTankId = _uiState.value.selectedTankId != newSelectedId
                    val newSelectedName = tanks.find { it.id == newSelectedId }?.name
                        ?: tanks.firstOrNull()?.name
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tanks = tanks,
                        selectedTankId = if (shouldUpdateSelectedTankId) newSelectedId else _uiState.value.selectedTankId,
                        selectedTankName = newSelectedName,
                        error = null
                    )
                    
                    println("🎯 Estado final após carregar tanques - selectedTankName: ${_uiState.value.selectedTankName}")
                    
                    // Carregar dados do tanque selecionado usando o NOME
                    if (newSelectedName != null) {
                        println("🎯 Carregando dados para tanque: $newSelectedName")
                        loadSensorData(newSelectedName)
                    } else {
                        println("⚠️ Nenhum tanque selecionado para carregar dados")
                    }
                },
                onFailure = { error ->
                    println("❌ Erro ao carregar tanques: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Erro ao carregar tanques"
                    )
                }
            )
        }
    }

    fun selectTankByName(tankName: String?) {
        println("🎯 HomeViewModel.selectTankByName chamado com tankName: $tankName")
        
        val currentState = _uiState.value
        println("🎯 Estado atual - selectedTankName: ${currentState.selectedTankName}")
        println("🎯 Lista de tanques disponíveis: ${currentState.tanks.map { it.name }}")
        
        // Atualizar o estado com o novo nome selecionado
        _uiState.value = currentState.copy(selectedTankName = tankName)
        println("🎯 Estado atualizado - novo selectedTankName: ${_uiState.value.selectedTankName}")
        
        // Carregar dados do tanque selecionado
        if (tankName != null) {
            println("🎯 Carregando dados para tanque: $tankName")
            loadSensorData(tankName)
        } else {
            println("❌ Nome do tanque é null")
        }
    }

    fun selectTank(tankId: String?) {
        println("🎯 HomeViewModel.selectTank chamado com tankId: $tankId")
        
        val currentState = _uiState.value
        println("🎯 Estado atual - selectedTankId: ${currentState.selectedTankId}")
        println("🎯 Lista de tanques disponíveis: ${currentState.tanks.map { "${it.name}(${it.id})" }}")
        
        // Atualizar o estado primeiro
        _uiState.value = currentState.copy(selectedTankId = tankId)
        println("🎯 Estado atualizado - novo selectedTankId: ${_uiState.value.selectedTankId}")
        
        // Encontrar o tanque selecionado usando o tankId passado como parâmetro
        val selectedTank = currentState.tanks.find { it.id == tankId }
        if (selectedTank != null) {
            println("🎯 Tanque encontrado: ${selectedTank.name} (ID: ${selectedTank.id})")
            loadSensorData(selectedTank.name) // API Flask espera nome, não ID
        } else {
            println("❌ Tanque não encontrado com ID: $tankId")
            println("❌ Tanques disponíveis: ${currentState.tanks.joinToString { "${it.name}(${it.id})" }}")
        }
    }

    fun loadSensorData(tankName: String) {
        viewModelScope.launch {
            println("🔄 Carregando dados dos sensores para tanque: $tankName")
            _uiState.value = _uiState.value.copy(isLoadingSensors = true)

            sensorRepository.getSensorData(tankName).fold(
                onSuccess = { sensorData ->
                    println("✅ Dados dos sensores carregados com sucesso:")
                    println("   - Temperatura: ${sensorData.temperature}°C")
                    println("   - pH: ${sensorData.ph}")
                    println("   - TDS: ${sensorData.tds} PPM")
                    println("   - Volume: ${sensorData.volume} L")
                    
                    val health = calculateOverallHealth(sensorData)
                    val status = determineStatusMessage(sensorData)
                    
                    println("📊 Saúde geral calculada: ${health}%")
                    println("📊 Status: $status")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoadingSensors = false,
                        sensorData = sensorData,
                        overallHealth = health,
                        statusMessage = status,
                        error = null
                    )
                },
                onFailure = { error ->
                    println("❌ Erro ao carregar dados dos sensores: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoadingSensors = false,
                        error = error.message ?: "Erro ao carregar dados dos sensores"
                    )
                }
            )
        }
    }

    private fun calculateOverallHealth(sensorData: SensorData?): Float {
        if (sensorData == null) return 0f

        val tempProgress = calculateProgress(
            value = sensorData.temperature,
            min = 18.0,
            max = 28.0
        )
        val phProgress = calculateProgress(
            value = sensorData.ph,
            min = 6.8,
            max = 7.2
        )
        val tdsProgress = calculateProgress(
            value = sensorData.tds,
            min = 200.0,
            max = 400.0
        )
        val volumeProgress = calculateProgress(
            value = sensorData.volume,
            min = 50.0,  // Volume mínimo
            max = 200.0  // Volume máximo
        )

        // Incluir volume no cálculo se disponível, senão usar apenas os 3 anteriores
        return if (sensorData.volume > 0) {
            (tempProgress + phProgress + tdsProgress + volumeProgress) / 4f
        } else {
            (tempProgress + phProgress + tdsProgress) / 3f
        }
    }

    private fun calculateProgress(value: Double?, min: Double, max: Double): Float {
        if (value == null) return 0f
        return when {
            value < min -> ((value / min) * 100).toFloat()
            value > max -> (((max - value) / max) * 100 + 100).toFloat()
            else -> 100f
        }.coerceIn(0f, 100f)
    }

    private fun determineStatusMessage(sensorData: SensorData?): String {
        if (sensorData == null) return context.getString(R.string.home_loading)

        val issues = mutableListOf<String>()

        // Verificar temperatura
        if (sensorData.temperature != null) {
            when {
                sensorData.temperature < 18.0 -> issues.add(context.getString(R.string.home_temperature_low))
                sensorData.temperature > 28.0 -> issues.add(context.getString(R.string.home_temperature_high))
            }
        }

        // Verificar pH
        if (sensorData.ph != null) {
            when {
                sensorData.ph < 6.8 -> issues.add(context.getString(R.string.home_ph_low))
                sensorData.ph > 7.2 -> issues.add(context.getString(R.string.home_ph_high))
            }
        }

        // Verificar TDS
        if (sensorData.tds != null) {
            when {
                sensorData.tds < 200.0 -> issues.add(context.getString(R.string.home_tds_low))
                sensorData.tds > 400.0 -> issues.add(context.getString(R.string.home_tds_high))
            }
        }

        return if (issues.isEmpty()) {
            context.getString(R.string.home_all_good)
        } else {
            context.getString(R.string.home_attention, issues.joinToString(", "))
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoadingSensors: Boolean = false,
    val isAuthenticated: Boolean = true,
    val currentUser: User? = null,
    val tanks: List<Tank> = emptyList(),
    val selectedTankId: String? = null,
    val selectedTankName: String? = null, // Novo campo para trabalhar com nomes
    val sensorData: SensorData? = null,
    val overallHealth: Float = 0f,
    val statusMessage: String = "",
    val error: String? = null
)
