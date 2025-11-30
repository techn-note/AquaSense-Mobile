package com.technote.aquasense.data.repository

import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.data.remote.dto.NeuralPredictRequest
import com.technote.aquasense.data.remote.dto.NeuralPredictResponse
import com.technote.aquasense.domain.repository.AIRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepositoryImpl @Inject constructor(
    private val api: AquasenseApi
) : AIRepository {

    override suspend fun getNeuralPrediction(request: NeuralPredictRequest): Result<NeuralPredictResponse> {
        return try {
            println("🤖 Enviando dados para neural-predict: temp=${request.temperatura}, ph=${request.ph}, tds=${request.tds}")
            
            val response = api.getNeuralPrediction(request)
            
            if (response.isSuccessful) {
                val neuralResponse = response.body()
                if (neuralResponse != null) {
                    println("✅ Resposta da IA recebida: ${neuralResponse.classificacao}")
                    println("📊 Scores: pH=${neuralResponse.scoresPercent.ph}%, TDS=${neuralResponse.scoresPercent.tds}%, Temp=${neuralResponse.scoresPercent.temperatura}%")
                    println("🔍 SHAP values: pH=${neuralResponse.shap.ph}, TDS=${neuralResponse.shap.tds}, Temp=${neuralResponse.shap.temperatura}")
                    Result.success(neuralResponse)
                } else {
                    Result.failure(Exception("Resposta vazia da API neural-predict"))
                }
            } else {
                Result.failure(Exception("Não foi possível obter análise de IA. Tente novamente."))
            }
        } catch (e: Exception) {
            println("❌ Erro ao chamar neural-predict: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}