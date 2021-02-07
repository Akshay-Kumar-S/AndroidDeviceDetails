package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.permissionsModel.PermissionDetailsItemViewHolder
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.utils.Utils

class PermittedAppsAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<PermittedAppsCookedData>
) : ArrayAdapter<PermittedAppsCookedData>(_context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView

        val holder: PermissionDetailsItemViewHolder?
        if (convertView == null) {
            vi = layoutInflater.inflate(resource, null)
            holder = PermissionDetailsItemViewHolder(
                vi.findViewById(R.id.appName),
                vi.findViewById(R.id.appVersionCode),
                vi.findViewById(R.id.appIcon)
            )
            vi.tag = holder
        } else holder = vi?.tag as PermissionDetailsItemViewHolder
        setAppInfoHolder(holder, position)

        return vi!!
    }

    private fun setAppInfoHolder(
        holder: PermissionDetailsItemViewHolder,
        position: Int
    ): PermissionDetailsItemViewHolder {
        holder.appName.text = items[position].apkTitle
        holder.versionCode.text = items[position].versionName
        holder.appIcon.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))
        return holder
    }
}