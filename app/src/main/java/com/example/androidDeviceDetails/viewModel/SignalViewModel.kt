package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.models.signalModels.SignalCookedData
import com.example.androidDeviceDetails.models.signalModels.SignalEntry
import com.example.androidDeviceDetails.utils.Signal

/**
 * Implements [BaseViewModel]
 */
class SignalViewModel(
    private val signalBinding: ActivitySignalBinding,
    val context: Context
) : BaseViewModel() {
    private var cellularStrength: Float = 0F
    private var wifiStrength: Float = 0F
    private val db = RoomDB.getDatabase()!!
    var signalList = arrayListOf<SignalEntry>()
    lateinit var listData: SignalCookedData

    init {
        observeSignal()
    }

    /**
     * This method is called on the initialisation of the [SignalViewModel].
     * It is used to observe the last live data of [SignalRaw].
     * And on notification, updates values via [updateValue].
     */
    private fun observeSignal() {
        db.signalDao().getLastLive().observe(signalBinding.lifecycleOwner!!) {
            if (it != null) updateValue(it)
        }
    }

    /**
     * This method updates the values of wifi strength, link speed,
     * cellular strength and cell info type upon call from [observeSignal].
     */
    @SuppressLint("SetTextI18n")
    fun updateValue(signalRaw: SignalRaw) {
        when (signalRaw.signal) {
            Signal.WIFI.ordinal ->
                wifiStrength = signalRaw.strengthPercentage
            Signal.CELLULAR.ordinal ->
                cellularStrength = signalRaw.strengthPercentage
        }
        updateGuage()
    }

    /**
     * This method updates the Card view in the UI based on the selected menu - CELLULAR or WIFI.
     */
    @SuppressLint("SetTextI18n")
    fun updateGuage() {
        signalBinding.pointerCellularSpeedometer.post {
            if(signalBinding.pointerCellularSpeedometer.tag=="true")
            signalBinding.apply {
                pointerCellularSpeedometer.speedTo(
                    cellularStrength,
                    1000
                )
                pointerWifiSpeedometer.speedTo(
                    wifiStrength, 1000
                )
               pointerCellularSpeedometer.tag="false"
            }

        }

    }

    /**
     * This method is called once the [SignalCooker] finishes cooking.
     * This method separates the cooked data into CELLULAR list and WIFI list
     * >
     * Overrides : [onDone] in [BaseViewModel].
     * @param outputList List of cooked data.
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        listData = outputList.filterIsInstance<SignalCookedData>().first()
        signalList = outputList.filterIsInstance<ArrayList<SignalEntry>>().first()
        updateGuage()
        updateList()
    }

    private fun updateList() {
        signalBinding.mostUsedOperator.cookedValue.text = listData.mostUsedOperator
        signalBinding.mostUsedBand.cookedValue.text = listData.mostUsedLevel
        signalBinding.roamingTime.cookedValue.text = listData.roamingTime.toString()
        signalBinding.mostUsedWifi.cookedValue.text = listData.mostUsedWifi
        signalBinding.mostUsedWifiLevel.cookedValue.text = listData.mostUsedWifiLevel
        cellularStrength = listData.lastCellularStrength
        wifiStrength = listData.lastWifiStrength
    }
}