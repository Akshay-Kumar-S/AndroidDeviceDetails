package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.viewModel.SignalViewModel
import com.google.gson.Gson

class SignalActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignalBinding
    private lateinit var signalController: ActivityController<SignalCookedData>
    private lateinit var signalViewModel: SignalViewModel

    companion object {
        const val NAME = "signal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal)
        binding.lifecycleOwner = this
        signalController = ActivityController(this, NAME, binding)
        binding.pickerBinding.apply {
            startTime.setOnClickListener(this@SignalActivity)
            startDate.setOnClickListener(this@SignalActivity)
            endTime.setOnClickListener(this@SignalActivity)
            endDate.setOnClickListener(this@SignalActivity)
        }
        binding.moreDetails.setOnClickListener(this@SignalActivity)
    }

    override fun onClick(v: View?) {
        signalViewModel = signalController.viewModel as SignalViewModel
        when (v!!.id) {
            R.id.startTime -> signalController.setTime(this, R.id.startTime)
            R.id.startDate -> signalController.setDate(this, R.id.startDate)
            R.id.endTime -> signalController.setTime(this, R.id.endTime)
            R.id.endDate -> signalController.setDate(this, R.id.endDate)
            R.id.more_details -> {
                val signalJson = Gson().toJson(signalViewModel.graphEntryList)
                val intent = Intent(this@SignalActivity, SignalGraphActivity::class.java)
                intent.putExtra("signal", signalJson)
                startActivity(intent)
            }
        }
    }
}