package com.example.androidDeviceDetails.controller

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.models.TimeInterval

class AppController<T>(dataType: String, binding: ViewBinding, val context: Context) {

    private var cooker: BaseCooker = BaseCooker.getCooker(dataType)
    var viewModel: BaseViewModel = BaseViewModel.getViewModel(dataType, binding, context)

    fun cook(timeInterval: TimeInterval) {
        cooker.cook(timeInterval, onCookingDone)
    }

    private val onCookingDone = object : ICookingDone<T> {
        override fun onDone(outputList: ArrayList<T>) =
            viewModel.onData(outputList)
    }
}

