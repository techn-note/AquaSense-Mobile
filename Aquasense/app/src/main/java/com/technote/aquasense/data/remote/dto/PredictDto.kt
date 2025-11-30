package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PredictDto(
    @SerializedName("tank_id")
    val tankId: String,

    @SerializedName("prediction")
    val prediction: PredictionDataDto,

    @SerializedName("confidence")
    val confidence: Double,

    @SerializedName("timestamp")
    val timestamp: String,
)

data class PredictionDataDto(
    @SerializedName("water_quality")
    val waterQuality: String, // "excellent", "good", "ph", "poor"

    @SerializedName("recommendations")
    val recommendations: List<String>,

    @SerializedName("alerts")
    val alerts: List<AlertDto>
)

data class AlertDto(
    @SerializedName("type")
    val type: String, // "temperature", "ph", "tds"

    @SerializedName("message")
    val message: String,

    @SerializedName("severity")
    val severity: String // "high", "medium", "low"
)

