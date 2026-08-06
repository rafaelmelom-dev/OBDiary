package br.ufu.OBDiary.feature.obd

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ObdSimulatorScreen(
    obdSimulatorViewModel: ObdSimulatorViewModel
) {
    val uiState by obdSimulatorViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            obdSimulatorViewModel.connectBluetooth()
        } else {
            Toast.makeText(context, "Permissão de Bluetooth é necessária para conectar", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BluetoothHeaderCard(
            status = uiState.connectionStatus,
            onConnectClick = {
                val hasAllPermissions = bluetoothPermissions.all {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                if (hasAllPermissions) {
                    obdSimulatorViewModel.connectBluetooth()
                } else {
                    permissionLauncher.launch(bluetoothPermissions)
                }
            },
            onDisconnectClick = {
                obdSimulatorViewModel.disconnectBluetooth()
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                GaugeDisplay(
                    value = uiState.rpm,
                    maxValue = 8000,
                    label = "x1000 rpm",
                    unitLabelMultiplier = 0.001f,
                    gaugeColor = if (uiState.rpm > 6000) Color(0xFFEF5350) else Color(0xFF3F51B5),
                    displayLabel = "ROTAÇÃO"
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                GaugeDisplay(
                    value = uiState.speed,
                    maxValue = 220,
                    label = "km/h",
                    unitLabelMultiplier = 1f,
                    gaugeColor = Color(0xFF00B0FF),
                    displayLabel = "VELOCIDADE"
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val formattedOdometer = remember(uiState.odometer) {
                        try {
                            NumberFormat.getNumberInstance(Locale("pt", "BR")).format(uiState.odometer)
                        } catch (e: Exception) {
                            String.format(Locale("pt", "BR"), "%,.1f", uiState.odometer)
                        }
                    }
                    Text(
                        text = "$formattedOdometer km",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = "Nível de Combustível",
                        tint = if (uiState.fuel < 20) Color(0xFFEF5350) else Color(0xFF546E7A),
                        modifier = Modifier.size(24.dp)
                    )
                    FuelBarDisplay(fuelPercentage = uiState.fuel)
                }
            }
        }
    }
}

@Composable
fun BluetoothHeaderCard(
    status: ConnectionStatus,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    val cardBackground = when (status) {
        ConnectionStatus.CONNECTED -> Color(0xFFE8F5E9)
        ConnectionStatus.CONNECTING -> Color(0xFFFFF3E0)
        ConnectionStatus.DISCONNECTED -> Color(0xFFECEFF1)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "BluetoothBlink")
    val alphaAnim by if (status == ConnectionStatus.CONNECTING) {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "BlinkAlpha"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (status) {
                                ConnectionStatus.CONNECTED -> Color(0xFF2E7D32)
                                ConnectionStatus.CONNECTING -> Color(0xFFEF6C00)
                                ConnectionStatus.DISCONNECTED -> Color(0xFF546E7A)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bluetooth,
                        contentDescription = "Bluetooth",
                        tint = Color.White.copy(alpha = alphaAnim),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = when (status) {
                            ConnectionStatus.CONNECTED -> "Conectado"
                            ConnectionStatus.CONNECTING -> "Conectando..."
                            ConnectionStatus.DISCONNECTED -> "Desconectado"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF263238)
                    )
                    Text(
                        text = when (status) {
                            ConnectionStatus.CONNECTED -> "OBDiary-ESP32 ativo"
                            ConnectionStatus.CONNECTING -> "Procurando sinal..."
                            ConnectionStatus.DISCONNECTED -> "Sem conexão serial OBD"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF78909C)
                    )
                }
            }

            if (status == ConnectionStatus.CONNECTED) {
                Button(
                    onClick = onDisconnectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Desconectar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onConnectClick,
                    enabled = (status != ConnectionStatus.CONNECTING),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Conectar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GaugeDisplay(
    value: Int,
    maxValue: Int,
    label: String,
    unitLabelMultiplier: Float,
    gaugeColor: Color,
    displayLabel: String
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val size = minOf(maxWidth, maxHeight) * 0.95f

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.size(size)
        ) {
            Box(
                modifier = Modifier
                    .size(size * 0.85f)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val arcSize = this.size.width - strokeWidth

                    drawArc(
                        color = Color(0xFFECEFF1),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                    )

                    val ratio = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
                    drawArc(
                        color = gaugeColor,
                        startAngle = 135f,
                        sweepAngle = 270f * ratio,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                        topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val displayValue = if (unitLabelMultiplier != 1f) {
                        String.format(Locale("pt", "BR"), "%.1f", value * unitLabelMultiplier)
                    } else {
                        value.toString()
                    }
                    Text(
                        text = displayValue,
                        fontSize = (size.value * 0.18f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Text(
                        text = label,
                        fontSize = (size.value * 0.07f).sp,
                        color = Color(0xFF78909C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = displayLabel,
                fontSize = (size.value * 0.065f).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF546E7A),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun FuelBarDisplay(fuelPercentage: Int) {
    val activeBlocks = (fuelPercentage / 10).coerceIn(0, 10)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..10) {
            val isActive = i <= activeBlocks
            val blockColor = when {
                !isActive -> Color(0xFFECEFF1)
                fuelPercentage < 20 -> Color(0xFFEF5350)
                else -> Color(0xFF81C784)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(blockColor)
            )
        }
    }
}
