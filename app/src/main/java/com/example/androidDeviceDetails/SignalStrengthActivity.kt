package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw
import com.example.androidDeviceDetails.utils.ListAdaptor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SignalStrengthActivity : AppCompatActivity(){

    private var db = RoomDB.getDatabase()!!
    private lateinit var filter: Button
    private lateinit var binding: ActivitySignalStrengthBinding
    private var fromTimestamp: Long = 0
    private var toTimestamp: Long = 0
    private var toggle = 0
    private var signal = 0

    lateinit var signalStrengthViewModel:SignalStrengthViewModel
    lateinit var signalStrengthCooker:SignalStrengthCooker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
         signalStrengthViewModel = SignalStrengthViewModel(binding, this)
         signalStrengthCooker=SignalStrengthCooker(binding,this)
        signalStrengthCooker.onCreate()
        db.cellularDao().getLastLive().observe(this) {
            if (signal == 0)
                  signalStrengthViewModel.updateCellularGauge(it)
        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    binding.gauge.setMaxValue(-50f)
                    binding.gauge.setMinValue(-150f)
                    signal = 0
                    db.cellularDao().getLastLive().observe(this) {
                        if (signal == 0)
                                signalStrengthViewModel.updateCellularGauge(it)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    binding.gauge.setMaxValue(0f)
                    binding.gauge.setMinValue(-100f)
                    signal = 1
                    db.wifiDao().getLastLive().observe(this) {
                        if (signal == 1)
                        signalStrengthViewModel.updateWifiGauge(it)
                    }
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
                signalStrengthViewModel.updateListView(cellularList)
            }
        }
    }
}
