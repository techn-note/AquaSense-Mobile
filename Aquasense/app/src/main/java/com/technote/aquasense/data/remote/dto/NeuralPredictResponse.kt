package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NeuralPredictResponse(
    @SerializedName("classificacao")
    val classificacao: String,
    
    @SerializedName("probabilidades")
    val probabilidades: Map<String, Double>,
    
    @SerializedName("scores_percent")
    val scoresPercent: ScoresPercent,
    
    @SerializedName("shap")
    val shap: Shap
)

data class ScoresPercent(
    @SerializedName("pH")
    val ph: Double,
    
    @SerializedName("tds")
    val tds: Double,
    
    @SerializedName("temperatura")
    val temperatura: Double
)

data class Shap(
    @SerializedName("ph")
    val ph: Double,
    
    @SerializedName("tds")
    val tds: Double,
    
    @SerializedName("temperatura")
    val temperatura: Double
)