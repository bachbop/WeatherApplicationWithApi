package com.example.weatherapplication.model

data class WeatherModel(
    val city: City,
    val cnt: String,
    val cod: String,
    val list: List<WeatherList>,
    val message: String

)