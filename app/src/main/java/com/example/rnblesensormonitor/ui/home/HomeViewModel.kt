package com.example.rnblesensormonitor.ui.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rnblesensormonitor.bluetooth.BleScanner
import com.example.rnblesensormonitor.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.rnblesensormonitor.bluetooth.BleConnector

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val bleScanCallback: com.example.rnblesensormonitor.bluetooth.BleScanCallback,
    private val bleConnector: BleConnector
) : ViewModel() {

    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    init {
        observeScanResults()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScanning() {
        bleScanner.startScanning()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning() {
        bleScanner.stopScanning()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: Device) {
        bleConnector.connect(device.address)
    }

    @SuppressLint("MissingPermission")
    private fun observeScanResults() {
        bleScanCallback.scanResults.observeForever { scanResults ->
            _devices.postValue(scanResults.map { Device.fromScanResult(it) })
        }
    }
}