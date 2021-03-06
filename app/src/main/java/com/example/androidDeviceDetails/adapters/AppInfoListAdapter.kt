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
import com.example.androidDeviceDetails.models.appInfo.*
import com.example.androidDeviceDetails.utils.Utils
import kotlin.math.ceil


class AppInfoListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<AppInfoCookedData>,
    private val chart: DonutChartData?,
    private var isAppType: Boolean
) : ArrayAdapter<AppInfoCookedData>(_context, resource, items) {

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int = if (position == 0) 0 else 1

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView
        if (position == 0 && !isAppType) {
            val holder: ProgressbarViewHolder?
            if (convertView == null) {
                vi = layoutInflater.inflate(R.layout.appinfo_pie_chart, null)
                holder = ProgressbarViewHolder(
                    vi.findViewById(R.id.updated_progressBar),
                    vi.findViewById(R.id.enroll_progressbar),
                    vi.findViewById(R.id.installed_progressBar),
                    vi.findViewById(R.id.uninstalled_progressbar),
                    vi.findViewById(R.id.enroll_count), vi.findViewById(R.id.install_count),
                    vi.findViewById(R.id.update_count), vi.findViewById(R.id.uninstall_count)
                )
                vi.tag = holder
            } else holder = vi?.tag as ProgressbarViewHolder
            setProgressbarHolder(holder)
        } else {
            val holder: AppInfoItemViewHolder?
            if (convertView == null) {
                vi = layoutInflater.inflate(resource, null)
                holder = AppInfoItemViewHolder(
                    vi.findViewById(R.id.appName), vi.findViewById(R.id.appVersionCode),
                    vi.findViewById(R.id.appEvent), vi.findViewById(R.id.appIcon),
                    vi.findViewById(R.id.event_badge), vi.findViewById(R.id.uninstall_button)
                )
                vi.tag = holder
            } else holder = vi?.tag as AppInfoItemViewHolder
            setAppInfoHolder(holder, position)
        }
        return vi!!
    }

    private fun setProgressbarHolder(holder: ProgressbarViewHolder): ProgressbarViewHolder {
        if (chart != null) {
            val total = chart.value1 + chart.value2 + chart.value3 + chart.value4
            val enrolled = (chart.value1.toDouble().div(total).times(100))
            val installed = ceil((chart.value2.toDouble().div(total).times(100)))
            val updated = ceil((chart.value3.toDouble().div(total).times(100)))
            val uninstalled = ceil((chart.value4.toDouble().div(total).times(100)))
            holder.updatedProgressbar.progress = (updated.toInt())
            holder.installedProgressbar.progress = (updated + installed).toInt()
            holder.enrollProgressbar.progress = (updated + installed + enrolled).toInt()
            holder.uninstalledProgressbar.progress =
                (updated + installed + enrolled + uninstalled).toInt()
            holder.enrollCount.text = chart.value1.toString()
            holder.installCount.text = chart.value2.toString()
            holder.updateCount.text = chart.value3.toString()
            holder.uninstallCount.text = chart.value4.toString()
        }
        return holder
    }

    private fun setAppInfoHolder(
        holder: AppInfoItemViewHolder, pos: Int
    ): AppInfoItemViewHolder {
        holder.appNameView.text = items[pos].appName
        holder.eventTypeTextView.isVisible = !isAppType
        holder.uninstallButton.isVisible = !isAppType
        holder.eventBadge.isVisible = !isAppType
        (" | Event : " + items[pos].eventType.toString()).also {
            holder.eventTypeTextView.text = it
        }
        holder.appIconView.setImageDrawable(Utils.getApplicationIcon(items[pos].packageName))
        if (!isAppType) {
            ("Version Code: " + items[pos].versionCode.toString()).also {
                holder.versionCodeTextView.text = it
            }
            val color = when (items[pos].eventType.ordinal) {
                0 -> R.color.teal_700
                1 -> R.color.purple_500
                2 -> R.color.pink
                3 -> R.color.mat_yellow
                else -> R.color.teal_700
            }
            holder.uninstallButton.isVisible = true
            holder.eventBadge.setColorFilter(
                ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.MULTIPLY
            )
            holder.uninstallButton.tag = items[pos].packageName
            if (items[pos].eventType.ordinal == EventType.ENROLL.ordinal)
                holder.eventBadge.isVisible = false
            if (items[pos].isSystemApp) holder.uninstallButton.isVisible = false
            else if (items[pos].eventType == EventType.UNINSTALLED)
                holder.uninstallButton.isVisible = false
        } else holder.versionCodeTextView.text = items[pos].packageName
        return holder
    }
}