package com.example.rnblesensormonitor.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.util.Log
import javax.inject.Inject

class BleScanner @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val scanCallback: ScanCallback
) {
    private val bleScanner = bluetoothAdapter.bluetoothLeScanner
    private val scanFilters = mutableListOf<ScanFilter>()
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    fun startScanning() {
        try {
            bleScanner.startScan(null, scanSettings, scanCallback)
        } catch (e: SecurityException) {
            Log.e("BleScanner", "SecurityException in startScanning", e)
        }
    }

    fun stopScanning() {
        try {
            bleScanner.stopScan(scanCallback)
        } catch (e: SecurityException) {
            Log.e("BleScanner", "SecurityException in stopScanning", e)
        }
    }
}
