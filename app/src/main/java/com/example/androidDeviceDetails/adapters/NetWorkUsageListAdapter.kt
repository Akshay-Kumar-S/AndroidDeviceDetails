package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import com.example.androidDeviceDetails.utils.Utils

class NetWorkUsageListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: ArrayList<AppNetworkUsageEntity>
) : ArrayAdapter<AppNetworkUsageEntity>(_context, resource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView
        val holder: NetworkUsageItemViewHolder
        if (convertView == null) {
            vi = layoutInflater.inflate(resource, null)
            holder = NetworkUsageItemViewHolder()
            holder.appNameView = vi.findViewById(R.id.appName)
            holder.wifiUsageView = vi.findViewById(R.id.appWifiData)
            holder.cellularUsageView = vi.findViewById(R.id.appCellularData)
            holder.appIconView = vi.findViewById(R.id.appIcon)
            vi.tag = holder
        } else holder = vi?.tag as NetworkUsageItemViewHolder

        holder.appNameView?.text = Utils.getApplicationLabel(items[position].packageName)
        var text =
            Utils.getFileSize(items[position].receivedDataWifi + items[position].transferredDataWifi)
        holder.wifiUsageView?.text = text
        text =
            Utils.getFileSize(items[position].receivedDataMobile + items[position].transferredDataMobile)
        holder.cellularUsageView?.text = text
        holder.appIconView?.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))

        return vi!!
    }
}


