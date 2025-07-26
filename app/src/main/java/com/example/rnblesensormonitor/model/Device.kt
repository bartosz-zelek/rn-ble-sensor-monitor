package com.example.rnblesensormonitor.model

import android.Manifest
import android.bluetooth.le.ScanResult
import androidx.annotation.RequiresPermission

data class Device(
    val name: String?,
    val address: String,
    val rssi: Int
) {
    companion object {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        fun fromScanResult(scanResult: ScanResult): Device {
            return Device(
                name = scanResult.device.name,
                address = scanResult.device.address,
                rssi = scanResult.rssi
            )
        }
    }
}
