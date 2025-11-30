package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Wrapper das respostas da API Flask
 */
data class ApiResponse<T>(
    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: T?,

    @SerializedName("error")
    val error: String?
)
