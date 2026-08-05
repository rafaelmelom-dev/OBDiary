package br.ufu.OBDiary.feature.history

import android.content.ClipDescription
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufu.OBDiary.core.datasource.OBDiaryRepository
import br.ufu.OBDiary.core.datasource.RefuelingEntity
import br.ufu.OBDiary.core.datasource.RepairEntity
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.core.datasource.VehiclePreferences
import br.ufu.OBDiary.core.datasource.VehicleWithRefuelsAndRepairs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class HistoryUiState(
    val refuelsList: List<RefuelingEntity> = listOf(),
    val repairsList: List<RepairEntity> = listOf(),
    val activeVehicleId: Int? = null,
    val selectedTabIndex: Int = 0
)

class HistoryViewModel(
    private val obdiaryRepository: OBDiaryRepository,
    private val vehiclePreferences: VehiclePreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                obdiaryRepository.allVehicles,
                vehiclePreferences.activeVehicleId
            ) { list, activeId ->
                HistoryUiState(
                    refuelsList = list.find { it.vehicle.id == activeId }?.refuels?.sortedByDescending { it.date } ?: emptyList(),
                    repairsList = list.find { it.vehicle.id == activeId }?.repairs?.sortedByDescending { it.date } ?: emptyList(),
                    activeVehicleId = activeId,
                    selectedTabIndex = uiState.value.selectedTabIndex
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }

    fun addRefuel(
        date: Date,
        hodometer: Int,
        liters: Double,
        value_by_liter: Double,
        fuel_type: String,
        gas_station: String = ""
    ) {
        viewModelScope.launch {
            obdiaryRepository.insertRefuel(
                RefuelingEntity(
                    id_vehicle = vehiclePreferences.activeVehicleId.first() ?: 0,
                    date = date,
                    hodometer = hodometer,
                    liters = liters,
                    value_by_liter = value_by_liter,
                    fuel_type = fuel_type,
                    gas_station = gas_station
                )
            )
        }
    }

    fun addRepair(
        date: Date,
        type: String,
        description: String,
        category: String,
        workshop: String,
        value: Double
    ) {
        viewModelScope.launch {
            obdiaryRepository.insertRepair(
                RepairEntity(
                    id_vehicle = vehiclePreferences.activeVehicleId.first() ?: 0,
                    date = date,
                    type = type,
                    description = description,
                    category = category,
                    workshop = workshop,
                    value = value
                )
            )
        }
    }

    fun removeRefuelById(id: Int) {
        viewModelScope.launch {
            obdiaryRepository.deleteRefuelById(id)
        }
    }

    fun removeRepairById(id: Int) {
        viewModelScope.launch {
            obdiaryRepository.deleteRepairById(id)
        }
    }

    fun setTabIndex(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                _uiState.value.copy(selectedTabIndex = id)
            }
        }
    }
}

