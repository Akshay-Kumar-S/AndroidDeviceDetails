package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.appInfoModels.*


class PermissionsAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<String>
) : ArrayAdapter<String>(_context, resource, items) {

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView
        var holder: PermissionsItemViewHolder?
        if (convertView == null) {
            vi = layoutInflater.inflate(resource, null)
            holder = PermissionsItemViewHolder(
                vi.findViewById(R.id.permissionName),
                vi.findViewById(R.id.icon)
            )
            vi.tag = holder
        } else holder = vi?.tag as PermissionsItemViewHolder
        holder = setPermissionsInfoHolder(holder, position)

        return vi!!
    }

    @SuppressLint("SetTextI18n")
    private fun setPermissionsInfoHolder(
        holder: PermissionsItemViewHolder,
        position: Int
    ): PermissionsItemViewHolder {
        holder.permissionNameTextView.text = items[position]
        holder.iconView.setImageResource(getPermissionIcon(position))
        return holder
    }

    private fun getPermissionIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ic_baseline_smartphone_24
            1 -> R.drawable.ic_baseline_call_logs
            2 -> R.drawable.ic_baseline_contacts_24
            3 -> R.drawable.ic_baseline_message_24
            4 -> R.drawable.ic_baseline_location_on_24
            5 -> R.drawable.ic_baseline_camera_alt_24
            6 -> R.drawable.ic_baseline_mic_24
            7 -> R.drawable.ic_baseline_sd_storage_24
            8 -> R.drawable.ic_baseline_calender_24
            9 -> R.drawable.ic_baseline_sensors_24
            10 -> R.drawable.ic_baseline_physical_run_24
            else -> R.drawable.ic_baseline_android_24
        }
    }

}