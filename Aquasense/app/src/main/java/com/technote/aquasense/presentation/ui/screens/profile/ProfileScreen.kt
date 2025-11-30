package com.technote.aquasense.presentation.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.technote.aquasense.presentation.ui.components.common.AquaBottomNavigation
import com.technote.aquasense.presentation.ui.components.common.AquaHeader
import com.technote.aquasense.presentation.viewmodel.AuthViewModel
import com.technote.aquasense.presentation.viewmodel.ProfileViewModel
import com.technote.aquasense.presentation.ui.components.common.ProfileImagePicker

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(2) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    // Debug e verificação de logout mais robusta
    LaunchedEffect(authUiState.isLoggedIn, authUiState.currentUser, authUiState.isLoading) {
        println("🔍 ProfileScreen - Estado Auth:")
        println("   isLoggedIn: ${authUiState.isLoggedIn}")
        println("   currentUser: '${authUiState.currentUser}'")
        println("   isLoading: ${authUiState.isLoading}")
        
        if (!authUiState.isLoggedIn && 
            authUiState.currentUser.isEmpty() && 
            !authUiState.isLoading) {
            
            println("⏳ ProfileScreen - Aguardando verificação de sessão...")
            kotlinx.coroutines.delay(1000)
            
            if (!authUiState.isLoggedIn && authUiState.currentUser.isEmpty()) {
                println("❌ ProfileScreen - Fazendo logout...")
                Toast.makeText(context, context.getString(R.string.session_ended), Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar do usuário (apenas visualização)
            if (profileUiState.isLoading) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD))
                        .border(4.dp, Color(0xFF2196F3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF2196F3)
                    )
                }
            } else {
                ProfileImagePicker(
                    currentImageUri = profileUiState.user?.profileImageUri,
                    onImageSelected = { }, // Não editável na tela de visualização
                    size = 120.dp,
                    isEditable = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nome do usuário
            Text(
                text = if (profileUiState.isLoading) stringResource(R.string.loading) 
                      else (profileUiState.user?.name ?: authUiState.currentUser.ifBlank { stringResource(R.string.greeting_default).substringAfter(", ").substringBefore("!") }),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botões de ação
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Editar dados Pessoais
                ProfileActionButton(
                    text = stringResource(R.string.edit_personal_data),
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate("edit_profile") }
                )

                // Alterar a Senha
                ProfileActionButton(
                    text = stringResource(R.string.change_password),
                    icon = Icons.Default.Lock,
                    onClick = { showChangePasswordDialog = true }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botão Sair da conta
            Button(
                onClick = {
                    authViewModel.logout()
                    Toast.makeText(context, context.getString(R.string.logout_success_simple), Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53E3E)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = stringResource(R.string.cd_logout),
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.logout_account),
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
        
        // Diálogo de mudança de senha
        if (showChangePasswordDialog) {
            ChangePasswordDialog(
                onConfirm = { newPassword ->
                    val success = profileViewModel.updateUserPassword(newPassword)
                    if (success) {
                        Toast.makeText(context, context.getString(R.string.password_changed_success), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.password_change_error), Toast.LENGTH_SHORT).show()
                    }
                    showChangePasswordDialog = false
                },
                onDismiss = { showChangePasswordDialog = false }
            )
        }
    }
}

@Composable
private fun ProfileActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF666666),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.cd_navigate),
                tint = Color(0xFF666666),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.change_password_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Senha atual
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(stringResource(R.string.current_password)) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Nova senha
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.new_password)) },
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                            Icon(
                                imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isNewPasswordVisible) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirmar nova senha
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.confirm_new_password)) },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isConfirmPasswordVisible) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword
                )

                if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                    Text(
                        text = "As senhas não coincidem",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isNotEmpty() && newPassword == confirmPassword && newPassword.length >= 6) {
                        onConfirm(newPassword)
                    }
                },
                enabled = newPassword.isNotEmpty() && newPassword == confirmPassword && newPassword.length >= 6,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(stringResource(R.string.update_button), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = Color(0xFF666666))
            }
        },
        containerColor = Color.White
    )
}