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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.ufu.OBDiary.R
import br.ufu.OBDiary.ui.theme.CarBlue


@Composable
fun NewRefuelScreen(historyViewModel: HistoryViewModel, onBack: () -> Unit) {
    val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val today = sdf.format(Date())

    var date by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }
    var hodometer by remember { mutableStateOf("") }
    var hodometerFieldError by remember { mutableStateOf(false) }
    var liters by remember { mutableStateOf("") }
    var litersFieldError by remember { mutableStateOf(false) }
    var value_by_liter by remember { mutableStateOf("") }
    var valueByLiterFieldError by remember { mutableStateOf(false) }
    var fuel_type by remember { mutableStateOf("Gasolina") }
    var gas_station by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    val totalValue = (liters.toDoubleOrNull() ?: 0.0) * (value_by_liter.toDoubleOrNull() ?: 0.0)

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    date = sdf.format(Date(millis))
                }
                showDatePicker = false
            }) {
                Text("OK")
            }
        }, dismissButton = {
            Button(onClick = { showDatePicker = false }) {
                Text("Cancel")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painterResource(R.drawable.calendar_today_24px),
                        contentDescription = "Calendar"
                    )
                })
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true })
        }

        OutlinedTextField(
            value = hodometer,
            onValueChange = { hodometer = it },
            label = { Text("Hodometer (km)") },
            isError = hodometerFieldError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = liters,
            onValueChange = { liters = it },
            label = { Text("Liters refueled") },
            isError = litersFieldError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = value_by_liter,
            onValueChange = { value_by_liter = it },
            label = { Text("Value by liter (R$)") },
            isError = valueByLiterFieldError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total value", fontWeight = FontWeight.Medium)
                Text(
                    text = "R$ ${String.format("%.2f", totalValue)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        Text(
            text = "Fuel",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Gasoline", "Ethanol", "Diesel").forEach { type ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (fuel_type == type) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { fuel_type = type }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = type,
                        fontWeight = if (fuel_type == type) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        OutlinedTextField(
            value = gas_station,
            onValueChange = { gas_station = it },
            label = { Text("Gas station (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = {
                val areFieldsFilled =
                    hodometer.isNotBlank() && liters.isNotBlank() && value_by_liter.isNotBlank()
                hodometerFieldError = hodometer.isBlank()
                litersFieldError = liters.isBlank()
                valueByLiterFieldError = value_by_liter.isBlank()

                if (areFieldsFilled) {
                    historyViewModel.addRefuel(
                        date = parseDateString(date) ?: Date(),
                        hodometer = hodometer.toIntOrNull() ?: 0,
                        liters = liters.toDoubleOrNull() ?: 0.0,
                        value_by_liter = value_by_liter.toDoubleOrNull() ?: 0.0,
                        fuel_type = fuel_type,
                        gas_station = gas_station
                    )
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(CarBlue, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Save refuel", color = Color.White)
        }
    }
}
