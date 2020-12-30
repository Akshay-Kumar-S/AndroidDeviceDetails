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

class SignalStrengthActivity : AppCompatActivity() {

    private var db = RoomDB.getDatabase()!!
    private lateinit var filter: Button
    private lateinit var binding: ActivitySignalStrengthBinding
    private var fromTimestamp: Long = 0
    private var toTimestamp: Long = 0
    lateinit var controller: SignalController

    lateinit var signalStrengthViewModel: SignalStrengthViewModel
    lateinit var signalStrengthCooker: SignalStrengthCooker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        signalStrengthViewModel = SignalStrengthViewModel(binding, this)
        signalStrengthCooker = SignalStrengthCooker(binding, this)
        signalStrengthCooker.onCreate()
        controller = SignalController(binding, this, lifecycleOwner = this)
        controller.onCreate()
        filter = findViewById(R.id.filter)
        filter.setOnClickListener() {
           // addListView()
            controller.updateListView()
        }
    }

//    fun addListView() {
//        fromTimestamp = signalStrengthCooker.fromTimeStamp()
//        toTimestamp = signalStrengthCooker.toTimeStamp()
//        GlobalScope.launch {
//            val wifiList = db.wifiDao().getAllBetween(
//                fromTimestamp,
//                toTimestamp
//            )
//            val cellularList = db.cellularDao().getAllBetween(
//                fromTimestamp,
//                toTimestamp
//            )
//            runOnUiThread {
//                signalStrengthViewModel.updateListView(cellularList)
//            }
//        }
//    }
}
