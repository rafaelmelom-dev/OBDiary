package br.ufu.OBDiary.feature.home

import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufu.OBDiary.R
import br.ufu.OBDiary.core.datasource.OBDiaryRepository
import br.ufu.OBDiary.core.datasource.RefuelingEntity
import br.ufu.OBDiary.core.datasource.RepairEntity
import br.ufu.OBDiary.core.datasource.VehiclePreferences
import br.ufu.OBDiary.core.datasource.VehicleWithRefuelsAndRepairs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

data class Activity(
    val title: String,
    val date: Date,
    val value: Double,
    val icon: Int
)

data class HomeUiState(
    val vehicleActive: VehicleWithRefuelsAndRepairs? = null,
    val lastHodometer: Int? = null,
    val lastConsumption: Double? = null,
    val lastRefuelValue: Double? = null,
    val lastRefuelDate: Date = Date(),
    val totalSpentInMonth: Double? = null,
    val refuelMonthCount: Int? = null,
    val repairMonthCount: Int? = null,
    val activeVehicleId: Int? = null,
    val lastActivities: List<Activity> = listOf()
)

class HomeViewModel(
    private val obdiaryRepository: OBDiaryRepository,
    private val vehiclePreferences: VehiclePreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                obdiaryRepository.allVehicles, vehiclePreferences.activeVehicleId
            ) { list, activeId ->
                val vehicleActive = list.find { it.vehicle.id == activeId }
                val refuels = vehicleActive?.refuels?.sortedByDescending { it.date }
                val repairs = vehicleActive?.repairs?.sortedByDescending { it.date }

                var lastHodometer: Int? = null
                var lastRefuelValue: Double? = null
                var lastRefuelDate: Date = Date()

                if (refuels != null && refuels.isNotEmpty()) {
                    lastHodometer = refuels.first().hodometer
                    lastRefuelValue = refuels.first().let { it.value_by_liter * it.liters }
                    lastRefuelDate = refuels.first().date
                }

                var lastConsumption: Double? = null

                if (refuels?.size ?: 0 > 1) {
                    val first = refuels?.get(0)
                    val second = refuels?.get(1)
                    lastConsumption =
                        ((first?.hodometer ?: 0) - (second?.hodometer ?: 0)) / (second?.liters
                            ?: 1.0)
                }

                val refuelsOfMonth =
                    refuels?.filter {
                        it.date.toInstant()
                            .atZone(ZoneId.systemDefault()).monthValue == Date().toInstant()
                            .atZone(ZoneId.systemDefault()).monthValue
                    }
                val repairsOfMonth =
                    repairs?.filter {
                        it.date.toInstant()
                            .atZone(ZoneId.systemDefault()).monthValue == Date().toInstant()
                            .atZone(ZoneId.systemDefault()).monthValue
                    }

                var totalSpentInMonth = 0.0
                totalSpentInMonth += refuelsOfMonth?.sumOf { it.value_by_liter * it.liters } ?: 0.0
                totalSpentInMonth += repairsOfMonth?.sumOf { it.value } ?: 0.0

                val refuelsActivity = refuels?.map {
                    Activity(
                        title = it.fuel_type,
                        date = it.date,
                        value = it.let { it.value_by_liter * it.liters },
                        icon = R.drawable.local_gas_station_24px
                    )
                }
                val repairsActivity = repairs?.map {
                    Activity(
                        title = it.type,
                        date = it.date,
                        value = it.value,
                        icon = R.drawable.build_24px
                    )
                }
                val totalActivies = (refuelsActivity ?: listOf()) + (repairsActivity ?: listOf())
                totalActivies.sortedByDescending { it.date }

                HomeUiState(
                    vehicleActive = list.find { it.vehicle.id == activeId },
                    lastHodometer = lastHodometer,
                    lastRefuelValue = lastRefuelValue,
                    lastConsumption = lastConsumption,
                    lastRefuelDate = lastRefuelDate,
                    refuelMonthCount = refuelsOfMonth?.size,
                    repairMonthCount = repairsOfMonth?.size,
                    totalSpentInMonth = totalSpentInMonth,
                    activeVehicleId = activeId,
                    lastActivities = totalActivies.take(10)
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }
}

