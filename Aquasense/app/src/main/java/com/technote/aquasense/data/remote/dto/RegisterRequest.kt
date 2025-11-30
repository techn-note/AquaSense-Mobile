package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("country")
    val country: String,

    @SerializedName("password")
    val password: String,
)