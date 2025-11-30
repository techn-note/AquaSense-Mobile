package com.technote.aquasense.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.R
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.data.remote.dto.NeuralPredictRequest
import com.technote.aquasense.domain.repository.AIRepository
import com.technote.aquasense.domain.repository.SensorRepository
import com.technote.aquasense.domain.repository.TankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import javax.inject.Inject

@HiltViewModel
class GraphAIViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tankRepository: TankRepository,
    private val sensorRepository: SensorRepository,
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GraphAIUiState(classification = context.getString(R.string.ai_loading)))
    val uiState: StateFlow<GraphAIUiState> = _uiState.asStateFlow()

    fun loadTanks(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            tankRepository.getTanksByUser(userId).fold(
                onSuccess = { tanks ->
                    val firstTank = tanks.firstOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tanks = tanks,
                        selectedTankId = firstTank?.id,
                        selectedTankName = firstTank?.name ?: "",
                        error = null
                    )
                    // Carregar dados do primeiro tanque
                    firstTank?.name?.let { loadAIData(it) }
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Erro ao carregar tanques"
                    )
                }
            )
        }
    }

    fun selectTank(tankId: String?) {
        val selectedTank = _uiState.value.tanks.find { it.id == tankId }
        _uiState.value = _uiState.value.copy(
            selectedTankId = tankId,
            selectedTankName = selectedTank?.name ?: ""
        )
        selectedTank?.name?.let { loadAIData(it) }
    }

    fun selectTankByName(tankName: String?) {
        println("🎯 GraphAIViewModel.selectTankByName chamado com tankName: $tankName")
        
        val currentState = _uiState.value
        println("🎯 Estado atual - selectedTankId: ${currentState.selectedTankId}")
        println("🎯 Lista de tanques disponíveis: ${currentState.tanks.map { it.name }}")
        
        // Encontrar o tanque pelo nome
        val selectedTank = currentState.tanks.find { it.name == tankName }
        
        if (selectedTank != null && selectedTank.id != null) {
            println("🎯 Tanque encontrado: ${selectedTank.name} (ID: ${selectedTank.id})")
            _uiState.value = currentState.copy(
                selectedTankId = selectedTank.id,
                selectedTankName = selectedTank.name ?: ""
            )
            
            // Carregar dados do tanque selecionado
            loadAIData(selectedTank.name ?: "")
        } else {
            println("❌ Tanque não encontrado com nome: $tankName")
        }
    }

    fun loadAIData(tankName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingAI = true)

            // 1. Buscar dados dos sensores (usando o nome do tanque)
            sensorRepository.getSensorData(tankName).fold(
                onSuccess = { sensorData ->
                    println("🔍 Dados dos sensores obtidos: temp=${sensorData.temperature}, ph=${sensorData.ph}, tds=${sensorData.tds}, volume=${sensorData.volume}")
                    
                    // 2. Buscar dados do tanque para capacidade
                    val selectedTank = _uiState.value.tanks.find { it.name == tankName }
                    val tankCapacity = selectedTank?.volume ?: 100.0
                    println("🏗️ Capacidade do tanque: $tankCapacity L")
                    
                    // 3. Calcular porcentagem do volume
                    val volumePercent = calculateVolumePercentage(sensorData.volume, tankCapacity)
                    println("💧 Volume atual: ${sensorData.volume}L, Capacidade: ${tankCapacity}L, Porcentagem: ${volumePercent}%")
                    
                    // 4. Fazer request para neural-predict
                    val neuralRequest = NeuralPredictRequest(
                        temperatura = sensorData.temperature,
                        ph = sensorData.ph,
                        tds = sensorData.tds,
                        fase = "juvenil"
                    )
                    
                    aiRepository.getNeuralPrediction(neuralRequest).fold(
                        onSuccess = { aiResponse ->
                            println("🤖 Resposta da IA: ${aiResponse.classificacao}")
                            println("📊 Scores da IA - pH: ${aiResponse.scoresPercent.ph}%, TDS: ${aiResponse.scoresPercent.tds}%, Temp: ${aiResponse.scoresPercent.temperatura}%")
                            
                            // 5. Usar scores da IA, mas ajustar apenas quando muito baixos/confusos
                            val aiTdsScore = aiResponse.scoresPercent.tds.toFloat()
                            val aiTempScore = aiResponse.scoresPercent.temperatura.toFloat()
                            val aiPhScore = aiResponse.scoresPercent.ph.toFloat()
                            
                            // Só ajustar se a IA retornar valores muito baixos que prejudiquem a visualização
                            val finalTdsScore = if (aiTdsScore <= 5f) {
                                calculateAdjustedScore(sensorData.tds, 100.0, 1000.0, 200.0, 500.0)
                            } else aiTdsScore
                            
                            val finalTempScore = if (aiTempScore <= 5f) {
                                calculateAdjustedScore(sensorData.temperature, 22.0, 32.0, 27.0, 30.0)
                            } else aiTempScore
                            
                            val finalPhScore = if (aiPhScore <= 5f) {
                                calculateAdjustedScore(sensorData.ph, 5.5, 9.0, 6.5, 8.5)
                            } else aiPhScore
                            
                            println("📊 Scores finais - pH: ${finalPhScore}% (IA: ${aiPhScore}%), TDS: ${finalTdsScore}% (IA: ${aiTdsScore}%), Temp: ${finalTempScore}% (IA: ${aiTempScore}%)")
                            
                            // 6. Analisar SHAP values para alertas
                            val alerts = analyzeShapValues(
                                aiResponse.shap,
                                sensorData.temperature,
                                sensorData.ph,
                                sensorData.tds,
                                aiResponse.classificacao
                            )
                            
                            // 7. Adicionar alerta de volume se necessário
                            val volumeAlert = analyzeVolumeAlert(volumePercent)
                            val allAlerts = if (volumeAlert.isNotEmpty()) {
                                alerts + volumeAlert
                            } else alerts
                            
                            // 8. Combinar todas as informações
                            val finalClassification = combineClassificationWithAlerts(
                                aiResponse.classificacao, 
                                allAlerts,
                                volumePercent
                            )
                            
                            _uiState.value = _uiState.value.copy(
                                isLoadingAI = false,
                                tdsScore = finalTdsScore,
                                temperatureScore = finalTempScore,
                                phScore = finalPhScore,
                                volumeScore = volumePercent,
                                classification = finalClassification,
                                error = null
                            )
                        },
                        onFailure = { aiError ->
                            println("❌ Erro na IA: ${aiError.message}")
                            // Fallback: usar cálculo simples se IA falhar
                            val tdsScore = calculateScore(sensorData.tds, 200.0, 500.0)
                            val tempScore = calculateScore(sensorData.temperature, 22.0, 32.0)
                            val phScore = calculateScore(sensorData.ph, 5.5, 9.0)
                            
                            val averageScore = (tdsScore + tempScore + phScore) / 3
                            val fallbackClassification = when {
                                averageScore >= 80 -> context.getString(R.string.ai_normal_no_ia)
                                averageScore >= 60 -> context.getString(R.string.ai_alert_no_ia)
                                else -> context.getString(R.string.ai_crisis_no_ia)
                            }
                            
                            val volumeAlert = analyzeVolumeAlert(volumePercent)
                            val finalClassification = if (volumeAlert.isNotEmpty()) {
                                "$fallbackClassification + $volumeAlert"
                            } else fallbackClassification
                            
                            _uiState.value = _uiState.value.copy(
                                isLoadingAI = false,
                                tdsScore = tdsScore,
                                temperatureScore = tempScore,
                                phScore = phScore,
                                volumeScore = volumePercent,
                                classification = finalClassification,
                                error = context.getString(R.string.ai_unavailable)
                            )
                        }
                    )
                },
                onFailure = { sensorError ->
                    println("❌ Erro nos sensores: ${sensorError.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoadingAI = false,
                        error = sensorError.message ?: "Erro ao buscar dados dos sensores"
                    )
                }
            )
        }
    }

    private fun calculateScore(value: Double?, min: Double, max: Double): Float {
        if (value == null) return 0f
        
        val optimal = (min + max) / 2
        val range = max - min
        
        return when {
            value < min -> {
                val deviation = (min - value) / range
                ((1 - deviation) * 100).toFloat().coerceIn(0f, 100f)
            }
            value > max -> {
                val deviation = (value - max) / range
                ((1 - deviation) * 100).toFloat().coerceIn(0f, 100f)
            }
            else -> {
                val deviation = kotlin.math.abs(value - optimal) / (range / 2)
                ((1 - deviation) * 100).toFloat().coerceIn(0f, 100f)
            }
        }
    }
    
    private fun calculateAdjustedScore(
        value: Double,
        absoluteMin: Double,
        absoluteMax: Double,
        idealMin: Double,
        idealMax: Double
    ): Float {
        println("📊 Calculando score para valor $value (range: $absoluteMin-$absoluteMax, ideal: $idealMin-$idealMax)")
        
        return when {
            // Se está na faixa ideal, score alto
            value >= idealMin && value <= idealMax -> {
                val idealCenter = (idealMin + idealMax) / 2
                val idealRange = idealMax - idealMin
                val distanceFromCenter = abs(value - idealCenter)
                val score = ((1 - (distanceFromCenter / (idealRange / 2))) * 100).toFloat().coerceIn(80f, 100f)
                println("✅ Valor na faixa ideal: ${score}%")
                score
            }
            // Se está abaixo do ideal mas dentro do aceitável
            value >= absoluteMin && value < idealMin -> {
                val distanceFromIdeal = idealMin - value
                val totalBelowRange = idealMin - absoluteMin
                val score = (50 - (distanceFromIdeal / totalBelowRange) * 40).toFloat().coerceIn(10f, 49f)
                println("⚠️ Valor abaixo do ideal: ${score}%")
                score
            }
            // Se está acima do ideal mas dentro do aceitável
            value > idealMax && value <= absoluteMax -> {
                val distanceFromIdeal = value - idealMax
                val totalAboveRange = absoluteMax - idealMax
                val score = (50 - (distanceFromIdeal / totalAboveRange) * 40).toFloat().coerceIn(10f, 49f)
                println("⚠️ Valor acima do ideal: ${score}%")
                score
            }
            // Se está completamente fora dos limites
            value < absoluteMin -> {
                val score = (10 * (value / absoluteMin)).toFloat().coerceIn(1f, 10f)
                println("🚨 Valor muito baixo: ${score}%")
                score
            }
            value > absoluteMax -> {
                val excess = value - absoluteMax
                val score = (10 - (excess / absoluteMax) * 5).toFloat().coerceIn(1f, 10f)
                println("🚨 Valor muito alto: ${score}%")
                score
            }
            else -> {
                println("🤔 Caso não previsto, retornando 50%")
                50f
            }
        }
    }
    
    private fun calculateVolumePercentage(currentVolume: Double, capacity: Double): Float {
        if (capacity <= 0) return 0f
        val percentage = (currentVolume / capacity * 100).toFloat()
        return percentage.coerceIn(0f, 100f) // Nunca ultrapassar 100%
    }
    
    private fun analyzeShapValues(
        shap: com.technote.aquasense.data.remote.dto.Shap,
        temperature: Double,
        ph: Double,
        tds: Double,
        classification: String
    ): List<String> {
        val alerts = mutableListOf<String>()
        
        // Só analisar SHAP se a classificação for diferente de "Normal"
        if (classification.equals("Normal", ignoreCase = true)) {
            return alerts
        }
        
        // Ranges ideais para fase juvenil
        val temperatureRange = 27.0..30.0
        val phRange = 6.5..8.5
        val tdsRange = 200.0..500.0
        
        // Analisar qual parâmetro precisa de mais atenção (SHAP mais negativo = mais problemático)
        val shapValues = mapOf(
            "temperatura" to shap.temperatura,
            "ph" to shap.ph,
            "tds" to shap.tds
        )
        
        // Encontrar o parâmetro com o SHAP mais negativo (mais problemático)
        val mostProblematic = shapValues.minByOrNull { it.value }
        
        println("🔍 Analisando SHAP - Temp: ${shap.temperatura}, pH: ${shap.ph}, TDS: ${shap.tds}")
        println("🎯 Mais problemático: ${mostProblematic?.key} (${mostProblematic?.value})")
        
        when (mostProblematic?.key) {
            "temperatura" -> {
                when {
                    temperature < temperatureRange.start -> 
                        alerts.add(context.getString(R.string.temperature_low, temperature, temperatureRange.start, temperatureRange.endInclusive))
                    temperature > temperatureRange.endInclusive -> 
                        alerts.add(context.getString(R.string.temperature_high, temperature, temperatureRange.start, temperatureRange.endInclusive))
                }
            }
            "ph" -> {
                when {
                    ph < phRange.start -> 
                        alerts.add(context.getString(R.string.ph_low, ph, phRange.start, phRange.endInclusive))
                    ph > phRange.endInclusive -> 
                        alerts.add(context.getString(R.string.ph_high, ph, phRange.start, phRange.endInclusive))
                }
            }
            "tds" -> {
                when {
                    tds < tdsRange.start -> 
                        alerts.add(context.getString(R.string.tds_low, tds, tdsRange.start, tdsRange.endInclusive))
                    tds > tdsRange.endInclusive -> 
                        alerts.add(context.getString(R.string.tds_high, tds, tdsRange.start, tdsRange.endInclusive))
                }
            }
        }
        
        return alerts
    }
    
    private fun analyzeVolumeAlert(volumePercent: Float): String {
        return when {
            volumePercent >= 95f -> context.getString(R.string.volume_critical, volumePercent.toInt())
            volumePercent >= 85f -> context.getString(R.string.volume_alert, volumePercent.toInt())
            else -> ""
        }
    }
    
    private fun combineClassificationWithAlerts(
        aiClassification: String,
        alerts: List<String>,
        volumePercent: Float
    ): String {
        val baseClassification = aiClassification
        
        // Determinar estado geral baseado no volume
        val volumeState = when {
            volumePercent >= 95f -> context.getString(R.string.volume_crisis_state)
            volumePercent >= 85f -> context.getString(R.string.volume_alert_state)
            else -> null
        }
        
        // Verificar se há parâmetros em "Good" quando classificação é "Normal"
        val hasGoodParams = _uiState.value.let { state ->
            (state.tdsScore in 60f..79f) || 
            (state.temperatureScore in 60f..79f) || 
            (state.phScore in 60f..79f)
        }
        
        // Combinar tudo
        val allMessages = mutableListOf<String>()
        allMessages.add(baseClassification)
        
        // Adicionar consideração sobre parâmetros Good se classificação for Normal
        if (baseClassification.contains("Normal", ignoreCase = true) && hasGoodParams) {
            allMessages.add(context.getString(R.string.good_params_consideration))
        }
        
        if (alerts.isNotEmpty()) {
            allMessages.addAll(alerts)
        }
        
        volumeState?.let { state ->
            allMessages.add(context.getString(R.string.volume_in_state, state))
        }
        
        return allMessages.joinToString(" • ")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class GraphAIUiState(
    val isLoading: Boolean = false,
    val isLoadingAI: Boolean = false,
    val tanks: List<Tank> = emptyList(),
    val selectedTankId: String? = null,
    val selectedTankName: String? = null,
    val tdsScore: Float = 0f,
    val temperatureScore: Float = 0f,
    val phScore: Float = 0f,
    val volumeScore: Float = 0f,
    val classification: String = "",
    val error: String? = null
)
