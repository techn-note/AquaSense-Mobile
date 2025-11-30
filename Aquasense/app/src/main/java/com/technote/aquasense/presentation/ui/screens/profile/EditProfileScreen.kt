package com.technote.aquasense.presentation.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.foundation.BorderStroke
import com.technote.aquasense.presentation.viewmodel.ProfileViewModel
import com.technote.aquasense.presentation.viewmodel.ProfileImageAction
import com.technote.aquasense.presentation.ui.components.common.ProfileImagePicker

@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileUiState by profileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(2) }
    var editedUserName by remember { mutableStateOf("") }
    var editedUserEmail by remember { mutableStateOf("") }
    var editedUserAge by remember { mutableStateOf("") }
    var editedUserCountry by remember { mutableStateOf("") }
    
    // Flag para controlar se uma atualização foi solicitada
    var isUpdating by remember { mutableStateOf(false) }
    
    // Estado para controlar erro no campo de email
    var hasEmailError by remember { mutableStateOf(false) }
    
    // Atualizar os campos editados quando os dados do perfil carregarem
    LaunchedEffect(profileUiState.user) {
        profileUiState.user?.let { user ->
            editedUserName = user.name
            editedUserEmail = user.email
            editedUserAge = user.age?.toString() ?: ""
            editedUserCountry = user.country ?: ""
        }
    }

    // Observar mudanças no estado do perfil apenas se uma atualização foi solicitada
    LaunchedEffect(profileUiState.error, profileUiState.isLoading, isUpdating) {
        if (isUpdating) {
            if (!profileUiState.isLoading && profileUiState.error == null) {
                // Sucesso - atualização bem-sucedida
                hasEmailError = false // Resetar erro de email
                profileViewModel.saveProfileChanges(context)
                Toast.makeText(context, context.getString(R.string.profile_updated_success), Toast.LENGTH_SHORT).show()
                navController.navigateUp()
                isUpdating = false
            } else if (!profileUiState.isLoading && profileUiState.error != null) {
                // Erro - verificar se é erro de email
                val errorMessage = profileUiState.error!!
                hasEmailError = errorMessage.contains("email", ignoreCase = true) && 
                               (errorMessage.contains("já está em uso", ignoreCase = true) || 
                                errorMessage.contains("already", ignoreCase = true))
                
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                profileViewModel.clearError()
                isUpdating = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 100.dp), // Espaço para o FAB
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))

                // Avatar do usuário com seleção de imagem
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
                    // Decidir qual imagem mostrar: staged (preview) ou salva
                    val displayImageUri = when (profileUiState.stagedProfileImageAction) {
                        ProfileImageAction.REMOVE -> null // Mostrar ícone padrão se vai remover
                        ProfileImageAction.SET -> profileUiState.stagedProfileImageUri // Mostrar preview da staged
                        ProfileImageAction.NONE -> profileUiState.user?.profileImageUri // Mostrar imagem salva
                    }
                    
                    ProfileImagePicker(
                        currentImageUri = displayImageUri,
                        onImageSelected = { newImageUri ->
                            if (newImageUri == null) {
                                profileViewModel.requestRemoveProfileImage()
                            } else {
                                profileViewModel.updateProfileImage(newImageUri)
                            }
                        },
                        size = 120.dp,
                        isEditable = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nome do usuário (flutuando)
                Text(
                    text = if (profileUiState.isLoading) stringResource(R.string.loading) 
                          else (profileUiState.user?.name ?: stringResource(R.string.greeting_default).substringAfter(", ").substringBefore("!")),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Estatísticas do usuário
                ProfileStatistics(
                    totalTanks = profileUiState.totalTanks,
                    totalFishes = profileUiState.totalFishes,
                    isLoading = profileUiState.isLoading
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Informações editáveis do perfil
                ProfileInformation(
                    userName = editedUserName,
                    userEmail = editedUserEmail,
                    userAge = editedUserAge,
                    userCountry = editedUserCountry,
                    createdAt = profileUiState.user?.createdAt ?: "",
                    isLoading = profileUiState.isLoading,
                    hasEmailError = hasEmailError,
                    onNameChanged = { newName -> editedUserName = newName },
                    onEmailChanged = { newEmail -> 
                        editedUserEmail = newEmail
                        hasEmailError = false // Resetar erro quando o usuário edita o campo
                    },
                    onAgeChanged = { newAge -> editedUserAge = newAge },
                    onCountryChanged = { newCountry -> editedUserCountry = newCountry }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Ações do perfil
                    EditProfileActions(
                        onSaveClick = {
                            // Marcar que uma atualização foi solicitada
                            isUpdating = true
                            // Atualizar dados do usuário no servidor
                            val age = editedUserAge.toIntOrNull()
                            profileViewModel.updateUserProfile(
                                newName = editedUserName,
                                newEmail = editedUserEmail,
                                newAge = age,
                                newCountry = editedUserCountry
                            )
                        },
                        onCancelClick = {
                            navController.navigateUp()
                        }
                    )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}



@Composable
private fun ProfileStatistics(
    totalTanks: Int,
    totalFishes: Int,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card de Tanques
        StatisticCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.tanks_count),
            value = if (isLoading) "..." else totalTanks.toString(),
            icon = Icons.Default.WaterDrop,
            backgroundColor = Color(0xFFE3F2FD),
            iconColor = Color(0xFF2196F3)
        )

        // Card de Peixes
        StatisticCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.fishes_count),
            value = if (isLoading) "..." else totalFishes.toString(),
            icon = Icons.Default.SetMeal,
            backgroundColor = Color(0xFFFFE8D6),
            iconColor = Color(0xFFFF9800)
        )
    }
}

