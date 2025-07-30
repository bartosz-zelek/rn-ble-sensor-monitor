package com.example.rnblesensormonitor.bluetooth

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class BleGattCallback @Inject constructor(
    @ApplicationContext private val context: Context
) : BluetoothGattCallback() {
    private val mainHandler = Handler(Looper.getMainLooper())

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        val deviceName = gatt?.device?.name ?: gatt?.device?.address ?: "device"
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d("BleGattCallback", "Connected to GATT server.")
            mainHandler.post { Toast.makeText(context, "Connected to $deviceName", Toast.LENGTH_SHORT).show() }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d("BleGattCallback", "Disconnected from GATT server.")
            mainHandler.post { Toast.makeText(context, "Disconnected from $deviceName", Toast.LENGTH_SHORT).show() }
        }
    }
}
