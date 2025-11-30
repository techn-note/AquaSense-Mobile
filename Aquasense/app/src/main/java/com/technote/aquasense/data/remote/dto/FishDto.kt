package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FishDto(
    @SerializedName("id")
    val id: String? = null, // Quando Criado automaticamente será nulo

    @SerializedName("name")
    val name: String,

    @SerializedName("species")
    val species: String,

    @SerializedName("tank_id")
    val tankId: String,

    @SerializedName("created_at")
    val createdAt: String? = null, // Quando Criado automaticamente será nulo
)

data class CreateFishRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("species")
    val species: String,

    @SerializedName("tank_id")
    val userId: String,
)

data class UpdateFishRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("species")
    val species: String,
)

