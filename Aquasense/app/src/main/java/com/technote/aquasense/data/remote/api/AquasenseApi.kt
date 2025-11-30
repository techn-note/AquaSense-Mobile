package com.technote.aquasense.data.remote.api

import com.technote.aquasense.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AquasenseApi {
    /**
     * ===============================
     *    ENDPOINTS DE AUTENTICAÇÃO
     * ===============================
     */

    @POST(ApiConstants.ENDPOINT_LOGIN)
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST(ApiConstants.ENDPOINT_REGISTER)
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<ApiResponse<UserDto>>

    @GET(ApiConstants.ENDPOINT_USER_PROFILE)
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    @GET("me")
    suspend fun getCurrentUserProfile(): Response<ApiResponse<UserDto>>

    @PUT(ApiConstants.ENDPOINT_UPDATE_USER)
    suspend fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    ): Response<ApiResponse<UserDto>>

    /**
     * ===============================
     *    ENDPOINTS DE TANQUES
     * ===============================
     */

    @GET(ApiConstants.ENDPOINT_TANKS)
    suspend fun getTanks(): Response<ApiResponse<List<TankDto>>>

    @POST(ApiConstants.ENDPOINT_TANKS)
    suspend fun createTank(
        @Body createTankRequest: CreateTankRequest
    ): Response<ApiResponse<TankDto>>

    @PUT("${ApiConstants.ENDPOINT_TANKS}/{tankId}")
    suspend fun updateTank(
        @Path("tankId") tankId: String,
        @Body request: UpdateTankRequest
    ): Response<ApiResponse<TankDto>>

    @DELETE("${ApiConstants.ENDPOINT_TANKS}/{tankId}")
    suspend fun deleteTank(
        @Path("tankId") tankId: String
    ): Response<ApiResponse<Unit>>

    @GET(ApiConstants.ENDPOINT_TANK_BY_NAME)
    suspend fun getTankByName(
        @Path("tankName") tankName: String
    ): Response<ApiResponse<TankDto>>

    /**
     * ===============================
     *    ENDPOINTS DE PEIXES
     * ===============================
     */

    @GET(ApiConstants.ENDPOINT_PEIXES)
    suspend fun getPeixes(): Response<ApiResponse<List<PeixeDto>>>
    
    // Endpoint alternativo para debug
    @GET(ApiConstants.ENDPOINT_PEIXES)
    suspend fun getPeixesRaw(): Response<okhttp3.ResponseBody>

    @POST(ApiConstants.ENDPOINT_PEIXES)
    suspend fun createPeixe(
        @Body createPeixeRequest: CreatePeixeRequest
    ): Response<ApiResponse<PeixeDto>>

    @PUT("${ApiConstants.ENDPOINT_PEIXES}/{peixeId}")
    suspend fun updatePeixe(
        @Path("peixeId") peixeId: String,
        @Body updatePeixeRequest: UpdatePeixeRequest
    ): Response<ApiResponse<PeixeDto>>

    @DELETE("${ApiConstants.ENDPOINT_PEIXES}/{peixeId}")
    suspend fun deletePeixe(
        @Path("peixeId") peixeId: String
    ): Response<ApiResponse<Unit>>
    
    // Alias para deletar peixe (consistência com nomenclatura)
    @DELETE("${ApiConstants.ENDPOINT_PEIXES}/{fishId}")
    suspend fun deleteFish(
        @Path("fishId") fishId: String
    ): Response<ApiResponse<Unit>>

    @GET(ApiConstants.ENDPOINT_PEIXE_BY_NAME)
    suspend fun getPeixeByName(
        @Path("peixeName") peixeName: String
    ): Response<ApiResponse<PeixeDto>>

    /**
     * ===============================
     *    ENDPOINTS DE SENSORES
     * ===============================
     */

    @GET(ApiConstants.ENDPOINT_SENSORES_LATEST)
    suspend fun getLatestSensorData(
        @Query("tipo") tipo: String? = null,
        @Query("tanque") tanqueId: String? = null
    ): Response<ApiResponse<LatestSensorDto>>

    // Endpoint para obter últimos 10 valores de um tipo de sensor para um tanque
    @GET(ApiConstants.ENDPOINT_SENSORES_HISTORY)
    suspend fun getLatestSensorHistory(
        @Query("tipo") tipo: String? = null,
        @Query("tanque") tanqueId: String? = null
    ): Response<ApiResponse<List<LatestSensorDto>>>

    @GET(ApiConstants.ENDPOINT_SENSORES)
    suspend fun getAllSensors(): Response<ApiResponse<List<LatestSensorDto>>>

    /**
     * ===============================
     *    ENDPOINTS DE PREDIÇÕES (IA)
     * ===============================
     */

    @GET(ApiConstants.ENDPOINT_PREDICT)
    suspend fun getPrediction(
        @Path("tankId") tankId: String
    ): Response<PredictDto>

    @POST(ApiConstants.ENDPOINT_NEURAL_PREDICT)
    suspend fun getNeuralPrediction(
        @Body request: NeuralPredictRequest
    ): Response<NeuralPredictResponse>
}