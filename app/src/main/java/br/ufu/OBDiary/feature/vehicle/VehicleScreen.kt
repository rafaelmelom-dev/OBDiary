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
                            text = "Active",
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
            Text(text = "Add vehicle", color = MaterialTheme.colorScheme.onSurface)
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
                Text("Não foi encontrado nenhum veículo")
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

@Composable
fun NewVehicleScreen(
    onBack: () -> Unit,
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    var isCar by remember { mutableStateOf(true) }
    var isMotorcycle by remember { mutableStateOf(false) }
    var model by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CarBlue) }
    var plateError by remember { mutableStateOf(false) }
    var modelFieldError by remember { mutableStateOf(false) }
    var plateFieldError by remember { mutableStateOf(false) }
    var yearFieldError by remember { mutableStateOf(false) }

    val colors = remember {
        listOf(
            CarBlue to "Blue",
            CarRed to "Red",
            CarGreen to "Green",
            CarYellow to "Yellow",
            CarPurple to "Purple",
            CarCyan to "Cyan"
        )
    }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { isCar = true; isMotorcycle = false }
                    .padding(end = 12.dp)
                    .background(
                        if (isCar) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(size = 12.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.directions_car_24px),
                    contentDescription = "Car",
                    modifier = Modifier.size(40.dp)
                )
                Text("Car", modifier = Modifier.padding(6.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { isCar = false; isMotorcycle = true }
                    .background(
                        if (isMotorcycle) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(size = 12.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.motorcycle_24px),
                    contentDescription = "Motorcycle",
                    modifier = Modifier.size(40.dp)
                )
                Text("Motorcycle", modifier = Modifier.padding(6.dp))
            }
        }

        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            singleLine = true,
            isError = modelFieldError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        OutlinedTextField(
            value = plate,
            onValueChange = { input ->
                plate = input.filter { it.isLetterOrDigit() }.uppercase()
            },
            label = { Text("Plate") },
            singleLine = true,
            isError = plateError || plateFieldError,
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = year,
            onValueChange = { year = it },
            label = { Text("Year") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = yearFieldError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        Text(
            text = "Select Color",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            colors.forEach { (color, _) ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, shape = CircleShape)
                        .border(
                            width = if (selectedColor == color) 3.dp else 1.dp,
                            color = if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = color }
                )
            }
        }

        Button(
            onClick = {
                val plateRegex = Regex("^[A-Z]{3}\\d{4}$|^[A-Z]{3}\\d[A-Z]\\d{2}$")
                val isValidPlate = plateRegex.matches(plate)
                val areFieldsFilled = model.isNotBlank() && plate.isNotBlank() && year.isNotBlank()

                modelFieldError = model.isBlank()
                plateFieldError = plate.isBlank()
                yearFieldError = year.isBlank()
                plateError = !isValidPlate && plate.isNotBlank()

                if (areFieldsFilled && isValidPlate) {
                    val vehicleYear = year.toIntOrNull() ?: 0
                    if (isCar) {
                        vehicleViewModel.addCar(
                            model = model,
                            plate = plate,
                            year = vehicleYear,
                            color = selectedColor
                        )
                    } else if (isMotorcycle) {
                        vehicleViewModel.addMotorcycle(
                            model = model,
                            plate = plate,
                            year = vehicleYear,
                            color = selectedColor
                        )
                    }
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Add vehicle")
        }
    }
}
