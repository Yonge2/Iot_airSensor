package com.example.iot_test

data class get_air_info(
    var code: Int,
    var message: List<air_info>
)

data class air_info(
    var humidity: String,
    var co2: String,
    var temperature: String,
    var check_time: String
)
