package com.example.androidDeviceDetails

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.base.BaseViewModel
import java.util.*

class AppController<T>(private val dataType: String,private val context:Context, val binding: ViewBinding) {

    private lateinit var cooker: BaseCooker
    private lateinit var vm: BaseViewModel

    fun start() {
        cooker = BaseCooker.getCooker(dataType)
        vm = BaseViewModel.getViewModel(dataType,context,binding)
        cooker.cook(onCookingDone, Calendar.getInstance().timeInMillis)
    }

    private val onCookingDone = object : ICookingDone<T> {
        override fun onNoData() {
            vm.onNoData()
        }
        override fun onData(list: MutableList<T>) = vm.populateList(list)
    }
}