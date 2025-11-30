package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SensorDataDto(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("tank_id")
    val tankId: String,

    @SerializedName("temperatura")
    val temperature: Double,

    @SerializedName("ph")
    val ph: Double,

    @SerializedName("tds")
    val tds: Double,

    @SerializedName("created_at")
    val createdAt: String? = null,
)

/**
 * DTO para o endpoint /sensores/ultima
 * A API retorna um único valor por tipo de sensor
 */
data class LatestSensorDto(
    @SerializedName("valor")
    val value: Double? = null,

    @SerializedName("tipo")
    val type: String,

    @SerializedName("tanque")
    val tank: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("data")
    val date: String? = null,
)

data class UpdateSensorDataRequest(
    @SerializedName("tank_id")
    val tankId: String,

    @SerializedName("temperatura")
    val temperature: Double,

    @SerializedName("ph")
    val ph: Double,

    @SerializedName("tds")
    val tds: Double,
)