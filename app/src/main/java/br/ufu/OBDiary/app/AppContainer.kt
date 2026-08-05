package br.ufu.OBDiary.app

import android.content.Context
import androidx.room.Room
import br.ufu.OBDiary.core.datasource.OBDiaryDatabase
import br.ufu.OBDiary.core.datasource.OBDiaryRepository
import br.ufu.OBDiary.core.datasource.VehiclePreferences
import br.ufu.OBDiary.feature.consumption.ConsumptionViewModel
import br.ufu.OBDiary.feature.history.HistoryViewModel
import br.ufu.OBDiary.feature.home.HomeViewModel
import br.ufu.OBDiary.feature.vehicle.VehicleViewModel

class AppContainer(private val context: Context) {
    val database =
        Room.databaseBuilder(context, OBDiaryDatabase::class.java, "obdiary_database").build()

    val vehiclePreferences = VehiclePreferences(context)

    val obdiaryRepository: OBDiaryRepository by lazy {
        OBDiaryRepository(
            database.vehicleDao(),
            database.refuelingDao(),
            database.repairDao()
        )
    }

    // viewmodels
    val vehicleViewModel = VehicleViewModel(obdiaryRepository, vehiclePreferences)
    val historyViewModel = HistoryViewModel(obdiaryRepository, vehiclePreferences)
    val homeViewModel = HomeViewModel(obdiaryRepository, vehiclePreferences)
    val consumptionViewModel = ConsumptionViewModel(obdiaryRepository, vehiclePreferences)
}
