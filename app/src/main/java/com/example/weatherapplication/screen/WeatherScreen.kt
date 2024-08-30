@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.weatherapplication.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.api.NetWorkResponse
import com.example.weatherapplication.model.WeatherModel
import com.example.weatherapplication.screen.components.ActionBar
import com.example.weatherapplication.screen.components.AirQuality
import com.example.weatherapplication.screen.components.DailyForecast
import com.example.weatherapplication.screen.components.WeeklyForecast
import com.example.weatherapplication.ui.theme.ColorBackground
import com.example.weatherapplication.viewmodel.WeatherViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    var city by remember {
        mutableStateOf("")
    }

    val weatherResult = weatherViewModel.weatherResult.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorBackground
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddings)
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = city,
                    onValueChange ={city = it},
                    label = {
                        Text(text = "Search for any location")
                    }
                )

                IconButton(onClick = {
                        weatherViewModel.getWeatherByCity(city)
                        keyboardController?.hide()
                    city = ""
                }) {
                    Icon(imageVector = Icons.Default.Search,
                        contentDescription = "Search for any location"
                    )
                }
            }
            when (val result = weatherResult.value) {
                is NetWorkResponse.Error -> {
                    Text(text = result.message)
                }

                NetWorkResponse.Loading -> {
                    CircularProgressIndicator()
                }

                is NetWorkResponse.Success -> {
                    WeatherDetail(data = result.data)
                }

                null -> {}
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeatherDetail(data: WeatherModel) {
    val curentDate = LocalDate.now()
    val day = curentDate.dayOfMonth
    val month = curentDate.month.name
    val temp = data.list.take(10).map { it.main.temp }
    ActionBar(city = data.city.name)
    Spacer(
        modifier = Modifier.height(12.dp)
    )
    DailyForecast(
        forecast = data.list[0].weather[0].main,
        date = "$day $month 2024",
        degree = data.list[0].main.temp,
        des = data.list[0].weather[0].description,
    )
    Spacer(
        modifier = Modifier.height(24.dp)
    )
    AirQuality(
        realFeel = data.list[0].main.feels_like,
        tempMin = data.list[0].main.temp_min,
        tempMax = data.list[0].main.temp_max,
        humidity = data.list[0].main.humidity,
        pressure = data.list[0].main.pressure,
        windSpeed = data.list[0].wind.speed
    )
    Spacer(
        modifier = Modifier.height(24.dp)
    )
    WeeklyForecast(temp= temp)
}