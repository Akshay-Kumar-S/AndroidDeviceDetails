package com.example.androidDeviceDetails

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.controllers.SignalController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SignalStrengthActivity : AppCompatActivity(){

    private var db = RoomDB.getDatabase()!!
    private lateinit var filter: Button
    private lateinit var binding: ActivitySignalStrengthBinding
    private var fromTimestamp: Long = 0
    private var toTimestamp: Long = 0
    private var toggle = 0
    private var signal = 0
    lateinit var controller:SignalController

    lateinit var signalViewModel:SignalViewModel
    lateinit var signalStrengthCooker:SignalStrengthCooker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
         signalViewModel = SignalViewModel(binding, this)
         signalStrengthCooker=SignalStrengthCooker(binding,this)
        signalStrengthCooker.onCreate()

        controller = SignalController(binding,this,lifecycleOwner = this)
        controller.observeSignal(Signal.CELLULAR.ordinal)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    binding.gauge.setMaxValue(-50f)
                    binding.gauge.setMinValue(-150f)
                    controller.observeSignal(Signal.CELLULAR.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    binding.gauge.setMaxValue(0f)
                    binding.gauge.setMinValue(-100f)
                    controller.observeSignal(Signal.WIFI.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        filter = findViewById(R.id.filter)
        filter.setOnClickListener() {
            addListView()
        }
    }
    fun addListView() {
        fromTimestamp=signalStrengthCooker.fromTimeStamp()
        toTimestamp=signalStrengthCooker.toTimeStamp()
        GlobalScope.launch {
            val wifiList = db.wifiDao().getAllBetween(
                fromTimestamp,
                toTimestamp
            )
            val cellularList = db.cellularDao().getAllBetween(
                fromTimestamp,
                toTimestamp
            )
            runOnUiThread {
                signalViewModel.updateListView(cellularList)
            }
        }
    }
}
