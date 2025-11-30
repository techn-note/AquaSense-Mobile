package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NeuralPredictRequest(
    @SerializedName("temperatura")
    val temperatura: Double,
    
    @SerializedName("ph")
    val ph: Double,
    
    @SerializedName("tds") 
    val tds: Double,
    
    @SerializedName("fase")
    val fase: String = "juvenil"
)