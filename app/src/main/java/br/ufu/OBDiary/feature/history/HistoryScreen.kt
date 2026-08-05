package br.ufu.OBDiary.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import br.ufu.OBDiary.app.Destination
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ufu.OBDiary.R
import br.ufu.OBDiary.core.datasource.RefuelingEntity
import br.ufu.OBDiary.core.datasource.RepairEntity
import br.ufu.OBDiary.feature.vehicle.VehicleViewModel
import br.ufu.OBDiary.ui.theme.CarBlue
import br.ufu.OBDiary.ui.theme.MediumGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun parseDateString(dateString: String): Date? {
    return try {
        val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.parse(dateString)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun HistoryScreen(
    vehicleViewModel: VehicleViewModel,
    historyViewModel: HistoryViewModel,
    onAddClick: (Destination) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val uiState = vehicleViewModel.uiState.collectAsStateWithLifecycle()

    val mockRefuels = remember {
        listOf(
            RefuelingEntity(
                id_vehicle = 1,
                date = Date(),
                hodometer = 48230,
                liters = 42.5,
                value_by_liter = 5.89,
                fuel_type = "Gasolina",
                gas_station = "Posto Shell Centro"
            ),
            RefuelingEntity(
                id_vehicle = 1,
                date = Date(),
                hodometer = 48000,
                liters = 35.2,
                value_by_liter = 4.72,
                fuel_type = "Etanol",
                gas_station = "Posto Petrobras"
            ),
            RefuelingEntity(
                id_vehicle = 1,
                date = Date(),
                hodometer = 47500,
                liters = 60.1,
                value_by_liter = 5.63,
                fuel_type = "Diesel",
                gas_station = "Posto Ipiranga"
            )
        )
    }

    val mockRepairs = remember {
        listOf(
            RepairEntity(
                id_vehicle = 1,
                date = Date(),
                type = "Troca de Óleo",
                description = "Troca de óleo do motor e filtro",
                category = "Manutenção",
                workshop = "Oficina do Zé",
                value = 250.0
            ),
            RepairEntity(
                id_vehicle = 1,
                date = Date(),
                type = "Alinhamento e Balanceamento",
                description = "Alinhamento das quatro rodas",
                category = "Manutenção",
                workshop = "Centro Automotivo",
                value = 120.0
            ),
            RepairEntity(
                id_vehicle = 1,
                date = Date(),
                type = "Troca de Pastilhas",
                description = "Troca de pastilhas de freio dianteiras",
                category = "Segurança",
                workshop = "Oficina do Zé",
                value = 350.0
            )
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (selectedTabIndex == 0) {
                    onAddClick(Destination.NewRefuel)
                } else {
                    onAddClick(Destination.NewRepair)
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.local_gas_station_24px),
                                contentDescription = "Refuel"
                            )
                            Text(text = "Refuels")
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.build_24px),
                                contentDescription = "Repair"
                            )
                            Text(text = "Repairs")
                        }
                    }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    if (uiState.value.activeVehicleId == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("First, select a vehicle")
                        }
                    } else {
                        RefuelHistoryList(
                            uiState.value.list.find { it.vehicle.id == uiState.value.activeVehicleId ?: 0 }?.refuels
                                ?: emptyList(),
                            { id -> historyViewModel.removeRefuelById(id) }
                        )
                    }
                }

                1 -> {
                    if (uiState.value.activeVehicleId == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("First, select a vehicle")
                        }
                    } else {
                        RepairHistoryList(
                            uiState.value.list.find { it.vehicle.id == uiState.value.activeVehicleId ?: 0 }?.repairs
                                ?: emptyList(),
                            { id -> historyViewModel.removeRepairById(id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RefuelHistoryList(refuels: List<RefuelingEntity>, onDelete: (Int) -> Unit) {
    if (refuels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.local_gas_station_24px),
                    contentDescription = "Empty",
                    modifier = Modifier.size(64.dp),
                    tint = MediumGray
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "No refuel found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap the + icon to add",
                    fontSize = 14.sp,
                    color = MediumGray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(refuels) { refuel ->
                RefuelHistoryItem(refuel, { onDelete(refuel.id) })
            }
        }
    }
}

@Composable
fun RefuelHistoryItem(refuel: RefuelingEntity, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(CarBlue, RoundedCornerShape(size = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.local_gas_station_24px),
                contentDescription = "Fuel Icon",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = refuel.fuel_type,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(refuel.date)
                } - ${refuel.hodometer} km",
                fontSize = 12.sp,
                color = MediumGray
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${refuel.liters} L",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Text(
                    text = refuel.gas_station,
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "R$ ${String.format("%.2f", refuel.liters * refuel.value_by_liter)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "R$ ${String.format("%.3f", refuel.value_by_liter)}/L",
                fontSize = 12.sp,
                color = MediumGray
            )
            Icon(
                painter = painterResource(id = R.drawable.delete_24px),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 4.dp)
                    .clickable { onDelete() }
            )
        }

    }
}

@Composable
fun RepairHistoryList(repairs: List<RepairEntity>, onDelete: (Int) -> Unit) {
    if (repairs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.build_24px),
                    contentDescription = "Empty",
                    modifier = Modifier.size(64.dp),
                    tint = MediumGray
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "No repair found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap the + icon to add",
                    fontSize = 14.sp,
                    color = MediumGray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(repairs) { repair ->
                RepairHistoryItem(repair, { onDelete(repair.id) })
            }
        }
    }
}

@Composable
fun RepairHistoryItem(repair: RepairEntity, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = repair.type,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(repair.date)
                }",
                fontSize = 12.sp,
                color = MediumGray
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = repair.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Text(
                    text = repair.workshop,
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }
            if (repair.description.isNotBlank()) {
                Text(
                    text = repair.description,
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "R$ ${String.format("%.2f", repair.value)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(id = R.drawable.delete_24px),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 4.dp)
                    .clickable { onDelete() }
            )
        }

    }
}
