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
    private var items: ArrayList<Pair<String, () -> Unit>>,
    private var selectedOption: Int
) : ArrayAdapter<Pair<String, () -> Unit>>(_context, resource, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(_context).inflate(resource, null)
        view.apply {
            if (position != selectedOption)
                findViewById<ImageView>(R.id.check).isVisible = false
            findViewById<TextView>(R.id.option).text = items[position].first
        }
        return view
    }
}