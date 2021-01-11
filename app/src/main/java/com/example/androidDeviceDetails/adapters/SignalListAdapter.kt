package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import java.text.SimpleDateFormat

class SignalListAdapter
    (
    private var _context: Context,
    private var resource: Int,
    private var items: List<SignalEntity>
) : ArrayAdapter<SignalEntity>(_context, resource, items) {

    @SuppressLint("SimpleDateFormat", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val formatter = SimpleDateFormat("dd MMM yyyy\nHH : mm : ss")

        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)
        val timeStamp = view.findViewById<TextView>(R.id.textView2)
        val strength = view.findViewById<ImageView>(R.id.imageView2)
        val general = view.findViewById<TextView>(R.id.textView3)
        val level = view.findViewById<TextView>(R.id.textView1)

        timeStamp.text = formatter.format(items[position].timeStamp)
        when (items[position].level) {
            0 -> {
                strength.setImageResource(R.drawable.ic_strength_0)
            }
            1 -> {
                strength.setImageResource(R.drawable.ic_strength_1)
            }
            2 -> {
                strength.setImageResource(R.drawable.ic_strength_2)
            }
            3 -> {
                strength.setImageResource(R.drawable.ic_strength_3)
            }
            else -> {
                strength.setImageResource(R.drawable.ic_strength_4)
            }
        }
        general.text = items[position].attribute
        level.text = items[position].strength.toString() + " dbm"
        return view
    }


}