package br.ufu.OBDiary.feature.history

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
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
import androidx.compose.ui.unit.sp
import br.ufu.OBDiary.R
import br.ufu.OBDiary.ui.theme.CarBlue
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun NewRepairScreen(historyViewModel: HistoryViewModel, onBack: () -> Unit) {
    val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val today = sdf.format(Date())

    var date by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("Troca de óleo") }
    var description by remember { mutableStateOf("") }
    var descriptionFieldError by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Preventiva") }
    var workshop by remember { mutableStateOf("") }
    var workshopFieldError by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }
    var valueFieldError by remember { mutableStateOf(false) }

    val serviceTypes = listOf(
        "Troca de óleo", "Alinhamento e balanceamento", "Pastilha de freio",
        "Pneu", "Revisão geral", "Suspensão", "Motor", "Elétrica", "Ar-condicionado", "Outro"
    )
    val categories = listOf("Preventiva", "Corretiva", "Estética", "Outro")

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
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
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
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
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        Text(
            text = "Service type",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            serviceTypes.forEach { service ->
                Box(
                    modifier = Modifier
                        .background(
                            if (type == service) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { type = service }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = service,
                        fontSize = 14.sp,
                        fontWeight = if (type == service) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            isError = descriptionFieldError,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                Box(
                    modifier = Modifier
                        .background(
                            if (category == cat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { category = cat }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        OutlinedTextField(
            value = workshop,
            onValueChange = { workshop = it },
            label = { Text("Workshop") },
            isError = workshopFieldError,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Value (R$)") },
            isError = valueFieldError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = {
                val areFieldsFilled =
                    description.isNotBlank() && workshop.isNotBlank() && value.isNotBlank()
                descriptionFieldError = description.isBlank()
                workshopFieldError = workshop.isBlank()
                valueFieldError = value.isBlank()

                if (areFieldsFilled) {
                    historyViewModel.addRepair(
                        date = parseDateString(date) ?: Date(),
                        type = type,
                        description = description,
                        category = category,
                        workshop = workshop,
                        value = value.toDoubleOrNull() ?: 0.0
                    )
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(CarBlue, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Save repair", color = Color.White)
        }
    }
}
