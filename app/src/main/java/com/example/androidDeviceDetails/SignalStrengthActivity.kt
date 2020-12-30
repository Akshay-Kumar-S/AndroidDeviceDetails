package com.example.androidDeviceDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.controllers.SignalController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding

class SignalStrengthActivity : AppCompatActivity() {
    private lateinit var signalBinding: ActivitySignalStrengthBinding
    lateinit var signalController: SignalController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signalBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        signalController = SignalController(signalBinding, this, lifecycleOwner = this)
        signalController.onCreate()
        signalBinding.filter.setOnClickListener() {
            signalController.setCooker()
        }
    }
}
