package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
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

    private var cellularStrengthPercentage: Float = 0F
    private var wifiStrengthPercentage: Float = 0F
    private val db = RoomDB.getDatabase()!!
    var signalList = arrayListOf<SignalGraphEntry>()
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
    @SuppressLint("SetTextI18n")
    fun updateStrengthPercentage(signalRaw: SignalRaw) {
        when (signalRaw.signal) {
            Signal.WIFI.ordinal ->
                wifiStrengthPercentage = signalRaw.strengthPercentage
            Signal.CELLULAR.ordinal ->
                cellularStrengthPercentage = signalRaw.strengthPercentage
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
                    cellularStrengthPercentage, 1000
                )
                pointerWifiSpeedometer.speedTo(
                    wifiStrengthPercentage, 1000
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
        mostUsedData = outputList.filterIsInstance<SignalCookedData>().first()
        signalList = outputList.filterIsInstance<ArrayList<SignalGraphEntry>>().first()
        if (signalBinding.pointerCellularSpeedometer.tag == "true") {
            cellularStrengthPercentage = mostUsedData.lastCellularStrength
            wifiStrengthPercentage = mostUsedData.lastWifiStrength
            updateGauge()
        }

        signalBinding.pointerCellularSpeedometer.tag = "false"
        updateList()
    }

    private fun updateList() {
        signalBinding.mostUsedOperator.cookedValue.text = mostUsedData.mostUsedOperator
        signalBinding.mostUsedBand.cookedValue.text = mostUsedData.mostUsedLevel
        signalBinding.roamingTime.cookedValue.text = mostUsedData.roamingTime
        signalBinding.mostUsedWifi.cookedValue.text = mostUsedData.mostUsedWifi
        signalBinding.mostUsedWifiLevel.cookedValue.text =
            getWifiLevel(mostUsedData.mostUsedWifiLevel.toInt())
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