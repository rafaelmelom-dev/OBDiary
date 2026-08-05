package br.ufu.OBDiary.core.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey as _intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vehicle_prefs")

class VehiclePreferences(private val context: Context) {
    companion object {
        private val ACTIVE_VEHICLE_ID = intPreferencesKey("active_vehicle_id")
    }

    val activeVehicleId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[ACTIVE_VEHICLE_ID]?.takeIf { it != 0 } // Assuming 0 is not a valid ID or default
        }

    suspend fun setActiveVehicleId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_VEHICLE_ID] = id
        }
    }

    suspend fun clearActiveVehicle() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACTIVE_VEHICLE_ID)
        }
    }
}
