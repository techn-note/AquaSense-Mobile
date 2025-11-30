package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PeixeDto(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("user_id")
    val userId: String = "",

    @SerializedName("nome")
    val nome: String,

    @SerializedName("idade")
    val idade: Int,

    @SerializedName("especie")
    val especie: String,

    @SerializedName("peso")
    val peso: Float,

    @SerializedName("quantidade")
    val quantidade: Int,

    @SerializedName("tanque")
    val tanque: String = "",

    @SerializedName("created_at")
    val createdAt: String? = null
)
