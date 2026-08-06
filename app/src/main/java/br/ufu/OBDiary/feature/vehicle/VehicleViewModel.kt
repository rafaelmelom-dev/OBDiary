package br.ufu.OBDiary.feature.vehicle

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufu.OBDiary.core.datasource.OBDiaryRepository
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.core.datasource.VehiclePreferences
import br.ufu.OBDiary.core.datasource.VehicleWithRefuelsAndRepairs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehicleUiState(
    val list: List<VehicleWithRefuelsAndRepairs> = listOf(),
    val activeVehicleId: Int? = null
)

class VehicleViewModel(
    private val obdiaryRepository: OBDiaryRepository,
    private val vehiclePreferences: VehiclePreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                obdiaryRepository.allVehicles,
                vehiclePreferences.activeVehicleId
            ) { list, activeId ->
                VehicleUiState(list = list, activeVehicleId = activeId)
            }.collect { newState ->
                _uiState.update { newState }
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
            if (vehiclePreferences.activeVehicleId.first() == id) {
                vehiclePreferences.clearActiveVehicle()
            }
        }
    }

    fun updateVehicle(id: Int, model: String, plate: String, year: Int, color: Color, type: String) {
        viewModelScope.launch {
            obdiaryRepository.updateVehicle(
                VehicleEntity(
                    id = id,
                    type = type,
                    model = model,
                    plate = plate,
                    year = year,
                    color = color
                )
            )
        }
    }

    fun setActiveVehicle(id: Int) {
        viewModelScope.launch {
            vehiclePreferences.setActiveVehicleId(id)
        }
    }
}
