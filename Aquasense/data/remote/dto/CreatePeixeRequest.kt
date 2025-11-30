package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreatePeixeRequest(
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

    @SerializedName("user_id")
    val userId: String
)
