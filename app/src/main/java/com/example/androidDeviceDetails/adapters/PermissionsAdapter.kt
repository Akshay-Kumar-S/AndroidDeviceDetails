package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.permissionsModel.PermissionsItemViewHolder

class PermissionsAdapter(
    private var _context: Context, private var resource: Int,
    private var items: List<String>
) : ArrayAdapter<String>(_context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView
        val holder: PermissionsItemViewHolder?
        if (convertView == null) {
            vi = layoutInflater.inflate(resource, null)
            holder = PermissionsItemViewHolder(
                vi.findViewById(R.id.permissionName),
                vi.findViewById(R.id.icon)
            )
            vi.tag = holder
        } else holder = vi?.tag as PermissionsItemViewHolder
        setPermissionsInfoHolder(holder, position)

        return vi!!
    }

    private fun setPermissionsInfoHolder(holder: PermissionsItemViewHolder,position: Int) {
        val permission = items[position]
        val drawable = getPermissionDrawable(permission)
        holder.permission.text = items[position]
        holder.icon.setImageResource(drawable)
    }

    private fun getPermissionDrawable(permission: String): Int {
        var drawable = R.drawable.ic_android_24
        try {
            val permissionInfo =
                context.packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA)
            val groupInfo =
                context.packageManager.getPermissionGroupInfo(permissionInfo.group.toString(), PackageManager.GET_META_DATA)
            drawable = groupInfo.icon
        } catch (e: Exception) {
        }
        return drawable
    }
}