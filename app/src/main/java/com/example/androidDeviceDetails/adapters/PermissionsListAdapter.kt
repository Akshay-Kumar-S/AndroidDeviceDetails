
package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.appInfoModels.*


class PermissionsListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<String>,
    private var appList: List<String>
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
        holder = setPermissionsInfoHolder(holder,position)

        return vi!!
    }

    @SuppressLint("SetTextI18n")
    private fun setPermissionsInfoHolder(holder: PermissionsItemViewHolder, position: Int): PermissionsItemViewHolder{
        holder.permissionNameTextView.text = items[position]
        holder.iconView.setImageDrawable(ContextCompat.getDrawable(
            DeviceDetailsApplication.instance,
            R.drawable.ic_baseline_android_24
        )!!)
        return holder
    }

}