package com.technote.aquasense.presentation.viewmodel

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.data.model.User
import com.technote.aquasense.domain.usecase.auth.GetCurrentUserUseCase
import com.technote.aquasense.domain.usecase.auth.UpdateUserUseCase
import com.technote.aquasense.domain.usecase.fish.GetFishesByUserUseCase
import com.technote.aquasense.domain.usecase.tank.GetTanksByUserUseCase
import com.technote.aquasense.utils.ProfileImageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getTanksByUserUseCase: GetTanksByUserUseCase,
    private val getFishesByUserUseCase: GetFishesByUserUseCase,
    private val profileImageManager: ProfileImageManager,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    data class ProfileUiState(
        val isLoading: Boolean = false,
        val user: User? = null,
        val tanks: List<Tank> = emptyList(),
        val fishes: List<Fish> = emptyList(),
        val error: String? = null,
        val totalTanks: Int = 0,
        val totalFishes: Int = 0,
        // URI temporária (staged) selecionada pela UI. Só aplicada quando o usuário confirma (Salvar)
        val stagedProfileImageUri: String? = null,
        // Ação a ser aplicada no salvar: NONE = nada, SET = salvar stagedUri, REMOVE = apagar imagem existente
        val stagedProfileImageAction: ProfileImageAction = ProfileImageAction.NONE,
        // Nome temporário para edição
        val tempUserName: String? = null
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Buscar dados do usuário
                val userResult = getCurrentUserUseCase.getUserProfile()

                userResult.fold(
                    onSuccess = { user ->
                        // Tentar carregar imagem local salva por userId (se existir)
                        var updatedUser = user
                        user.id?.let { userId ->
                            val localPath = profileImageManager.getProfileImageUri(appContext, userId)
                            if (!localPath.isNullOrBlank()) {
                                updatedUser = user.copy(profileImageUri = localPath)
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            user = updatedUser,
                            isLoading = false
                        )

                        // Buscar estatísticas (tanques e peixes)
                        user.id?.let { userId ->
                            loadUserStatistics(userId)
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Erro ao carregar perfil: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    private fun loadUserStatistics(userId: String) {
        viewModelScope.launch {
            try {
                // Buscar tanques do usuário
                val tanksResult = getTanksByUserUseCase(userId)
                tanksResult.fold(
                    onSuccess = { tanks ->
                        _uiState.value = _uiState.value.copy(
                            tanks = tanks,
                            totalTanks = tanks.size
                        )
                    },
                    onFailure = {
                        println("❌ Erro ao carregar tanques: ${it.message}")
                    }
                )

                // Buscar peixes do usuário
                val fishesResult = getFishesByUserUseCase(userId)
                fishesResult.fold(
                    onSuccess = { fishes ->
                        _uiState.value = _uiState.value.copy(
                            fishes = fishes,
                            totalFishes = fishes.size
                        )
                    },
                    onFailure = {
                        println("❌ Erro ao carregar peixes: ${it.message}")
                    }
                )
            } catch (e: Exception) {
                println("❌ Erro ao carregar estatísticas: ${e.message}")
            }
        }
    }

    fun refreshProfile() {
        loadProfileData()
    }
    
    /**
     * Atualiza a foto de perfil do usuário (staging)
     * @param newImageUri Nova URI da imagem (null = não definido)
     */
    fun updateProfileImage(newImageUri: String?) {
        _uiState.value = _uiState.value.copy(
            stagedProfileImageUri = newImageUri,
            stagedProfileImageAction = if (newImageUri != null) ProfileImageAction.SET else ProfileImageAction.NONE
        )
    }

    /**
     * O usuário solicitou remover a foto de perfil (staged)
     */
    fun requestRemoveProfileImage() {
        _uiState.value = _uiState.value.copy(
            stagedProfileImageUri = null,
            stagedProfileImageAction = ProfileImageAction.REMOVE
        )
    }

    /**
     * Confirma a imagem do perfil (usar após salvar o arquivo localmente)
     */
    fun finalizeProfileImage(finalPath: String?) {
        val currentUser = _uiState.value.user
        if (currentUser != null) {
            val updatedUser = currentUser.copy(profileImageUri = finalPath)
            _uiState.value = _uiState.value.copy(
                user = updatedUser, 
                stagedProfileImageUri = null,
                stagedProfileImageAction = ProfileImageAction.NONE
            )
        } else {
            // Se não temos usuário carregado, apenas limpar o staged
            _uiState.value = _uiState.value.copy(
                stagedProfileImageUri = null,
                stagedProfileImageAction = ProfileImageAction.NONE
            )
        }
    }

    fun clearStagedProfileImage() {
        _uiState.value = _uiState.value.copy(stagedProfileImageUri = null, stagedProfileImageAction = ProfileImageAction.NONE)
    }
    
    /**
     * Salva as alterações do perfil (incluindo foto e nome)
     * @param context Contexto necessário para gravar arquivos locais
     */
    fun saveProfileChanges(context: Context): Boolean {
        return try {
            val currentUser = _uiState.value.user
            if (currentUser == null) {
                println("❌ Nenhum usuário carregado para salvar")
                return false
            }

            when (_uiState.value.stagedProfileImageAction) {
                ProfileImageAction.SET -> {
                    val staged = _uiState.value.stagedProfileImageUri
                    if (!staged.isNullOrBlank()) {
                        val finalPath = profileImageManager.saveProfileImage(context, currentUser.id ?: return false, Uri.parse(staged))
                        // atualizar estado com imagem final
                        finalizeProfileImage(finalPath)
                    }
                }
                ProfileImageAction.REMOVE -> {
                    profileImageManager.deleteProfileImage(context, currentUser.id ?: return false)
                    finalizeProfileImage(null)
                }
                ProfileImageAction.NONE -> {
                    // Nada a fazer com imagem
                }
            }

            // Aqui você pode também persistir outras mudanças locais ou remotas (nome, etc.)
            println("✅ Alterações do perfil salvas localmente")

            // reset action
            _uiState.value = _uiState.value.copy(stagedProfileImageAction = ProfileImageAction.NONE)

            true
        } catch (e: Exception) {
            println("❌ Erro ao salvar alterações: ${e.message}")
            false
        }
    }

    /**
     * Atualiza o nome do usuário temporariamente (apenas no estado local)
     */
    fun updateUserName(newName: String) {
        _uiState.value = _uiState.value.copy(tempUserName = newName)
    }

    /**
     * Atualiza nome, email, idade e país do usuário no servidor (para EditProfileScreen)
     * Apenas campos que foram alterados serão enviados na requisição
     */
    fun updateUserProfile(newName: String, newEmail: String, newAge: Int?, newCountry: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            println("🔄 Tentando atualizar perfil com:")
            println("   - Nome: $newName")
            println("   - Email: $newEmail") 
            println("   - Idade: $newAge")
            println("   - País: $newCountry")
            
            updateUserUseCase.updateProfile(name = newName, email = newEmail, age = newAge, country = newCountry).fold(
                onSuccess = { updatedUser ->
                    _uiState.value = _uiState.value.copy(
                        user = updatedUser,
                        tempUserName = null,
                        isLoading = false,
                        error = null
                    )
                    println("✅ Perfil atualizado com sucesso. Dados atuais: $updatedUser")
                },
                onFailure = { exception ->
                    val errorMessage = when {
                        exception.message?.contains("email", ignoreCase = true) == true && 
                        exception.message?.contains("existe", ignoreCase = true) == true -> 
                            "Este e-mail já está sendo usado por outro usuário"
                        exception.message?.contains("email", ignoreCase = true) == true && 
                        exception.message?.contains("already", ignoreCase = true) == true -> 
                            "Este e-mail já está sendo usado por outro usuário"
                        else -> "Erro ao atualizar perfil: ${exception.message}"
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                    println("❌ Erro ao atualizar perfil: ${exception.message}")
                }
            )
        }
    }

    /**
     * Atualiza apenas a senha do usuário (para ProfileScreen)
     */
    fun updateUserPassword(newPassword: String): Boolean {
        return try {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                updateUserUseCase.updatePassword(password = newPassword).fold(
                    onSuccess = { updatedUser ->
                        _uiState.value = _uiState.value.copy(
                            user = updatedUser,
                            isLoading = false
                        )
                        println("✅ Senha atualizada com sucesso")
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Erro ao atualizar senha: ${exception.message}"
                        )
                        println("❌ Erro ao atualizar senha: ${exception.message}")
                    }
                )
            }
            true
        } catch (e: Exception) {
            println("❌ Erro inesperado ao atualizar senha: ${e.message}")
            false
        }
    }

    /**
     * Limpa erros
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

enum class ProfileImageAction {
    NONE,
    SET,
    REMOVE
}