package com.technote.aquasense.presentation.ui.screens.graph

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.res.stringResource
import com.technote.aquasense.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaHeader
import com.technote.aquasense.presentation.viewmodel.GraphAIViewModel

@Composable
fun GraphAIScreen(
    navController: NavController,
    graphAIViewModel: GraphAIViewModel = hiltViewModel()
) {
    val uiState by graphAIViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // Data tab (GraphAI é acessado via Data)

    // Carregar tanques ao iniciar
    LaunchedEffect(Unit) {
        val userId = "user_id_aqui"
        graphAIViewModel.loadTanks(userId)
    }

    // Mostrar erro via Toast
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            graphAIViewModel.clearError()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            AquaHeader(isExpanded = false)
        },
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
                .padding(bottom = 8.dp) // Espaço menor na parte inferior
                .verticalScroll(rememberScrollState())
        ) {
            // Header com título e dropdown - mais compacto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Título "Vamos Verificar?" - tamanho reduzido
                Column {
                    Text(
                        text = stringResource(R.string.lets_check_title_1),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007BFF),
                        lineHeight = 28.sp
                    )
                    Text(
                        text = stringResource(R.string.lets_check_title_2),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007BFF),
                        lineHeight = 28.sp
                    )
                }

                // Dropdown de Tanques
                if (uiState.tanks.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    // Buscar tanque selecionado pelo nome
                    val selectedTank = uiState.tanks.find { it.name == uiState.selectedTankName }
                    
                    // Debug: Log para verificar a seleção
                    LaunchedEffect(uiState.tanks, uiState.selectedTankName) {
                        println("🎯 GraphAI - Total de tanques carregados: ${uiState.tanks.size}")
                        uiState.tanks.forEachIndexed { index, tank ->
                            println("🎯 GraphAI - Tank[$index]: Name=${tank.name}")
                        }
                        println("🎯 GraphAI - Tank name selecionado: ${uiState.selectedTankName}")
                        println("🎯 GraphAI - Tank objeto encontrado: ${selectedTank?.name ?: "NENHUM"}")
                    }
                    
                    Box {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .clickable { 
                                    println("🖱️ GraphAI - Dropdown clicado, abrindo menu")
                                    expanded = true 
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = selectedTank?.name ?: stringResource(R.string.select_tank),
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333), // Cor fixa independente do tema
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(R.string.cd_select_tank),
                                    tint = Color(0xFF333333),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { 
                                println("🖱️ GraphAI - Dropdown fechado")
                                expanded = false 
                            },
                            modifier = Modifier.background(Color.White)
                        ) {
                            uiState.tanks.forEach { tank ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = tank.name,
                                            fontWeight = if (tank.name == selectedTank?.name) 
                                                FontWeight.Bold 
                                            else 
                                                FontWeight.Normal,
                                            color = if (tank.name == selectedTank?.name) 
                                                Color(0xFF007BFF) 
                                            else 
                                                Color(0xFF333333) // Cor fixa em vez de Color.Black
                                        )
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color(0xFF333333),
                                        leadingIconColor = Color(0xFF333333),
                                        trailingIconColor = Color(0xFF333333)
                                    ),
                                    onClick = {
                                        println("✅ GraphAI - Tanque clicado: ${tank.name}")
                                        println("✅ GraphAI - Antes da seleção - tanque atual: ${selectedTank?.name}")
                                        graphAIViewModel.selectTankByName(tank.name)
                                        expanded = false
                                        println("✅ GraphAI - Dropdown fechado após seleção")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Grid de Cards com barras verticais - Layout 2x2 mais compacto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerticalProgressCard(
                        label = stringResource(R.string.tds),
                        value = uiState.tdsScore,
                        modifier = Modifier.weight(1f)
                    )
                    VerticalProgressCard(
                        label = stringResource(R.string.temperature),
                        value = uiState.temperatureScore,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerticalProgressCard(
                        label = stringResource(R.string.ph),
                        value = uiState.phScore,
                        modifier = Modifier.weight(1f)
                    )
                    VerticalProgressCard(
                        label = stringResource(R.string.volume),
                        value = uiState.volumeScore,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card de Classificação da IA com cores dinâmicas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getClassificationColor(uiState.classification)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoadingAI) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(
                            text = uiState.classification,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun VerticalProgressCard(
    label: String,
    value: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        value >= 80 -> Color(0xFF4CAF50) // Verde
        value >= 60 -> Color(0xFFFFC107) // Amarelo
        value >= 40 -> Color(0xFFFF9800) // Laranja
        else -> Color(0xFFF44336) // Vermelho
    }
    
    val context = LocalContext.current
    val statusText = when {
        value >= 80 -> context.getString(R.string.excellent)
        value >= 60 -> context.getString(R.string.good)
        value >= 40 -> context.getString(R.string.attention)
        else -> context.getString(R.string.critical)
    }
    
    Card(
        modifier = modifier.height(180.dp), // Altura fixa para melhor controle
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Seção superior: Label e Status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            // Cilindro de progresso com arredondamento perfeito
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp)) // Clip para garantir que tudo fique dentro
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Preenchimento que se adapta perfeitamente ao cilindro
                val fillHeight = (value / 100f).coerceIn(0f, 1f)
                
                if (fillHeight > 0f) {
                    // Container com clip para garantir que o preenchimento siga exatamente o formato
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(fillHeight)
                            .clip(
                                if (fillHeight >= 0.98f) {
                                    // Se quase cheio (≥98%), usar arredondamento completo
                                    RoundedCornerShape(16.dp)
                                } else {
                                    // Para valores menores, criar formato que se adapta gradualmente
                                    RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 16.dp,
                                        bottomEnd = 16.dp
                                    )
                                }
                            )
                            .background(progressColor)
                    )
                }
            }

            // Valor percentual
            Text(
                text = "${value.toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun getClassificationColor(classification: String): Color {
    return when {
        classification.contains("Normal", ignoreCase = true) -> Color(0xFF4CAF50) // Verde
        classification.contains("Alerta", ignoreCase = true) || 
        classification.contains("Atenção", ignoreCase = true) -> Color(0xFFFF9800) // Laranja
        classification.contains("Crise", ignoreCase = true) || 
        classification.contains("Crítico", ignoreCase = true) -> Color(0xFFF44336) // Vermelho
        classification.contains("Carregando", ignoreCase = true) -> Color(0xFF007BFF) // Azul
        else -> Color(0xFF007BFF) // Azul padrão
    }
}
