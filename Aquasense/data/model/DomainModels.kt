package com.technote.aquasense.data.model


/**
 * Modelos de domínio - representam entidades do negócio
 * Independentes de detalhes de implementação da API
 */

data class User(
    val id: String?,
    val name: String,
    val email: String,
    val createdAt: String? = null,
)

data class Tank(
    val id: String?,
    val name: String,
    val volume: Double,
    val userId: String,
    val createdAt: String? = null, // Quando Criado automaticamente será nulo
)

data class Fish(
    val id: String?,
    val name: String,
    val idade: Int,
    val species: String,
    val peso: Float,
    val quantidade: Int,
    val tankId: String,
    val userId: String,
    val createdAt: String? = null, // Quando Criado automaticamente será nulo
)

data class SensorData(
    val id: String?,
    val tankId: String,
    val temperature: Double,
    val ph: Double,
    val tds: Double,
    val createdAt: String? = null, // Quando Criado automaticamente será nulo
)

data class WaterPrediction(
    val tankId: String,
    val prediction: PredictionData,
    val confidence: Double,
    val timestamp: String
)

data class PredictionData(
    val waterQuality: String,
    val recommendations: List<String>,
    val alerts: List<Alert>
)

data class Alert(
    val type: String,
    val message: String,
    val severity: String
)

