package br.ufu.OBDiary.feature.vehicle

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufu.OBDiary.core.datasource.OBDiaryRepository
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.core.datasource.VehicleWithRefuelsAndRepairs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehicleUiState(
    val lista: List<VehicleWithRefuelsAndRepairs> = listOf()
)

class VehicleViewModel(private val obdiaryRepository: OBDiaryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obdiaryRepository.allVehicles.collect { listaDB ->
                _uiState.update {
                    it.copy(lista = listaDB)
                }
            }
        }
    }

    fun addCar(model: String, plate: String, year: Int, color: Color) {
        viewModelScope.launch {
            obdiaryRepository.insertVehicle(
                VehicleEntity(
                    type = "car",
                    model = model,
                    plate = plate,
                    year = year,
                    color = color
                )
            )
        }
    }


    fun addMotorcycle(model: String, plate: String, year: Int, color: Color) {
        viewModelScope.launch {
            obdiaryRepository.insertVehicle(
                VehicleEntity(
                    type = "motorcycle",
                    model = model,
                    plate = plate,
                    year = year,
                    color = color
                )
            )
        }
    }

    fun removeVehicleById(id: Int) {
        viewModelScope.launch {
            obdiaryRepository.deleteVehicleById(id)
        }
    }
}