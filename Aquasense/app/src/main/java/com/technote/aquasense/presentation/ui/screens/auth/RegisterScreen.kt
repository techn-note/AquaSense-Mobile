package com.technote.aquasense.presentation.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.technote.aquasense.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaTextField
import com.technote.aquasense.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Navegar para Home quando registro for bem-sucedido
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            Toast.makeText(context, context.getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    // Mostrar erro via Toast
    LaunchedEffect(uiState.authError) {
        uiState.authError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            authViewModel.clearError()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = stringResource(R.string.register_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // Campo Nome
            Text(
                text = stringResource(R.string.name_label_simple),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            AquaTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.name_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                enabled = !uiState.isLoading
            )

            // Campo E-mail
            Text(
                text = stringResource(R.string.email_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            AquaTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(R.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                enabled = !uiState.isLoading
            )

            // Campo Idade
            Text(
                text = stringResource(R.string.age_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            AquaTextField(
                value = age,
                onValueChange = { age = it },
                placeholder = stringResource(R.string.age_placeholder),
                keyboardType = KeyboardType.Number,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                enabled = !uiState.isLoading
            )

            // Campo País
            Text(
                text = stringResource(R.string.country_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            AquaTextField(
                value = country,
                onValueChange = { country = it },
                placeholder = stringResource(R.string.country_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                enabled = !uiState.isLoading
            )

            // Campo Senha
            Text(
                text = stringResource(R.string.password_label_simple),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            AquaTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(R.string.password_placeholder),
                isPassword = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                enabled = !uiState.isLoading
            )

            // Campo Confirmar Senha
            Text(
                text = stringResource(R.string.confirm_new_password),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp)
            )
            AquaTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = stringResource(R.string.confirm_password_placeholder),
                isPassword = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                enabled = !uiState.isLoading
            )

            // Botões
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Link "Logar"
                TextButton(
                    onClick = { navController.navigate("login") },
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = stringResource(R.string.login_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF007BFF)
                    )
                }

                // Botão Cadastrar
                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull() ?: 0
                        authViewModel.register(name, email, password, confirmPassword, ageInt, country)
                    },
                    modifier = Modifier
                        .width(130.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007BFF)
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.register_button),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
