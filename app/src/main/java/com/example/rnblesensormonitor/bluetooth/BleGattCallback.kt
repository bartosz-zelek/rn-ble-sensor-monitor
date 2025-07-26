package com.example.rnblesensormonitor.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.util.Log
import javax.inject.Inject

class BleGattCallback @Inject constructor() : BluetoothGattCallback() {

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d("BleGattCallback", "Connected to GATT server.")
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d("BleGattCallback", "Disconnected from GATT server.")
        }
    }
}

