package com.example.androidDeviceDetails.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.Nullable
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
        val permission: String = items[position]
        val drawable = getPermissionDrawable(permission)
        holder.permissionNameTextView.text = items[position]
        holder.iconView.setImageResource(drawable)
//        holder.iconView.setImageDrawable(drawable)
        return holder
    }

    private fun getPermissionIcon(position: Int): Int {
        return R.drawable.ic_baseline_android_24 //when (position) {
//            0 -> R.drawable.ic_baseline_smartphone_24
//            1 -> R.drawable.ic_baseline_call_logs
//            2 -> R.drawable.ic_baseline_contacts_24
//            3 -> R.drawable.ic_baseline_message_24
//            4 -> R.drawable.ic_baseline_location_on_24
//            5 -> R.drawable.ic_baseline_camera_alt_24
//            6 -> R.drawable.ic_baseline_mic_24
//            7 -> R.drawable.ic_baseline_sd_storage_24
//            8 -> R.drawable.ic_baseline_calender_24
//            9 -> R.drawable.ic_baseline_sensors_24
//            10 -> R.drawable.ic_baseline_physical_run_24
//            else -> R.drawable.ic_baseline_android_24
//        }
    }

    private fun getPermissionDrawable(permission: String): Int {
        var drawable: Int = R.drawable.ic_baseline_android_24
        try {
            val permissionInfo: PermissionInfo = context.packageManager.getPermissionInfo(permission, 0)
            Log.d("Group",permissionInfo.group.toString())
            val groupInfo: PermissionGroupInfo =
                context.packageManager.getPermissionGroupInfo(permissionInfo.group.toString(), 0)
            drawable = groupInfo.icon
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: Resources.NotFoundException) {
        }
        return drawable
    }
}