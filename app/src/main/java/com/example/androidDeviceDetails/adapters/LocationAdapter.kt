package com.example.androidDeviceDetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
import com.example.androidDeviceDetails.models.locationModels.LocationDisplayModel
import com.example.androidDeviceDetails.utils.SortBy


class LocationAdapter(var dataSet: ArrayList<LocationDisplayModel>, private val onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val geoHash: TextView = view.findViewById(R.id.geoHash)
        var  count: TextView = view.findViewById(R.id.count)
        val address: TextView = view.findViewById(R.id.address)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_tittle, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.geoHash.text = dataSet[position].geoHash
        viewHolder.count.text = dataSet[position].count.toString()
        viewHolder.address.text = dataSet[position].address
        viewHolder.itemView.setOnClickListener { onItemClickListener.onItemClicked(dataSet[position]) }
    }

    override fun getItemCount() = dataSet.size

    fun refreshList(locationDisplayList: ArrayList<LocationDisplayModel>) {
        dataSet.clear()
        dataSet.addAll(locationDisplayList)
        notifyDataSetChanged()
    }

    fun sortView(type: Int){
        when (type) {
            SortBy.Ascending.ordinal -> dataSet.sortBy { it.count }
            else -> dataSet.sortByDescending { it.count }
        }
        notifyDataSetChanged()
    }
}