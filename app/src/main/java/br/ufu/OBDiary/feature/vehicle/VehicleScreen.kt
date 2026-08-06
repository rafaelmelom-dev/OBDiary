package br.ufu.OBDiary.feature.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.ufu.OBDiary.R
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.ui.theme.CarBlue
import br.ufu.OBDiary.ui.theme.CarRed
import br.ufu.OBDiary.ui.theme.CarGreen
import br.ufu.OBDiary.ui.theme.CarYellow
import br.ufu.OBDiary.ui.theme.CarPurple
import br.ufu.OBDiary.ui.theme.CarCyan

@Composable
fun VehicleCard(
    vehicle: VehicleEntity,
    isActive: Boolean = false,
    onSetActive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = if (isActive) Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .background(
                MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(size = 10.dp)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .clickable { onSetActive() }
        else Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .background(
                MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(size = 10.dp)
            )
            .clickable { onSetActive() }
    ) {
        Row() {
            Row(horizontalArrangement = Arrangement.Center) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(vertical = 24.dp, horizontal = 12.dp)
                        .background(color = vehicle.color, shape = RoundedCornerShape(size = 10.dp))
                        .fillMaxHeight()
                ) {
                    if (vehicle.type == "car") {
                        Icon(
                            painter = painterResource(R.drawable.directions_car_24px),
                            contentDescription = "Car",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(40.dp)
                        )
                    } else if (vehicle.type == "motorcycle") {
                        Icon(
                            painter = painterResource(R.drawable.motorcycle_24px),
                            contentDescription = "Motorcycle",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(40.dp)
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Row() {
                    Text(
                        text = vehicle.model,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 2.em,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    if (isActive) {
                        Text(
                            text = stringResource(R.string.active),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(size = 20.dp)
                                )
                                .padding(5.dp)
                        )
                    }
                }
                Row() {
                    Text(
                        text = vehicle.plate, color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " - ", color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = vehicle.year.toString(), color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = vehicle.type.replaceFirstChar { char -> char.uppercaseChar() },
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Icon(
                    painter = painterResource(R.drawable.delete_24px),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )
            }
        }
    }
}

@Composable
fun NewVehicleButton(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(size = 10.dp))
            .clickable { onClick() }) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.add_circle_24px), contentDescription = "Add"
            )
            Text(text = stringResource(R.string.add_a_vehicle), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun VehicleScreen(
    onAddVehicle: () -> Unit, vehicleViewModel: VehicleViewModel = viewModel()
) {
    val uiState = vehicleViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        if (uiState.value.list.isEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(stringResource(R.string.no_vehicles_found))
            }
        } else {

            uiState.value.list.forEach { vehicleWithRefuelAndRepair ->
                VehicleCard(
                    vehicle = vehicleWithRefuelAndRepair.vehicle,
                    isActive = vehicleWithRefuelAndRepair.vehicle.id == uiState.value.activeVehicleId,
                    onSetActive = { vehicleViewModel.setActiveVehicle(vehicleWithRefuelAndRepair.vehicle.id) },
                    onDelete = { vehicleViewModel.removeVehicleById(vehicleWithRefuelAndRepair.vehicle.id) }
                )
            }

        }

        NewVehicleButton(onAddVehicle)
    }
}

