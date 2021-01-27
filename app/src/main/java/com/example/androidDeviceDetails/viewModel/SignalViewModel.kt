package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalEntry
import com.example.androidDeviceDetails.utils.Signal

/**
 * Implements [BaseViewModel]
 */
class SignalViewModel(
    private val signalBinding: ActivitySignalBinding,
    val context: Context
) : BaseViewModel() {
    companion object {
        const val LEVEL_POOR = 0
        const val LEVEL_LOW = 1
        const val LEVEL_MEDIUM = 2
        const val LEVEL_GOOD = 3
        const val LEVEL_EXCELLENT = 4
    }

    private var cellularStrength: Float = 0F
    private var wifiStrength: Float = 0F
    private val db = RoomDB.getDatabase()!!
    var signalList = arrayListOf<SignalEntry>()
    private lateinit var listData: SignalCookedData

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
        updateGauge()
    }

    /**
     * This method updates gauge in the UI.
     */
    @SuppressLint("SetTextI18n")
    fun updateGauge() {
        signalBinding.pointerCellularSpeedometer.post {
            signalBinding.apply {
                pointerCellularSpeedometer.speedTo(
                    cellularStrength, 1000
                )
                pointerWifiSpeedometer.speedTo(
                    wifiStrength, 1000
                )
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
        if (signalBinding.pointerCellularSpeedometer.tag == "true") {
            cellularStrength = listData.lastCellularStrength
            wifiStrength = listData.lastWifiStrength
            updateGauge()
        }

        signalBinding.pointerCellularSpeedometer.tag = "false"
        updateList()
    }

    private fun updateList() {
        signalBinding.mostUsedOperator.cookedValue.text = listData.mostUsedOperator
        signalBinding.mostUsedBand.cookedValue.text = listData.mostUsedLevel
        signalBinding.roamingTime.cookedValue.text = listData.roamingTime
        signalBinding.mostUsedWifi.cookedValue.text = listData.mostUsedWifi
        signalBinding.mostUsedWifiLevel.cookedValue.text =
            getWifiLevel(listData.mostUsedWifiLevel.toInt())
    }

    private fun getWifiLevel(level: Int): String {
        return when (level) {
            LEVEL_POOR -> "Poor"
            LEVEL_LOW -> "Low"
            LEVEL_MEDIUM -> "Medium"
            LEVEL_GOOD -> "Good"
            LEVEL_EXCELLENT -> "Excellent"
            else -> "unknown"
        }
    }
}