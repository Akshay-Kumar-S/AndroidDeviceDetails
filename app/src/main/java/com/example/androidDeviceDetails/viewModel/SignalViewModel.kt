package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalCookedData
import com.example.androidDeviceDetails.models.signalModels.SignalRaw
import com.example.androidDeviceDetails.ui.SignalActivity
import com.example.androidDeviceDetails.utils.Signal

/**
 * Implements [BaseViewModel]
 */
@RequiresApi(Build.VERSION_CODES.N)
class SignalViewModel(
    private val signalBinding: ActivitySignalBinding,
    val context: Context
) : BaseViewModel() {
    private var cellularStrength: Int = -120
    private var wifiStrength: Int = -127
    private var linkspeed: String = ""
    private var cellInfoType: String = ""
    private var signal: Int = Signal.CELLULAR.ordinal
    private lateinit var cellularList: ArrayList<SignalRaw>
    private lateinit var wifiList: ArrayList<SignalRaw>
    private val db = RoomDB.getDatabase()!!

    init {
        observeSignal()
    }

    /**
     * This method is called on the initialisation of the [SignalViewModel].
     * It is used to observe the last live data of [SignalRaw].
     * And on notification, updates values via [updateValue].
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeSignal() {
        db.signalDao().getLastLive().observe(signalBinding.lifecycleOwner!!) {
            if (it != null) updateValue(it)
        }
    }

    /**
     * This method is called only initially to prepopulate the card view.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initialView() {
        if (cellularList.isNotEmpty()) {
            cellularStrength = cellularList.last().strength
            cellInfoType = cellularList.last().attribute
        }
        if (wifiList.isNotEmpty()) {
            wifiStrength = wifiList.last().strength
            linkspeed = "${wifiList.last().attribute} Mbps"
        }
        updateCardView()
    }

    /**
     * This method updates the values of wifi strength, link speed,
     * cellular strength and cell info type upon call from [observeSignal].
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    fun updateValue(signalRaw: SignalRaw) {
        when (signalRaw.signal) {
            Signal.WIFI.ordinal -> {
                wifiStrength = signalRaw.strength
                linkspeed = "${signalRaw.attribute} Mbps"
            }
            Signal.CELLULAR.ordinal -> {
                cellularStrength = signalRaw.strength
                cellInfoType = signalRaw.attribute
            }
        }
        updateCardView()
    }

    /**
     * This method updates the Card view in the UI based on the selected menu - CELLULAR or WIFI.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    fun updateCardView() {

        Log.e("updating", "working $cellularStrength $wifiStrength")
        signalBinding.pointerCellularSpeedometer.post() {
            signalBinding.pointerCellularSpeedometer.speedTo(
                (-124 - cellularStrength) / 96.toFloat() * (-100),
                1000
            )
            signalBinding.pointerWifiSpeedometer.speedTo(
                (-127 - wifiStrength) / 103.toFloat() * (-100),
                1000
            )
        }

    }

    /**
     * This method is called once the [SignalCooker] finishes cooking.
     * This method separates the cooked data into CELLULAR list and WIFI list
     * and calls [updateListView] to update list.
     * >
     * Overrides : [onDone] in [BaseViewModel].
     * @param outputList List of cooked data.
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        wifiList = arrayListOf()
        cellularList = arrayListOf()
       // val signalList = outputList as ArrayList<SignalRaw>
//        if (signalList.isNotEmpty()) {
//            for (signal in signalList) {
//                when (signal.signal) {
//                    Signal.WIFI.ordinal -> wifiList.add(signal)
//                    Signal.CELLULAR.ordinal -> cellularList.add(signal)
//                }
//            }
//        }
        initialView()
        signalBinding.mostUsedOperator.textView3.text=(outputList.first() as SignalCookedData).mostUsedOperator
        signalBinding.mostUsedBand.textView3.text=(outputList.first() as SignalCookedData).mostUsedLevel
        signalBinding.roamingTime.textView3.text=(outputList.first() as SignalCookedData).roamingTime.toString()
        signalBinding.mostUsedWifi.textView3.text=(outputList.first() as SignalCookedData).mostUsedWifi
        signalBinding.mostUsedWifiLevel.textView3.text=(outputList.first() as SignalCookedData).mostUsedWifiLevel

    }

    /**
     * This method is called whenever the menu is chosen from the [SignalActivity].
     * Depending on the menu chosen - WIFI or CELLULAR, respective gauge, card and list
     * views are updated.
     * @param type to indicate which signal menu is chosen - CELLULAR or WIFI.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun filter(type: Int) {
        // signal = type
        // updateCardView()
        //updateListView()
    }

    /**
     * This method updates List in the UI based on the selected menu.
     * If the selected menu is CELLULAR, displays list of CELLULAR signal values.
     * And if the selected menu is WIFI, displays list of WIFI signal values.
     */
    private fun updateListView() {

    }

}