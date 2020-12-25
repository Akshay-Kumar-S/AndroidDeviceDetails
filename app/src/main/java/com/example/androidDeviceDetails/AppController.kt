package com.example.androidDeviceDetails

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.base.BaseViewModel

class AppController(private val dataType: String) {

    private lateinit var cooker: BaseCooker

    fun start() {
        cooker = BaseCooker.getCooker(dataType)
        val list = cooker.cook(0L)
        val vm = BaseViewModel.getViewModel(dataType)
        vm.populateList(list)
    }
}