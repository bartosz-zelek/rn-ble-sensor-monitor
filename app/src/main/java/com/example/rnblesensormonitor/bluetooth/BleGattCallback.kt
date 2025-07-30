package com.example.rnblesensormonitor.bluetooth

import android.Manifest
import android.annotation.SuppressLint
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
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import java.util.UUID

@Singleton
class BleGattCallback @Inject constructor(
    @ApplicationContext private val context: Context
) : BluetoothGattCallback() {
    private val mainHandler = Handler(Looper.getMainLooper())
    // LiveData to emit received characteristic data
    private val _receivedData = MutableLiveData<ByteArray>()
    val receivedData: LiveData<ByteArray> get() = _receivedData

    companion object {
        // Test service and characteristic UUIDs from GATTS defines
        val SERVICE_UUID: UUID = UUID.fromString("000000ff-0000-1000-8000-00805f9b34fb") // 0x00FF
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb") // 0xFF01
        val CLIENT_CHAR_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        const val KEEP_ALIVE_INTERVAL = 5000L

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        val deviceName = gatt?.device?.name ?: gatt?.device?.address ?: "device"
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d("BleGattCallback", "Connected to GATT server.")
            // store gatt and request optimal connection parameters
            bluetoothGatt = gatt
            gatt?.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
            gatt?.requestMtu(517)
            mainHandler.post { Toast.makeText(context, "Connected to $deviceName", Toast.LENGTH_SHORT).show() }
            // Start service discovery to enable characteristic notifications
            gatt?.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d("BleGattCallback", "Disconnected from GATT server.")
            mainHandler.post { Toast.makeText(context, "Disconnected from $deviceName", Toast.LENGTH_SHORT).show() }
            // stop keep-alive
            mainHandler.removeCallbacks(keepAliveRunnable)
            bluetoothGatt = null
            notificationCharacteristic = null
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Suppress("Deprecation")
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
            val service = gatt.getService(SERVICE_UUID)
            val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)
            if (characteristic != null) {
                // Enable notifications locally
                gatt.setCharacteristicNotification(characteristic, true)
                // Enable notifications on the device (descriptor)
                val descriptor = characteristic.getDescriptor(CLIENT_CHAR_CONFIG_UUID)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
                // start keep-alive pings
                notificationCharacteristic = characteristic
                mainHandler.postDelayed(keepAliveRunnable, KEEP_ALIVE_INTERVAL)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Suppress("Deprecation")
    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: android.bluetooth.BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
        characteristic?.value?.let { data ->
            mainHandler.post { _receivedData.value = data }
        }
    }

    // keep-alive implementation
    private var bluetoothGatt: BluetoothGatt? = null
    private var notificationCharacteristic: BluetoothGattCharacteristic? = null
    private val keepAliveRunnable = object : Runnable {
        @SuppressLint("MissingPermission")
        override fun run() {
            notificationCharacteristic?.let { bluetoothGatt?.readCharacteristic(it) }
            mainHandler.postDelayed(this, KEEP_ALIVE_INTERVAL)
        }
    }
}
