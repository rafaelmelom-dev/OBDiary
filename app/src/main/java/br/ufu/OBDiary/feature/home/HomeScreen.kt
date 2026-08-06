package br.ufu.OBDiary.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ufu.OBDiary.R
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.feature.vehicle.VehicleUiState
import br.ufu.OBDiary.ui.theme.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun HomeScreen(homeViewModel: HomeViewModel, onAddRefuel: () -> Unit) {
    val uiState = homeViewModel.uiState.collectAsStateWithLifecycle()



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddRefuel() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            VehicleDashboard(uiState.value)
        }
    }
}

@Composable
fun VehicleDashboard(uiState: HomeUiState) {
    val colorMap = remember<List<Pair<Color, Color>>> {
        listOf(
            CarBlue to MainBlue,
            CarRed to MainRed,
            CarGreen to MainGreen,
            CarYellow to MainYellow,
            CarPurple to MainPurple,
            CarCyan to MainCyan
        )
    }
    val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
    val dateString = sdf.format(uiState.lastRefuelDate)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            VehicleHeaderCard(
                vehicle = uiState.vehicleActive?.vehicle,
                lastHodometer = uiState.lastHodometer,
                color = colorMap.filter { it.first == uiState.vehicleActive?.vehicle?.color ?: CarBlue }
                    .first().second
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = CarBlue,
                    iconBackgroundColor = LightSurface,
                    textColor = LightOnBackground,
                    icon = R.drawable.speed_4_24px,
                    label = stringResource(R.string.consumption_avg),
                    value = String.format("%.1f km/L", uiState.lastConsumption ?: 0.0),
                    subtext = stringResource(R.string.last_refuel)
                )
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = DarkGray,
                    iconBackgroundColor = MediumGray,
                    textColor = LightSurface,
                    icon = R.drawable.local_gas_station_24px,
                    label = stringResource(R.string.last_refuel_alt),
                    value = String.format("R$ %.2f", uiState.lastRefuelValue ?: 0.0),
                    subtext = dateString
                )
            }
        }
        item {
            DashboardCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MainPurple,
                iconBackgroundColor = CarPurple,
                textColor = LightSurface,
                icon = R.drawable.payments_24px,
                label = stringResource(R.string.total_spent_in_month),
                value = String.format("R$ %.2f", uiState.totalSpentInMonth ?: 0.0),
                subtext = String.format(
                    stringResource(R.string.d_refuels_d_repairs),
                    uiState.refuelMonthCount ?: 0,
                    uiState.repairMonthCount ?: 0
                ),
                isFullWidth = true
            )
        }
        item {
            Text(
                text = stringResource(R.string.recent_activity),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(uiState.lastActivities) { activity ->
            RecentActivityItem(activity)
        }
        if (uiState.lastActivities.isEmpty()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.no_recent_activities),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleHeaderCard(vehicle: VehicleEntity?, lastHodometer: Int?, color: Color = MainBlue) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(LightSurface, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (vehicle?.type ?: "" == "car") {
                    Icon(
                        painter = painterResource(id = R.drawable.directions_car_24px),
                        contentDescription = "Vehicle Icon",
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                } else if (vehicle?.type ?: "" == "motorcycle") {
                    Icon(
                        painter = painterResource(id = R.drawable.motorcycle_24px),
                        contentDescription = "Vehicle Icon",
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = vehicle?.model ?: "---",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightOnPrimary
                )
                Text(
                    text = String.format("%s - %d", vehicle?.plate ?: "---", vehicle?.year ?: 0),
                    fontSize = 14.sp,
                    color = LightOnPrimary.copy(alpha = 0.8f)
                )
                Text(
                    text = String.format(
                        stringResource(R.string.last_refuel_d_km),
                        lastHodometer ?: 0
                    ),
                    fontSize = 14.sp,
                    color = LightOnPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    iconBackgroundColor: Color,
    textColor: Color,
    icon: Int,
    label: String,
    value: String,
    subtext: String,
    isFullWidth: Boolean = false
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackgroundColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = subtext,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RecentActivityItem(activity: Activity) {
    val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
    val dateString = sdf.format(activity.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(CarBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = activity.icon),
                contentDescription = null,
                tint = MainBlue
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = dateString,
                fontSize = 12.sp,
                color = MediumGray
            )
        }
        Text(
            text = String.format("R$ %.2f", activity.value),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

