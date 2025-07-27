package com.example.rnblesensormonitor.bluetooth

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleScanCallback @Inject constructor() : ScanCallback() {
    private val _scanResults = MutableLiveData<List<ScanResult>>()
    val scanResults: LiveData<List<ScanResult>>
        get() = _scanResults

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        val currentList = _scanResults.value ?: emptyList()
        if (currentList.none { it.device.address == result.device.address }) {
            _scanResults.postValue(currentList + result)
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        super.onBatchScanResults(results)
        val currentList = _scanResults.value ?: emptyList()
        val newResults = results.filter { newResult ->
            currentList.none { it.device.address == newResult.device.address }
        }
        if (newResults.isNotEmpty()) {
            _scanResults.postValue(currentList + newResults)
        }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        // Handle scan failure
        // You can log the error or notify the user
        _scanResults.postValue(emptyList())
        Log.e("BleScanCallback", "Scan failed with error code: $errorCode")
    }
}
