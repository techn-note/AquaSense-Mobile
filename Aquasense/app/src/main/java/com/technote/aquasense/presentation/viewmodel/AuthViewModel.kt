package com.technote.aquasense.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.domain.usecase.auth.LoginUseCase
import com.technote.aquasense.domain.usecase.auth.RegisterUseCase
import com.technote.aquasense.domain.usecase.auth.LogoutUseCase
import com.technote.aquasense.domain.usecase.auth.GetCurrentUserUseCase
import com.technote.aquasense.domain.usecase.auth.CheckTokenExpirationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsável pelo gerenciamento de autenticação
 *
 * Responsabilidades:
 * - Gerenciar estado de login/registro
 * - Executar operações de autenticação
 * - Manter estado da UI consistente
 * - Tratar erros de autenticação
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val checkTokenExpirationUseCase: CheckTokenExpirationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // *** DESABILITADO TEMPORARIAMENTE PARA EVITAR LOOP INFINITO ***
        // checkExistingSession()
        
        // Verificação simples de token sem usar UseCase complexo
        viewModelScope.launch {
            val isLoggedIn = getCurrentUserUseCase.isUserLoggedIn()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = isLoggedIn,
                currentUser = if (isLoggedIn) "Usuário" else "",
                isLoading = false
            )
            println("🔍 AuthViewModel - Estado inicial: isLoggedIn=$isLoggedIn")
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                authError = null
            )

            val result = loginUseCase(email, password)

            result.fold(
                onSuccess = { (token, user) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user.toString(),
                        authError = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        authError = "Email ou senha inválidos. Verifique seus dados e tente novamente."
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String, age: Int, country: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                authError = null
            )

            val result = registerUseCase(name, email, password, confirmPassword, age, country)

            result.fold(
                onSuccess = { (token, user) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user.name ?: user.email ?: "Usuário",
                        authError = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        authError = "Não foi possível criar sua conta. Verifique os dados informados."
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(authError = null)
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = AuthUiState()
        }
    }
    
    /**
     * Verifica se o token expirou e executa logout automático se necessário
     * SIMPLIFICADO PARA EVITAR LOOP INFINITO
     */
    fun checkTokenExpiration() {
        // Por enquanto, não fazer nada - deixar o AuthInterceptor cuidar disso
        println("🔍 AuthViewModel - checkTokenExpiration chamado (sem ação por enquanto)")
    }
    
    private fun checkExistingSession() {
        // Evitar verificação múltipla se já está processando
        if (_uiState.value.isLoading) return
        
        viewModelScope.launch {
            println("🔍 AuthViewModel - Verificando sessão existente...")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // *** VERIFICAÇÃO DE TOKEN EXPIRADO ***
                val tokenExpired = checkTokenExpirationUseCase.checkAndHandleExpiration()
                if (tokenExpired) {
                    println("⚠️ AuthViewModel - Token expirado detectado - forçando logout")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        currentUser = "",
                        authError = "Sua sessão expirou. Por favor, faça login novamente."
                    )
                    return@launch
                }
                
                val isLoggedIn = getCurrentUserUseCase.isUserLoggedIn()
                
                if (isLoggedIn) {
                    println("✅ AuthViewModel - Sessão ativa encontrada")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = "Usuário",
                        authError = null
                    )
                } else {
                    println("❌ AuthViewModel - Nenhuma sessão ativa encontrada")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        currentUser = "",
                        authError = null
                    )
                }
            } catch (e: Exception) {
                println("❌ AuthViewModel - Erro na verificação de sessão: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    currentUser = "",
                    authError = null
                )
            }
        }
    }
}

/**
 * Estado da UI para autenticação
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: String = "",
    val authError: String? = null
)