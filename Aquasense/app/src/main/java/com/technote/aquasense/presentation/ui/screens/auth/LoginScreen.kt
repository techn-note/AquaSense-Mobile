package com.technote.aquasense.presentation.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.technote.aquasense.R
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaLogo
import com.technote.aquasense.presentation.ui.components.common.AquaTextField
import com.technote.aquasense.presentation.ui.components.common.AuthActionRow
import com.technote.aquasense.presentation.ui.components.common.FormContainer
import com.technote.aquasense.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val uiState by authViewModel.uiState.collectAsState()
    
    // Navegar para home quando login bem-sucedido
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    FormContainer {
        AquaLogo(
            size = 225.dp,
        )
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF737373)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Exibir erro se houver
        uiState.authError?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = error,
                    color = Color(0xFFC62828),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Campo Email
        AquaTextField(
            value = email,
            onValueChange = { 
                email = it
                // Limpar erro ao digitar
                if (uiState.authError != null) {
                    authViewModel.clearError()
                }
            },
            label = stringResource(R.string.email_label_simple),
            placeholder = stringResource(R.string.email_placeholder),
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Senha
        AquaTextField(
            value = password,
            onValueChange = { 
                password = it
                // Limpar erro ao digitar
                if (uiState.authError != null) {
                    authViewModel.clearError()
                }
            },
            label = stringResource(R.string.password_label_simple),
            placeholder = stringResource(R.string.password_placeholder),
            isPassword = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthActionRow(
            primaryText = stringResource(R.string.login_button),
            secondaryText = stringResource(R.string.register_button),
            onPrimaryClick = {
                authViewModel.login(email, password)
            },
            onSecondaryClick = {
                navController.navigate("register")
            },
            isLoading = uiState.isLoading,
            primaryEnabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading
        )
    }
}
