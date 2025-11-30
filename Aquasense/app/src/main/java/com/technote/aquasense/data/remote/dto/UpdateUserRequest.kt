package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("age")
    val age: Int? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("password")
    val password: String? = null
) {
    /**
     * Retorna apenas os campos não-nulos para debug
     */
    fun getModifiedFields(): String {
        val fields = mutableListOf<String>()
        name?.let { fields.add("name: $it") }
        email?.let { fields.add("email: $it") }
        age?.let { fields.add("age: $it") }
        country?.let { fields.add("country: $it") }
        password?.let { fields.add("password: [FORNECIDA]") }
        
        return if (fields.isEmpty()) {
            "Nenhum campo para atualizar"
        } else {
            "Campos modificados: ${fields.joinToString(", ")}"
        }
    }
}