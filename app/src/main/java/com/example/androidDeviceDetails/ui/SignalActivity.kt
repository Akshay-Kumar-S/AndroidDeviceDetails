package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.viewModel.SignalViewModel
import com.google.gson.Gson

class SignalActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignalBinding
    private lateinit var signalController: ActivityController<Any>

    companion object {
        const val NAME = "signal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal)
        binding.lifecycleOwner = this
        signalController = ActivityController(
            NAME, binding, this, binding.pickerBinding, supportFragmentManager
        )
        val signalViewModel = signalController.viewModel as SignalViewModel
        binding.apply {
            pickerBinding.startTime.setOnClickListener(this@SignalActivity)
            pickerBinding.startDate.setOnClickListener(this@SignalActivity)
            pickerBinding.endTime.setOnClickListener(this@SignalActivity)
            pickerBinding.endDate.setOnClickListener(this@SignalActivity)
        }

        binding.more.setOnClickListener {
            val signalList = signalViewModel.signalList
            val gson = Gson()
            val signalJson = gson.toJson(signalList)
            val intent = Intent(this@SignalActivity, GraphActivity::class.java)
            intent.putExtra("signal", signalJson)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> signalController.setTime(this, R.id.startTime)
            R.id.startDate -> signalController.setDate(this, R.id.startDate)
            R.id.endTime -> signalController.setTime(this, R.id.endTime)
            R.id.endDate -> signalController.setDate(this, R.id.endDate)
        }
    }

}