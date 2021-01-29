package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.models.battery.BatteryItemViewHolder
import com.example.androidDeviceDetails.utils.Utils

class BatteryListAdapter(
    private var _context: Context, private var resource: Int,
    private var items: ArrayList<BatteryAppEntry>
) : ArrayAdapter<BatteryAppEntry>(_context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView
        val holder: BatteryItemViewHolder

        if (convertView == null) {
            vi = layoutInflater.inflate(resource, null)
            holder = BatteryItemViewHolder(
                vi.findViewById(R.id.appName),
                vi.findViewById(R.id.dropText),
                vi.findViewById(R.id.appIcon)
            )
            vi.tag = holder
        } else holder = vi?.tag as BatteryItemViewHolder

        holder.appNameView.text = Utils.getApplicationLabel(items[position].packageId)
        val text = "Dropped ${items[position].drop} %"
        holder.dropTextView.text = text
        holder.appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].packageId))

        return vi!!
    }
}