@Composable
private fun StatisticCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Valor
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            // Título
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ProfileInformation(
    userName: String,
    userEmail: String,
    userAge: String,
    userCountry: String,
    createdAt: String,
    isLoading: Boolean,
    hasEmailError: Boolean = false,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onCountryChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.personal_information),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    )
                    if (it < 3) Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                // Campo editável para o nome
                EditableProfileField(
                    label = stringResource(R.string.username_label),
                    value = userName,
                    onValueChange = onNameChanged,
                    icon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo editável para o email
                EditableProfileField(
                    label = stringResource(R.string.email_label),
                    value = userEmail,
                    onValueChange = onEmailChanged,
                    icon = Icons.Default.Email,
                    hasError = hasEmailError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo editável para a idade
                EditableProfileField(
                    label = stringResource(R.string.age_label),
                    value = userAge,
                    onValueChange = onAgeChanged,
                    icon = Icons.Default.Cake,
                    isNumeric = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo editável para o país
                EditableProfileField(
                    label = stringResource(R.string.country_label),
                    value = userCountry,
                    onValueChange = onCountryChanged,
                    icon = Icons.Default.LocationOn
                )

                if (createdAt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoItem(
                        label = stringResource(R.string.member_since),
                        value = createdAt,
                        icon = Icons.Default.DateRange
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value.ifBlank { stringResource(R.string.required_field) },
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun EditableProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isNumeric: Boolean = false,
    hasError: Boolean = false
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        text = when(label) {
                            context.getString(R.string.username_label) -> context.getString(R.string.name_placeholder)
                            context.getString(R.string.email_label) -> context.getString(R.string.email_placeholder)
                            context.getString(R.string.age_label) -> context.getString(R.string.age_placeholder)
                            context.getString(R.string.country_label) -> context.getString(R.string.country_placeholder)
                            else -> "Digite $label"
                        },
                        color = Color(0xFF999999),
                        fontSize = 14.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (hasError) Color(0xFFE53E3E) else Color(0xFF2196F3),
                    unfocusedBorderColor = if (hasError) Color(0xFFE53E3E) else Color(0xFFE0E0E0),
                    focusedTextColor = Color(0xFF1A1A1A),
                    unfocusedTextColor = Color(0xFF1A1A1A),
                    errorBorderColor = Color(0xFFE53E3E)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun EditProfileActions(
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botão Salvar Alterações
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Salvar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.save_changes),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }

        // Botão Cancelar
        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            border = BorderStroke(2.dp, Color(0xFF757575)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = "Cancelar",
                tint = Color(0xFF757575),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.cancel),
                color = Color(0xFF757575),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}