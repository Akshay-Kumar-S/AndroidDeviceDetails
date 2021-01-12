package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.CardItem


class MainActivityAdapter(context: Context, arrayList: ArrayList<CardItem>) :
    RecyclerView.Adapter<MainActivityAdapter.viewHolder>() {
    var context: Context = context
    var arrayList: ArrayList<CardItem>
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): viewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_main_item, viewGroup, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: viewHolder, position: Int) {
        viewHolder.iconName.setText(arrayList[position].name)
        viewHolder.icon.setImageResource(arrayList[position].image)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.icon) as ImageView
        var iconName: TextView

        init {
            iconName = itemView.findViewById(R.id.icon_name)
        }
    }

    init {
        this.arrayList = arrayList
    }
}