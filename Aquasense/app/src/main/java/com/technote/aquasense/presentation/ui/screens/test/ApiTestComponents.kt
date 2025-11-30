package com.technote.aquasense.presentation.ui.screens.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.data.model.User
import com.technote.aquasense.data.model.WaterPrediction
import com.technote.aquasense.utils.Constants

/**
 * Componentes reutilizáveis para a tela de testes
 */

/**
 * Displays API information and connection details
 */
@Composable
fun ApiInfoDisplay() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "API Info",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Informações da API",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // API URL
            InfoItem(
                label = "URL da API:",
                value = Constants.BASE_URL
            )
            
            // Available Endpoints
            InfoItem(
                label = "Endpoints:",
                value = """
                    • POST /register - Registro
                    • POST /login - Autenticação
                    • GET /tanks - Listar tanques
                    • POST /tanks - Criar tanque
                    • PUT /tanks/{id} - Atualizar tanque
                    • DELETE /tanks/{id} - Deletar tanque
                    • GET/POST/PUT/DELETE /peixes - Peixes
                    • GET /sensores/latest - Dados dos sensores
                    • POST /predict - Previsão da qualidade
                """.trimIndent()
            )
            
            InfoItem(
                label = "Timeout:",
                value = "${Constants.TIMEOUT_SECONDS}s"
            )
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSection(
    title: String,
    isLoading: Boolean = false,
    error: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Carregando...")
                }
            }

            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Erro: $error",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            content()
        }
    }
}

@Composable
fun TestButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.padding(vertical = 2.dp),
        singleLine = true
    )
}

@Composable
fun AuthStatusDisplay(
    isLoggedIn: Boolean,
    currentUser: User?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoggedIn)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (isLoggedIn) "✅ Logado" else "❌ Deslogado",
                fontWeight = FontWeight.Bold
            )
            if (currentUser != null) {
                Text("👤 ${currentUser.name}")
                Text("📧 ${currentUser.email}")
                Text("🆔 ID: ${currentUser.id}")
            }
        }
    }
}

@Composable
fun TankList(
    tanks: List<Tank>,
    selectedTank: Tank?,
    onTankSelect: (Tank) -> Unit
) {
    if (tanks.isNotEmpty()) {
        Text("🐠 Aquários (${tanks.size}):", fontWeight = FontWeight.Bold)
        LazyColumn(
            modifier = Modifier.heightIn(max = 150.dp)
        ) {
            items(tanks) { tank ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tank == selectedTank)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = { onTankSelect(tank) }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📦 ${tank.name}", fontWeight = FontWeight.Bold)
                        Text("💧 Volume: ${tank.volume}L")
                        Text("🆔 ID: ${tank.id}")
                    }
                }
            }
        }
    } else {
        Text("Nenhum aquário encontrado")
    }
}

@Composable
fun FishList(
    fishList: List<Fish>
) {
    if (fishList.isNotEmpty()) {
        Text("🐟 Peixes (${fishList.size}):", fontWeight = FontWeight.Bold)
        LazyColumn(
            modifier = Modifier.heightIn(max = 150.dp)
        ) {
            items(fishList) { fish ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("🐠 ${fish.name}", fontWeight = FontWeight.Bold)
                        Text("🧬 Espécie: ${fish.species}")
                        Text("🆔 ID: ${fish.id}")
                    }
                }
            }
        }
    } else {
        Text("Nenhum peixe encontrado")
    }
}

@Composable
fun SensorDataDisplay(
    sensorData: SensorData?
) {
    if (sensorData != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("📊 Dados dos Sensores", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("🌡️ Temperatura: ${sensorData.temperature}°C")
                Text("⚗️ pH: ${sensorData.ph}")
                Text("💎 TDS: ${sensorData.tds} ppm")
                Text("🆔 Tank ID: ${sensorData.tankId}")
                if (sensorData.createdAt != null) {
                    Text("⏰ Atualizado: ${sensorData.createdAt}")
                }
            }
        }
    } else {
        Text("Nenhum dado de sensor disponível")
    }
}

@Composable
fun PredictionDisplay(
    prediction: WaterPrediction?
) {
    if (prediction != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🤖 Predições IA", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("💧 Qualidade: ${prediction.prediction.waterQuality}")
                Text("🎯 Confiança: ${(prediction.confidence * 100).toInt()}%")

                if (prediction.prediction.recommendations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📝 Recomendações:", fontWeight = FontWeight.Bold)
                    prediction.prediction.recommendations.forEach { rec ->
                        Text("• $rec")
                    }
                }

                if (prediction.prediction.alerts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("⚠️ Alertas:", fontWeight = FontWeight.Bold)
                    prediction.prediction.alerts.forEach { alert ->
                        Text("🚨 ${alert.type}: ${alert.message} (${alert.severity})")
                    }
                }
            }
        }
    } else {
        Text("Nenhuma predição disponível")
    }
}

@Composable
fun LogDisplay(
    logs: List<String>,
    onClearLogs: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 Logs (${logs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClearLogs) {
                Text("Limpar")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp),
                reverseLayout = true // Logs mais recentes no topo
            ) {
                items(logs.reversed()) { log ->
                    Text(
                        text = log,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}