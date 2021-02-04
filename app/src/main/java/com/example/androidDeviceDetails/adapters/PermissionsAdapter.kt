package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.appInfoModels.PermissionsItemViewHolder

class PermissionsAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<String>
) : ArrayAdapter<String>(_context, resource, items) {
    @SuppressLint("InflateParams")
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

    @SuppressLint("SetTextI18n")
    private fun setPermissionsInfoHolder(
        holder: PermissionsItemViewHolder,
        position: Int
    ): PermissionsItemViewHolder {
        val permission: String = items[position]
        val drawable = getPermissionDrawable(permission)
        holder.permissionNameTextView.text = items[position]
        holder.iconView.setImageResource(drawable)
        return holder
    }

    private fun getPermissionDrawable(permission: String): Int {
        var drawable: Int = R.drawable.ic_baseline_android_24
        try {
            val permissionInfo: PermissionInfo =
                context.packageManager.getPermissionInfo(permission, 0)
            val groupInfo: PermissionGroupInfo =
                context.packageManager.getPermissionGroupInfo(permissionInfo.group.toString(), 0)
            drawable = groupInfo.icon
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: Resources.NotFoundException) {
        }
        return drawable
    }
}