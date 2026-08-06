package br.ufu.OBDiary.feature.obd

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ObdUiState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val rpm: Int = 0,
    val speed: Int = 0,
    val odometer: Double = 0.0,
    val fuel: Int = 0
)

class ObdSimulatorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val TAG = "OBD_DEBUG"
    private val bluetoothManager = ObdBluetoothManager(application)

    private val _uiState = MutableStateFlow(ObdUiState())
    val uiState: StateFlow<ObdUiState> = _uiState.asStateFlow()

    init {
        // Observa a conexão bluetooth reativamente
        viewModelScope.launch {
            bluetoothManager.connectionStatus.collect { status ->
                Log.d(TAG, "[ViewModel] NOVO STATUS DE CONEXÃO RECEBIDO: $status")
                _uiState.update { it.copy(connectionStatus = status) }
                if (status == ConnectionStatus.DISCONNECTED) {
                    // Zera os medidores se desconectar
                    _uiState.update {
                        it.copy(
                            rpm = 0,
                            speed = 0,
                            odometer = 0.0,
                            fuel = 0
                        )
                    }
                }
            }
        }

        // Observa os dados do OBD recebidos
        viewModelScope.launch {
            bluetoothManager.obdData.collect { obdData ->
                Log.d(TAG, "[ViewModel] NOVOS DADOS OBD RECEBIDOS -> RPM: ${obdData.rpm}, Speed: ${obdData.speed}")
                _uiState.update {
                    it.copy(
                        rpm = obdData.rpm,
                        speed = obdData.speed,
                        odometer = obdData.odometer,
                        fuel = obdData.fuel
                    )
                }
            }
        }
    }

    fun connectBluetooth() {
        bluetoothManager.connect()
    }

    fun disconnectBluetooth() {
        bluetoothManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothManager.disconnect()
    }
}
