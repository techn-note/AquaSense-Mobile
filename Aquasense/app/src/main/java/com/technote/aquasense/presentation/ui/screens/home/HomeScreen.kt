package com.technote.aquasense.presentation.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.technote.aquasense.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.*
import com.technote.aquasense.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(1) } // Home tab

    // Inicializar dados ao abrir a tela apenas uma vez
    LaunchedEffect(Unit) {
        // Só inicializar se não tiver dados carregados
        if (uiState.currentUser == null && !uiState.isLoading) {
            homeViewModel.initialize()
        }
    }
    
    // Verificar se não está autenticado e redirecionar
    LaunchedEffect(uiState.isAuthenticated) {
        // Só redirecionar se explicitamente não autenticado E não estiver carregando
        if (!uiState.isAuthenticated && !uiState.isLoading && uiState.error != null) {
            Toast.makeText(context, context.getString(R.string.session_expired), Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // Mostrar erro via Toast
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            homeViewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header expandido com fundo azul
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF007BFF))
                    .padding(20.dp)
            ) {
                // Logo e Nome Aquasense
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo usando AquaLogo component
                    AquaLogo(
                        size = 40.dp,
                        negative = true
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Texto "Aquasense"
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Saudação e Dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.greeting, uiState.currentUser?.name ?: stringResource(R.string.greeting_default).substringAfter(", ").substringBefore("!")),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Dropdown de Tanques
                    if (uiState.tanks.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        // Buscar tanque selecionado pelo nome ao invés do ID
                        val selectedTank = uiState.tanks.find { it.name == uiState.selectedTankName }
                        
                        // Debug: Log para verificar a seleção
                        LaunchedEffect(uiState.tanks, uiState.selectedTankName) {
                            println("🎯 Total de tanques carregados: ${uiState.tanks.size}")
                            uiState.tanks.forEachIndexed { index, tank ->
                                println("🎯 Tank[$index]: Name=${tank.name}")
                            }
                            println("🎯 Tank name selecionado: ${uiState.selectedTankName}")
                            println("🎯 Tank objeto encontrado: ${selectedTank?.name ?: "NENHUM"}")
                        }
                        
                        Box {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 140.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .clickable { 
                                        println("🖱️ Dropdown clicado, abrindo menu")
                                        expanded = true 
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = selectedTank?.name ?: "Selecione tanque",
                                        fontSize = 14.sp,
                                        color = Color(0xFF333333),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
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
                                    println("🖱️ Dropdown fechado")
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
                                                    Color.Black
                                            )
                                        },
                                        onClick = {
                                            println("✅ Tanque clicado: ${tank.name}")
                                            println("✅ Antes da seleção - tanque atual: ${selectedTank?.name}")
                                            homeViewModel.selectTankByName(tank.name)
                                            expanded = false
                                            println("✅ Dropdown fechado após seleção")
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Debug: mostrar quando não há tanques
                        Text(
                            text = stringResource(R.string.no_tanks_found),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        println("⚠️ Nenhum tanque disponível - lista vazia")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Status Principal - Container centralizado
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ícone de Status
                    val allGoodMessage = stringResource(R.string.home_all_good)
                    Icon(
                        imageVector = if (uiState.statusMessage == allGoodMessage) 
                            Icons.Default.CheckCircle 
                        else 
                            Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mensagem de Status
                    Text(
                        text = uiState.statusMessage,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Barra de Progresso personalizada para evitar bugs visuais
                    Box(
                        modifier = Modifier.width(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Fundo da barra (track)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    RoundedCornerShape(18.dp)
                                )
                        )
                        
                        // Barra de progresso (fill)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(uiState.overallHealth / 100f)
                                .height(36.dp)
                                .background(
                                    if (uiState.overallHealth >= 80f) 
                                        Color(0xFF00DF3A) 
                                    else 
                                        Color(0xFFFF5733),
                                    RoundedCornerShape(18.dp)
                                )
                                .align(Alignment.CenterStart)
                        )
                        
                        // Texto de porcentagem sobreposto
                        Text(
                            text = "${uiState.overallHealth.toInt()}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Cards Informativos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Seção Água
                Text(
                    text = stringResource(R.string.water),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF007BFF),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // TDS
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.tds),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6C6C6C)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.isLoadingSensors) 
                                    stringResource(R.string.loading) 
                                else 
                                    "${uiState.sensorData?.tds ?: 0} PPM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007BFF)
                            )
                        }

                        // Separador vertical azul arredondado
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(40.dp)
                                .background(
                                    Color(0xFF007BFF),
                                    RoundedCornerShape(5.dp)
                                )
                        )

                        // Volume
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.volume),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6C6C6C)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.isLoadingSensors) 
                                    stringResource(R.string.loading) 
                                else 
                                    "${uiState.sensorData?.volume ?: 0} L",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007BFF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Seção Condições Gerais
                Text(
                    text = stringResource(R.string.general_conditions),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF007BFF),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Temperatura
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.temp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6C6C6C)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.isLoadingSensors) 
                                    stringResource(R.string.loading) 
                                else 
                                    "${uiState.sensorData?.temperature ?: 0}°C",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007BFF)
                            )
                        }

                        // Separador vertical azul arredondado
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(40.dp)
                                .background(
                                    Color(0xFF007BFF),
                                    RoundedCornerShape(5.dp)
                                )
                        )

                        // pH
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.ph),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6C6C6C)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.isLoadingSensors) 
                                    stringResource(R.string.loading) 
                                else 
                                    "${uiState.sensorData?.ph ?: 0}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007BFF)
                            )
                        }
                    }
                }
            }
        }

        // Navigation Button fixo no bottomBar do Scaffold
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
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
}