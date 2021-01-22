package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.appInfo.*
import com.example.androidDeviceDetails.utils.Utils
import kotlin.math.roundToInt


class AppInfoListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<AppInfoCookedData>,
    private var chart : DonutChartData,
    private var isAppType : Boolean
) : ArrayAdapter<AppInfoCookedData>(_context, resource, items) {

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
        if (position == 0) {
            var holder: ProgressbarViewHolder?
            if (convertView == null) {
                vi = layoutInflater.inflate(R.layout.appinfo_pie_chart, null)
                holder = ProgressbarViewHolder(
                    vi.findViewById(R.id.stats1),
                    vi.findViewById(R.id.stats4),
                    vi.findViewById(R.id.updated_progressBar),
                    vi.findViewById(R.id.enroll_progressbar),
                    vi.findViewById(R.id.installed_progressBar),
                    vi.findViewById(R.id.uninstalled_progressbar),
                    vi.findViewById(R.id.stats2Text),
                    vi.findViewById(R.id.stats3Text),
                    vi.findViewById(R.id.enroll_count),
                    vi.findViewById(R.id.install_count),
                    vi.findViewById(R.id.update_count),
                    vi.findViewById(R.id.uninstall_count)
                )
                vi.tag = holder
            } else holder = vi?.tag as ProgressbarViewHolder
            holder = setProgressbarHolder(holder)

        } else {
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
        }
        return vi!!
    }

    @SuppressLint("SetTextI18n")
    private fun setProgressbarHolder(holder: ProgressbarViewHolder): ProgressbarViewHolder {
        val total = chart.total.toDouble()
        val value1 = ((chart.value1).toDouble().div(total).times(100)).roundToInt()
        val value2 = ((chart.value2).toDouble().div(total).times(100)).roundToInt()
        val value3 = ((chart.value3).toDouble().div(total).times(100)).roundToInt()
        val value4 = ((chart.value4).toDouble().div(total).times(100)).roundToInt()

        holder.stats2Text.text = "System: "
        holder.stats3Text.text = "User  : "
        holder.updated_progressBar.progress = (value1)
        holder.installed_progressBar.progress = (value1 + value2)
        holder.update_count.text = (chart.value1).toString()
        holder.install_count.text = (chart.value2).toString()
        holder.enroll_progressbar.visibility = getVisibility()
        holder.uninstalled_progressbar.visibility = getVisibility()
        holder.stats1.visibility = getVisibility()
        holder.stats4.visibility = getVisibility()

        if(!isAppType) {
            holder.stats2Text.text = "Install : "
            holder.stats3Text.text = "Update : "
            holder.enroll_progressbar.progress = (value1 + value2 + value3).toInt()
            holder.uninstalled_progressbar.progress = (value1 + value2 + value3 + value4).toInt()
            holder.enroll_count.text = (chart.value3).toString()
            holder.uninstall_count.text = (chart.value4).toString()
        }
        return holder
    }

    @SuppressLint("SetTextI18n")
    private fun setAppInfoHolder(holder: AppInfoItemViewHolder, position: Int): AppInfoItemViewHolder{
        holder.appNameView.text = items[position].appName
        holder.versionCodeTextView.text =
            "Version Code : " + items[position].versionCode.toString()
        holder.appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))
        if(!isAppType) {
            holder.eventTypeTextView.text = " | Event : " + items[position].eventType.toString()
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
        }
        else{
            holder.eventTypeTextView.isVisible = false
            holder.uninstallButton.isVisible = false
            val color = when (items[position].isSystemApp.toInt()) {
                0 -> R.color.pink
                else -> R.color.purple_500
            }
            holder.eventBadge.setColorFilter(
                ContextCompat.getColor(context, color),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
        return holder
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    private fun getVisibility() = if(isAppType) INVISIBLE else VISIBLE
}