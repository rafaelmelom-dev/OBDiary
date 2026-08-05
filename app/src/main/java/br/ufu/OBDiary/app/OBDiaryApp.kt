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
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import br.ufu.OBDiary.R
import br.ufu.OBDiary.feature.consumption.ConsumptionScreen
import br.ufu.OBDiary.feature.history.HistoryScreen
import br.ufu.OBDiary.feature.history.NewRefuelScreen
import br.ufu.OBDiary.feature.history.NewRepairScreen
import br.ufu.OBDiary.feature.home.HomeScreen
import br.ufu.OBDiary.feature.obd.ObdSimulatorScreen
import br.ufu.OBDiary.feature.vehicle.NewVehicleScreen
import br.ufu.OBDiary.feature.vehicle.VehicleScreen
import kotlinx.serialization.Serializable

//app/src/main/java/br/ufu/OBDiary/
//├── MainActivity.kt
//├── app/
//│   ├── OBDiaryApp.kt          # Scaffold externo + NavDisplay
//│   └── AppScaffold.kt         # bottom bar + composição de conteúdo
//├── core/
//│   ├── database/
//│   │   ├── OBDiaryDataSource.kt
//│   │   ├── entity/  (VehicleEntity, RefuelingEntity, RepairEntity, ConsumptionReadingEntity)
//│   │   ├── dao/     (VehicleDao, RefuelingDao, RepairDao, ConsumptionDao)
//│   │   └── converter/Converters.kt
//│   ├── repository/  (VehicleRepository, RefuelingRepository, RepairRepository, ConsumptionRepository)
//│   ├── datastore/ActiveVehicleStore.kt   # guarda vehicleIdAtivo
//│   └── di/AppContainer.kt
//└── feature/
//├── home/        (HomeDestination, HomeScreen, HomeViewModel, HomeUiState)
//├── history/
//│   ├── HistoryScreen.kt        # 1 tela com SegmentedButton
//│   ├── ListViewModel.kt
//│   ├── RefuelingFormViewModel.kt
//│   └── RepairFormViewModel.kt
//├── obd/         (ObdSimulatorScreen, ObdSimulatorViewModel)
//├── consumption/ (ConsumptionScreen, ConsumptionViewModel)
//└── vehicle/     (VehicleScreen, VehicleViewModel)

sealed class Destination(val title: String) {
    object Home : Destination("OBDiary")
    object History : Destination("History")
    object Obd : Destination("OBD Simulator")
    object Consumption : Destination("Consumption")
    object Vehicles : Destination("My vehicles")
    object NewVehicle : Destination("New vehicle")
    object NewRefuel : Destination("New Refuel")
    object NewRepair : Destination("New Repair")
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
                    HomeScreen()
                }

                is Destination.History -> NavEntry<Destination>(destination) {
                    HistoryScreen(
                        vehicleViewModel = appContainer.vehicleViewModel,
                        historyViewModel = appContainer.historyViewModel,
                        onAddClick = { destination -> backStack.add(destination) })
                }

                is Destination.Obd -> NavEntry<Destination>(destination) {
                    ObdSimulatorScreen()
                }

                is Destination.Consumption -> NavEntry<Destination>(destination) {
                    ConsumptionScreen()
                }

                is Destination.Vehicles -> NavEntry<Destination>(destination) {
                    VehicleScreen(
                        vehicleViewModel = appContainer.vehicleViewModel,
                        onAddVehicle = { backStack.add(Destination.NewVehicle) })
                }

                is Destination.NewVehicle -> NavEntry<Destination>(destination) {
                    NewVehicleScreen(
                        vehicleViewModel = appContainer.vehicleViewModel,
                        onBack = { backStack.removeLastOrNull() })
                }

                is Destination.NewRefuel -> NavEntry<Destination>(destination) {
                    NewRefuelScreen(historyViewModel = appContainer.historyViewModel, onBack = { backStack.removeLastOrNull() })
                }

                is Destination.NewRepair -> NavEntry<Destination>(destination) {
                    NewRepairScreen(historyViewModel = appContainer.historyViewModel, onBack = { backStack.removeLastOrNull() })
                }
            }
        }
    }
}

@Composable
fun OBDiaryBottomBar(currentDestination: Destination, onTabSelected: (Destination) -> Unit) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,        // azul (LightPrimary)
        selectedTextColor = MaterialTheme.colorScheme.primary,        // azul
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,// azul claro (LightPrimaryContainer)
        unselectedIconColor = MaterialTheme.colorScheme.secondary,      // cinza (LightSecondary)
        unselectedTextColor = MaterialTheme.colorScheme.secondary       // cinza
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
                Text("Home")
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
                Text("History")
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
                Text("OBD")
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
                Text("Consumption")
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
                Text("Vehicles")
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
            if (currentDestination is Destination.NewVehicle || currentDestination is Destination.NewRefuel || currentDestination is Destination.NewRepair) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.keyboard_backspace_24px),
                        contentDescription = "Back",
                        modifier = Modifier.clickable { onBack() }
                    )
                    Text(text = currentDestination.title)
                }
            } else {
                Text(text = currentDestination.title)
            }
        }
    )
}