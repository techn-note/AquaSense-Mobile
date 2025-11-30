package com.technote.aquasense.utils

import android.util.Base64
import org.json.JSONObject

/**
 * Utilitário para trabalhar com JWT tokens
 */
object JwtUtils {
    
    /**
     * Decodifica um JWT token e extrai o userId do campo 'sub'
     * 
     * JWT format: header.payload.signature
     * O payload contém: {"sub": "userId", "exp": timestamp, ...}
     */
    fun extractUserIdFromToken(token: String): String? {
        return try {
            // JWT tem 3 partes separadas por '.'
            val parts = token.split(".")
            if (parts.size != 3) {
                println("❌ JWT inválido: não tem 3 partes")
                return null
            }
            
            // Decodifica a segunda parte (payload)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes)
            
            // Parse JSON
            val json = JSONObject(decodedString)
            val userId = json.optString("sub", null)
            
            println("✅ JWT decodificado - userId: $userId")
            userId
        } catch (e: Exception) {
            println("❌ Erro ao decodificar JWT: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Verifica se o token está expirado
     */
    fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes)
            val json = JSONObject(decodedString)
            
            val exp = json.optLong("exp", 0)
            val currentTime = System.currentTimeMillis() / 1000
            
            currentTime >= exp
        } catch (e: Exception) {
            true
        }
    }
}
