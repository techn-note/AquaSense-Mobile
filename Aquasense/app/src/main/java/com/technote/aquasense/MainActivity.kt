package com.technote.aquasense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.technote.aquasense.presentation.ui.screens.auth.LoginScreen
import com.technote.aquasense.presentation.ui.screens.auth.RegisterScreen
import com.technote.aquasense.presentation.ui.screens.home.HomeScreen
import com.technote.aquasense.presentation.ui.screens.profile.ProfileScreen
import com.technote.aquasense.presentation.ui.screens.profile.EditProfileScreen
import com.technote.aquasense.presentation.ui.screens.data.DataScreen
import com.technote.aquasense.presentation.ui.screens.tank.AddTankScreen
import com.technote.aquasense.presentation.ui.screens.tank.UpdateTankScreen
import com.technote.aquasense.presentation.ui.screens.tank.DeleteTankScreen
import com.technote.aquasense.presentation.ui.screens.fish.AddFishScreen
import com.technote.aquasense.presentation.ui.screens.fish.UpdateFishScreen
import com.technote.aquasense.presentation.ui.screens.fish.DeleteFishScreen
import com.technote.aquasense.presentation.ui.screens.graph.GraphAIScreen
import com.technote.aquasense.presentation.ui.screens.graph.GraphScreen
import com.technote.aquasense.presentation.ui.screens.test.ApiTestScreen
import com.technote.aquasense.presentation.ui.theme.AquasenseTheme
import com.technote.aquasense.utils.AppLifecycleObserver
import dagger.hilt.android.AndroidEntryPoint
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquasenseTheme {
                MainNavigation(appLifecycleObserver)
            }
        }
    }
}

@Composable
fun MainNavigation(appLifecycleObserver: AppLifecycleObserver) {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    val statusBarColor = when (currentDestination) {
        "login" -> Color.White
        else -> Color.White
    }

    val darkIcons = statusBarColor == Color.White

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = darkIcons
        )
    }
    
    // *** OBSERVADOR DE EXPIRAÇÃO DE TOKEN ***
    LaunchedEffect(Unit) {
        appLifecycleObserver.tokenExpiredEvents.collect {
            println("🔄 Token expirado detectado - redirecionando para login")
            
            // Verificar se não está já na tela de login
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute !in listOf("login", "register")) {
                try {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                } catch (e: Exception) {
                    println("⚠️ Erro ao redirecionar para login: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
     ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Tela de testes da API
            composable("test") {
                ApiTestScreen()
            }

            // Autenticação
            composable("login") {
                LoginScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            // Tela Principal
            composable("home") {
                HomeScreen(navController)
            }

            // Tela de Perfil
            composable("profile") {
                ProfileScreen(navController)
            }

            // Tela de Edição de Perfil
            composable("edit_profile") {
                EditProfileScreen(navController)
            }

            // Tela de Dados (Menu com SegmentedButton)
            composable("data") {
                DataScreen(navController)
            }

            // Telas de Tanque
            composable("add_tank") {
                AddTankScreen(navController)
            }

            composable("update_tank") {
                UpdateTankScreen(navController)
            }

            composable("delete_tank") {
                DeleteTankScreen(navController)
            }

            // Telas de Peixe
            composable("add_fish") {
                AddFishScreen(navController)
            }

            composable("update_fish") {
                UpdateFishScreen(navController)
            }

            composable("delete_fish") {
                DeleteFishScreen(navController)
            }

            // Tela de Análise com IA
            composable("graph") {
                GraphAIScreen(navController)
            }

            // Tela de Gráficos
            composable("graph_screen") {
                GraphScreen(navController)
            }

            // Tela de Relatórios
            composable(
                route = "report/{sensorType}",
                arguments = listOf(navArgument("sensorType") { type = NavType.StringType })
            ) { backStackEntry ->
                val sensorType = backStackEntry.arguments?.getString("sensorType") ?: "Temperatura"
                com.technote.aquasense.presentation.ui.screens.report.ReportScreen(
                    navController = navController,
                    initialSensorType = sensorType
                )
            }
        }
    }
}   

@Composable
fun AuthenticationObserver(navController: androidx.navigation.NavController) {
    // *** COMENTADO TEMPORARIAMENTE PARA EVITAR LOOP INFINITO ***
    // O sistema de validação de token funcionará através do AuthInterceptor
    // que já detecta automaticamente tokens expirados e limpa o cache
    
    /*
    val authViewModel: com.technote.aquasense.presentation.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val uiState by authViewModel.uiState.collectAsState()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    // Observar mudanças no estado de autenticação apenas
    androidx.compose.runtime.LaunchedEffect(uiState.isLoggedIn) {
        if (!uiState.isLoggedIn && currentRoute !in listOf("login", "register", null)) {
            println("🔄 AuthObserver - Redirecionando para login (não autenticado)")
            try {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                println("⚠️ AuthObserver - Erro ao navegar: ${e.message}")
            }
        }
    }
    */
}
