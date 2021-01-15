package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoItemViewHolder
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import com.example.androidDeviceDetails.utils.Utils

class AppInfoListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<AppInfoCookedData>
) : ArrayAdapter<AppInfoCookedData>(_context, resource, items) {
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView
        val holder: AppInfoItemViewHolder
        if (convertView == null) {
            vi = layoutInflater.inflate(resource, null)
            holder = AppInfoItemViewHolder(
                vi.findViewById(R.id.appName),
                vi.findViewById(R.id.appVersionCode),
                vi.findViewById(R.id.appEvent),
                vi.findViewById(R.id.appIcon),
                vi.findViewById(R.id.event_badge),
                vi.findViewById(R.id.uninstall_button)
            )
        } else holder = vi?.tag as AppInfoItemViewHolder

        holder.appNameView.text = items[position].appName
        holder.versionCodeTextView.text =
            "Version Code : " + items[position].versionCode.toString()
        holder.eventTypeTextView.text = " | Event : " + items[position].eventType.toString()
        holder.appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))
        val color = when (items[position].eventType.ordinal) {
            0 -> R.color.teal_700
            1 -> R.color.purple_500
            2 -> R.color.pink
            3 -> R.color.mat_yellow
            else -> R.color.teal_700
        }
        holder.eventBadge.setColorFilter(
            ContextCompat.getColor(context, color),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        holder.uninstallButton.tag = items[position].packageName
        if (items[position].eventType.ordinal == EventType.APP_ENROLL.ordinal) {
            holder.eventBadge.isVisible = false
        }
        if (items[position].isSystemApp) {
            holder.uninstallButton.isVisible = false
        } else if (items[position].eventType == EventType.APP_UNINSTALLED) {
            holder.uninstallButton.isVisible = false
        }
        return vi!!
    }
}