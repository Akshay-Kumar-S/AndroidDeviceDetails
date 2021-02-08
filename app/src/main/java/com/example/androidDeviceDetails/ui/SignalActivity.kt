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
import com.example.androidDeviceDetails.utils.DateTimePicker
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
        DateTimePicker(
            this, binding.pickerBinding, signalController::setTime, signalController::setDate
        )
        binding.moreDetails.setOnClickListener(this@SignalActivity)
    }

    override fun onClick(v: View?) {
        signalViewModel = signalController.viewModel as SignalViewModel
        when (v!!.id) {
            R.id.more_details -> {
                val signalJson = Gson().toJson(signalViewModel.graphEntryList)
                val intent = Intent(this@SignalActivity, SignalGraphActivity::class.java)
                intent.putExtra("signal", signalJson)
                startActivity(intent)
            }
        }
    }
}