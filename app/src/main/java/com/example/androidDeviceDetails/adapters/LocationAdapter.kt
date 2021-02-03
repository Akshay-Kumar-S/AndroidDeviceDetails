package com.example.androidDeviceDetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
import com.example.androidDeviceDetails.models.location.LocationData
import com.example.androidDeviceDetails.utils.SortBy
import org.apache.commons.lang3.time.DurationFormatUtils.formatDuration


class LocationAdapter(
    private var list: ArrayList<LocationData>,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val count: TextView = view.findViewById(R.id.count)
        val time: TextView = view.findViewById(R.id.time)
        val address: TextView = view.findViewById(R.id.address)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_tittle, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        viewHolder.count.text = list[pos].count.toString()
        viewHolder.address.text = list[pos].address
        viewHolder.time.text = formatDuration(list[pos].totalTime, "d' days, 'H' hrs, 'mm' mins'")
        viewHolder.itemView.setOnClickListener { onItemClickListener.onItemClicked(list[pos]) }
    }

    override fun getItemCount() = list.size

    fun refreshList(locationDisplayList: ArrayList<LocationData>) {
        list.clear()
        list.addAll(locationDisplayList)
        notifyDataSetChanged()
    }

    fun sortView(type: Int) {
        when (type) {
            SortBy.ASCENDING.ordinal -> list.sortBy { it.count }
            else -> list.sortByDescending { it.count }
        }
        notifyDataSetChanged()
    }
}