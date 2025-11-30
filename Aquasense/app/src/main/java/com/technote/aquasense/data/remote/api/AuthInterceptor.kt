package com.technote.aquasense.data.remote.api

import android.content.Context
import com.technote.aquasense.utils.JwtUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    
    private val sharedPrefs by lazy {
        context.getSharedPreferences("aquasense_auth", Context.MODE_PRIVATE)
    }
    
    private var _authToken: String? = null

    /**
     * Intercepta todas as requisições e adiciona o header de autorização quando necessário
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Lista de endpoints que não precisam de autenticação
        val publicEndpoints = listOf(
            "entrar",
            "registrar"
        )
        
        // Verifica se o endpoint atual é público
        val isPublicEndpoint = publicEndpoints.any { endpoint -> 
            url.contains("/$endpoint")
        }

        // Log para debug
        println("🔒 AuthInterceptor - URL: $url")
        println("🔒 AuthInterceptor - É público? $isPublicEndpoint")

        // Se for endpoint público, continue sem modificar
        if (isPublicEndpoint) {
            println("✅ Endpoint público - Prosseguindo sem token")
            return chain.proceed(originalRequest)
        }

        // Para endpoints privados, verificar e adicionar token de autenticação
        val token = getAuthToken()

        if (token == null) {
            println("⚠️ Token não disponível - Requisição sem autenticação")
            return chain.proceed(originalRequest)
        }

        // *** VALIDAÇÃO DE EXPIRAÇÃO DO TOKEN ***
        if (JwtUtils.isTokenExpired(token)) {
            println("❌ Token expirado detectado! Removendo token...")
            clearAuthToken()
            
            // Proceder sem token - deixar a API retornar 401
            return chain.proceed(originalRequest)
        }

        println("✅ Adicionando token válido: Bearer ${token.take(20)}...")
        val authenticatedRequest = originalRequest.newBuilder()
            .header(ApiConstants.HEADER_AUTHORIZATION, "${ApiConstants.HEADER_BEARER} $token")
            .build()
        
        val response = chain.proceed(authenticatedRequest)
        
        // *** TRATAMENTO DE RESPOSTA 401 ***
        if (response.code == 401) {
            println("🔒 Resposta 401 recebida - Token inválido ou expirado")
            clearAuthToken()
            // Marcar para notificar a UI sobre necessidade de login
            markTokenExpired()
        }
        
        return response
    }

    /**
     * Obtém o token de autenticação atual
     */
    fun getAuthToken(): String? {
        if (_authToken == null) {
            _authToken = sharedPrefs.getString("auth_token", null)
        }
        return _authToken
    }

    fun setAuthToken(token: String) {
        _authToken = token
        sharedPrefs.edit().putString("auth_token", token).apply()
    }

    fun clearAuthToken() {
        _authToken = null
        sharedPrefs.edit().remove("auth_token").apply()
        println("🗑️ Token removido do armazenamento")
    }
    
    /**
     * Marca que o token expirou para notificar a UI
     */
    fun markTokenExpired() {
        sharedPrefs.edit().putBoolean("token_expired", true).apply()
        println("⚠️ Token marcado como expirado")
    }
    
    /**
     * Verifica se o token foi marcado como expirado
     */
    fun isTokenMarkedExpired(): Boolean {
        return sharedPrefs.getBoolean("token_expired", false)
    }
    
    /**
     * Limpa a marcação de token expirado (usado após logout/novo login)
     */
    fun clearTokenExpiredFlag() {
        sharedPrefs.edit().remove("token_expired").apply()
        println("✅ Flag de token expirado limpa")
    }
}


