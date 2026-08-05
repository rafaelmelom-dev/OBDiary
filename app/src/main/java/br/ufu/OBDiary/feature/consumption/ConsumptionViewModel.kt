package br.ufu.OBDiary.feature.consumption

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

data class ConsumptionEntry(
    val date: Date,
    val refuelNumber: Int,
    val value: Double,
    val isPositive: Boolean
)

data class ConsumptionUiState(
    val consumptions: List<ConsumptionEntry> = listOf(),
    val average: Double? = null,
    val best: Double? = null,
    val activeVehicleId: Int? = null,
)

class ConsumptionViewModel(
    private val obdiaryRepository: OBDiaryRepository,
    private val vehiclePreferences: VehiclePreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConsumptionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                obdiaryRepository.allVehicles,
                vehiclePreferences.activeVehicleId
            ) { list, activeId ->
                val refuels =
                    list.find { it.vehicle.id == activeId }?.refuels?.sortedByDescending { it.date }
                        ?: emptyList()

                var consumptions = refuels.zipWithNext { first, second ->
                    val value = (first.hodometer - second.hodometer).toFloat() / second.liters

                    ConsumptionEntry(
                        date = second.date,
                        refuelNumber = second.id,
                        value = value,
                        isPositive = true
                    )
                }

                consumptions = consumptions.mapIndexed { index, entry ->
                    if (index == 0) {
                        entry
                    } else {
                        val previousValue = consumptions[index - 1].value
                        entry.copy(isPositive = entry.value >= previousValue)
                    }
                }

                var best: Double? = null
                var average: Double? = null
                if (consumptions.isNotEmpty()) {
                    best = consumptions.maxBy { it.value }.value
                    average = consumptions.sumOf { it.value } / consumptions.size
                }


                ConsumptionUiState(
                    consumptions = consumptions,
                    average = average,
                    best = best,
                    activeVehicleId = activeId
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }
}

