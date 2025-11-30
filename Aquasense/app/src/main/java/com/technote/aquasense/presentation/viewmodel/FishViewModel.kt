package com.technote.aquasense.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FishViewModel @Inject constructor(
    private val fishRepository: FishRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FishUiState())
    val uiState: StateFlow<FishUiState> = _uiState.asStateFlow()

    fun createFish(
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int,
        userId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            fishRepository.createFish(nome, idade, especie, peso, quantidade, userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        fishCreated = true,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        fishCreated = false,
                        error = "Não foi possível adicionar o peixe. Verifique os dados e tente novamente."
                    )
                }
            )
        }
    }

    fun loadFishes(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            fishRepository.getFishesByUser(userId).fold(
                onSuccess = { fishes ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        fishes = fishes,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Não foi possível carregar a lista de peixes. Tente novamente."
                    )
                }
            )
        }
    }

    fun updateFish(
        fishId: String,
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Se o fishId parecer ser um nome (não um ID válido), buscar pelo nome primeiro
            if (fishId.length < 20) { // IDs geralmente são mais longos
                fishRepository.getFishByName(fishId).fold(
                    onSuccess = { fish ->
                        val realFishId = fish.id ?: fishId
                        updateFishById(realFishId, nome, idade, especie, peso, quantidade)
                    },
                    onFailure = {
                        // Se não encontrar por nome, tentar usar o ID diretamente
                        updateFishById(fishId, nome, idade, especie, peso, quantidade)
                    }
                )
            } else {
                updateFishById(fishId, nome, idade, especie, peso, quantidade)
            }
        }
    }

    private suspend fun updateFishById(
        fishId: String,
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int
    ) {
        fishRepository.updateFish(fishId, nome, idade, especie, peso, quantidade).fold(
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    fishUpdated = true,
                    error = null
                )
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    fishUpdated = false,
                    error = "Não foi possível atualizar os dados do peixe. Tente novamente."
                )
            }
        )
    }

    fun resetFishCreated() {
        _uiState.value = _uiState.value.copy(fishCreated = false)
    }

    fun deleteFish(fishName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Primeiro, busca o peixe pelo nome para obter o ID real (igual ao update)
            fishRepository.getFishByName(fishName).fold(
                onSuccess = { fish ->
                    // Usa o ID real do peixe para a exclusão
                    val realFishId = fish.id
                    if (realFishId.isNullOrEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            fishDeleted = false,
                            error = "ID do peixe não encontrado"
                        )
                        return@fold
                    }
                    
                    fishRepository.deleteFish(realFishId).fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                fishDeleted = true,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                fishDeleted = false,
                                error = error.message ?: "Erro ao deletar peixe"
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        fishDeleted = false,
                        error = error.message ?: "Erro ao buscar peixe para deletar"
                    )
                }
            )
        }
    }

    fun resetFishUpdated() {
        _uiState.value = _uiState.value.copy(fishUpdated = false)
    }

    fun resetFishDeleted() {
        _uiState.value = _uiState.value.copy(fishDeleted = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FishUiState(
    val isLoading: Boolean = false,
    val fishes: List<Fish> = emptyList(),
    val fishCreated: Boolean = false,
    val fishUpdated: Boolean = false,
    val fishDeleted: Boolean = false,
    val error: String? = null
)
