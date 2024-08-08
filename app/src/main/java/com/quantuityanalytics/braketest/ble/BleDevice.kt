package com.quantuityanalytics.braketest.ble

data class BleDevice(
    var deviceName: String = "default name",
    var deviceDescription: String = "default description",
    var deviceDetails: String = "default details about the device",
)
