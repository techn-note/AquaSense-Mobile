package com.technote.aquasense.presentation.ui.screens.graph

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaHeader
import com.technote.aquasense.presentation.viewmodel.GraphViewModel
import androidx.compose.ui.res.stringResource
import com.technote.aquasense.R

@Composable
fun GraphScreen(
    navController: NavController,
    graphViewModel: GraphViewModel = hiltViewModel()
) {
    val uiState by graphViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(-1) }

    // Mostrar toast de erro se houver
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            // TODO: Mostrar toast de erro
            println("❌ Erro nos gráficos: $error")
            graphViewModel.clearError()
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF007BFF))
            }
        } else if (uiState.tanks.isEmpty()) {
            // Estado vazio - nenhum tanque cadastrado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color(0xFFF3F4F6),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocalDrink,
                            contentDescription = stringResource(R.string.tank_label),
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.no_tank_registered),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = stringResource(R.string.register_tank_to_see_graphs),
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header com botão de refresh
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sensor_graphs_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF23272A)
                    )
                    
                    IconButton(
                        onClick = { graphViewModel.refreshData() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh_data),
                            tint = Color(0xFF007BFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 40.dp), // Espaço para o FAB
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    
                    item {
                        // Card de seleção de tanques
                        TankSelectionCard(
                            availableTanks = uiState.tanks,
                            selectedTankIds = uiState.selectedTankIds,
                            onTankToggle = { tankId -> graphViewModel.toggleTankSelection(tankId) }
                        )
                    }
                
                    // Mostrar gráficos apenas quando há tanques selecionados
                    if (uiState.selectedTankIds.isNotEmpty()) {
                        item {
                            val selectedTanks = uiState.tanks.filter { tank -> 
                                uiState.selectedTankIds.contains(tank.name) 
                            }
                            
                            GraphCard(
                                title = stringResource(R.string.temperature),
                                subtitle = stringResource(R.string.temperature_subtitle),
                                data = uiState.temperatureData,
                                yAxisMax = 50f,
                                unit = "°C",
                                tankNames = selectedTanks.map { it.name },
                                colors = generateColorsForTanks(selectedTanks.size),
                                navController = navController,
                                sensorType = stringResource(R.string.temperature)
                            )
                        }

                        item {
                            val selectedTanks = uiState.tanks.filter { tank -> 
                                uiState.selectedTankIds.contains(tank.name) 
                            }
                            
                            GraphCard(
                                title = stringResource(R.string.ph),
                                subtitle = stringResource(R.string.ph_subtitle),
                                data = uiState.phData,
                                yAxisMax = 14f,
                                unit = "pH",
                                tankNames = selectedTanks.map { it.name },
                                colors = generateColorsForTanks(selectedTanks.size),
                                navController = navController,
                                sensorType = "Ph"
                            )
                        }

                        item {
                            val selectedTanks = uiState.tanks.filter { tank -> 
                                uiState.selectedTankIds.contains(tank.name) 
                            }
                            
                            GraphCard(
                                title = stringResource(R.string.tds),
                                subtitle = stringResource(R.string.tds_subtitle),
                                data = uiState.tdsData,
                                yAxisMax = 2000f,
                                unit = "ppm",
                                tankNames = selectedTanks.map { it.name },
                                colors = generateColorsForTanks(selectedTanks.size),
                                navController = navController,
                                sensorType = "Tds"
                            )
                        }

                        item {
                            val selectedTanks = uiState.tanks.filter { tank -> 
                                uiState.selectedTankIds.contains(tank.name) 
                            }
                            
                            GraphCard(
                                title = stringResource(R.string.volume),
                                subtitle = stringResource(R.string.volume_subtitle),
                                data = uiState.volumeData,
                                yAxisMax = 200f,
                                unit = "L",
                                tankNames = selectedTanks.map { it.name },
                                colors = generateColorsForTanks(selectedTanks.size),
                                navController = navController,
                                sensorType = "Volume"
                            )
                        }
                    } else {
                        // Estado quando nenhum tanque está selecionado
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(
                                                Color(0xFFF3F4F6),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.BarChart,
                                            contentDescription = stringResource(R.string.chart_label),
                                            tint = Color(0xFF9CA3AF),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.select_up_to_3_tanks),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = stringResource(R.string.choose_tanks_to_compare),
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// Função para gerar cores dinamicamente baseado no número de tanques
fun generateColorsForTanks(tankCount: Int): List<Color> {
    val baseColors = listOf(
        Color(0xFFE53E3E), // Vermelho
        Color(0xFF38A169), // Verde
        Color(0xFFDD6B20), // Laranja
        Color(0xFF3182CE), // Azul
        Color(0xFFD69E2E), // Amarelo
        Color(0xFF9F7AEA), // Roxo
        Color(0xFFEC4899), // Rosa
        Color(0xFF06B6D4)  // Ciano
    )
    
    return (0 until minOf(tankCount, baseColors.size)).map { baseColors[it] }
}

@Composable
fun GraphCard(
    title: String,
    subtitle: String? = null,
    data: List<Pair<String, List<Float>>>,
    yAxisMax: Float = 100f,
    unit: String = "%",
    tankNames: List<String> = emptyList(),
    colors: List<Color>,
    navController: NavController,
    sensorType: String = "Temperatura"
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF23272A)
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = { 
                        navController.navigate("report/$sensorType") 
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = stringResource(R.string.generate_report),
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gráfico ou placeholder quando não houver dados suficientes
            val hasAnyData = data.any { (_, lists) -> lists.any { it != 0f } }
            val hasEnoughData = data.size >= 2 && hasAnyData
            
            // DEBUG: Log dos dados recebidos
            println("🎯 GraphCard [$title]: hasAnyData=$hasAnyData, dataPoints=${data.size}, hasEnoughData=$hasEnoughData")
            println("🎯 GraphCard [$title]: data=$data")
            data.forEach { (label, values) ->
                println("🎯 [$title] $label -> $values")
            }

            if (hasEnoughData) {
                LineChart(
                    data = data,
                    yAxisMax = yAxisMax,
                    unit = unit,
                    colors = colors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else {
                // Placeholder quando não há dados suficientes - estilo consistente com o app
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ícone em estilo circular como outros elementos do app
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color(0xFFF3F4F6),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = stringResource(R.string.chart_label),
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val message = if (data.isEmpty() || !hasAnyData) {
                        stringResource(R.string.no_data_available)
                    } else if (data.size < 2) {
                        stringResource(R.string.insufficient_data)
                    } else {
                        stringResource(R.string.no_data_to_display)
                    }
                    
                    Text(
                        text = message,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    val subMessage = if (data.size < 2 && hasAnyData) {
                        stringResource(R.string.wait_for_more_data)
                    } else {
                        stringResource(R.string.data_will_display_automatically)
                    }
                    
                    Text(
                        text = subMessage,
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legenda dinâmica baseada nos tanques reais
            if (tankNames.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tankNames.take(colors.size).forEachIndexed { index, tankName ->
                        LegendItem(
                            color = colors.getOrNull(index) ?: Color.Gray,
                            label = tankName
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun LineChart(
    data: List<Pair<String, List<Float>>>,
    yAxisMax: Float,
    unit: String = "",
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp) // Mais padding embaixo para labels X
    ) {
        val chartWidth = size.width * 0.75f // Reduzido para dar espaço aos labels Y
        val chartHeight = size.height * 0.75f // Reduzido para dar espaço aos labels X
        val chartLeft = size.width * 0.15f // Mais espaço à esquerda para labels Y
        val chartTop = size.height * 0.1f
        val chartRight = chartLeft + chartWidth
        val chartBottom = chartTop + chartHeight

        // Desenhar grade
        drawGrid(
            chartLeft = chartLeft,
            chartTop = chartTop,
            chartRight = chartRight,
            chartBottom = chartBottom,
            yAxisMax = yAxisMax
        )

        // Desenhar eixos
        drawAxes(
            chartLeft = chartLeft,
            chartTop = chartTop,
            chartRight = chartRight,
            chartBottom = chartBottom,
            yAxisMax = yAxisMax,
            unit = unit,
            xLabels = data.map { it.first }
        )

        // Desenhar linhas para cada tanque (dinâmico)
        if (data.isNotEmpty()) {
            val maxTanks = data.first().second.size
            println("📈 LineChart: data.size=${data.size}, maxTanks=$maxTanks, colors.size=${colors.size}")
            
            for (tankIndex in 0 until minOf(maxTanks, colors.size)) {
                val points = data.mapIndexed { dayIndex, (label, values) ->
                    val x = if (data.size == 1) {
                        chartLeft + chartWidth / 2f // Centro se só há um ponto
                    } else {
                        chartLeft + (dayIndex.toFloat() * chartWidth / (data.size - 1))
                    }
                    val value = values.getOrNull(tankIndex) ?: 0f
                    val y = chartBottom - (value * chartHeight / yAxisMax)
                    
                    println("📈 Point [$label]: tank$tankIndex value=$value -> x=$x, y=$y")
                    Offset(x, y)
                }

                if (points.isNotEmpty() && tankIndex < colors.size) {
                    println("📈 Desenhando linha para tank$tankIndex com cor ${colors[tankIndex]}")
                    drawLine(points, colors[tankIndex])
                } else {
                    println("❌ Não desenhou linha: points.isEmpty=${points.isEmpty()}, tankIndex=$tankIndex >= colors.size=${colors.size}")
                }
            }
        } else {
            println("❌ LineChart: data está vazio!")
        }
    }
}

private fun DrawScope.drawGrid(
    chartLeft: Float,
    chartTop: Float,
    chartRight: Float,
    chartBottom: Float,
    yAxisMax: Float
) {
    val gridColor = Color(0xFFE5E7EB)
    
    // Linhas horizontais
    for (i in 0..5) {
        val y = chartBottom - (i * (chartBottom - chartTop) / 5)
        drawLine(
            color = gridColor,
            start = Offset(chartLeft, y),
            end = Offset(chartRight, y),
            strokeWidth = 1.dp.toPx(),
            alpha = 0.3f
        )
    }
    
    // Linhas verticais
    for (i in 0..4) {
        val x = chartLeft + (i * (chartRight - chartLeft) / 4)
        drawLine(
            color = gridColor,
            start = Offset(x, chartTop),
            end = Offset(x, chartBottom),
            strokeWidth = 1.dp.toPx(),
            alpha = 0.3f
        )
    }
}

private fun DrawScope.drawAxes(
    chartLeft: Float,
    chartTop: Float,
    chartRight: Float,
    chartBottom: Float,
    yAxisMax: Float,
    unit: String = "",
    xLabels: List<String>
) {
    val axisColor = Color(0xFF374151)
    val textColor = Color(0xFF374151) // Mudei para cor mais escura para ser mais visível
    
    // Desenhar linhas dos eixos principais
    // Eixo Y (vertical)
    drawLine(
        color = axisColor,
        start = Offset(chartLeft, chartTop),
        end = Offset(chartLeft, chartBottom),
        strokeWidth = 2.dp.toPx()
    )
    
    // Eixo X (horizontal)
    drawLine(
        color = axisColor,
        start = Offset(chartLeft, chartBottom),
        end = Offset(chartRight, chartBottom),
        strokeWidth = 2.dp.toPx()
    )
    
    // Eixo Y (valores com unidades apropriadas)
    for (i in 0..5) {
        val value = (i * yAxisMax / 5)
        val displayValue = when {
            unit == "%" -> "${value.toInt()}%"
            unit == "°C" -> "${value.toInt()}°C"
            unit == "ppm" -> "${value.toInt()}"
            unit == "pH" -> if (value % 1 == 0f) value.toInt().toString() else String.format("%.1f", value)
            unit == "L" -> "${value.toInt()}L"
            value >= 100 -> value.toInt().toString() // Valores grandes sem decimais
            value % 1 == 0f -> value.toInt().toString() // Inteiros sem decimais  
            else -> String.format("%.1f", value) // Decimais com 1 casa
        }
        val y = chartBottom - (i * (chartBottom - chartTop) / 5)
        
        // Desenhar marca no eixo Y
        drawLine(
            color = axisColor,
            start = Offset(chartLeft - 5.dp.toPx(), y),
            end = Offset(chartLeft, y),
            strokeWidth = 1.dp.toPx()
        )
        
        drawContext.canvas.nativeCanvas.drawText(
            displayValue,
            chartLeft - 40.dp.toPx(), // Mais espaço para labels maiores
            y + 6.dp.toPx(), // Ajuste vertical para centralizar
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK // Cor preta para máxima visibilidade
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.RIGHT
                isAntiAlias = true
            }
        )
    }
    
    // Eixo X (horários)
    val chartWidth = chartRight - chartLeft
    xLabels.forEachIndexed { index, label ->
        val x = if (xLabels.size == 1) {
            chartLeft + chartWidth / 2f // Centro se só há um ponto
        } else {
            chartLeft + (index * chartWidth / (xLabels.size - 1))
        }
        
        // Desenhar marca no eixo X
        drawLine(
            color = axisColor,
            start = Offset(x, chartBottom),
            end = Offset(x, chartBottom + 5.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        
        drawContext.canvas.nativeCanvas.drawText(
            label, // Mostrar o horário completo (ex: "14:30")
            x,
            chartBottom + 25.dp.toPx(), // Mais espaço para não sobrepor
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK // Cor preta para máxima visibilidade
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
        )
    }
}

private fun DrawScope.drawLine(points: List<Offset>, color: Color) {
    if (points.size < 2) return
    
    val path = Path()
    path.moveTo(points[0].x, points[0].y)
    
    for (i in 1 until points.size) {
        path.lineTo(points[i].x, points[i].y)
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
    
    // Desenhar pontos
    points.forEach { point ->
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = point
        )
    }
}

@Composable
private fun TankSelectionCard(
    availableTanks: List<Tank>,
    selectedTankIds: List<String>,
    onTankToggle: (String) -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header da seleção
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.select_tanks),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.tanks_selected, selectedTankIds.size),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                // Botão do dropdown
                Button(
                    onClick = { expandedDropdown = !expandedDropdown },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007BFF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (expandedDropdown) stringResource(R.string.close) else stringResource(R.string.select),
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expandedDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Lista dos tanques selecionados (preview)
            if (selectedTankIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedTankIds) { tankName ->
                        val tank = availableTanks.firstOrNull { it.name == tankName }
                        if (tank != null) {
                            SelectedTankChip(
                                tankName = tank.name,
                                onRemove = { onTankToggle(tankName) }
                            )
                        }
                    }
                }
            }
            
            // Dropdown expandido com lista de tanques
            AnimatedVisibility(
                visible = expandedDropdown,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    availableTanks.forEach { tank ->
                        val tankName = tank.name
                        val isSelected = selectedTankIds.contains(tankName)
                        val canSelect = selectedTankIds.size < 3 || isSelected
                        
                        println("🎯 UI DEBUG: Tank=${tank.name}, IsSelected=$isSelected, SelectedList=$selectedTankIds")
                        
                        TankSelectionItem(
                            tank = tank,
                            isSelected = isSelected,
                            isEnabled = canSelect,
                            onToggle = { 
                                println("🎯 UI DEBUG: Toggle clicado para tankName: $tankName")
                                onTankToggle(tankName) 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedTankChip(
    tankName: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF007BFF),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = tankName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.remove_tank),
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@Composable
private fun TankSelectionItem(
    tank: Tank,
    isSelected: Boolean,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { onToggle() }
            .padding(vertical = 4.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tank.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isEnabled) Color(0xFF1A1A1A) else Color(0xFF9CA3AF)
                )
                Text(
                    text = "Volume: ${tank.volume}L",
                    fontSize = 12.sp,
                    color = if (isEnabled) Color(0xFF666666) else Color(0xFF9CA3AF)
                )
            }
            
            // Checkbox customizado
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = when {
                            isSelected -> Color(0xFF007BFF)
                            isEnabled -> Color.Transparent
                            else -> Color(0xFFF5F5F5)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            isSelected -> Color(0xFF007BFF)
                            isEnabled -> Color(0xFFD1D5DB)
                            else -> Color(0xFFE5E7EB)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}