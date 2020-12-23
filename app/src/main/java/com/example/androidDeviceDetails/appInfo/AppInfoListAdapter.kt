package com.example.androidDeviceDetails.appInfo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.appInfo.models.AppInfoCookedData
import com.example.androidDeviceDetails.appInfo.models.EventType
import com.example.androidDeviceDetails.utils.Utils

class AppInfoListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<AppInfoCookedData>
): ArrayAdapter<AppInfoCookedData>(_context, resource, items){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)

        val appNameView = view.findViewById<TextView>(R.id.appName)
        val versionCodeTextView = view.findViewById<TextView>(R.id.appVersionCode)
        val eventTypeTextView = view.findViewById<TextView>(R.id.appEvent)
        val appIconView = view.findViewById<ImageView>(R.id.appIcon)
        val eventBadge = view.findViewById<ImageView>(R.id.event_badge)
        val uninstallButton = view.findViewById<ImageButton>(R.id.uninstall_button)

        appNameView.text = items[position].appName
        versionCodeTextView.text = "Version Code : " + items[position].versionCode.toString()
        eventTypeTextView.text = " | Event : " + items[position].eventType.toString()
        appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))
        val color = when (items[position].eventType.ordinal) {
            0 -> R.color.teal_700
            1 -> R.color.purple_500
            2 -> R.color.pink
            3 -> R.color.mat_yellow
            else -> R.color.teal_700
        }
        eventBadge.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.MULTIPLY)
        uninstallButton.tag = items[position].packageName
        if(items[position].eventType.ordinal == EventType.APP_ENROLL.ordinal)
        {
            eventBadge.isVisible = false
        }
        if(items[position].isSystemApp)
        {
            uninstallButton.isVisible = false
        }
        else if(items[position].eventType == EventType.APP_UNINSTALLED)
        {
            uninstallButton.isVisible = false
        }
        return view
    }
}