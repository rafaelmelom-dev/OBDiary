package br.ufu.OBDiary.app

import android.content.Context
import androidx.room.Room
import br.ufu.OBDiary.core.datasource.OBDiaryDatabase
import br.ufu.OBDiary.core.datasource.OBDiaryRepository

class AppContainer(private val context: Context) {
    val database = Room.databaseBuilder(context, OBDiaryDatabase::class.java, "obdiary_database").build()

    val obdiaryRepository: OBDiaryRepository by lazy {
        OBDiaryRepository(
            database.vehicleDao(),
            database.refuelingDao(),
            database.repairDao()
        )
    }

    // viewmodels
}