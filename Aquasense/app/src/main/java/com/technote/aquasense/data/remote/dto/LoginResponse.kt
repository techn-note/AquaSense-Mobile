package com.technote.aquasense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String? = null
)

data class UserDto(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("age")
    val age: Int? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,
)