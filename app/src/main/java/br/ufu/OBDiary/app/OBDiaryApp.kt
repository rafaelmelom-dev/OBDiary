package br.ufu.OBDiary.app

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import br.ufu.OBDiary.R
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.feature.consumption.ConsumptionScreen
import br.ufu.OBDiary.feature.history.HistoryScreen
import br.ufu.OBDiary.feature.history.NewRefuelScreen
import br.ufu.OBDiary.feature.history.NewRepairScreen
import br.ufu.OBDiary.feature.home.HomeScreen
import br.ufu.OBDiary.feature.obd.ObdSimulatorScreen
import br.ufu.OBDiary.feature.vehicle.EditVehicleScreen
import br.ufu.OBDiary.feature.vehicle.NewVehicleScreen
import br.ufu.OBDiary.feature.vehicle.VehicleScreen
import kotlinx.serialization.Serializable

sealed class Destination(val titleRes: Int) {
    object Home : Destination(R.string.homeTitle)
    object History : Destination(R.string.historyTitle)
    object Obd : Destination(R.string.obdSImulatorTitle)
    object Consumption : Destination(R.string.consumptionTitle)
    object Vehicles : Destination(R.string.myVehiclesTitle)
    object NewVehicle : Destination(R.string.newVehicleTitle)
    data class EditVehicle(val oldVehicle: VehicleEntity) : Destination(R.string.editVehicleTitle)
    object NewRefuel : Destination(R.string.newRefuelTitle)
    object NewRepair : Destination(R.string.newRepairTitle)
}

@Composable
fun OBDiaryApp(context: Context) {
    val backStack = rememberSaveable { mutableStateListOf<Destination>(Destination.Home) }
    val appContainer = AppContainer(context)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { OBDiaryTopBar(backStack.last(), onBack = { backStack.removeLastOrNull() }) },
        bottomBar = {
            OBDiaryBottomBar(backStack.last(), onTabSelected = { destination ->
                if (backStack.last() != destination) {
                    backStack.clear()
                    backStack.add(destination)
                }
            })
        }) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding)
        ) { destination ->
            when (destination) {
                is Destination.Home -> NavEntry<Destination>(destination) {
                    HomeScreen(
                        appContainer.homeViewModel,
                        onAddRefuel = { backStack.add(Destination.NewRefuel) })
                }

                is Destination.History -> NavEntry<Destination>(destination) {
                    HistoryScreen(
                        historyViewModel = appContainer.historyViewModel,
                        onAddClick = { destination -> backStack.add(destination) })
                }

                is Destination.Obd -> NavEntry<Destination>(destination) {
                    ObdSimulatorScreen(
                        obdSimulatorViewModel = appContainer.obdSimulatorViewModel
                    )
                }

                is Destination.Consumption -> NavEntry<Destination>(destination) {
                    ConsumptionScreen(appContainer.consumptionViewModel)
                }

                is Destination.Vehicles -> NavEntry<Destination>(destination) {
                    VehicleScreen(
                        vehicleViewModel = appContainer.vehicleViewModel,
                        onAddVehicle = { backStack.add(Destination.NewVehicle) },
                        onEdit = { vehicle -> backStack.add(Destination.EditVehicle(vehicle)) })
                }

                is Destination.NewVehicle -> NavEntry<Destination>(destination) {
                    NewVehicleScreen(
                        vehicleViewModel = appContainer.vehicleViewModel,
                        onBack = { backStack.removeLastOrNull() })
                }

                is Destination.NewRefuel -> NavEntry<Destination>(destination) {
                    NewRefuelScreen(
                        historyViewModel = appContainer.historyViewModel,
                        onBack = { backStack.removeLastOrNull() })
                }

                is Destination.NewRepair -> NavEntry<Destination>(destination) {
                    NewRepairScreen(
                        historyViewModel = appContainer.historyViewModel,
                        onBack = { backStack.removeLastOrNull() })
                }

                is Destination.EditVehicle -> NavEntry<Destination>(destination) { dest ->
                    val editDestination = dest as Destination.EditVehicle

                    EditVehicleScreen(
                        onBack = { backStack.removeLastOrNull() },
                        vehicleViewModel = appContainer.vehicleViewModel,
                        oldVehicle = editDestination.oldVehicle
                    )
                }
            }
        }
    }
}

@Composable
fun OBDiaryBottomBar(currentDestination: Destination, onTabSelected: (Destination) -> Unit) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.secondary,
        unselectedTextColor = MaterialTheme.colorScheme.secondary
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            selected = currentDestination is Destination.Home,
            onClick = { onTabSelected(Destination.Home) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home_24px),
                    contentDescription = "Home"
                )
            },
            label = {
                Text(stringResource(R.string.homeNav))
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentDestination is Destination.History,
            onClick = { onTabSelected(Destination.History) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.history_24px),
                    contentDescription = "History"
                )
            },
            label = {
                Text(stringResource(R.string.historyNav))
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentDestination is Destination.Obd,
            onClick = { onTabSelected(Destination.Obd) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.sensors_24px),
                    contentDescription = "Sensors"
                )
            },
            label = {
                Text(stringResource(R.string.obdNav))
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentDestination is Destination.Consumption,
            onClick = { onTabSelected(Destination.Consumption) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.show_chart_24px),
                    contentDescription = "Chart"
                )
            },
            label = {
                Text(stringResource(R.string.consumptionNav))
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentDestination is Destination.Vehicles,
            onClick = { onTabSelected(Destination.Vehicles) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.directions_car_24px),
                    contentDescription = "Cars"
                )
            },
            label = {
                Text(stringResource(R.string.vehiclesNav))
            },
            colors = itemColors
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OBDiaryTopBar(currentDestination: Destination, onBack: () -> Unit) {
    TopAppBar(
        title = {
            if (currentDestination is Destination.NewVehicle || currentDestination is Destination.NewRefuel || currentDestination is Destination.NewRepair || currentDestination is Destination.EditVehicle) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.keyboard_backspace_24px),
                        contentDescription = "Back",
                        modifier = Modifier.clickable { onBack() })
                    Text(text = stringResource(currentDestination.titleRes))
                }
            } else {
                Text(text = stringResource(currentDestination.titleRes))
            }
        })
}