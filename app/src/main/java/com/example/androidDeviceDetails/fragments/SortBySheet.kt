package com.example.androidDeviceDetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.SortOptionAdaptor
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBySheet(private val options: ArrayList<Pair<String, () -> Unit>>) :
    BottomSheetDialogFragment() {
    private var v: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.sort_by_bottomsheet, container, false)
        val listView = v?.findViewById<ListView>(R.id.listView)
        listView?.adapter = SortOptionAdaptor(requireContext(), R.layout.sort_by_tile, options)
        listView?.setOnItemClickListener { parent, _, position, _ ->
            val adaptor = parent.adapter as SortOptionAdaptor
            adaptor.getItem(position)?.second?.invoke()
            this.dismiss()
        }
        return v
    }
}

