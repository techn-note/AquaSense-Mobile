package com.technote.aquasense.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.technote.aquasense.data.remote.api.AuthInterceptor
import com.technote.aquasense.domain.repository.AuthRepository
import com.technote.aquasense.utils.JwtUtils
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observer do ciclo de vida da aplicação para detectar quando o usuário
 * volta ao aplicativo e verificar se o token expirou
 */
@Singleton
class AppLifecycleObserver @Inject constructor(
    private val authInterceptor: AuthInterceptor,
    private val authRepository: AuthRepository
) : DefaultLifecycleObserver {
    
    private val _tokenExpiredEvents = MutableSharedFlow<Unit>()
    val tokenExpiredEvents: SharedFlow<Unit> = _tokenExpiredEvents.asSharedFlow()
    
    private var wasInBackground = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        println("🔄 AppLifecycleObserver - App voltou para foreground")
        
        // Se o app estava em background, verificar token
        if (wasInBackground) {
            checkTokenOnAppResume()
            wasInBackground = false
        }
    }
    
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        println("⏸️ AppLifecycleObserver - App foi para background")
        wasInBackground = true
    }
    
    /**
     * Verifica se o token expirou quando o usuário volta ao app
     * Faz verificação real com a API para confirmar se o token ainda é válido
     */
    private fun checkTokenOnAppResume() {
        val token = authInterceptor.getAuthToken()
        
        if (token != null) {
            println("🔍 AppLifecycleObserver - Verificando token ao voltar ao app...")
            
            // Primeiro, verificar se o token está expirado localmente
            if (JwtUtils.isTokenExpired(token)) {
                println("❌ AppLifecycleObserver - Token expirado localmente!")
                handleTokenExpired()
                return
            }
            
            // Token parece válido localmente, verificar com a API
            scope.launch {
                try {
                    println("🔍 AppLifecycleObserver - Fazendo verificação real com API...")
                    val result = authRepository.getUserProfile("")
                    
                    result.fold(
                        onSuccess = {
                            println("✅ AppLifecycleObserver - Token válido confirmado pela API")
                        },
                        onFailure = { exception ->
                            println("❌ AppLifecycleObserver - Token inválido detectado pela API: ${exception.message}")
                            handleTokenExpired()
                        }
                    )
                } catch (e: Exception) {
                    println("⚠️ AppLifecycleObserver - Erro ao verificar token: ${e.message}")
                    // Em caso de erro de rede, não invalidar o token
                }
            }
        }
    }
    
    /**
     * Manipula token expirado
     */
    private fun handleTokenExpired() {
        authInterceptor.clearAuthToken()
        authInterceptor.markTokenExpired()
        
        // Emitir evento para a UI
        try {
            _tokenExpiredEvents.tryEmit(Unit)
        } catch (e: Exception) {
            println("⚠️ Erro ao emitir evento de token expirado: ${e.message}")
        }
    }
    
    /**
     * Força verificação manual do token
     */
    fun checkTokenNow() {
        checkTokenOnAppResume()
    }
}