package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.database.AppNetworkUsageRaw
import com.example.androidDeviceDetails.models.networkUsage.NetworkUsageItemViewHolder
import com.example.androidDeviceDetails.utils.Utils

class NetWorkUsageListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: ArrayList<AppNetworkUsageRaw>
) : ArrayAdapter<AppNetworkUsageRaw>(_context, resource, items) {
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

        if (items[position].receivedDataMobile + items[position].transferredDataMobile == 0L) {
            holder.cellularUsageView?.visibility = GONE
        } else {
            holder.cellularUsageView?.visibility = VISIBLE
        }
        if (items[position].receivedDataWifi + items[position].receivedDataWifi == 0L) {
            holder.wifiUsageView?.visibility = GONE
        } else {
            holder.wifiUsageView?.visibility = VISIBLE
        }

       /* if (items[position].receivedDataMobile + items[position].transferredDataMobile == 0L || items[position].receivedDataWifi + items[position].receivedDataWifi == 0L) {
            if(items[position].receivedDataMobile + items[position].transferredDataMobile == 0L){
                holder.cellularUsageView?.visibility = GONE
                holder.cellularIcon?.visibility = GONE
            }
            else{
                holder.wifiUsageView?.visibility = GONE
                holder.wifiIcon?.visibility = GONE
            }
        }
        else{
            holder.cellularUsageView?.visibility = VISIBLE
            holder.cellularIcon?.visibility = VISIBLE
            holder.wifiUsageView?.visibility = VISIBLE
            holder.wifiIcon?.visibility = VISIBLE
        }*/
        holder.appIconView?.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))

        return vi!!
    }
}


