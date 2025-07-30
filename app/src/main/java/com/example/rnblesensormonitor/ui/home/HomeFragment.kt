package com.example.rnblesensormonitor.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rnblesensormonitor.databinding.FragmentHomeBinding
import com.example.rnblesensormonitor.ui.adapter.DeviceListAdapter
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var deviceListAdapter: DeviceListAdapter

    @SuppressLint("MissingPermission")
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if (permissions.entries.all { it.value }) {
                homeViewModel.startScanning()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.scanButton.setOnClickListener {
            checkAndRequestPermissions()
        }

        homeViewModel.devices.observe(viewLifecycleOwner) { devices ->
            deviceListAdapter.submitList(devices)
        }
        // Observe incoming BLE data and display it
        homeViewModel.receivedData.observe(viewLifecycleOwner) { data ->
            // Convert bytes to hex string
            val hexString = data.joinToString(" ") { String.format("%02X", it) }
            binding.dataText.text = "Data: $hexString"
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestMultiplePermissions.launch(permissionsToRequest.toTypedArray())
        }
        homeViewModel.startScanning()

    }

    @SuppressLint("MissingPermission")
    private fun setupRecyclerView() {
        deviceListAdapter = DeviceListAdapter { device ->
            Log.d("HomeFragment", "Device clicked: ${device.name}, ${device.address}")
            homeViewModel.connectToDevice(device)
        }
        binding.deviceList.apply {
            adapter = deviceListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.stopScanning()
        _binding = null
    }
}