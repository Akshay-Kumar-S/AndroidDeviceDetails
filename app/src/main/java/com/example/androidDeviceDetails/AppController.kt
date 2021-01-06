package com.example.androidDeviceDetails

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.base.BaseViewModel
import java.util.*
import javax.inject.Inject

class AppController<T> constructor(private val dataType: String, private val context:Context, private val binding: ViewBinding) {

    private lateinit var cooker: BaseCooker
    lateinit var vm: BaseViewModel

    fun start() {
        cooker = BaseCooker.getCooker(dataType)
        vm = BaseViewModel.getViewModel(dataType,context,binding)
        cooker.cook(onCookingDone, Calendar.getInstance().timeInMillis)
    }

    private val onCookingDone = object : ICookingDone<T> {
        override fun onDone(outputList: MutableList<T>) = vm.populateList(outputList)
    }
}