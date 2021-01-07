package com.example.androidDeviceDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet(private var onApply: () -> Unit) : BottomSheetDialogFragment() {
    private var v: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.bottom_sheet, container, false)
        v?.setOnClickListener {
            onApply.invoke()
            this.dismiss()
        }
        return v
    }

}