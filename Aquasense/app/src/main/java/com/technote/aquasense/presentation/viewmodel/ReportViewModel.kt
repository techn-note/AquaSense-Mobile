package com.technote.aquasense.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.usecase.auth.GetCurrentUserUseCase
import com.technote.aquasense.domain.usecase.sensor.GetAllSensorDataUseCase
import com.technote.aquasense.domain.usecase.tank.GetTanksByUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getTanksByUserUseCase: GetTanksByUserUseCase,
    private val getAllSensorDataUseCase: GetAllSensorDataUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    data class ReportUiState(
        val tanks: List<Tank> = emptyList(),
        val selectedSensorType: String? = null,
        val selectedTankId: String? = null,
        val selectedTankName: String? = null,
        val isGenerating: Boolean = false,
        val message: String? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadTanks()
    }

    private fun loadTanks() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserUseCase.getUserId() ?: ""
                val result = getTanksByUserUseCase(userId)
                result.fold(
                    onSuccess = { tanks ->
                        _uiState.value = _uiState.value.copy(tanks = tanks)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Erro ao carregar tanques: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun selectSensorType(sensorType: String) {
        _uiState.value = _uiState.value.copy(selectedSensorType = sensorType)
    }

    fun selectTank(tankId: String, tankName: String) {
        _uiState.value = _uiState.value.copy(
            selectedTankId = tankId,
            selectedTankName = tankName
        )
    }

    fun generateReport(context: Context) {
        val sensorType = _uiState.value.selectedSensorType ?: run {
            _uiState.value = _uiState.value.copy(error = "Nenhum tipo de sensor selecionado")
            return
        }
        val tankId = _uiState.value.selectedTankId ?: run {
            _uiState.value = _uiState.value.copy(error = "Nenhum tanque selecionado")
            return
        }
        val tankName = _uiState.value.selectedTankName ?: "Tanque_Desconhecido"

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

            try {
                println("DEBUG: Iniciando geração de relatório")
                println("DEBUG: Sensor: $sensorType, Tanque: $tankId ($tankName)")
                
                val result = getAllSensorDataUseCase(tankId)
                
                result.fold(
                    onSuccess = { sensorHistoryList ->
                        println("DEBUG: Dados recebidos: ${sensorHistoryList.size} registros")
                        
                        // Debug: mostrar alguns dados
                        sensorHistoryList.take(3).forEach { data ->
                            println("DEBUG: Amostra - Temp: ${data.temperature}, pH: ${data.ph}, TDS: ${data.tds}, Volume: ${data.volume}, Data: ${data.createdAt}")
                        }
                        
                        if (sensorHistoryList.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isGenerating = false,
                                error = "Nenhum dado encontrado para este tanque. Verifique se há dados de sensores registrados."
                            )
                            return@fold
                        }
                        
                        val csvFile = createCsvReport(
                            context = context,
                            sensorType = sensorType,
                            tankName = tankName,
                            sensorHistoryList = sensorHistoryList
                        )
                        
                        println("DEBUG: Arquivo CSV criado: ${csvFile.absolutePath}")
                        shareReport(context, csvFile)
                        
                        _uiState.value = _uiState.value.copy(
                            isGenerating = false,
                            message = "Relatório gerado com sucesso! ${sensorHistoryList.size} registros exportados."
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isGenerating = false,
                            error = "Erro ao gerar relatório: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    private fun createCsvReport(
        context: Context,
        sensorType: String,
        tankName: String,
        sensorHistoryList: List<com.technote.aquasense.data.model.SensorData>
    ): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Relatorio_${sensorType}_${tankName}_$timestamp.csv"
        
        // Usar cache directory root para evitar problemas de FileProvider
        val csvFile = File(context.cacheDir, fileName)
        
        // Verificar se o arquivo pode ser criado
        if (csvFile.exists()) {
            csvFile.delete()
        }
        
        FileWriter(csvFile).use { writer ->
            // Adicionar BOM para UTF-8 (Excel compatibility)
            writer.append("\uFEFF")
            
            // Cabeçalho do CSV melhorado
            writer.append("Data/Hora;Sensor;Tanque;Valor;Unidade;Observacoes\n")
            
            // Filtrar apenas dados relevantes do sensor selecionado
            val relevantData = sensorHistoryList.filter { sensorData ->
                when (sensorType) {
                    "Temperatura" -> sensorData.temperature > 0.0
                    "Ph" -> sensorData.ph > 0.0
                    "Tds" -> sensorData.tds > 0.0
                    "Volume" -> sensorData.volume > 0.0
                    else -> false
                }
            }
            
            println("DEBUG: Dados relevantes filtrados: ${relevantData.size} de ${sensorHistoryList.size}")
            
            // Dados do sensor selecionado
            relevantData.forEach { sensorData ->
                val (valor, unidade) = when (sensorType) {
                    "Temperatura" -> Pair(String.format("%.1f", sensorData.temperature), "°C")
                    "Ph" -> Pair(String.format("%.2f", sensorData.ph), "")
                    "Tds" -> Pair(String.format("%.0f", sensorData.tds), "PPM")
                    "Volume" -> Pair(String.format("%.2f", sensorData.volume), "L")
                    else -> Pair("0", "")
                }
                
                val dataFormatada = sensorData.createdAt ?: "Sem data"
                val observacao = "Sensor: $sensorType"
                
                writer.append("$dataFormatada;$sensorType;$tankName;$valor;$unidade;$observacao\n")
            }
            
            // Adicionar estatísticas melhoradas
            writer.append("\n--- ESTATÍSTICAS DO RELATÓRIO ---\n")
            writer.append("Total de registros;${relevantData.size};;;;;;\n")
            writer.append("Período;${getDateRange(relevantData)};;;;;;\n")
            writer.append("Sensor analisado;$sensorType;;;;;;\n")
            writer.append("Tanque analisado;$tankName;;;;;;\n")
            
            val values = relevantData.mapNotNull { sensorData ->
                when (sensorType) {
                    "Temperatura" -> if (sensorData.temperature > 0.0) sensorData.temperature else null
                    "Ph" -> if (sensorData.ph > 0.0) sensorData.ph else null
                    "Tds" -> if (sensorData.tds > 0.0) sensorData.tds else null
                    "Volume" -> if (sensorData.volume > 0.0) sensorData.volume else null
                    else -> null
                }
            }
            
            if (values.isNotEmpty()) {
                writer.append("Valor mínimo;${String.format("%.2f", values.minOrNull() ?: 0.0)};;;;;;\n")
                writer.append("Valor máximo;${String.format("%.2f", values.maxOrNull() ?: 0.0)};;;;;;\n")
                writer.append("Valor médio;${String.format("%.2f", values.average())};;;;;;\n")
                writer.append("Desvio padrão;${String.format("%.2f", calculateStandardDeviation(values))};;;;;;\n")
            } else {
                writer.append("ATENÇÃO;Nenhum dado válido encontrado para $sensorType;;;;;;\n")
            }
            
            writer.append("\nRelatório gerado em;${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())};;;;;;\n")
            writer.append("Gerado por;AquaSense Mobile App;;;;;;\n")
        }
        
        // Verificar se o arquivo foi criado com sucesso
        if (!csvFile.exists() || csvFile.length() == 0L) {
            throw Exception("Erro ao criar arquivo CSV")
        }
        
        println("DEBUG: Arquivo CSV criado: ${csvFile.absolutePath} (${csvFile.length()} bytes)")
        return csvFile
    }
    
    private fun calculateStandardDeviation(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return sqrt(variance)
    }

    private fun getDateRange(sensorDataList: List<com.technote.aquasense.data.model.SensorData>): String {
        if (sensorDataList.isEmpty()) return "Sem dados"
        
        val dates = sensorDataList.mapNotNull { it.createdAt }.sorted()
        return if (dates.isNotEmpty()) {
            "${dates.first()} a ${dates.last()}"
        } else {
            "Sem dados de data"
        }
    }

    private fun shareReport(context: Context, csvFile: File) {
        try {
            println("DEBUG: Tentando compartilhar arquivo: ${csvFile.absolutePath}")
            println("DEBUG: Arquivo existe: ${csvFile.exists()}")
            println("DEBUG: Tamanho do arquivo: ${csvFile.length()} bytes")
            
            if (!csvFile.exists() || csvFile.length() == 0L) {
                throw Exception("Arquivo CSV não foi criado corretamente")
            }
            
            val uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    csvFile
                )
            } catch (e: IllegalArgumentException) {
                println("DEBUG: Erro ao gerar URI: ${e.message}")
                println("DEBUG: Caminho do arquivo: ${csvFile.absolutePath}")
                println("DEBUG: Cache dir: ${context.cacheDir.absolutePath}")
                throw e
            }
            
            println("DEBUG: URI gerada: $uri")
            
            // Intent específico para arquivo CSV
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv" // Tipo específico para CSV
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Relatório AquaSense - ${csvFile.name}")
                putExtra(Intent.EXTRA_TEXT, 
                    "Relatório de dados dos sensores gerado pelo AquaSense.\n\n" +
                    "Arquivo: ${csvFile.name}\n" +
                    "Tamanho: ${String.format("%.2f", csvFile.length() / 1024.0)} KB\n" +
                    "Formato: CSV (compatível com Excel)\n\n" +
                    "Abra o arquivo em Excel, Google Sheets ou qualquer editor de planilhas."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Tentar intent VIEW também para abrir diretamente
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/csv")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Criar chooser com opções
            val chooserIntent = Intent.createChooser(shareIntent, "Salvar/Compartilhar Relatório CSV")
            
            // Adicionar intent VIEW como opção extra
            try {
                val extraIntents = arrayOf(viewIntent)
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents)
            } catch (e: Exception) {
                println("DEBUG: Não foi possível adicionar intent VIEW: ${e.message}")
            }
            
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
            println("DEBUG: Intent de compartilhamento iniciado com sucesso")
            
        } catch (e: Exception) {
            println("DEBUG: Erro ao compartilhar: ${e.message}")
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(
                error = "Erro ao compartilhar relatório: ${e.message}. Verifique se tem aplicativos para abrir arquivos CSV instalados."
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}