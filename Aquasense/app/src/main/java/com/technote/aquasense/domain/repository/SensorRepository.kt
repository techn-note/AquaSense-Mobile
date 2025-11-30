package com.technote.aquasense.domain.repository

import com.technote.aquasense.data.model.SensorData

interface SensorRepository {
    /**
     * Obtém dados atuais dos sensores
     * @param tankId ID do aquário
     * @return Result com dados dos sensores ou erro
     */
    suspend fun getSensorData(tankId: String): Result<SensorData>

    /**
     * Obtém histórico dos sensores (últimos 10)
     * @param tankId ID do aquário
     * @return Result com lista histórica ou erro
     */
    suspend fun getSensorHistory(tankId: String): Result<List<SensorData>>

    /**
     * Obtém TODOS os dados dos sensores para relatórios
     * @param tankId ID do aquário
     * @return Result com lista completa ou erro
     */
    suspend fun getAllSensorData(tankId: String): Result<List<SensorData>>

    /**
     * Atualiza dados dos sensores
     * @param tankId ID do aquário
     * @param temperature Temperatura
     * @param ph Nível de pH
     * @param tds tds
     * @return Result com dados atualizados ou erro
     */
    suspend fun updateSensorData(
        tankId: String,
        temperature: Double,
        ph: Double,
        tds: Double
    ): Result<SensorData>
}