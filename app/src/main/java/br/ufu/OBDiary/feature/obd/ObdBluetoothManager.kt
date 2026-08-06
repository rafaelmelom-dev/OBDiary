package br.ufu.OBDiary.feature.obd

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class ObdData(
    val rpm: Int = 0,
    val speed: Int = 0,
    val odometer: Double = 0.0,
    val fuel: Int = 0
)

@SuppressLint("MissingPermission")
class ObdBluetoothManager(private val context: Context) {

    private val TAG = "OBD_DEBUG"

    // UUIDs padrão para o Serviço UART da Nordic (NUS) usado pelo seu ESP32-C3
    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private var bluetoothGatt: BluetoothGatt? = null

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _obdData = MutableStateFlow(ObdData())
    val obdData: StateFlow<ObdData> = _obdData.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val buffer = StringBuilder()

    fun connect() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e(TAG, "[Manager] Bluetooth está desativado ou não disponível.")
            return
        }

        if (_connectionStatus.value == ConnectionStatus.CONNECTING ||
            _connectionStatus.value == ConnectionStatus.CONNECTED
        ) {
            Log.d(TAG, "[Manager] Conexão já em andamento ou ativa.")
            return
        }

        _connectionStatus.value = ConnectionStatus.CONNECTING
        Log.d(TAG, "[Manager] Iniciando conexão BLE com OBDiary-ESP32...")

        val pairedDevices = bluetoothAdapter.bondedDevices
        Log.d(TAG, "[Manager] Total de pareados encontrados: ${pairedDevices.size}")

        var targetDevice: BluetoothDevice? = null
        for (device in pairedDevices) {
            Log.d(TAG, "[Manager] Pareado: '${device.name}' - Address: ${device.address}")
            if (device.name == "OBDiary-ESP32") {
                targetDevice = device
                break
            }
        }

        if (targetDevice == null) {
            Log.e(TAG, "[Manager] ERRO: Dispositivo 'OBDiary-ESP32' não encontrado nos pareados.")
            try {
                targetDevice = bluetoothAdapter.getRemoteDevice("64:E8:33:87:57:BA")
                Log.d(TAG, "[Manager] Tentando conectar pelo MAC Address conhecido: 64:E8:33:87:57:BA")
            } catch (e: Exception) {
                Log.e(TAG, "[Manager] Falha ao obter por MAC: ${e.message}")
            }
        }

        if (targetDevice == null) {
            Log.e(TAG, "[Manager] Conexão abortada: Dispositivo OBDiary-ESP32 não localizado.")
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            return
        }

        Log.d(TAG, "[Manager] SUCESSO: Dispositivo alvo localizado. Conectando via GATT...")

        bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            targetDevice.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            targetDevice.connectGatt(context, false, gattCallback)
        }
    }

    fun disconnect() {
        Log.d(TAG, "[Manager] Desconectando por solicitação do usuário...")
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: Exception) {
            Log.e(TAG, "[Manager] Erro ao fechar gatt: ${e.message}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "[Manager] Erro de estado GATT detectado. Status: $status, Novo Estado: $newState")
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
                gatt.close()
                bluetoothGatt = null
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "[Manager] SUCESSO: Conectado ao servidor GATT do ESP32!")

                // PASSO 1: Solicita aumento do MTU para 512 bytes para receber o JSON completo!
                Log.d(TAG, "[Manager] Solicitando MTU de 512 bytes...")
                gatt.requestMtu(512)

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "[Manager] Dispositivo desconectado.")
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
                gatt.close()
                bluetoothGatt = null
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d(TAG, "[Manager] MTU alterado para: $mtu, Status: $status")

            // PASSO 2: Descobre os serviços somente após a negociação do MTU
            Log.d(TAG, "[Manager] Descobrindo serviços...")
            gatt.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "[Manager] Erro ao descobrir serviços. Status: $status")
                disconnect()
                return
            }

            Log.d(TAG, "[Manager] Serviços descobertos com sucesso!")
            val uartService = gatt.getService(UART_SERVICE_UUID)
            if (uartService == null) {
                Log.e(TAG, "[Manager] ERRO: Serviço UART (NUS) não encontrado no ESP32!")
                disconnect()
                return
            }

            Log.d(TAG, "[Manager] SUCESSO: Serviço UART (NUS) encontrado no ESP32: $UART_SERVICE_UUID")
            val txChar = uartService.getCharacteristic(TX_CHAR_UUID)
            if (txChar == null) {
                Log.e(TAG, "[Manager] ERRO: Característica de Transmissão (TX) não encontrada!")
                disconnect()
                return
            }

            Log.d(TAG, "[Manager] SUCESSO: Característica TX encontrada: $TX_CHAR_UUID")

            // PASSO 3: Ativa notificações para receber os dados serialmente
            val notificationRegistered = gatt.setCharacteristicNotification(txChar, true)
            Log.d(TAG, "[Manager] Registro local de notificação TX: $notificationRegistered")

            val descriptor = txChar.getDescriptor(CCCD_UUID)
            if (descriptor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    @Suppress("DEPRECATION")
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    @Suppress("DEPRECATION")
                    gatt.writeDescriptor(descriptor)
                }
                Log.d(TAG, "[Manager] Escrita do Descritor de Notificação (CCCD) enviada com sucesso!")
                _connectionStatus.value = ConnectionStatus.CONNECTED
            } else {
                Log.e(TAG, "[Manager] ERRO: Descritor CCCD não encontrado na característica TX.")
                disconnect()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            @Suppress("DEPRECATION")
            val data = characteristic.value
            processReceivedData(data)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            processReceivedData(value)
        }
    }

    private fun processReceivedData(data: ByteArray?) {
        if (data == null || data.isEmpty()) return

        val chunkStr = String(data, Charsets.UTF_8)
        Log.v(TAG, "[Manager] Chunk recebido: '$chunkStr'")

        synchronized(buffer) {
            buffer.append(chunkStr)
            var currentBuffer = buffer.toString()

            while (true) {
                val startIndex = currentBuffer.indexOf('{')
                if (startIndex == -1) {
                    buffer.clear()
                    break
                }

                if (startIndex > 0) {
                    currentBuffer = currentBuffer.substring(startIndex)
                    buffer.clear()
                    buffer.append(currentBuffer)
                }

                val endIndex = currentBuffer.indexOf('}')
                if (endIndex == -1) {
                    break
                }

                val jsonString = currentBuffer.substring(0, endIndex + 1)
                Log.d(TAG, "[Manager] JSON COMPLETO RECONSTRUÍDO: '$jsonString'")

                currentBuffer = currentBuffer.substring(endIndex + 1)
                buffer.clear()
                buffer.append(currentBuffer)

                coroutineScope.launch {
                    parseAndPublishObd(jsonString)
                }
            }
        }
    }

    private fun parseAndPublishObd(jsonStr: String) {
        try {
            val json = JSONObject(jsonStr)
            val rpmVal = json.optInt("rpm", 0)
            val speedVal = json.optInt("speed", 0)
            val odometerVal = json.optDouble("odometer", 0.0)
            val fuelVal = json.optInt("fuel", 0)

            Log.d(TAG, "[Manager] PARSER SUCESSO -> RPM: $rpmVal, Vel: $speedVal, Odo: $odometerVal, Comb: $fuelVal")

            _obdData.value = ObdData(
                rpm = rpmVal,
                speed = speedVal,
                odometer = odometerVal,
                fuel = fuelVal
            )
        } catch (e: Exception) {
            Log.e(TAG, "[Manager] ERRO AO PARSEAR JSON: ${e.message} para a string: '$jsonStr'")
        }
    }
}
