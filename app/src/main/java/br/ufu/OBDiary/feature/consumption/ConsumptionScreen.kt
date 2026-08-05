package br.ufu.OBDiary.feature.consumption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ufu.OBDiary.R
import br.ufu.OBDiary.ui.theme.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone


@Composable
fun ConsumptionScreen(consumptionViewModel: ConsumptionViewModel) {
    val uiState = consumptionViewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    label = "GENERAL AVG.",
                    value = String.format("%.1f", uiState.value.average ?: 0.0),
                    unit = "km/L",
                    backgroundColor = Color(0xFFD1E4FF), // Light blue
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "BEST AVG.",
                    value = String.format("%.1f", uiState.value.best ?: 0.0),
                    unit = "km/L",
                    backgroundColor = Color(0xFFF0F2F5), // Light gray
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "By refuel",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(uiState.value.consumptions) { consumption ->
            RefuelItem(consumption)
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: String,
    unit: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = unit,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RefuelItem(consumption: ConsumptionEntry) {
    val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
    val dateString = sdf.format(consumption.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = dateString,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Refuel #${consumption.refuelNumber}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(
                        if (consumption.isPositive) Color(0xFFD1E4FF) else Color(0xFFFFD1D1),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = String.format("%.2f km/L", consumption.value),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (consumption.isPositive) Color(0xFF1A73E8) else Color(0xFFD32F2F)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(if (consumption.isPositive) R.drawable.trending_up_24px else R.drawable.trending_down_24px),
                contentDescription = null,
                tint = if (consumption.isPositive) Color(0xFF1A73E8) else Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
