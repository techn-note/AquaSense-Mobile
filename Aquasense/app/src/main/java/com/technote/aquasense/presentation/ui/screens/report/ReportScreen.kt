package com.technote.aquasense.presentation.ui.screens.report

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.technote.aquasense.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaHeader
import com.technote.aquasense.presentation.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    initialSensorType: String = "Temperatura",
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by reportViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(-1) }

    // Definir o tipo de sensor inicial
    LaunchedEffect(initialSensorType) {
        reportViewModel.selectSensorType(initialSensorType)
    }

    // Mostrar toast de sucesso/erro
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            reportViewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = { 
            AquaHeader(isExpanded = false)
        },
        bottomBar = {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Título da tela
                Text(
                    text = stringResource(R.string.sensor_reports_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = stringResource(R.string.sensor_reports_description),
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
            }

            item {
                // Card informativo do sensor selecionado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (icon, color, subtitle) = when(uiState.selectedSensorType) {
                            "Temperatura" -> Triple(Icons.Default.Thermostat, Color(0xFFFF6B6B), stringResource(R.string.thermal_data))
                            "Ph" -> Triple(Icons.Default.Science, Color(0xFF4ECDC4), stringResource(R.string.water_acidity))
                            "Tds" -> Triple(Icons.Default.Opacity, Color(0xFF45B7D1), stringResource(R.string.dissolved_solids))
                            "Volume" -> Triple(Icons.Default.WaterDrop, Color(0xFF96CEB4), stringResource(R.string.water_level))
                            else -> Triple(Icons.Default.Thermostat, Color(0xFFFF6B6B), stringResource(R.string.thermal_data))
                        }
                        
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(48.dp)
                        )
                        
                        Column {
                            Text(
                                text = stringResource(R.string.sensor_selected, uiState.selectedSensorType ?: ""),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Text(
                                text = subtitle,
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            item {
                // Card de seleção de tanque
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.select_tank_report),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        if (uiState.tanks.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_tanks_registered),
                                fontSize = 16.sp,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.tanks.size) { index ->
                                    val tank = uiState.tanks[index]
                                    TankOptionCard(
                                        tankName = tank.name,
                                        tankDescription = stringResource(R.string.capacity, tank.volume.toString()),
                                        isSelected = uiState.selectedTankId == tank.name, // Usar nome em vez de ID
                                        onClick = { reportViewModel.selectTank(tank.name, tank.name) } // Passar nome como ID também
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Card de informações do relatório
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.report_information),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        ReportInfoRow(
                            icon = Icons.Default.DateRange,
                            title = stringResource(R.string.period),
                            subtitle = stringResource(R.string.all_historical_data)
                        )
                        
                        ReportInfoRow(
                            icon = Icons.Default.FileDownload,
                            title = stringResource(R.string.format),
                            subtitle = stringResource(R.string.csv_file_analysis)
                        )
                        
                        ReportInfoRow(
                            icon = Icons.Default.QueryStats,
                            title = stringResource(R.string.included_data),
                            subtitle = stringResource(R.string.values_timestamps_stats)
                        )
                    }
                }
            }

            item {
                // Botão de gerar relatório
                Button(
                    onClick = {
                        if (uiState.selectedSensorType != null && uiState.selectedTankId != null) {
                            reportViewModel.generateReport(context)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    enabled = uiState.selectedSensorType != null && 
                             uiState.selectedTankId != null && 
                             !uiState.isGenerating,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.generating_report),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(R.string.download),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.generate_download_report),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SensorOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, color, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TankOptionCard(
    tankName: String,
    tankDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF2196F3)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de seleção com ícone de tanque
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) Color(0xFF2196F3) else Color(0xFFE0E0E0),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDrink, // Ícone de bebida/líquido para representar tanque de água
                    contentDescription = stringResource(R.string.tank_label),
                    tint = if (isSelected) Color.White else Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informações do tanque
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tankName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color(0xFF2196F3) else Color(0xFF1A1A1A)
                )
                Text(
                    text = tankDescription,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun ReportInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}