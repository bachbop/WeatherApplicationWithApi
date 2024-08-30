package com.example.weatherapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.api.Constant
import com.example.weatherapplication.api.NetWorkResponse
import com.example.weatherapplication.api.RetrofitInstance
import com.example.weatherapplication.model.WeatherModel
import kotlinx.coroutines.launch
class WeatherViewModel : ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetWorkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetWorkResponse<WeatherModel>> = _weatherResult

    fun getWeatherByCity(city: String){
        _weatherResult.value = NetWorkResponse.Loading
        viewModelScope.launch {
            val nameCity = weatherApi.getWeatherByCity(
                city = city,
                apiKey = Constant.apiKey,
                units = "metric"
            )
            if (nameCity.isSuccessful){
                nameCity.body()?.let {
                    _weatherResult.value = NetWorkResponse.Success(it)
                }
            } else {
                _weatherResult.value = NetWorkResponse.Error("Failed to load data")
            }
        }
    }
}