package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TankDto(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("capacity")
    val volume: Double? = null,

    @SerializedName("number")
    val number: Double? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

data class CreateTankRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("capacity")
    val capacity: Double,

    @SerializedName("number")
    val number: Double,

    @SerializedName("user_id")
    val userId: String,
)

data class UpdateTankRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("capacity")
    val capacity: Double,

    @SerializedName("number")
    val number: Double,
)