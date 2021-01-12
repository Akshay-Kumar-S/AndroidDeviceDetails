package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.CardItem


class MainActivityAdapter(var context: Context, private var arrayList: ArrayList<CardItem>) :
    RecyclerView.Adapter<MainActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.activity_main_item,
            viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(ViewHolder: ViewHolder, position: Int) {
        ViewHolder.iconName.text = arrayList[position].name
        ViewHolder.icon.setImageResource(arrayList[position].image)
        ViewHolder.iconName.setTextColor(R.attr.batteryTitle)
//        ViewHolder.cardView.setCardBackgroundColor(R.attr.mainButtonBg)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.icon) as ImageView
        var iconName: TextView = itemView.findViewById(R.id.icon_name)
        var cardView: CardView = itemView.findViewById(R.id.cardView)

    }

}