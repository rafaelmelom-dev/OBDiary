package br.ufu.OBDiary.feature.vehicle

import android.content.res.Configuration
import android.graphics.DashPathEffect
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.ufu.OBDiary.R
import br.ufu.OBDiary.core.datasource.VehicleEntity
import br.ufu.OBDiary.ui.theme.CarBlue
import br.ufu.OBDiary.ui.theme.CarGreen

@Composable
fun VehicleCard(vehicle: VehicleEntity, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(size = 10.dp))
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
                    }
                }
            }
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = vehicle.model,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 2.em,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row() {
                    Text(
                        text = vehicle.plate,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " - ",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = vehicle.year.toString(),
                        color = MaterialTheme.colorScheme.onSurface
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
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun NewVehicleButton() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(size = 10.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.add_circle_24px),
                contentDescription = "Add"
            )
            Text(text = "Add vehicle", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(
    showBackground = false,
    showSystemUi = false, backgroundColor = 0xFFE31B1B,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun VehicleCardPreview() {
    VehicleCard(
        VehicleEntity(
            type = "car", model = "Celta 1.0", plate = "AAA-1234", year = 2009, color = CarGreen
        )
    )
}

@Composable
fun VehicleScreen(
//    viewModel: VehicleViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight()
    ) {
        VehicleCard(
            VehicleEntity(
                type = "car", model = "Celta 1.0", plate = "AAA-1234", year = 2009, color = CarBlue
            )
        )
        VehicleCard(
            VehicleEntity(
                type = "car", model = "Celta 1.0", plate = "AAA-1234", year = 2009, color = CarBlue
            )
        )
        NewVehicleButton()
    }
}