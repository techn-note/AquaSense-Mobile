package com.technote.aquasense.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.repository.TankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TankViewModel @Inject constructor(
    private val tankRepository: TankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TankUiState())
    val uiState: StateFlow<TankUiState> = _uiState.asStateFlow()

    fun createTank(name: String, volume: Double, number: Double, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            tankRepository.createTank(name, volume, number, userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tankCreated = true,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tankCreated = false,
                        error = "Não foi possível criar o tanque. Verifique os dados e tente novamente."
                    )
                }
            )
        }
    }

    fun loadTanks(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            tankRepository.getTanksByUser(userId).fold(
                onSuccess = { tanks ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tanks = tanks,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Não foi possível carregar a lista de tanques. Tente novamente."
                    )
                }
            )
        }
    }

    fun updateTank(tankName: String, name: String, volume: Double, number: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Primeiro, busca o tanque pelo nome para obter o ID real
            tankRepository.getTankByName(tankName).fold(
                onSuccess = { tank ->
                    // Usa o ID real do tanque para a atualização
                    val realTankId = tank.id
                    if (realTankId.isNullOrEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tankUpdated = false,
                            error = "ID do tanque não encontrado"
                        )
                        return@fold
                    }
                    
                    tankRepository.updateTank(realTankId, name, volume, number).fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                tankUpdated = true,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                tankUpdated = false,
                                error = "Não foi possível atualizar os dados do tanque. Tente novamente."
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tankUpdated = false,
                        error = "Não foi possível encontrar o tanque solicitado."
                    )
                }
            )
        }
    }

    fun resetTankCreated() {
        _uiState.value = _uiState.value.copy(tankCreated = false)
    }

    fun deleteTank(tankName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Primeiro, busca o tanque pelo nome para obter o ID real
            tankRepository.getTankByName(tankName).fold(
                onSuccess = { tank ->
                    // Usa o ID real do tanque para a exclusão
                    val realTankId = tank.id
                    if (realTankId.isNullOrEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tankDeleted = false,
                            error = "ID do tanque não encontrado"
                        )
                        return@fold
                    }
                    
                    tankRepository.deleteTank(realTankId).fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                tankDeleted = true,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                tankDeleted = false,
                                error = "Não foi possível remover o tanque. Tente novamente."
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tankDeleted = false,
                        error = "Não foi possível encontrar o tanque para remoção."
                    )
                }
            )
        }
    }

    fun resetTankUpdated() {
        _uiState.value = _uiState.value.copy(tankUpdated = false)
    }
    
    fun resetTankDeleted() {
        _uiState.value = _uiState.value.copy(tankDeleted = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TankUiState(
    val isLoading: Boolean = false,
    val tanks: List<Tank> = emptyList(),
    val tankCreated: Boolean = false,
    val tankUpdated: Boolean = false,
    val tankDeleted: Boolean = false,
    val error: String? = null
)
