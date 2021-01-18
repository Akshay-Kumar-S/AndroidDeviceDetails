package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R

class SortOptionAdaptor(
    private var _context: Context, private var resource: Int,
    private var items: ArrayList<Pair<String, () -> Unit>>
) : ArrayAdapter<Pair<String, () -> Unit>>(_context, resource, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)
        val optionName = view.findViewById<TextView>(R.id.option)
        val check = view.findViewById<ImageView>(R.id.check)
        check.isVisible = false
        optionName.text = items[position].first
        return view
    }
}