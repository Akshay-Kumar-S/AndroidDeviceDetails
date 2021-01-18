package com.example.androidDeviceDetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
import com.example.androidDeviceDetails.models.locationModels.CountModel
import com.example.androidDeviceDetails.utils.SortBy


class LocationAdapter(var dataSet: ArrayList<CountModel>, private val onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val geoHash: TextView = view.findViewById(R.id.geoHash)
        var  count: TextView = view.findViewById(R.id.count)
        val address: TextView = view.findViewById(R.id.address)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_tittle, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.geoHash.text = dataSet[position].geoHash
        viewHolder.count.text = dataSet[position].count.toString()
        viewHolder.address.text = dataSet[position].address
        viewHolder.itemView.setOnClickListener { onItemClickListener.onItemClicked(dataSet[position]) }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun refreshList(countList: ArrayList<CountModel>) {
        dataSet.clear()
        dataSet.addAll(countList)
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