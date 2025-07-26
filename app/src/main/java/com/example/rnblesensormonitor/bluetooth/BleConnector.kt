package com.example.rnblesensormonitor.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BleConnector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val gattCallback: BleGattCallback
) {

    private var gatt: BluetoothGatt? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(address: String) {
        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(address)
        gatt = device?.connectGatt(context, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
    }
}

