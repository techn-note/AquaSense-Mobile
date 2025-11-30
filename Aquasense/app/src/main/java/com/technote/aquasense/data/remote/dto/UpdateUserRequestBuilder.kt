package com.technote.aquasense.data.remote.dto

import com.technote.aquasense.data.model.User

/**
 * Builder para criar UpdateUserRequest apenas com campos que foram modificados
 */
class UpdateUserRequestBuilder {
    
    companion object {
        /**
         * Cria um UpdateUserRequest comparando valores atuais com os novos
         * Apenas campos que foram alterados serão incluídos na requisição
         */
        fun buildUpdateRequest(
            currentUser: User?,
            newName: String? = null,
            newEmail: String? = null,
            newAge: Int? = null,
            newCountry: String? = null,
            newPassword: String? = null
        ): UpdateUserRequest {
            
            println("🔍 Comparando valores para construir requisição:")
            println("   - Usuário atual: $currentUser")
            println("   - Novos valores: name=$newName, email=$newEmail, age=$newAge, country=$newCountry")
            
            val requestName = if (newName != null && newName != currentUser?.name) {
                println("   ✅ Nome será atualizado: '${currentUser?.name}' -> '$newName'")
                newName
            } else {
                if (newName == currentUser?.name) {
                    println("   ⚠️ Nome não modificado: '$newName'")
                }
                null
            }
            
            val requestEmail = if (newEmail != null && newEmail != currentUser?.email) {
                println("   ✅ Email será atualizado: '${currentUser?.email}' -> '$newEmail'")
                newEmail
            } else {
                if (newEmail == currentUser?.email) {
                    println("   ⚠️ Email não modificado: '$newEmail'")
                }
                null
            }
            
            val requestAge = if (newAge != null && newAge != currentUser?.age) {
                println("   ✅ Idade será atualizada: '${currentUser?.age}' -> '$newAge'")
                newAge
            } else {
                if (newAge == currentUser?.age) {
                    println("   ⚠️ Idade não modificada: '$newAge'")
                }
                null
            }
            
            val requestCountry = if (newCountry != null && newCountry != currentUser?.country) {
                println("   ✅ País será atualizado: '${currentUser?.country}' -> '$newCountry'")
                newCountry
            } else {
                if (newCountry == currentUser?.country) {
                    println("   ⚠️ País não modificado: '$newCountry'")
                }
                null
            }
            
            // Password sempre será enviado se fornecido (não podemos comparar por segurança)
            val requestPassword = newPassword
            if (requestPassword != null) {
                println("   🔐 Senha será atualizada")
            }
            
            return UpdateUserRequest(
                name = requestName,
                email = requestEmail,
                age = requestAge,
                country = requestCountry,
                password = requestPassword
            )
        }
    }
}