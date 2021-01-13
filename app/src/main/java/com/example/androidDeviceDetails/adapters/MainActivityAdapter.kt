package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
        ViewHolder.title.text = arrayList[position].title
        ViewHolder.icon.setImageResource(arrayList[position].image)
        ViewHolder.title.setTextColor(R.attr.batteryTitle)
        if(arrayList[position].layoutType == 1){

        }
//        ViewHolder.cardView.setCardBackgroundColor(R.attr.mainButtonBg)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.icon) as ImageView
        var title: TextView = itemView.findViewById(R.id.title)
        var usingSingleValue : ConstraintLayout = itemView.findViewById(R.id.usingSingleValue)
        var mainValue : TextView = itemView.findViewById(R.id.mainValue)
        var superscript : TextView = itemView.findViewById(R.id.superscript)
        var subscript : TextView = itemView.findViewById(R.id.subscript)
        var usingProgressBar : ConstraintLayout = itemView.findViewById(R.id.usingProgressBar)
        var progressbarFirst : ProgressBar = itemView.findViewById(R.id.progressbarFirst)
        var progressbarSecond : ProgressBar = itemView.findViewById(R.id.progressbarSecond)
        var label1 : TextView = itemView.findViewById(R.id.label_1)
        var label2 : TextView = itemView.findViewById(R.id.label_2)
        var label1Value : TextView = itemView.findViewById(R.id.label_1_value)
        var label2Value : TextView = itemView.findViewById(R.id.label_2_value)
    }

}