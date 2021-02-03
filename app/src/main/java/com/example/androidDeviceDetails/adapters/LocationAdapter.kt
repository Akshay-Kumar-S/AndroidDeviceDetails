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
import com.example.androidDeviceDetails.utils.Utils


class LocationAdapter(
    private var list: ArrayList<LocationData>, private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val count: TextView = view.findViewById(R.id.count)
        val time: TextView = view.findViewById(R.id.time)
        val address: TextView = view.findViewById(R.id.address)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_tile, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        viewHolder.count.text = list[pos].count.toString()
        viewHolder.address.text = list[pos].address
        viewHolder.time.text = Utils.getTimePeriodInWords(list[pos].totalTime)
        viewHolder.itemView.setOnClickListener { onItemClickListener.onItemClicked(list[pos]) }
    }

    override fun getItemCount() = list.size

    fun refreshList(locationDisplayList: ArrayList<LocationData>) {
        list.clear()
        list.addAll(locationDisplayList)
        list.sortBy { it.count }
        notifyDataSetChanged()
    }

    fun sortView(type: Int) {
        when (type) {
            SortBy.ASCENDING.ordinal -> list.sortBy { it.count }
            SortBy.TIME_ASCENDING.ordinal -> list.sortBy { it.totalTime }
            SortBy.TIME_DESCENDING.ordinal -> list.sortByDescending { it.totalTime }
            else -> list.sortByDescending { it.count }
        }
        notifyDataSetChanged()
    }
}