package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.database.SignalRaw
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.signal.Signal
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
import com.example.androidDeviceDetails.utils.Utils

/**
 * Implements [BaseViewModel]
 */
class SignalViewModel(
    private val signalBinding: ActivitySignalBinding, val context: Context
) : BaseViewModel() {
    private var cellularStrengthPercentage = 0F
    private var wifiStrengthPercentage = 0F
    private val db = RoomDB.getDatabase()!!
    var graphEntryList = listOf<SignalGraphEntry>()
    private lateinit var mostUsedData: SignalCookedData

    init {
        observeSignal()
    }

    /**
     * This method is called on the initialisation of the [SignalViewModel].
     * It is used to observe the last live data of [SignalRaw].
     * And on notification, updates values via [updateStrengthPercentage].
     */
    private fun observeSignal() {
        db.signalDao().getLastLive().observe(signalBinding.lifecycleOwner!!) {
            if (it != null) updateStrengthPercentage(it)
        }
    }

    /**
     * This method updates the values of wifi strength, link speed,
     * cellular strength and cell info type upon call from [observeSignal].
     */
    private fun updateStrengthPercentage(signalRaw: SignalRaw) {
        when (signalRaw.signal) {
            Signal.WIFI -> wifiStrengthPercentage = signalRaw.strengthPercentage
            Signal.CELLULAR -> cellularStrengthPercentage = signalRaw.strengthPercentage
        }
        updateGauge()
    }

    /**
     * This method updates gauge in the UI.
     */
    private fun updateGauge() {
        signalBinding.pointerCellularSpeedometer.post {
            signalBinding.apply {
                pointerCellularSpeedometer.speedTo(cellularStrengthPercentage, 1000)
                pointerWifiSpeedometer.speedTo(wifiStrengthPercentage, 1000)
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
    override fun <T> onComplete(outputList: ArrayList<T>) {
        mostUsedData = outputList.filterIsInstance<SignalCookedData>().first()
        graphEntryList = outputList.filterIsInstance<SignalGraphEntry>()
        if (signalBinding.pointerCellularSpeedometer.tag == "true") {
            cellularStrengthPercentage = mostUsedData.lastCellularStrength
            wifiStrengthPercentage = mostUsedData.lastWifiStrength
            updateGauge()
            signalBinding.pointerCellularSpeedometer.tag = "false"
        }
        updateList()
    }

    private fun updateList() {
        signalBinding.mostUsedOperator.cookedValue.text = mostUsedData.mostUsedOperator
        signalBinding.mostUsedBand.cookedValue.text = mostUsedData.mostUsedCellularBand
        signalBinding.roamingTime.cookedValue.text = Utils.getTimePeriod(mostUsedData.roamingTime)
        signalBinding.mostUsedWifi.cookedValue.text = mostUsedData.mostUsedWifi
        signalBinding.mostUsedWifiLevel.cookedValue.text = mostUsedData.mostUsedWifiLevel
    }
}