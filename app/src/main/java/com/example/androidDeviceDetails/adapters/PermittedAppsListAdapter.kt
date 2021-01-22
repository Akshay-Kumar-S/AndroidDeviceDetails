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
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.AppInfoItemViewHolder
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.example.androidDeviceDetails.models.appInfo.ProgressbarViewHolder
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.utils.Utils
import kotlin.math.ceil


class PermittedAppsListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<PermittedAppsCookedData>,
    private var appList: List<PermittedAppsCookedData>
) : ArrayAdapter<PermittedAppsCookedData>(_context, resource, items) {

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView

            var holder: AppInfoItemViewHolder?
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
                vi.tag = holder
            } else holder = vi?.tag as AppInfoItemViewHolder
            holder = setAppInfoHolder(holder,position)

        return vi!!
    }

    @SuppressLint("SetTextI18n")
    private fun setAppInfoHolder(holder: AppInfoItemViewHolder, position: Int): AppInfoItemViewHolder{
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
        holder.uninstallButton.isVisible = true
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
        return holder
    }

}