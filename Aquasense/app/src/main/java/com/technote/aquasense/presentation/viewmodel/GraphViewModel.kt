package com.technote.aquasense.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.domain.repository.TankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val tankRepository: TankRepository,
    private val sensorRepository: com.technote.aquasense.domain.repository.SensorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GraphUiState())
    val uiState: StateFlow<GraphUiState> = _uiState.asStateFlow()
    
    // Job para debounce do carregamento de dados
    private var loadDataJob: Job? = null

    init {
        loadGraphData()
    }

    private fun loadGraphData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Usar string vazia para pegar tanques do usuário autenticado
            val userId = ""
            
            tankRepository.getTanksByUser(userId).fold(
                onSuccess = { tanks ->
                    println("🎯 Tanques encontrados para gráficos: ${tanks.size}")
                    
                    if (tanks.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tanks = emptyList(),
                            temperatureData = emptyList(),
                            phData = emptyList(),
                            tdsData = emptyList(),
                            volumeData = emptyList(),
                            error = null
                        )
                        return@fold
                    }

                    // Carregar apenas os tanques, dados serão carregados quando selecionados
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tanks = tanks,
                        selectedTankIds = emptyList(),
                        temperatureData = emptyList(),
                        phData = emptyList(),
                        tdsData = emptyList(),
                        volumeData = emptyList(),
                        error = null
                    )
                    
                    println("📊 Tanques carregados: ${tanks.map { "${it.name} (${it.id})" }}")
                    println("📊 Aguardando seleção de tanques pelo usuário...")
                },
                onFailure = { error ->
                    println("❌ Erro ao carregar tanques: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Erro ao carregar dados dos tanques"
                    )
                }
            )
        }
    }

    fun toggleTankSelection(tankName: String) {
        println("🔄 DEBUG: toggleTankSelection chamado com tankName: '$tankName'")
        
        val currentSelection = _uiState.value.selectedTankIds.toMutableList()
        println("🔄 DEBUG: Seleção atual: $currentSelection")
        
        if (currentSelection.contains(tankName)) {
            // Remover se já selecionado
            currentSelection.remove(tankName)
            println("🔄 DEBUG: Removendo tanque '$tankName'. Nova seleção: $currentSelection")
        } else {
            // Adicionar se não selecionado, mas máximo 3
            if (currentSelection.size < 3) {
                currentSelection.add(tankName)
                println("🔄 DEBUG: Adicionando tanque '$tankName'. Nova seleção: $currentSelection")
            } else {
                println("🔄 DEBUG: Máximo de 3 tanques já selecionados. Ignorando tentativa de adicionar '$tankName'")
                return // Sair da função sem atualizar estado
            }
        }
        
        _uiState.value = _uiState.value.copy(selectedTankIds = currentSelection)
        println("🔄 DEBUG: Estado atualizado com selectedTankIds: ${_uiState.value.selectedTankIds}")
        
        // Cancelar job anterior se existir
        loadDataJob?.cancel()
        
        // Recarregar dados com debounce de 800ms
        if (currentSelection.isNotEmpty()) {
            loadDataJob = viewModelScope.launch {
                delay(800) // Aguardar 800ms antes de carregar dados
                loadSelectedTanksData(currentSelection)
            }
        } else {
            // Se nenhum tanque selecionado, limpar dados imediatamente
            _uiState.value = _uiState.value.copy(
                temperatureData = emptyList(),
                phData = emptyList(),
                tdsData = emptyList(),
                volumeData = emptyList()
            )
        }
    }

    private fun loadSelectedTanksData(selectedTankNames: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val selectedTanks = _uiState.value.tanks.filter { tank -> 
                selectedTankNames.contains(tank.name) 
            }
            
            val historyByTank = mutableMapOf<String, List<com.technote.aquasense.data.model.SensorData>>()
            
            // Coleta dados apenas dos tanques selecionados
            for (tank in selectedTanks) {
                try {
                    // IMPORTANTE: A API precisa do NOME do tanque, não do ID
                    val tankName = tank.name
                    println("📊 Carregando dados para tanque selecionado: ${tankName}")
                    val result = sensorRepository.getSensorHistory(tankName)
                    val sensorHistory = result.getOrNull() ?: emptyList()
                    historyByTank[tank.name] = sensorHistory.take(10)
                    println("📊 Histórico ${tank.name}: ${sensorHistory.size} registros")
                } catch (e: Exception) {
                    println("❌ Erro ao buscar histórico ${tank.name}: ${e.message}")
                    historyByTank[tank.name] = emptyList()
                }
            }
            
            val tankNames = selectedTanks.map { it.name }
            val processedData = processRealSensorData(historyByTank, tankNames)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                temperatureData = processedData.temperature,
                phData = processedData.ph,
                tdsData = processedData.tds,
                volumeData = processedData.volume
            )
        }
    }

    private fun processRealSensorData(
        historyByTank: Map<String, List<com.technote.aquasense.data.model.SensorData>>,
        tankNames: List<String>
    ): SensorGraphData {
        // Coletar todas as datas únicas e convertê-las para formato legível
        val allTimestamps = historyByTank.values
            .flatMap { it.mapNotNull { sensor -> sensor.createdAt } }
            .distinct()
            .sorted()
        
        println("📊 DEBUG: Timestamps encontrados: $allTimestamps")
        
        // Converter timestamps para formato HH:mm 
        val timeLabels = allTimestamps.map { timestamp ->
            formatTimestampToTime(timestamp)
        }.take(6) // Máximo 6 pontos no gráfico

        println("📊 DEBUG: TimeLabels processados: $timeLabels")
        println("📊 DEBUG: Dados por tanque: ${historyByTank.mapValues { (tank, data) -> 
            data.map { "temp:${it.temperature}, ph:${it.ph}, tds:${it.tds}, volume:${it.volume}" }
        }}")
        
        // Log detalhado de cada sensor data
        historyByTank.forEach { (tankName, sensorList) ->
            println("📊 TANK $tankName: ${sensorList.size} registros")
            sensorList.forEach { sensor ->
                println("  🔍 Sensor: timestamp=${sensor.createdAt}, temp=${sensor.temperature}, ph=${sensor.ph}, tds=${sensor.tds}, volume=${sensor.volume}")
            }
        }
        
        // Se não há labels, usar dados atuais
        val finalLabels = if (timeLabels.isEmpty()) {
            listOf("Agora")
        } else {
            timeLabels
        }

        // Função para processar valores reais (sem normalização)
        fun processValue(type: String, value: Double): Float {
            return when (type.lowercase()) {
                "temperatura" -> value.coerceIn(0.0, 50.0).toFloat() // Valores reais °C 0-50
                "ph" -> value.coerceIn(0.0, 14.0).toFloat() // Valores reais pH 0-14
                "tds" -> value.coerceIn(0.0, 2000.0).toFloat() // Valores reais TDS 0-2000 ppm
                "volume" -> value.coerceIn(0.0, 200.0).toFloat() // Valores reais litros 0-200L
                else -> value.toFloat()
            }
        }

        // Construir dados para cada tipo de sensor
        val temperatureData = if (timeLabels.isEmpty()) {
            // Usar dados mais recentes se não há timeline - SÓ dados reais
            finalLabels.map { label ->
                label to tankNames.map { tankName ->
                    val tankHistory = historyByTank[tankName] ?: emptyList()
                    val latestTemp = tankHistory.lastOrNull()?.temperature ?: 0.0
                    val processedTemp = if (latestTemp > 0) processValue("temperatura", latestTemp) else 0f // 0f = sem dados reais
                    println("📊 TEMP: Tanque $tankName -> $latestTemp°C -> ${processedTemp}°C")
                    processedTemp
                }
            }
        } else {
            // Mapear por timeline
            allTimestamps.take(6).mapIndexed { index, timestamp ->
                val timeLabel = if (index < timeLabels.size) timeLabels[index] else "Agora"
                timeLabel to tankNames.map { tankName ->
                    val sensor = historyByTank[tankName]?.find { it.createdAt == timestamp }
                    val temp = sensor?.temperature ?: 0.0
                    val processedTemp = if (temp > 0) processValue("temperatura", temp) else 0f // 0f = sem dados reais
                    println("📊 TEMP [$timeLabel]: Tanque $tankName -> $temp°C -> ${processedTemp}°C")
                    processedTemp
                }
            }
        }

        val phData = if (timeLabels.isEmpty()) {
            finalLabels.map { label ->
                label to tankNames.map { tankName ->
                    val tankHistory = historyByTank[tankName] ?: emptyList()
                    val latestPh = tankHistory.lastOrNull()?.ph ?: 0.0
                    val processedPh = if (latestPh > 0) processValue("ph", latestPh) else 0f // 0f = sem dados reais
                    println("📊 PH: Tanque $tankName -> $latestPh -> ${processedPh}")
                    processedPh
                }
            }
        } else {
            allTimestamps.take(6).mapIndexed { index, timestamp ->
                val timeLabel = if (index < timeLabels.size) timeLabels[index] else "Agora"
                timeLabel to tankNames.map { tankName ->
                    val sensor = historyByTank[tankName]?.find { it.createdAt == timestamp }
                    val ph = sensor?.ph ?: 0.0
                    val processedPh = if (ph > 0) processValue("ph", ph) else 0f // 0f = sem dados reais
                    println("📊 PH [$timeLabel]: Tanque $tankName -> $ph -> ${processedPh}")
                    processedPh
                }
            }
        }

        val tdsData = if (timeLabels.isEmpty()) {
            finalLabels.map { label ->
                label to tankNames.map { tankName ->
                    val tankHistory = historyByTank[tankName] ?: emptyList()
                    val latestTds = tankHistory.lastOrNull()?.tds ?: 0.0
                    val processedTds = if (latestTds > 0) processValue("tds", latestTds) else 0f // 0f = sem dados reais
                    println("📊 TDS: Tanque $tankName -> $latestTds ppm -> ${processedTds} ppm")
                    processedTds
                }
            }
        } else {
            allTimestamps.take(6).mapIndexed { index, timestamp ->
                val timeLabel = if (index < timeLabels.size) timeLabels[index] else "Agora"
                timeLabel to tankNames.map { tankName ->
                    val sensor = historyByTank[tankName]?.find { it.createdAt == timestamp }
                    val tds = sensor?.tds ?: 0.0
                    val processedTds = if (tds > 0) processValue("tds", tds) else 0f // 0f = sem dados reais
                    println("📊 TDS [$timeLabel]: Tanque $tankName -> $tds ppm -> ${processedTds} ppm")
                    processedTds
                }
            }
        }

        // Volume - buscar dados reais da API
        val volumeData = if (timeLabels.isEmpty()) {
            // Usar dados mais recentes se não há timeline
            finalLabels.map { label ->
                label to tankNames.map { tankName ->
                    val tankHistory = historyByTank[tankName] ?: emptyList()
                    val latestVolume = tankHistory.lastOrNull()?.volume ?: 0.0
                    val processedVolume = if (latestVolume > 0) processValue("volume", latestVolume) else 0f
                    println("📊 VOLUME: Tanque $tankName -> $latestVolume L -> ${processedVolume} L")
                    processedVolume
                }
            }
        } else {
            // Mapear por timeline
            allTimestamps.take(6).mapIndexed { index, timestamp ->
                val timeLabel = if (index < timeLabels.size) timeLabels[index] else "Agora"
                timeLabel to tankNames.map { tankName ->
                    val sensor = historyByTank[tankName]?.find { it.createdAt == timestamp }
                    val volume = sensor?.volume ?: 0.0
                    val processedVolume = if (volume > 0) processValue("volume", volume) else 0f
                    println("📊 VOLUME [$timeLabel]: Tanque $tankName -> $volume L -> ${processedVolume} L")
                    processedVolume
                }
            }
        }

        println("📊 FINAL: Dados processados - Temp: ${temperatureData.size} pontos, pH: ${phData.size} pontos, TDS: ${tdsData.size} pontos, Vol: ${volumeData.size} pontos")
        
        return SensorGraphData(
            temperature = temperatureData,
            ph = phData, 
            tds = tdsData,
            volume = volumeData
        )
    }

    private fun formatTimestampToTime(timestamp: String): String {
        return try {
            println("🕐 DEBUG: Formatando timestamp: '$timestamp'")
            
            // Diferentes formatos de entrada da API
            val inputFormat = when {
                timestamp.contains("T") && !timestamp.contains("Z") -> {
                    // Formato: "2025-10-04T15:00:00"
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                }
                timestamp.contains("T") && timestamp.contains("Z") -> {
                    // Formato: "2025-10-04T15:00:00Z"
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                }
                timestamp.contains("T") && timestamp.contains(".") -> {
                    // Formato: "2025-10-04T15:00:00.000Z"
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                }
                timestamp.contains("-") && timestamp.length > 16 -> {
                    // Formato: "2025-10-04 15:00:00"
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                }
                else -> {
                    // Fallback padrão
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                }
            }
            
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(timestamp)
            val result = outputFormat.format(date ?: Date())
            
            println("🕐 DEBUG: Timestamp convertido: '$timestamp' -> '$result'")
            result
        } catch (e: Exception) {
            println("❌ Erro ao formatar timestamp '$timestamp': ${e.message}")
            
            // Fallback melhorado: tentar extrair hora manualmente
            try {
                when {
                    timestamp.contains("T") -> {
                        // Extrair hora de "2025-10-04T15:00:00"
                        val timePart = timestamp.substringAfter("T").substringBefore("Z")
                        if (timePart.contains(":")) {
                            timePart.take(5) // "15:00"
                        } else {
                            "15:00" // Fallback
                        }
                    }
                    timestamp.contains(" ") && timestamp.contains(":") -> {
                        // Extrair hora de "2025-10-04 15:00:00"
                        timestamp.substringAfter(" ").take(5) // "15:00"
                    }
                    else -> "Agora"
                }
            } catch (e2: Exception) {
                "Agora"
            }
        }
    }



    fun refreshData() {
        loadGraphData()
    }



    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class GraphUiState(
    val isLoading: Boolean = false,
    val tanks: List<com.technote.aquasense.data.model.Tank> = emptyList(),
    val selectedTankIds: List<String> = emptyList(),
    val temperatureData: List<Pair<String, List<Float>>> = emptyList(),
    val phData: List<Pair<String, List<Float>>> = emptyList(),
    val tdsData: List<Pair<String, List<Float>>> = emptyList(),
    val volumeData: List<Pair<String, List<Float>>> = emptyList(),
    val error: String? = null
)

data class SensorGraphData(
    val temperature: List<Pair<String, List<Float>>>,
    val ph: List<Pair<String, List<Float>>>,
    val tds: List<Pair<String, List<Float>>>,
    val volume: List<Pair<String, List<Float>>>
)