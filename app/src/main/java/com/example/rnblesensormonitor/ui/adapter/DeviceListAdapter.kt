package com.example.rnblesensormonitor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rnblesensormonitor.R
import com.example.rnblesensormonitor.databinding.ListItemDeviceBinding
import com.example.rnblesensormonitor.model.Device

class DeviceListAdapter(private val onItemClicked: (Device) -> Unit) : ListAdapter<Device, DeviceListAdapter.ViewHolder>(DeviceDiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)

        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.color.teal_200)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }

        holder.itemView.setOnClickListener {
            onItemClicked(device)
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    class ViewHolder(private val binding: ListItemDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device) {
            binding.deviceName.text = device.name ?: "Unknown Device"
            binding.deviceAddress.text = device.address
            binding.deviceRssi.text = "${device.rssi} dBm"
        }
    }
}

class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}
