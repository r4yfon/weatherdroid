package com.rayfon.weatherdroid

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayfon.weatherdroid.ui.WeatherViewModel
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape

class MainActivity: ComponentActivity() {
  private val weatherViewModel: WeatherViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val locationPermissionRequest = registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
      when {
        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
          weatherViewModel.fetchWeatherForCurrentLocation(this)
        }
      }
    }

    locationPermissionRequest.launch(
      arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
      )
    )

    setContent {
      Surface(
        modifier = Modifier
        .fillMaxSize()
      ) {
        WeatherScreen(viewModel = weatherViewModel)
      }
    }

  }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState = viewModel.uiState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.systemBarsPadding()
    ) {
        CitySelectionDropdown(viewModel)

        Spacer(modifier = Modifier.height(48.dp))

        if (uiState.city.isNotEmpty()) {
            Text(
                text = uiState.city,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Text(
          text = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d")),
          fontSize = 20.sp,
          color = Color.Gray,
          modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Image(
          painter = painterResource(
            id = viewModel.mapWeatherCodeToIconRes(
              viewModel.uiState.hourlyForecast.firstOrNull()?.weatherCode ?: 0,
              viewModel.uiState.hourlyForecast.firstOrNull()?.isDay ?: 1
            )
          ),
          contentDescription = null,
          modifier = Modifier.size(96.dp).padding(vertical = 8.dp)
        )

        Text(
          text = uiState.weatherDescription,
          fontSize = 24.sp,
          modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = uiState.temperature,
            fontSize = 36.sp,
            fontWeight = FontWeight.Medium,
        )

      Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Text(
          text = "L: ${uiState.lowTemperature}",
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        )
        Spacer(
          modifier = Modifier.width(16.dp)
        )
        Text(
          text = "H: ${uiState.highTemperature}",
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium
        )
      }



        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.cloud_rain),
                    contentDescription = "Rain",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.precipitationProbability,
                    fontSize = 16.sp
                )
                Text(
                  text = "Rain",
                  fontSize = 16.sp,
                )
            }
            Spacer(modifier = Modifier.width(42.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.wind),
                    contentDescription = "Wind",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.windSpeed,
                    fontSize = 16.sp
                )
                Text(
                  text = "Wind",
                  fontSize = 16.sp,
                )
            }
            Spacer(modifier = Modifier.width(42.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.droplet),
                    contentDescription = "Humidity",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = uiState.humidity, fontSize = 16.sp)
                Text(text = "Humidity", fontSize = 16.sp)
            }
        }

        val containerColor = MaterialTheme.colorScheme.surfaceVariant

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = containerColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = "24-hour Forecast",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 12.dp, bottom = 16.dp)
                )
                val forecast24 = viewModel.getNext24HourlyForecast()
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    forecast24.forEach { hour ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .width(64.dp)
                        ) {
                            Image(
                                painter = painterResource(id = viewModel.mapWeatherCodeToIconRes(hour.weatherCode, hour.isDay)),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "${hour.temperature.toInt()}°",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (hour.precipitationProbability > 0) {
                                Text(
                                    text = "${hour.precipitationProbability.toInt()}%",
                                    fontSize = 14.sp,
                                    color = Color.Blue
                                )
                            }
                            Text(
                                text = hour.time.substringAfter("T").substring(0, 5),
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionDropdown(viewModel: WeatherViewModel) {
  var expanded by remember { mutableStateOf(false) }
  val uiState = viewModel.uiState
  val context = LocalContext.current

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded }
  ) {
    TextField(
      value = uiState.city,
      onValueChange = {},
      readOnly = true,
      label = { Text("City") },
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
      },
      modifier = Modifier
        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        .fillMaxWidth(),
      colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
      )
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      if (uiState.city.isNotEmpty()) {
        DropdownMenuItem(
          text = { Text("${uiState.city} (current location)") },
          onClick = {
            viewModel.fetchWeatherForCurrentLocation(context)
            expanded = false
          }
        )
      }

      uiState.cityList.forEach { city ->
        DropdownMenuItem(
          text = { Text(city) },
          onClick = {
            viewModel.getWeatherForCity(city)
            expanded = false
          }
        )
      }
    }
  }
}